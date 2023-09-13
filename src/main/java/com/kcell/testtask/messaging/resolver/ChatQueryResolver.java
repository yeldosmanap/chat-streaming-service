package com.kcell.testtask.messaging.resolver;

import com.kcell.testtask.messaging.model.Message;
import com.kcell.testtask.messaging.service.MessageService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatQueryResolver implements GraphQLQueryResolver {
    private final MessageService messageService;

    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public List<Message> messages() {
        return messageService.getMessagesWithAuthor();
    }
}
