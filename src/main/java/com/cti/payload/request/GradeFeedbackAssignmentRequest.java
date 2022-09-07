package com.cti.payload.request;

import javax.validation.constraints.NotNull;

public class GradeFeedbackAssignmentRequest {

    @NotNull
    private Double studentGrade;

    @NotNull
    private String studentFeedback;

    @NotNull
    private String studentName;

    @NotNull
    private String projectId;

    public Double getStudentGrade() {
        return studentGrade;
    }

    public void setStudentGrade(Double studentGrade) {
        this.studentGrade = studentGrade;
    }

    public String getStudentFeedback() {
        return studentFeedback;
    }

    public void setStudentFeedback(String studentFeedback) {
        this.studentFeedback = studentFeedback;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
