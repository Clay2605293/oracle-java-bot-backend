package com.oraclejavabot.messaging.consumer;

import com.oraclejavabot.messaging.event.UserAssignedEvent;
import com.oraclejavabot.features.users.model.UserEntity;
import com.oraclejavabot.features.users.repository.UserRepository;
import com.oraclejavabot.features.bot.util.BotHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Optional;
import java.util.UUID;

@Service
public class TaskEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TaskEventConsumer.class);

    private final UserRepository userRepository;
    private final TelegramClient telegramClient;

    public TaskEventConsumer(UserRepository userRepository,
                             TelegramClient telegramClient) {
        this.userRepository = userRepository;
        this.telegramClient = telegramClient;
    }

    @KafkaListener(topics = "task-events", groupId = "oracle-java-bot-group")
    public void handleUserAssigned(UserAssignedEvent event) {

        logger.info("🔥 Kafka event received → taskId={}, userId={}",
                event.getTaskId(),
                event.getUserId()
        );

        UUID userUUID = hexToUuid(event.getUserId());

        Optional<UserEntity> userOpt = userRepository.findById(userUUID);

        if (userOpt.isEmpty()) {
            logger.warn("❌ User not found → userId={}", event.getUserId());
            return;
        }

        UserEntity user = userOpt.get();

        if (user.getTelegramChatId() == null || user.getTelegramChatId().isBlank()) {
            logger.warn("❌ User has no TELEGRAM_CHAT_ID → userId={}", event.getUserId());
            return;
        }

        Long chatId;

        try {
            chatId = Long.parseLong(user.getTelegramChatId());
        } catch (NumberFormatException e) {
            logger.error("❌ Invalid TELEGRAM_CHAT_ID format → userId={}", event.getUserId(), e);
            return;
        }

        // =============================
        // 🔥 MENSAJE ENRIQUECIDO
        // =============================
        String message = buildMessage(event);

        BotHelper.sendMessage(chatId, message, telegramClient);

        logger.info("✅ Telegram notification sent → taskId={}, userId={}",
                event.getTaskId(),
                event.getUserId()
        );
    }

    // =============================
    // MESSAGE BUILDER
    // =============================
    private String buildMessage(UserAssignedEvent event) {

        return String.format(
                "📌 *New Task Assigned!*\n\n" +
                "📝 *Task:* %s\n" +
                "📁 *Project:* %s\n" +
                "⚡ *Priority:* %s\n" +
                "📅 *Due Date:* %s",
                safe(event.getTaskTitle()),
                safe(event.getProjectName()),
                safe(event.getPriority()),
                safe(event.getDueDate())
        );
    }

    private String safe(String value) {
        return (value == null || value.isBlank()) ? "N/A" : value;
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