package com.cti.controllers;

import com.cti.models.*;
import com.cti.payload.request.TeacherAddRequest;
import com.cti.payload.request.TeacherUpdateRequest;
import com.cti.repository.*;
import com.cti.service.UserService;
import com.cti.service.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/teacher-controller")
public class TeacherController {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private TitleRepository titleRepository;

    @Autowired
    private UserService userService;

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllTeachers(@RequestParam(name = "username", defaultValue = "", required = false) String username,
                                            @RequestParam(name = "available", defaultValue = "", required = false) String available,
                                            @RequestParam(name = "specialization", defaultValue = "", required = false) String specialization) {
        List<Teacher> teacherList, result;

        if (!specialization.equals("")) {
            result = new ArrayList<>();
            teacherList = teacherRepository.findAll();
            for (Teacher teacher : teacherList) {
                if (teacher.getSpecialization() != null && teacher.getSpecialization().equals(specialization))
                    result.add(teacher);
            }
            return ResponseEntity.ok(result);
        } else if (available.equals("true")) {
            result = new ArrayList<>();
            teacherList = teacherRepository.findAll();
            for (Teacher teacher : teacherList) {
                if (teacher.getSpecialization() == null)
                    result.add(teacher);
            }
            return ResponseEntity.ok(result);
        } else if (username.equals(""))
            return ResponseEntity.ok(teacherRepository.findAll());
        else
            return ResponseEntity.ok(teacherRepository.findByUsernameContainingIgnoreCase(username));

    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> addTeacher(@Valid @RequestBody TeacherAddRequest teacherAddRequest,
                                        Principal principal) {
        Teacher teacher;
        User user;

        System.out.println("111");

        if (userRepository.findByUsername(teacherAddRequest.getUsername()).isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("UserExist").get(userService.getPreferredLanguage(principal)));

        // Check if teacher title exists
        if (!titleRepository.findByName(teacherAddRequest.getTitle()).isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("TitleNotExists").get(userService.getPreferredLanguage(principal)));

        user = new User();
        user.setUsername(teacherAddRequest.getUsername());
        user.setEmail(teacherAddRequest.getEmailAddress());
        user.getRoles().add(new Role(ERole.ROLE_TEACHER));
        user.setPassword(encoder.encode(teacherAddRequest.getPassword()));
        userRepository.save(user);

        teacher = new Teacher(teacherAddRequest);
        teacherRepository.save(teacher);

        System.out.println("1");

        return ResponseEntity.ok(teacherRepository.findAll());
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTeacher(@RequestParam(name = "username", defaultValue = "") String username) {

        userRepository.deleteByUsername(username);
        teacherRepository.deleteByUsername(username);

        return ResponseEntity.ok(teacherRepository.findAll());
    }

    @PutMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateTeacher(@Valid @RequestBody TeacherUpdateRequest teacherUpdateRequest,
                                           Principal principal) {

        Optional<Teacher> optionalTeacher;
        Teacher teacher;
        Optional<User> optionalUser;
        User user;
        Set<Role> outputRoles;
        Role role;
        Optional<Student> optionalStudent;
        Student student;

        optionalTeacher = teacherRepository.findByUsername(teacherUpdateRequest.getUsername());
        optionalUser = userRepository.findByUsername(teacherUpdateRequest.getUsername());
        if (!optionalTeacher.isPresent() || !optionalUser.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("TeacherNotExists").get(userService.getPreferredLanguage(principal)));

        // Check if teacher title exists
        if (!titleRepository.findByName(teacherUpdateRequest.getTitle()).isPresent() && teacherUpdateRequest.getRole().equals("Teacher"))
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("TitleNotExists").get(userService.getPreferredLanguage(principal)));

        teacher = optionalTeacher.get();

        user = optionalUser.get();
        outputRoles = new HashSet<>();
        if (teacherUpdateRequest.getRole().equals("Student")) {
            optionalStudent = studentRepository.findByUsername(teacherUpdateRequest.getUsername());
            if (optionalStudent.isPresent())
                return ResponseEntity.badRequest().body(Utils.languageDictionary.get("StudentExist").get(userService.getPreferredLanguage(principal)));

            student = new Student();

            role = roleRepository.findByName(ERole.ROLE_STUDENT)
                    .orElseThrow(() -> new RuntimeException(Utils.languageDictionary.get("RoleNotFound").get(userService.getPreferredLanguage(principal))));
            outputRoles.add(role);
            user.setRoles(outputRoles);
            student.setUsername(teacher.getUsername());
            student.setEmailAddress(teacher.getEmailAddress());
            studentRepository.save(student);
            teacherRepository.delete(teacher);

            userRepository.save(user);

        } else if (teacherUpdateRequest.getRole().equals("Admin")) {
            role = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException(Utils.languageDictionary.get("RoleNotFound").get(userService.getPreferredLanguage(principal))));
            outputRoles.add(role);
            user.setRoles(outputRoles);
            teacherRepository.delete(teacher);
            userRepository.save(user);
        }

        if (teacherUpdateRequest.getRole().equals("Teacher") && teacherUpdateRequest.getTitle() != null) {
            teacher.setTitle(teacherUpdateRequest.getTitle());
            teacherRepository.save(teacher);
        }

        return ResponseEntity.ok(teacherRepository.findAll());
    }
}
