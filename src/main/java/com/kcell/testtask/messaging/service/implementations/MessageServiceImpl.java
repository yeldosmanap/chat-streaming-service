package com.kcell.testtask.messaging.service.implementations;

import com.kcell.testtask.messaging.model.Message;
import com.kcell.testtask.messaging.repository.MessageRepository;
import com.kcell.testtask.messaging.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public List<Message> getMessages() {
        return messageRepository.findAll();
    }
}
