package com.oraclejavabot.features.projects.model;

import jakarta.persistence.*;

@Entity
@Table(name = "USUARIO_A_PROYECTO")
public class ProjectMemberEntity {

    @EmbeddedId
    private ProjectMemberId id;

    public ProjectMemberEntity() {}

    public ProjectMemberEntity(ProjectMemberId id) {
        this.id = id;
    }

    public ProjectMemberId getId() {
        return id;
    }

    public void setId(ProjectMemberId id) {
        this.id = id;
    }
}