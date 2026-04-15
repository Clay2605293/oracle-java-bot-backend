package com.oraclejavabot.features.bot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class BotHelper {

    private static final Logger logger = LoggerFactory.getLogger(BotHelper.class);

    private BotHelper() {
    }

    public static void sendMessage(Long chatId, String text, TelegramClient client) {
        try {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .replyMarkup(new ReplyKeyboardRemove(true))
                    .build();
            client.execute(message);
        } catch (Exception e) {
            logger.error("Error sending Telegram message", e);
        }
    }

    public static void sendMessage(Long chatId, String text, TelegramClient client, ReplyKeyboardMarkup keyboard) {
        try {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .replyMarkup(keyboard)
                    .build();
            client.execute(message);
        } catch (Exception e) {
            logger.error("Error sending Telegram message with keyboard", e);
        }
    }
}
