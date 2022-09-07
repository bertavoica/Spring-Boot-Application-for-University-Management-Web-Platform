package com.cti.models;

public class Admin {

    private String username;
    private String emailAddress;

    public Admin(User user) {
        this.username = user.getUsername();
        this.emailAddress = user.getEmail();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
