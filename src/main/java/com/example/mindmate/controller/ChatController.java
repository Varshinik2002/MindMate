package com.example.mindmate.controller;

import com.example.mindmate.model.ChatMessage;
import com.example.mindmate.repository.ChatMessageRepository;
import com.example.mindmate.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;
    private final ChatMessageRepository chatRepo;

    public ChatController(ChatService chatService, ChatMessageRepository chatRepo) {
        this.chatService = chatService;
        this.chatRepo = chatRepo;
    }

    // ------------------------------
    // SAVE MESSAGE + RETURN BOT REPLY
    // ------------------------------
    @PostMapping(value = "/send", consumes = "application/json")
    public Map<String, String> sendMessage(@RequestBody Map<String, String> payload) {

        String email = payload.get("email");   // ⭐ NEW
        String message = payload.get("message");

        // 1️⃣ Save USER message
        ChatMessage userMsg = new ChatMessage(email, "user", message);
        chatRepo.save(userMsg);

        // 2️⃣ Bot reply
        String reply = chatService.getChatResponse(message);

        // 3️⃣ Save BOT message
        ChatMessage botMsg = new ChatMessage(email, "bot", reply);
        chatRepo.save(botMsg);

        // 4️⃣ Return reply
        return Map.of("reply", reply);
    }

    // ------------------------------
    // FETCH CHAT HISTORY FOR A USER
    // ------------------------------
    @GetMapping("/history")
    public List<ChatMessage> getHistory(@RequestParam String email) {
        return chatRepo.findByEmailOrderByIdAsc(email);
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
