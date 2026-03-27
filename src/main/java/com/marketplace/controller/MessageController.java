package com.marketplace.controller;

import com.marketplace.dto.MessageDTO;
import com.marketplace.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    
    @GetMapping
    public ResponseEntity<List<MessageDTO.ConversationResponse>> getConversations(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.getConversations(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<MessageDTO.Response> sendMessage(
            @RequestBody MessageDTO.SendRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.sendMessage(request, userDetails.getUsername()));
    }

    @GetMapping("/conversation/{otherId}")
    public ResponseEntity<List<MessageDTO.Response>> getConversation(
            @PathVariable Long otherId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.getConversation(otherId, userDetails.getUsername()));
    }

    @PutMapping("/read/{otherId}")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long otherId,
            @AuthenticationPrincipal UserDetails userDetails) {
        messageService.markAsRead(otherId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.getUnreadCount(userDetails.getUsername()));
    }
}
