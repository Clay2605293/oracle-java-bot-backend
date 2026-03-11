package com.oraclejavabot.features.taskstatus.repository;

import com.oraclejavabot.features.taskstatus.model.TaskStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStatusRepository extends JpaRepository<TaskStatusEntity, Integer> {

    /**
     * Comprueba si existe un estado con el nombre proporcionado.
     *
     * @param nombre nombre a comprobar
     * @return true si existe un registro con ese nombre, false en caso contrario
     */
    boolean existsByNombre(String nombre);

}