package com.kcell.testtask.messaging.service;

import com.kcell.testtask.messaging.model.Message;

public interface MessageSenderService {
    void sendMessage(Message message);
}
