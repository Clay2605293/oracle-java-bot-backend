package com.oraclejavabot.features.tasks.repository;

import com.oraclejavabot.features.tasks.model.TaskUserEntity;
import com.oraclejavabot.features.tasks.model.TaskUserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskUserRepository extends JpaRepository<TaskUserEntity, TaskUserId> {

    List<TaskUserEntity> findByIdTaskId(UUID taskId);

    boolean existsById(TaskUserId id);
}