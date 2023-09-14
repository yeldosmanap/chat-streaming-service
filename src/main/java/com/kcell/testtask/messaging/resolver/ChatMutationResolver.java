package com.kcell.testtask.messaging.resolver;

import com.kcell.testtask.messaging.dto.kafka.MessageDto;
import com.kcell.testtask.messaging.repository.UserRepository;
import com.kcell.testtask.messaging.service.MessageSenderService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Controller
@RequiredArgsConstructor
public class ChatMutationResolver implements GraphQLMutationResolver {
    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('USER')")
    @MutationMapping
    public MessageDto sendMessage(@AuthenticationPrincipal UserDetails userDetails, @Argument String text) {
        var userFromRepository = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var message = MessageDto.builder()
                .content(text)
                .createdAt(Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
                .userId(userFromRepository.getId())
                .build();

        messageSenderService.sendMessage(message);
        return message;
    }
}
