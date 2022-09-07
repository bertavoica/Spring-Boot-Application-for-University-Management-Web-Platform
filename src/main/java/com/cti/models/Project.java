package com.cti.models;

import com.cti.payload.request.ProjectAddRequest;
import com.cti.service.Utils;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;

    private String uniqueId;
    private String projectName;
    private String description;
    private String owner;

    private String assignee;
    private String assigneeAddress;
    private LocalDateTime assigned;
    private LocalDateTime deadline;
    private LocalDateTime uploadDate;
    private String outputLocation;
    private boolean notifyUpdates;

    private double grade;
    private String feedback;
    private EProjectStatus status;
    private Course course;

    public Project() {
    }

    public Project(ProjectAddRequest projectAddRequest) {
        this.projectName = projectAddRequest.getProjectName();
        this.description = projectAddRequest.getDescription();
        this.uniqueId = Utils.generateUniqueID();
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public EProjectStatus getStatus() {
        return status;
    }

    public void setStatus(EProjectStatus status) {
        this.status = status;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public LocalDateTime getAssigned() {
        return assigned;
    }

    public void setAssigned(LocalDateTime assigned) {
        this.assigned = assigned;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getOutputLocation() {
        return outputLocation;
    }

    public void setOutputLocation(String outputLocation) {
        this.outputLocation = outputLocation;
    }

    public boolean isNotifyUpdates() {
        return notifyUpdates;
    }

    public void setNotifyUpdates(boolean notifyUpdates) {
        this.notifyUpdates = notifyUpdates;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getAssigneeAddress() { return assigneeAddress; }

    public void setAssigneeAddress(String assigneeAddress) { this.assigneeAddress = assigneeAddress; }
}
