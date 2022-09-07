package com.cti.service;

import com.cti.repository.RoleRepository;
import com.cti.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public void run(String... args) throws Exception {
        Utils.generateAssignmentsFolder();
    }
}
