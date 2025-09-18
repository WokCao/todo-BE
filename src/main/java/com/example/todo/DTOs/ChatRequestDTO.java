package com.example.todo.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ChatRequestDTO {
    private String model;
    private List<Message> messages;

    @AllArgsConstructor
    @Data
    public static class Message {
        private String role;
        private String content;
    }
}
