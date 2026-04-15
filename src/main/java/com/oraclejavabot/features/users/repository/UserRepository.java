package com.oraclejavabot.features.users.repository;

import com.oraclejavabot.features.users.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Comprueba si existe un usuario con el email proporcionado.
     *
     * @param email email a comprobar
     * @return true si existe un registro con ese email, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Comprueba si existe un usuario con el telegramId proporcionado.
     *
     * @param telegramId telegramId a comprobar
     * @return true si existe un registro con ese telegramId, false en caso
     *         contrario
     */
    boolean existsByTelegramId(String telegramId);

    /**
     * Busca un usuario por su email.
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Busca un usuario por su identificador de Telegram.
     */
    Optional<UserEntity> findByTelegramId(String telegramId);

    /**
     * Busca un usuario por su identificador de Telegram ignorando mayúsculas.
     */
    Optional<UserEntity> findByTelegramIdIgnoreCase(String telegramId);
}