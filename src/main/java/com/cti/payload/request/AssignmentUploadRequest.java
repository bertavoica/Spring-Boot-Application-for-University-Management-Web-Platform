package com.cti.payload.request;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

public class AssignmentUploadRequest {

    @NotNull
    private String purpose;

    @NotNull
    private MultipartFile assignment;

    @NotNull
    private String fileName;

    @NotNull
    private String projectId;

    @NotNull
    private String username;

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public MultipartFile getAssignment() {
        return assignment;
    }

    public void setAssignment(MultipartFile assignment) {
        this.assignment = assignment;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
