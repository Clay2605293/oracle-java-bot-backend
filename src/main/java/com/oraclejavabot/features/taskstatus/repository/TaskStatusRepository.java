package com.oraclejavabot.features.taskstatus.repository;

import com.oraclejavabot.features.taskstatus.model.TaskStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStatusRepository extends JpaRepository<TaskStatusEntity, Integer> {

    boolean existsByNombre(String nombre);

}