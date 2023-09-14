package com.kcell.testtask.messaging.dto.kafka;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private String content;
    private Timestamp createdAt;
    private Long userId;
}
