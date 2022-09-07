package com.cti.models;

import com.cti.payload.request.TeacherAddRequest;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Document(collection = "teachers")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;

    private String username;
    private String emailAddress;
    private String title;
    private String superior;
    private String specialization;

    public Teacher() {
    }

    public Teacher(TeacherAddRequest teacherAddRequest) {
        this.username = teacherAddRequest.getUsername();
        this.emailAddress = teacherAddRequest.getEmailAddress();
        this.title = teacherAddRequest.getTitle();
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSuperior() {
        return superior;
    }

    public void setSuperior(String superior) {
        this.superior = superior;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return Objects.equals(username, teacher.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
