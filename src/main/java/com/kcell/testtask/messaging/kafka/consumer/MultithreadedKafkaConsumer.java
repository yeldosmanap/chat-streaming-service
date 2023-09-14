package com.kcell.testtask.messaging.kafka.consumer;

import com.kcell.testtask.messaging.dto.kafka.MessageDto;
import com.kcell.testtask.messaging.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class MultithreadedKafkaConsumer implements Runnable, ConsumerRebalanceListener {
    @Value("${spring.kafka.consumer.poll-timeout-ms}")
    private static int POLL_TIMEOUT_MILLISECONDS;

    @Value("${spring.kafka.consumer.commit-interval-ms}")
    private static int COMMIT_INTERVAL_MILLISECONDS;

    private final KafkaConsumer<String, MessageDto> consumer;

    private final MessageRepository messageRepository;

    private final ExecutorService executor;

    private final Map<TopicPartition, Task> activeTasks;

    private final Map<TopicPartition, OffsetAndMetadata> offsetsToCommit;

    private final AtomicBoolean stopped;

    private final String topic;

    private long lastCommitTime = System.currentTimeMillis();

    public MultithreadedKafkaConsumer(
            KafkaConsumer<String, MessageDto> kafkaConsumer,
            MessageRepository messageRepository,
            @Value("${spring.kafka.consumer.topic}") String topic,
            @Value("${spring.kafka.consumer.threads-amount}") Integer threadsCount) {
        this.messageRepository = messageRepository;
        this.consumer = kafkaConsumer;
        this.topic = topic;
        this.executor = Executors.newFixedThreadPool(threadsCount);
        this.activeTasks = new HashMap<>();
        this.offsetsToCommit = new HashMap<>();
        this.stopped = new AtomicBoolean(false);
        log.info("Consumer created!");
        new Thread(this).start();
    }

    /**
     * This method is called by the main thread
     */
    @Override
    public void run() {
        try {
            consumer.subscribe(Collections.singleton(topic), this);
            while (!stopped.get()) {
                // Fetch records from a Kafka Topic using a Kafka Consumer instance (poll method)
                // with a timeout of {POLL_TIMEOUT_MILLS} milliseconds
                ConsumerRecords<String, MessageDto> records = consumer.poll(Duration.of(POLL_TIMEOUT_MILLISECONDS, ChronoUnit.MILLIS));

                // Handle fetched records (create tasks for them)
                handleFetchedRecords(records);

                // Check if any active tasks are finished
                checkActiveTasks();

                // Commit offsets if needed (every {c}} seconds)
                commitOffsets();
            }
        } catch (WakeupException e) {
            if (!stopped.get())
                throw e;
        } finally {
            consumer.close();
        }
    }

    /**
     * Creates tasks for fetched records
     */
    private void handleFetchedRecords(ConsumerRecords<String, MessageDto> records) {
        if (records.count() > 0) {
            List<TopicPartition> partitionsToPause = new ArrayList<>();
            records.partitions().forEach(partition -> {
                List<ConsumerRecord<String, MessageDto>> partitionRecords = records.records(partition);
                Task task = new Task(partitionRecords, messageRepository);
                partitionsToPause.add(partition);
                executor.submit(task);
                activeTasks.put(partition, task);
            });
            consumer.pause(partitionsToPause);
        }
    }

    /**
     * Commits offsets if needed (every {c}} seconds)
     */
    private void commitOffsets() {
        try {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - lastCommitTime > COMMIT_INTERVAL_MILLISECONDS) {
                if (!offsetsToCommit.isEmpty()) {
                    consumer.commitSync(offsetsToCommit);
                    offsetsToCommit.clear();
                }
                lastCommitTime = currentTimeMillis;
            }
        } catch (Exception e) {
            log.error("Failed to commit offsets!", e);
        }
    }

    /**
     * Checks the offsets of active tasks
     */
    private void checkActiveTasks() {
        List<TopicPartition> finishedTasksPartitions = new ArrayList<>();
        activeTasks.forEach((partition, task) -> {
            // Check if task is finished and add it to the list of finished tasks
            if (task.isFinished()) {
                finishedTasksPartitions.add(partition);
            }

            long offset = task.getCurrentOffset();

            if (offset > 0) {
                // Add offset to map of offsets to commit
                offsetsToCommit.put(partition, new OffsetAndMetadata(offset));
            }
        });

        // Remove finished tasks from the map of active tasks
        finishedTasksPartitions.forEach(activeTasks::remove);

        // Resume partitions for finished tasks
        consumer.resume(finishedTasksPartitions);
    }

    /**
     * This method is called by the Kafka Consumer thread
     * when going to rebalance partitions between consumer instances.
     */
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {

        // Stop all tasks handling records from revoked partitions
        Map<TopicPartition, Task> stoppedTask = new HashMap<>();
        for (TopicPartition partition : partitions) {
            Task task = activeTasks.remove(partition);
            if (task != null) {
                task.stop();
                stoppedTask.put(partition, task);
            }
        }

        // Wait for stopped tasks to complete processing of current record
        stoppedTask.forEach((partition, task) -> {
            long offset = task.waitForCompletion();
            if (offset > 0)
                offsetsToCommit.put(partition, new OffsetAndMetadata(offset));
        });


        // Collect offsets for revoked partitions
        Map<TopicPartition, OffsetAndMetadata> revokedPartitionOffsets = new HashMap<>();
        partitions.forEach(partition -> {
            OffsetAndMetadata offset = offsetsToCommit.remove(partition);
            if (offset != null)
                revokedPartitionOffsets.put(partition, offset);
        });

        // Commit offsets for revoked partitions
        try {
            consumer.commitSync(revokedPartitionOffsets);
        } catch (Exception e) {
            log.warn("Failed to commit offsets for revoked partitions!");
        }
    }

    /**
     * This method is called when the Kafka Consumer thread is assigned partitions after rebalancing.
     */
    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        consumer.resume(partitions);
    }

    public MessageRepository getMessageRepository() {
        return messageRepository;
    }

    public void stopConsuming() {
        stopped.set(true);
        consumer.wakeup();
    }
}
