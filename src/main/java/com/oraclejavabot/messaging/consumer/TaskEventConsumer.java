package com.oraclejavabot.messaging.consumer;

import com.oraclejavabot.messaging.event.UserAssignedEvent;
import com.oraclejavabot.features.users.model.UserEntity;
import com.oraclejavabot.features.users.repository.UserRepository;
import com.oraclejavabot.features.bot.util.BotHelper;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Optional;
import java.util.UUID;

@Service
public class TaskEventConsumer {

    private final UserRepository userRepository;
    private final TelegramClient telegramClient;

    public TaskEventConsumer(UserRepository userRepository,
                             TelegramClient telegramClient) {
        this.userRepository = userRepository;
        this.telegramClient = telegramClient;
    }

    @KafkaListener(topics = "task-events", groupId = "oracle-java-bot-group")
    public void handleUserAssigned(UserAssignedEvent event) {

        System.out.println("🔥 EVENT RECEIVED FROM KAFKA");

        UUID userUUID = hexToUuid(event.getUserId());

        Optional<UserEntity> userOpt = userRepository.findById(userUUID);

        if (userOpt.isEmpty()) {
            System.out.println("❌ User not found");
            return;
        }

        UserEntity user = userOpt.get();

        // 🔥 CLAVE: usamos CHAT_ID real
        if (user.getTelegramChatId() == null || user.getTelegramChatId().isBlank()) {
            System.out.println("❌ User has no TELEGRAM_CHAT_ID");
            return;
        }

        Long chatId;

        try {
            chatId = Long.parseLong(user.getTelegramChatId());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid TELEGRAM_CHAT_ID format");
            return;
        }

        BotHelper.sendMessage(
                chatId,
                "📌 You have been assigned to a new task!",
                telegramClient
        );

        System.out.println("✅ TELEGRAM NOTIFICATION SENT");
    }

    private UUID hexToUuid(String hex) {
        return UUID.fromString(
                hex.replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                        "$1-$2-$3-$4-$5"
                )
        );
    }
}