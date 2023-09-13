package com.kcell.testtask.messaging.service;

import com.kcell.testtask.messaging.dto.kafka.MessageDto;

public interface MessageSenderService {
    void sendMessage(MessageDto message);
}
