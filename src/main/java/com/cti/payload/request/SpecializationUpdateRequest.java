package com.cti.payload.request;

import javax.validation.constraints.NotNull;

public class SpecializationUpdateRequest {

    @NotNull
    private String name;

    @NotNull
    private String uniqueId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
