package com.kcell.testtask.messaging.resolver;

import com.kcell.testtask.messaging.model.Message;
import com.kcell.testtask.messaging.service.MessageService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ChatQueryResolver implements GraphQLQueryResolver {
    private final MessageService messageService;

    public ChatQueryResolver(MessageService messageService) {
        this.messageService = messageService;
    }

    @QueryMapping
    public List<Message> messages() {
        return messageService.getMessages();
    }
}
