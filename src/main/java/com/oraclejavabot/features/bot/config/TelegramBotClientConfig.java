package com.oraclejavabot.features.bot.config;

import com.oraclejavabot.config.TelegramBotProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
@ConditionalOnProperty(prefix = "telegram.bot", name = "enabled", havingValue = "true")
public class TelegramBotClientConfig {

    @Bean
    public TelegramClient telegramClient(TelegramBotProperties botProperties) {
        if (botProperties.getToken() == null || botProperties.getToken().isBlank()) {
            throw new IllegalStateException("TELEGRAM_BOT_TOKEN is required when telegram.bot.enabled=true");
        }
        return new OkHttpTelegramClient(botProperties.getToken());
    }
}
