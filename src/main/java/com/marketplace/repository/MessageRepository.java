package com.marketplace.repository;

import com.marketplace.entity.Message;
import com.marketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender.id = :userId AND m.receiver.id = :otherId) OR " +
           "(m.sender.id = :otherId AND m.receiver.id = :userId) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findConversation(@Param("userId") Long userId, @Param("otherId") Long otherId);

    @Query("SELECT m FROM Message m WHERE m.receiver.id = :userId AND m.read = false")
    List<Message> findUnreadByReceiverId(@Param("userId") Long userId);

    Long countByReceiver_IdAndReadFalse(Long receiverId);

    @Query("SELECT DISTINCT u FROM User u WHERE u.id IN " +
           "(SELECT m.receiver.id FROM Message m WHERE m.sender.id = :userId) OR " +
           "u.id IN (SELECT m.sender.id FROM Message m WHERE m.receiver.id = :userId)")
    List<User> findConversationPartners(@Param("userId") Long userId);
}
