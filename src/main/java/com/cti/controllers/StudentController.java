package com.cti.controllers;


import com.cti.models.*;
import com.cti.payload.request.StudentAddRequest;
import com.cti.payload.request.StudentUpdateRequest;
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
@RequestMapping("/student-controller")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/course")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getStudentCourses(@RequestParam(name = "username", defaultValue = "", required = false) String username,
                                               Principal principal) {

        List<Course> courseList;
        Optional<Student> optionalStudent;
        Student student;
        Optional<Course> optionalCourse;

        if (username.equals(""))
            return ResponseEntity.ok(studentRepository.findAll());

        optionalStudent = studentRepository.findByUsername(username);
        if (!optionalStudent.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("UsernameNotExist").get(userService.getPreferredLanguage(principal)));

        student = optionalStudent.get();
        courseList = new ArrayList<>();

        if (student.getCoursesIds() == null)
            return ResponseEntity.ok(courseList);

        for (String courseId : student.getCoursesIds()) {
            optionalCourse = courseRepository.findByUniqueId(courseId);
            optionalCourse.ifPresent(courseList::add);
        }

        return ResponseEntity.ok(courseList);
    }

    @PutMapping(value = "/course")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> enrollStudent(@RequestParam(name = "username", defaultValue = "", required = false) String username,
                                           @RequestParam(name = "courseId", defaultValue = "", required = false) String courseId,
                                           Principal principal) {

        Optional<Student> optionalStudent;
        Student student;
        Optional<Course> optionalCourse;


        if (username.equals(""))
            return ResponseEntity.ok(studentRepository.findAll());

        optionalStudent = studentRepository.findByUsername(username);
        if (!optionalStudent.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("UsernameNotExist").get(userService.getPreferredLanguage(principal)));

        student = optionalStudent.get();

        optionalCourse = courseRepository.findByUniqueId(courseId);
        if (!optionalCourse.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("CourseNotFound").get(userService.getPreferredLanguage(principal)));

        if (student.getCoursesIds() == null)
            student.setCoursesIds(new ArrayList<>());

        if (student.getCoursesIds().contains(courseId))
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("StudentAlreadyEnrolled").get(userService.getPreferredLanguage(principal)));

        student.getCoursesIds().add(courseId);
        studentRepository.save(student);

        return ResponseEntity.ok(Utils.languageDictionary.get("StudentEnrolled").get(userService.getPreferredLanguage(principal)));
    }

    @DeleteMapping(value = "/course")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> removeStudentFromCourse(@RequestParam(name = "username", defaultValue = "", required = false) String username,
                                                     @RequestParam(name = "courseId", defaultValue = "", required = false) String courseId,
                                                     Principal principal) {

        Optional<Student> optionalStudent;
        Student student;

        if (username.equals(""))
            return ResponseEntity.ok(studentRepository.findAll());

        optionalStudent = studentRepository.findByUsername(username);
        if (!optionalStudent.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("UsernameNotExist").get(userService.getPreferredLanguage(principal)));

        student = optionalStudent.get();


        if (student.getCoursesIds() == null)
            student.setCoursesIds(new ArrayList<>());

        if (!student.getCoursesIds().contains(courseId))
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("StudentNotEnrolled").get(userService.getPreferredLanguage(principal)));

        student.getCoursesIds().remove(courseId);
        studentRepository.save(student);

        return ResponseEntity.ok(Utils.languageDictionary.get("StudentRemovedFromCourse").get(userService.getPreferredLanguage(principal)));
    }

    @GetMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getStudentDetails(@RequestParam(name = "username", defaultValue = "", required = false) String username) {

        if (username.equals(""))
            return ResponseEntity.ok(studentRepository.findAll());
        else
            return ResponseEntity.ok(studentRepository.findByUsernameContainingIgnoreCase(username));
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> addStudent(@Valid @RequestBody StudentAddRequest studentAddRequest,
                                        Principal principal) {
        Student student;
        User user;

        if (userRepository.findByUsername(studentAddRequest.getUsername()).isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("UserExist").get(userService.getPreferredLanguage(principal)));

        user = new User();
        user.setUsername(studentAddRequest.getUsername());
        user.setEmail(studentAddRequest.getEmailAddress());
        user.getRoles().add(new Role(ERole.ROLE_STUDENT));
        user.setPassword(encoder.encode(studentAddRequest.getPassword()));
        userRepository.save(user);

        student = new Student(studentAddRequest);
        studentRepository.save(student);

        return ResponseEntity.ok(studentRepository.findAll());
    }

    @PutMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateStudent(@Valid @RequestBody StudentUpdateRequest studentUpdateRequest,
                                           Principal principal) {

        Optional<Student> optionalStudent;
        Student student;
        Optional<User> optionalUser;
        User user;
        Set<Role> outputRoles;
        Optional<Teacher> optionalTeacher;
        Teacher teacher;
        Role role;

        optionalStudent = studentRepository.findByUsername(studentUpdateRequest.getUsername());
        optionalUser = userRepository.findByUsername(studentUpdateRequest.getUsername());
        if (!optionalStudent.isPresent() || !optionalUser.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("StudentNotExists").get(userService.getPreferredLanguage(principal)));

        student = optionalStudent.get();
        if (studentUpdateRequest.getSpecialization() != null && !studentUpdateRequest.getSpecialization().equals(""))
            student.setSpecialization(studentUpdateRequest.getSpecialization());

        if (studentUpdateRequest.getGroup() != null && !studentUpdateRequest.getGroup().equals(""))
            student.setGroup(studentUpdateRequest.getGroup());

        if (studentUpdateRequest.getCycle() != null && !studentUpdateRequest.getCycle().equals(""))
            student.setEducationCycle(studentUpdateRequest.getCycle());

        user = optionalUser.get();
        outputRoles = new HashSet<>();
        if (studentUpdateRequest.getRole().equals("Teacher")) {
            optionalTeacher = teacherRepository.findByUsername(studentUpdateRequest.getUsername());
            if (optionalTeacher.isPresent())
                return ResponseEntity.badRequest().body(Utils.languageDictionary.get("TeacherExist").get(userService.getPreferredLanguage(principal)) + " " + studentUpdateRequest.getUsername());

            teacher = new Teacher();

            role = roleRepository.findByName(ERole.ROLE_TEACHER)
                    .orElseThrow(() -> new RuntimeException(Utils.languageDictionary.get("RoleNotFound").get(userService.getPreferredLanguage(principal))));
            outputRoles.add(role);
            user.setRoles(outputRoles);
            teacher.setUsername(student.getUsername());
            teacher.setEmailAddress(student.getEmailAddress());
            teacherRepository.save(teacher);
            studentRepository.delete(student);

            userRepository.save(user);

        } else if (studentUpdateRequest.getRole().equals("Admin")) {
            role = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException(Utils.languageDictionary.get("RoleNotFound").get(userService.getPreferredLanguage(principal))));
            outputRoles.add(role);
            user.setRoles(outputRoles);
            studentRepository.delete(student);
            userRepository.save(user);

        }

        if (studentUpdateRequest.getRole().equals("Student")) {
            studentRepository.save(student);
        }

        return ResponseEntity.ok(studentRepository.findAll());
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteStudent(@RequestParam(name = "username", defaultValue = "") String username) {

        userRepository.deleteByUsername(username);
        studentRepository.deleteByUsername(username);

        return ResponseEntity.ok(studentRepository.findAll());
    }
}
