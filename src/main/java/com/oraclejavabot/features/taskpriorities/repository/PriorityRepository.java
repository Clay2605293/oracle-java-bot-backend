package com.oraclejavabot.features.taskpriorities.repository;

import com.oraclejavabot.features.taskpriorities.model.PriorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriorityRepository extends JpaRepository<PriorityEntity, Integer> {

    boolean existsByNombre(String nombre);

}