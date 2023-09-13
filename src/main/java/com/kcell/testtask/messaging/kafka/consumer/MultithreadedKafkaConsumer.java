package com.kcell.testtask.messaging.kafka.consumer;

import com.kcell.testtask.messaging.model.Message;
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
    private final KafkaConsumer<String, Message> consumer;
    private final MessageRepository messageRepository;
    private final ExecutorService executor;
    private final Map<TopicPartition, Task> activeTasks;
    private final Map<TopicPartition, OffsetAndMetadata> offsetsToCommit;
    private final AtomicBoolean stopped;
    private long lastCommitTime = System.currentTimeMillis();
    private final String topic;

    public MultithreadedKafkaConsumer(
            KafkaConsumer<String, Message> kafkaConsumer,
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


    @Override
    public void run() {
        try {
            consumer.subscribe(Collections.singleton(topic), this);
            while (!stopped.get()) {
                ConsumerRecords<String, Message> records = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
                handleFetchedRecords(records);
                checkActiveTasks();
                commitOffsets();
            }
        } catch (WakeupException e) {
            if (!stopped.get())
                throw e;
        } finally {
            consumer.close();
        }
    }

    private void handleFetchedRecords(ConsumerRecords<String, Message> records) {
        if (records.count() > 0) {
            List<TopicPartition> partitionsToPause = new ArrayList<>();
            records.partitions().forEach(partition -> {
                List<ConsumerRecord<String, Message>> partitionRecords = records.records(partition);
                Task task = new Task(partitionRecords, messageRepository);
                partitionsToPause.add(partition);
                executor.submit(task);
                activeTasks.put(partition, task);
            });
            consumer.pause(partitionsToPause);
        }
    }

    private void commitOffsets() {
        try {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - lastCommitTime > 5000) {
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


    private void checkActiveTasks() {
        List<TopicPartition> finishedTasksPartitions = new ArrayList<>();
        activeTasks.forEach((partition, task) -> {
            if (task.isFinished()) {
                finishedTasksPartitions.add(partition);
            }

            long offset = task.getCurrentOffset();

            if (offset > 0) {
                offsetsToCommit.put(partition, new OffsetAndMetadata(offset));
            }
        });
        finishedTasksPartitions.forEach(activeTasks::remove);
        consumer.resume(finishedTasksPartitions);
    }


    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {

        // 1. Stop all tasks handling records from revoked partitions
        Map<TopicPartition, Task> stoppedTask = new HashMap<>();
        for (TopicPartition partition : partitions) {
            Task task = activeTasks.remove(partition);
            if (task != null) {
                task.stop();
                stoppedTask.put(partition, task);
            }
        }

        // 2. Wait for stopped tasks to complete processing of current record
        stoppedTask.forEach((partition, task) -> {
            long offset = task.waitForCompletion();
            if (offset > 0)
                offsetsToCommit.put(partition, new OffsetAndMetadata(offset));
        });


        // 3. collect offsets for revoked partitions
        Map<TopicPartition, OffsetAndMetadata> revokedPartitionOffsets = new HashMap<>();
        partitions.forEach(partition -> {
            OffsetAndMetadata offset = offsetsToCommit.remove(partition);
            if (offset != null)
                revokedPartitionOffsets.put(partition, offset);
        });

        // 4. commit offsets for revoked partitions
        try {
            consumer.commitSync(revokedPartitionOffsets);
        } catch (Exception e) {
            log.warn("Failed to commit offsets for revoked partitions!");
        }
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        consumer.resume(partitions);
    }

    public void stopConsuming() {
        stopped.set(true);
        consumer.wakeup();
    }
}
