package com.kcell.testtask.messaging.kafka.consumer;

import com.kcell.testtask.messaging.model.Message;
import com.kcell.testtask.messaging.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Task implements Runnable {
    private volatile boolean stopped = false;
    private volatile boolean started = false;
    private volatile boolean finished = false;
    private final List<ConsumerRecord<String, Message>> records;
    private final MessageRepository messageRepository;
    private final CompletableFuture<Long> completion;
    private final ReentrantLock startStopLock;
    private final AtomicLong currentOffset;

    public Task(List<ConsumerRecord<String, Message>> records, MessageRepository messageRepository) {
        this.records = records;
        this.messageRepository = messageRepository;
        this.completion = new CompletableFuture<>();
        this.startStopLock = new ReentrantLock();
        this.currentOffset = new AtomicLong();
    }


    public void run() {
        startStopLock.lock();
        if (stopped){
            return;
        }
        started = true;
        startStopLock.unlock();

        for (ConsumerRecord<String, Message> record : records) {
            if (stopped)
                break;
            try {
                saveToDatabase(record);
                currentOffset.set(record.offset() + 1);
            } catch (Exception e) {
                log.error("Exception while saving message to database", e);

                throw new RuntimeException(e);
            }
        }

        finished = true;
        completion.complete(currentOffset.get());
    }

    public long getCurrentOffset() {
        return currentOffset.get();
    }

    public void stop() {
        startStopLock.lock();
        this.stopped = true;
        if (!started) {
            finished = true;
            completion.complete(currentOffset.get());
        }
        startStopLock.unlock();
    }

    public long waitForCompletion() {
        try {
            return completion.get();
        } catch (InterruptedException | ExecutionException e) {
            return -1;
        }
    }

    public boolean isFinished() {
        return finished;
    }

    private void saveToDatabase(ConsumerRecord<String, Message> record) {
        record.value().setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        messageRepository.save(record.value());
    }

}
