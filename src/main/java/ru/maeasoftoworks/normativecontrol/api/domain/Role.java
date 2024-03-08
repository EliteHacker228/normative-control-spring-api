package ru.maeasoftoworks.normativecontrol.api.domain;

import lombok.Getter;

public enum Role {
    STUDENT("STUDENT"), INSPECTOR("INSPECTOR"), ADMIN("ADMIN");

    @Getter
    private String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }
}
