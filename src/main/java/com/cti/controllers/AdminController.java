package com.cti.controllers;

import com.cti.models.*;
import com.cti.payload.request.AdminUpdateRequest;
import com.cti.repository.RoleRepository;
import com.cti.repository.StudentRepository;
import com.cti.repository.TeacherRepository;
import com.cti.repository.UserRepository;
import com.cti.service.UserService;
import com.cti.service.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin-controller")
public class AdminController {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserService userService;

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllAdmins(@RequestParam(name = "username", defaultValue = "") String username) {
        return ResponseEntity.ok(prepareAdminList(username));
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAdmin(@RequestParam(name = "username", defaultValue = "") String username) {

        userRepository.deleteByUsername(username);

        return ResponseEntity.ok(prepareAdminList(""));
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

    @PutMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateAdmin(@Valid @RequestBody AdminUpdateRequest adminUpdateRequest,
                                         Principal principal) {

        Optional<Teacher> optionalTeacher;
        Teacher teacher;
        Optional<User> optionalUser;
        User user;
        Set<Role> outputRoles;
        Role role;
        Optional<Student> optionalStudent;
        Student student;

        optionalUser = userRepository.findByUsername(adminUpdateRequest.getUsername());
        if (!optionalUser.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("UserNotFound").get(userService.getPreferredLanguage(principal)) + " " + adminUpdateRequest.getUsername());

        user = optionalUser.get();
        outputRoles = new HashSet<>();
        if (adminUpdateRequest.getRole().equals("Student")) {
            optionalStudent = studentRepository.findByUsername(adminUpdateRequest.getUsername());
            if (optionalStudent.isPresent())
                return ResponseEntity.badRequest().body(Utils.languageDictionary.get("StudentExist").get(userService.getPreferredLanguage(principal)) + " " + adminUpdateRequest.getUsername());

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
            if (optionalTeacher.isPresent())
                return ResponseEntity.badRequest().body(Utils.languageDictionary.get("TeacherExist").get(userService.getPreferredLanguage(principal)) + " " + adminUpdateRequest.getUsername());

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

        return ResponseEntity.ok(prepareAdminList(""));
    }
}
