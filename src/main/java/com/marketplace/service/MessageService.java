package com.marketplace.service;

import com.marketplace.dto.MessageDTO;
import com.marketplace.entity.Item;
import com.marketplace.entity.Message;
import com.marketplace.entity.User;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.ItemRepository;
import com.marketplace.repository.MessageRepository;
import com.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public MessageDTO.Response sendMessage(MessageDTO.SendRequest request, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User receiver;
        if (Message.ChatType.BUYER_ADMIN.name().equals(request.getChatType()) && request.getReceiverId() == null) {
            receiver = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == User.Role.ADMIN)
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No admin found to handle your request"));
        } else {
            receiver = userRepository.findById(request.getReceiverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));
        }

        Item item = null;
        if (request.getItemId() != null) {
            item = itemRepository.findById(request.getItemId()).orElse(null);
        }

        Message message = Message.builder()
                .content(request.getContent())
                .sender(sender)
                .receiver(receiver)
                .item(item)
                .chatType(request.getChatType() != null ? Message.ChatType.valueOf(request.getChatType()) : Message.ChatType.BUYER_SELLER)
                .build();

        Message saved = messageRepository.save(message);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO.Response> getConversation(Long otherId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return messageRepository.findConversation(user.getId(), otherId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long otherId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Message> unread = messageRepository.findConversation(user.getId(), otherId)
                .stream()
                .filter(m -> m.getReceiver().getId().equals(user.getId()) && !m.isRead())
                .collect(Collectors.toList());
        unread.forEach(m -> m.setRead(true));
        messageRepository.saveAll(unread);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO.ConversationResponse> getConversations(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<User> partners = messageRepository.findConversationPartners(user.getId());
        
        return partners.stream().map(partner -> {
            List<Message> conversation = messageRepository.findConversation(user.getId(), partner.getId());
            if (conversation.isEmpty()) return null;
            
            Message lastMsg = conversation.get(conversation.size() - 1);
            long unread = conversation.stream()
                    .filter(m -> m.getReceiver().getId().equals(user.getId()) && !m.isRead())
                    .count();
            
            return MessageDTO.ConversationResponse.builder()
                    .participantId(partner.getId())
                    .participantName(partner.getName())
                    .lastMessage(lastMsg.getContent())
                    .lastMessageTime(lastMsg.getCreatedAt())
                    .unreadCount(unread)
                    .chatType(lastMsg.getChatType().name())
                    .build();
        }).filter(java.util.Objects::nonNull).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return messageRepository.countByReceiver_IdAndReadFalse(user.getId());
    }

    private MessageDTO.Response toResponse(Message m) {
        return MessageDTO.Response.builder()
                .id(m.getId())
                .content(m.getContent())
                .senderId(m.getSender().getId())
                .senderName(m.getSender().getName())
                .senderProfileImage(m.getSender().getProfileImage())
                .receiverId(m.getReceiver().getId())
                .receiverName(m.getReceiver().getName())
                .itemId(m.getItem() != null ? m.getItem().getId() : null)
                .chatType(m.getChatType().name())
                .read(m.isRead())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
