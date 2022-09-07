package com.cti.models;

import com.cti.payload.request.CourseAddRequest;
import com.cti.service.Utils;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;

    private String uniqueId;
    private String completeName;
    private String abbreviation;
    private String description;
    private List<String> responsible;
    private int assignedUsers;

    public Course() {

    }

    public Course(CourseAddRequest courseAddRequest) {
        this.completeName = courseAddRequest.getCompleteName();
        this.abbreviation = courseAddRequest.getAbbreviation();
        this.description = courseAddRequest.getDescription();
        this.uniqueId = Utils.generateUniqueID();
        this.responsible = new ArrayList<>();
    }

    public int getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(int assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public List<String> getResponsible() {
        return responsible;
    }

    public void setResponsible(List<String> responsible) {
        this.responsible = responsible;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

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
