package com.example.mindmate.repository;

import com.example.mindmate.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // find all messages for a user ordered by id (or timestamp)
    List<ChatMessage> findByEmailOrderByIdAsc(String email);
}
