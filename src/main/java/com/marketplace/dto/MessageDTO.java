package com.marketplace.dto;

import lombok.*;

import java.time.LocalDateTime;

public class MessageDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendRequest {
        private String content;
        private Long receiverId;
        private Long itemId;
        private String chatType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String content;
        private Long senderId;
        private String senderName;
        private String senderProfileImage;
        private Long receiverId;
        private String receiverName;
        private Long itemId;
        private String chatType;
        private boolean read;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConversationResponse {
        private Long participantId;
        private String participantName;
        private String lastMessage;
        private LocalDateTime lastMessageTime;
        private Long unreadCount;
        private String chatType;
    }
}
