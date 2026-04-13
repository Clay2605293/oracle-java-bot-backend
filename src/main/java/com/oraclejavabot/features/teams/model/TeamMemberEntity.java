package com.oraclejavabot.features.teams.model;

import jakarta.persistence.*;

@Entity
@Table(name = "USUARIO_A_EQUIPO")
public class TeamMemberEntity {

    @EmbeddedId
    private TeamMemberId id;

    public TeamMemberEntity() {}

    public TeamMemberEntity(TeamMemberId id) {
        this.id = id;
    }

    public TeamMemberId getId() {
        return id;
    }

    public void setId(TeamMemberId id) {
        this.id = id;
    }
}