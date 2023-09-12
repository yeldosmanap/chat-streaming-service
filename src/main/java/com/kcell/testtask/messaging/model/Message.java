package com.kcell.testtask.messaging.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Message {
    private String content;
    private String timestamp;
}
