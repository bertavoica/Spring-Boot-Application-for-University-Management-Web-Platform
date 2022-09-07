package com.cti.payload.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CourseAddRequest {

    @NotNull
    private String completeName;

    @NotNull
    @Size(min = 2, max = 10)
    private String abbreviation;

    @NotNull
    private String description;

    public String getCompleteName() {
        return completeName;
    }

    public void setCompleteName(String completeName) {
        this.completeName = completeName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
