package com.oraclejavabot.features.users.repository;

import com.oraclejavabot.features.users.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    boolean existsByEmail(String email);

    boolean existsByTelegramId(String telegramId);

    boolean existsByEmailAndUserIdNot(String email, UUID userId);

    boolean existsByTelegramIdAndUserIdNot(String telegramId, UUID userId);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByTelegramId(String telegramId);

    Optional<UserEntity> findByTelegramIdIgnoreCase(String telegramId);

    List<UserEntity> findByEstadoIdOrderByPrimerNombreAscApellidoAsc(Integer estadoId);
}