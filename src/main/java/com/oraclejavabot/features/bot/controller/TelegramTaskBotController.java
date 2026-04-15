package com.oraclejavabot.features.bot.controller;

import com.oraclejavabot.config.TelegramBotProperties;
import com.oraclejavabot.features.bot.service.TelegramBotCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@ConditionalOnProperty(prefix = "telegram.bot", name = "enabled", havingValue = "true")
public class TelegramTaskBotController implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TelegramTaskBotController.class);

    private final TelegramClient telegramClient;
    private final TelegramBotProperties botProperties;
    private final TelegramBotCommandService commandService;

    public TelegramTaskBotController(TelegramClient telegramClient,
                                     TelegramBotProperties botProperties,
                                     TelegramBotCommandService commandService) {
        this.telegramClient = telegramClient;
        this.botProperties = botProperties;
        this.commandService = commandService;
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update == null || !update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        Long telegramUserId = null;
        String telegramUsername = null;

        if (update.getMessage().getFrom() != null) {
            telegramUserId = update.getMessage().getFrom().getId();
            telegramUsername = update.getMessage().getFrom().getUserName();
        }

        String messageText = update.getMessage().getText();
        commandService.handleTextMessage(chatId, telegramUserId, telegramUsername, messageText, telegramClient);
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        logger.info("Telegram bot registered. Running state: {}", botSession.isRunning());
    }
}
