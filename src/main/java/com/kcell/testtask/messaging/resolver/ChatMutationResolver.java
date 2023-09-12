package com.kcell.testtask.messaging.resolver;

import com.kcell.testtask.messaging.model.Message;
import com.kcell.testtask.messaging.service.MessageSenderService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Controller
public class ChatMutationResolver implements GraphQLMutationResolver {
    private final MessageSenderService messageSenderService;

    public ChatMutationResolver(MessageSenderService messageSenderService) {
        this.messageSenderService = messageSenderService;
    }

    @MutationMapping
    public Message sendMessage(@Argument String text) {
        Message message = Message.builder()
                .content(text)
                .createdAt(Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
                .build();

        messageSenderService.sendMessage(message);
        return message;
    }
}
