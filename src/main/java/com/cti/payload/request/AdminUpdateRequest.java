package com.cti.payload.request;

import javax.validation.constraints.NotNull;

public class AdminUpdateRequest {

    @NotNull
    private String username;

    @NotNull
    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
