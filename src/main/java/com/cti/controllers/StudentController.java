package com.cti.controllers;


import com.cti.exception.*;
import com.cti.models.*;
import com.cti.payload.request.StudentAddRequest;
import com.cti.payload.request.StudentUpdateRequest;
import com.cti.repository.*;
import com.cti.service.StudentService;
import com.cti.service.UserService;
import com.cti.utils.ApplicationConstants;
import com.cti.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
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
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @GetMapping(value = "/course")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getStudentCourses(@RequestParam(name = "username", defaultValue = "", required = false) String username,
                                               Principal principal) {
        try {
            List<Object> courses = this.studentService.getStudentCourses(username);
            return ResponseEntity.ok(courses);
        } catch (UsernameNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.USERNAME_NOT_EXISTS).get(userService.getPreferredLanguage(principal)));
        }
    }

    @PutMapping(value = "/course")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> enrollStudent(@RequestParam(name = "username", defaultValue = "", required = false) String username,
                                           @RequestParam(name = "courseId", defaultValue = "", required = false) String courseId,
                                           Principal principal) {
        try {
            this.studentService.enrollStudent(username, courseId);
            return ResponseEntity.ok(Utils.languageDictionary.get(ApplicationConstants.STUDENT_ENROLLED).get(userService.getPreferredLanguage(principal)));
        } catch (StudentNotExistsException e) {
            return ResponseEntity.ok(studentRepository.findAll());
        } catch (UsernameNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.USERNAME_NOT_EXISTS).get(userService.getPreferredLanguage(principal)));
        } catch (CourseNotFoundException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.COURSE_NOT_FOUND).get(userService.getPreferredLanguage(principal)));
        } catch (StudentAlreadyEnrolledException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.STUDENT_ALREADY_ENROLLED).get(userService.getPreferredLanguage(principal)));
        }
    }

    @DeleteMapping(value = "/course")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> removeStudentFromCourse(@RequestParam(name = "username", defaultValue = "", required = false) String username,
                                                     @RequestParam(name = "courseId", defaultValue = "", required = false) String courseId,
                                                     Principal principal) {
        try {
            this.studentService.removeStudentFromCourse(username, courseId);
            return ResponseEntity.ok(Utils.languageDictionary.get(ApplicationConstants.STUDENT_REMOVED_FROM_COURSE).get(userService.getPreferredLanguage(principal)));
        } catch (UsernameNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.USERNAME_NOT_EXISTS).get(userService.getPreferredLanguage(principal)));
        } catch (StudentsAlreadyRegisteredException e) {
            return ResponseEntity.ok(studentRepository.findAll());
        } catch (StudentNotEnrolledException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.STUDENT_NOT_ENROLLED).get(userService.getPreferredLanguage(principal)));
        }
    }

    @GetMapping()
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getStudentDetails(@RequestParam(name = "username", defaultValue = "", required = false) String username) {
        return ResponseEntity.ok(this.studentService.getStudentDetails(username));
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> addStudent(@Valid @RequestBody StudentAddRequest studentAddRequest,
                                        Principal principal) {
        try {
            return ResponseEntity.ok(this.studentService.addStudent(studentAddRequest));
        } catch (UserExistException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.USER_EXISTS).get(userService.getPreferredLanguage(principal)));
        }
    }

    @PutMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateStudent(@Valid @RequestBody StudentUpdateRequest studentUpdateRequest,
                                           Principal principal) {
        try {
            return ResponseEntity.ok(this.studentService.updateStudent(studentUpdateRequest));
        } catch (TeacherExistException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.TEACHER_EXIST).get(userService.getPreferredLanguage(principal)) + " " + studentUpdateRequest.getUsername());
        } catch (RoleNotFoundException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.ROLE_NOT_FOUND).get(userService.getPreferredLanguage(principal)));
        } catch (StudentNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.STUDENT_NOT_EXISTS).get(userService.getPreferredLanguage(principal)));
        }
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteStudent(@RequestParam(name = "username", defaultValue = "") String username) {
        return ResponseEntity.ok(this.studentService.deleteStudent(username));
    }
}
