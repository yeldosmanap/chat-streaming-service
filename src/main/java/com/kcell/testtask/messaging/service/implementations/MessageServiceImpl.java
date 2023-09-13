package com.kcell.testtask.messaging.service.implementations;

import com.kcell.testtask.messaging.model.Message;
import com.kcell.testtask.messaging.repository.MessageRepository;
import com.kcell.testtask.messaging.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    @Override
    public List<Message> getMessages() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> getMessagesWithAuthor() {
        return messageRepository.findAllBy();
    }
}
