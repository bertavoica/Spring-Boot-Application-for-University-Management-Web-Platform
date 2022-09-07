package com.cti.payload.request;

import javax.validation.constraints.NotNull;

public class ProjectAddRequest {

    @NotNull
    private String projectName;

    @NotNull
    private String description;

    @NotNull
    private String courseUniqueId;

    @NotNull
    private String inputDate;

    @NotNull
    private String inputTime;

    @NotNull
    private String owner;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCourseUniqueId() {
        return courseUniqueId;
    }

    public void setCourseUniqueId(String courseUniqueId) {
        this.courseUniqueId = courseUniqueId;
    }

    public String getInputDate() {
        return inputDate;
    }

    public void setInputDate(String inputDate) {
        this.inputDate = inputDate;
    }

    public String getInputTime() {
        return inputTime;
    }

    public void setInputTime(String inputTime) {
        this.inputTime = inputTime;
    }
}

