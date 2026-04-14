package com.oraclejavabot.features.tasks.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "USUARIO_A_TAREA")
public class TaskUserEntity {

    @EmbeddedId
    private TaskUserId id;

    public TaskUserEntity() {}

    public TaskUserEntity(TaskUserId id) {
        this.id = id;
    }

    public TaskUserId getId() {
        return id;
    }

    public void setId(TaskUserId id) {
        this.id = id;
    }
}