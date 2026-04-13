package com.oraclejavabot.features.auth.repository;

import com.oraclejavabot.features.auth.model.UserCredentialEntity;
import com.oraclejavabot.features.users.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserCredentialRepository extends JpaRepository<UserCredentialEntity, UUID> {

    Optional<UserCredentialEntity> findByUser(UserEntity user);

}