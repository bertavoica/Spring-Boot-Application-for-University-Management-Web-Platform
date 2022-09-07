package com.cti.payload.request;

import javax.validation.constraints.NotNull;


public class SpecializationMemberModifyRequest {

    @NotNull
    private String name;

    @NotNull
    private String specializationName;

    private String superiorName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecializationName() {
        return specializationName;
    }

    public void setSpecializationName(String specializationName) {
        this.specializationName = specializationName;
    }

    public String getSuperiorName() {
        return superiorName;
    }

    public void setSuperiorName(String superiorName) {
        this.superiorName = superiorName;
    }
}

