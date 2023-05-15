package com.cti.service;

import com.cti.models.*;
import com.cti.payload.request.AdminUpdateRequest;
import com.cti.repository.RoleRepository;
import com.cti.repository.StudentRepository;
import com.cti.repository.TeacherRepository;
import com.cti.repository.UserRepository;
import com.cti.utils.Utils;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final RoleRepository roleRepository;

    private final UserService userService;

    public AdminService(UserRepository userRepository, StudentRepository studentRepository, TeacherRepository teacherRepository, RoleRepository roleRepository, UserService userService) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    public List<Admin> prepareAdminList(String username) {
        List<Admin> result;
        List<User> userList;

        if (!username.equals("")) {
            userList = userRepository.findByUsernameContainingIgnoreCase(username);
        } else {
            userList = userRepository.findAll();
        }

        result = new ArrayList<>();

        for (User user : userList) {
            for (Role role : user.getRoles()) {
                if (role.getName().equals(ERole.ROLE_ADMIN)) {
                    result.add(new Admin(user));
                    break;
                }
            }
        }

        return result;
    }
    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
    }

    public List<Admin> updateAdmin(AdminUpdateRequest adminUpdateRequest, Principal principal) throws Exception {
        Optional<Teacher> optionalTeacher;
        Teacher teacher;
        Optional<User> optionalUser;
        User user;
        Set<Role> outputRoles;
        Role role;
        Optional<Student> optionalStudent;
        Student student;

        optionalUser = userRepository.findByUsername(adminUpdateRequest.getUsername());
        System.out.println(optionalUser);
        if (!optionalUser.isPresent())
            throw new Exception(Utils.languageDictionary.get("UserNotFound").get(userService.getPreferredLanguage(principal)) + " " + adminUpdateRequest.getUsername());

        user = optionalUser.get();
        outputRoles = new HashSet<>();
        if (adminUpdateRequest.getRole().equals("Student")) {
            optionalStudent = studentRepository.findByUsername(adminUpdateRequest.getUsername());
            if (!optionalStudent.isPresent())
                throw new Exception(Utils.languageDictionary.get("StudentExist").get(userService.getPreferredLanguage(principal)) + " " + adminUpdateRequest.getUsername());

            student = new Student();

            role = roleRepository.findByName(ERole.ROLE_STUDENT)
                    .orElseThrow(() -> new RuntimeException(Utils.languageDictionary.get("RoleNotFound").get(userService.getPreferredLanguage(principal))));
            outputRoles.add(role);
            user.setRoles(outputRoles);
            student.setUsername(user.getUsername());
            student.setEmailAddress(user.getEmail());
            studentRepository.save(student);
            userRepository.save(user);

        } else if (adminUpdateRequest.getRole().equals("Teacher")) {
            optionalTeacher = teacherRepository.findByUsername(adminUpdateRequest.getUsername());
            if (!optionalTeacher.isPresent()) {
                throw new Exception(Utils.languageDictionary.get("TeacherExist").get(userService.getPreferredLanguage(principal)) + " " + adminUpdateRequest.getUsername());
            }

            teacher = new Teacher();
            role = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException(Utils.languageDictionary.get("RoleNotFound").get(userService.getPreferredLanguage(principal))));
            outputRoles.add(role);
            user.setRoles(outputRoles);
            teacher.setUsername(user.getUsername());
            teacher.setEmailAddress(user.getEmail());
            teacherRepository.save(teacher);
            userRepository.save(user);
        }

        return this.prepareAdminList("");
    }
}
