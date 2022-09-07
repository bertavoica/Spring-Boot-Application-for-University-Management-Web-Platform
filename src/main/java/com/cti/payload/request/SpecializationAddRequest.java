package com.cti.payload.request;

import javax.validation.constraints.NotNull;

public class SpecializationAddRequest {

    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
