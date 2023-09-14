package com.kcell.testtask.messaging.service;

import com.kcell.testtask.messaging.model.Message;

import java.util.List;

public interface MessageService {
    List<Message> getMessages();
    List<Message> getMessagesWithAuthor();
}
