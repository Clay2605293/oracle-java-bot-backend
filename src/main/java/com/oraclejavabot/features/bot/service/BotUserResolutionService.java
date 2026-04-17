package com.oraclejavabot.features.bot.service;

import com.oraclejavabot.features.users.model.UserEntity;
import com.oraclejavabot.features.users.service.UserService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class BotUserResolutionService {

    private final UserService userService;

    public BotUserResolutionService(UserService userService) {
        this.userService = userService;
    }

    public Optional<UserEntity> resolveBotUser(Long telegramUserId, String telegramUsername) {
        Set<String> candidates = new LinkedHashSet<>();

        if (telegramUserId != null) {
            candidates.add(String.valueOf(telegramUserId));
        }

        if (telegramUsername != null && !telegramUsername.isBlank()) {
            String cleanUsername = telegramUsername.trim();

            candidates.add(cleanUsername);

            if (cleanUsername.startsWith("@")) {
                String withoutAt = cleanUsername.substring(1).trim();
                if (!withoutAt.isEmpty()) {
                    candidates.add(withoutAt);
                }
            } else {
                candidates.add("@" + cleanUsername);
            }
        }

        for (String candidate : candidates) {
            Optional<UserEntity> user = userService.findByTelegramIdFlexible(candidate);
            if (user.isPresent()) {
                return user;
            }
        }

        return Optional.empty();
    }
}
