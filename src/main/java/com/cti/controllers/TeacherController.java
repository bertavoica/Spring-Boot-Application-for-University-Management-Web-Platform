package com.cti.controllers;

import com.cti.exception.StudentExistException;
import com.cti.exception.TeacherNotExistsException;
import com.cti.exception.TitleNotExistsException;
import com.cti.exception.UserExistException;
import com.cti.models.Teacher;
import com.cti.payload.request.TeacherAddRequest;
import com.cti.payload.request.TeacherUpdateRequest;
import com.cti.service.TeacherService;
import com.cti.service.UserService;
import com.cti.utils.ApplicationConstants;
import com.cti.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/teacher-controller")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private TeacherService teacherService;

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllTeachers(@RequestParam(name = "username", defaultValue = "", required = false) String username,
                                            @RequestParam(name = "available", defaultValue = "", required = false) String available,
                                            @RequestParam(name = "specialization", defaultValue = "", required = false) String specialization) {
        return ResponseEntity.ok(this.teacherService.getAllTeachers(username, available, specialization));

    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> addTeacher(@Valid @RequestBody TeacherAddRequest teacherAddRequest,
                                        Principal principal) {
        try {
            List<Teacher> teachers = this.teacherService.addTeacher(teacherAddRequest);
            return ResponseEntity.ok(teachers);
        } catch (UserExistException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.USER_EXISTS).get(userService.getPreferredLanguage(principal)));
        } catch (TitleNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.TITLE_NOT_EXISTS).get(userService.getPreferredLanguage(principal)));
        }
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTeacher(@RequestParam(name = "username", defaultValue = "") String username) {
        return ResponseEntity.ok(teacherService.deleteTeacher(username));
    }

    @PutMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateTeacher(@Valid @RequestBody TeacherUpdateRequest teacherUpdateRequest,
                                           Principal principal) {
        try {
            return ResponseEntity.ok(this.teacherService.updateTeacher(teacherUpdateRequest));
        } catch (StudentExistException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.STUDENT_EXISTS).get(userService.getPreferredLanguage(principal)));
        } catch (RoleNotFoundException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.ROLE_NOT_FOUND).get(userService.getPreferredLanguage(principal)));
        } catch (TeacherNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.TEACHER_NOT_EXISTS).get(userService.getPreferredLanguage(principal)));
        } catch (TitleNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.TITLE_NOT_EXISTS).get(userService.getPreferredLanguage(principal)));
        }
    }
}
