package com.cti.service;

import com.cti.models.ERole;
import com.cti.models.Role;
import com.cti.models.User;
import com.cti.repository.RoleRepository;
import com.cti.repository.StudentRepository;
import com.cti.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        Utils.generateAssignmentsFolder();
        fillRoles();
        fillUsers();
    }

    private void fillRoles() {
        if (!roleRepository.findByName(ERole.ROLE_STUDENT).isPresent()) {
            roleRepository.save(new Role(ERole.ROLE_STUDENT));
        }

        if (!roleRepository.findByName(ERole.ROLE_ADMIN).isPresent()) {
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
        }

        if (!roleRepository.findByName(ERole.ROLE_TEACHER).isPresent()) {
            roleRepository.save(new Role(ERole.ROLE_TEACHER));
        }
    }

    private void fillUsers() {
        User user;
        Set<Role> outputRoles;
        if (!userRepository.findByUsername("admin").isPresent()) {
            user = new User();
            user.setUsername("admin");
            user.setEmail("admin@gmail.com");
            user.setPassword(encoder.encode("test1234"));

            outputRoles = new HashSet<>();
            outputRoles.add(new Role(ERole.ROLE_ADMIN));
            user.setRoles(outputRoles);

            userRepository.save(user);
        }

        if (!userRepository.findByUsername("student").isPresent()) {
            user = new User();
            user.setUsername("student");
            user.setEmail("student@gmail.com");
            user.setPassword(encoder.encode("test1234"));

            outputRoles = new HashSet<>();
            outputRoles.add(new Role(ERole.ROLE_STUDENT));
            user.setRoles(outputRoles);

            userRepository.save(user);
        }

        if (!userRepository.findByUsername("teacher").isPresent()) {
            user = new User();
            user.setUsername("teacher");
            user.setEmail("teacher@gmail.com");
            user.setPassword(encoder.encode("test1234"));

            outputRoles = new HashSet<>();
            outputRoles.add(new Role(ERole.ROLE_TEACHER));
            user.setRoles(outputRoles);

            userRepository.save(user);
        }

        if (!userRepository.findByUsername("teacher_2").isPresent()) {
            user = new User();
            user.setUsername("teacher_2");
            user.setEmail("teacher_2@gmail.com");
            user.setPassword(encoder.encode("test1234"));

            outputRoles = new HashSet<>();
            outputRoles.add(new Role(ERole.ROLE_TEACHER));
            user.setRoles(outputRoles);

            userRepository.save(user);
        }
    }
}
