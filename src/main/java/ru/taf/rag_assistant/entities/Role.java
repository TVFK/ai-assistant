package ru.taf.rag_assistant.entities;

public enum Role {
    USER, OPERATOR, ADMIN;

    public String asAuthority() {
        return "ROLE_" + this.name();
    }
}