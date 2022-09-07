package com.cti.models;

import com.cti.payload.request.StudentAddRequest;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;

    private String username;
    private String emailAddress;

    private String educationCycle;
    private String specialization;
    private List<Project> projects;
    private List<String> coursesIds;
    private String group;
    private String superior;

    public Student() {
        this.projects = new ArrayList<>();
        this.coursesIds =  new ArrayList<>();
    }

    public Student(StudentAddRequest studentAddRequest) {
        this.username = studentAddRequest.getUsername();
        this.emailAddress = studentAddRequest.getEmailAddress();
        this.educationCycle = studentAddRequest.getCycle();
        this.specialization = studentAddRequest.getSpecialization();
        this.group = studentAddRequest.getGroup();
        this.projects = new ArrayList<>();
        this.coursesIds = new ArrayList<>();
    }

    public Student(String username, String email) {
        this.username = username;
        this.emailAddress = email;
    }

    public String getSuperior() {
        return superior;
    }

    public void setSuperior(String superior) {
        this.superior = superior;
    }

    public List<String> getCoursesIds() {
        return coursesIds;
    }

    public void setCoursesIds(List<String> coursesIds) {
        this.coursesIds = coursesIds;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public String getEducationCycle() {
        return educationCycle;
    }

    public void setEducationCycle(String educationCycle) {
        this.educationCycle = educationCycle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
