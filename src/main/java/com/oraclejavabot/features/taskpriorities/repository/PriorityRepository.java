package com.oraclejavabot.features.taskpriorities.repository;

import com.oraclejavabot.features.taskpriorities.model.PriorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriorityRepository extends JpaRepository<PriorityEntity, Integer> {

    /**
     * Comprueba si existe una prioridad con el nombre proporcionado.
     *
     * @param nombre nombre a comprobar
     * @return true si existe un registro con ese nombre, false en caso contrario
     */
    boolean existsByNombre(String nombre);

}