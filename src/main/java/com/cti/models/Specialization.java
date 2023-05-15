package com.cti.models;

import com.cti.payload.request.SpecializationAddRequest;
import com.cti.utils.Utils;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "specializations")
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;

    private String uniqueId;
    private String name;
    private List<String> teachers;

    public Specialization() {}

    public Specialization(SpecializationAddRequest specializationAddRequest) {
        this.uniqueId = Utils.generateUniqueID();
        this.name = specializationAddRequest.getName();
        this.teachers = new ArrayList<>();
    }

    public List<String> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<String> teachers) {
        this.teachers = teachers;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
