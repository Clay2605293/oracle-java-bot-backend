package com.oraclejavabot.features.users.repository;

import com.oraclejavabot.features.users.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    boolean existsByEmail(String email);

    boolean existsByTelegramId(String telegramId);

}