package com.bupt.tarecruit.model;

public enum Role {
    APPLICANT,
    ORGANISER,
    ADMIN;

    public static Role fromString(final String value) {
        return Role.valueOf(value.trim().toUpperCase());
    }
}
