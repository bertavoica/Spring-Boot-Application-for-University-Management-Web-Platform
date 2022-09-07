package com.cti.payload.request;

import javax.validation.constraints.NotNull;

public class SpecializationMemberAddRequest {

    @NotNull
    private String name;

    @NotNull
    private String specializationName;

    private String superior;

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

    public String getSuperior() {
        return superior;
    }

    public void setSuperior(String superior) {
        this.superior = superior;
    }
}
