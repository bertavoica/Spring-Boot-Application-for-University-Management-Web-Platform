package com.cti.models;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Document(collection = "titles")
public class Title {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;

    private String name;

    public Title() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
