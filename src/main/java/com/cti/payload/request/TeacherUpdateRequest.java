package com.cti.payload.request;

import javax.validation.constraints.NotNull;

public class TeacherUpdateRequest {

    @NotNull
    private String username;

    private String title;

    @NotNull
    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}