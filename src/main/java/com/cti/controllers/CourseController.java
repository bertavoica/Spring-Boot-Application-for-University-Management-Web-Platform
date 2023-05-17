package com.cti.controllers;

import com.cti.exception.*;
import com.cti.models.Course;
import com.cti.payload.request.CourseAddRequest;
import com.cti.service.CourseService;
import com.cti.service.UserService;
import com.cti.utils.ApplicationConstants;
import com.cti.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/course-controller")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.ok(this.courseService.getAllCourses());
    }

    @GetMapping(value = "/user")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserCourses(@RequestParam(name = "username", defaultValue = "") String username) {
        return ResponseEntity.ok(this.courseService.getUserCourses(username));
    }

    @GetMapping(value = "/enrolled")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getEnrolledStudents(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId) {
        return ResponseEntity.ok(this.courseService.getEnrolledStudents(uniqueId));
    }

    @PutMapping(value = "/user")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> addUserCourses(@RequestParam(name = "username", defaultValue = "") String username,
                                            @RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                            Principal principal) {
        try {
            this.courseService.addUserCourses(uniqueId, username);
            return ResponseEntity.ok(Utils.languageDictionary.get(ApplicationConstants.STUDENT_ENROLLED).get(userService.getPreferredLanguage(principal)) + " " + username);
        } catch (CourseNotFoundException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.COURSE_NOT_FOUND).get(userService.getPreferredLanguage(principal)));
        } catch (StudentNotExistsException e) {
            return ResponseEntity.badRequest().body((Utils.languageDictionary.get(ApplicationConstants.STUDENT_NOT_EXISTS).get(userService.getPreferredLanguage(principal)) + " " + username));
        } catch (StudentAlreadyEnrolledException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.STUDENT_ALREADY_ENROLLED).get(userService.getPreferredLanguage(principal)) + " " + username);
        }
    }

    @DeleteMapping(value = "/user")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> removeUserCourses(@RequestParam(name = "username", defaultValue = "") String username,
                                               @RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                               Principal principal) {
        try {
            Course course = this.courseService.removeUserCourses(uniqueId, username);
            return ResponseEntity.ok(Utils.languageDictionary.get(ApplicationConstants.STUDENT_REMOVED_FROM_COURSE).get(userService.getPreferredLanguage(principal)) + " " + course.getCompleteName());
        } catch (CourseNotFoundException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.COURSE_NOT_FOUND).get(userService.getPreferredLanguage(principal)));
        } catch (StudentNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.STUDENT_NOT_EXISTS).get(userService.getPreferredLanguage(principal)) + " " + username);
        } catch (StudentAlreadyEnrolledException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.STUDENT_ALREADY_ENROLLED).get(userService.getPreferredLanguage(principal)) + " " + "...");
        }
    }

    @GetMapping(value = "/responsible")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getResponsible(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                            Principal principal) throws CourseNotFoundException {
        return ResponseEntity.ok(this.courseService.getResponsible(uniqueId));
    }

    @PutMapping(value = "/responsible")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addResponsible(@RequestParam(name = "username", defaultValue = "") String username,
                                            @RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                            Principal principal) {
        try {
            Course course = this.courseService.addResponsible(uniqueId, username);
            return ResponseEntity.ok(Utils.languageDictionary.get(ApplicationConstants.TEACHER_ADDED_RESPONSIBLE).get(userService.getPreferredLanguage(principal)) + " " + course.getCompleteName());
        } catch (CourseNotFoundException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.COURSE_NOT_FOUND).get(userService.getPreferredLanguage(principal)));
        } catch (UsernameIsStudentException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.USERNAME_IS_STUDENT).get(userService.getPreferredLanguage(principal)) + " " + username);
        } catch (UsernameNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.USERNAME_NOT_EXISTS).get(userService.getPreferredLanguage(principal)) + " " + username);
        } catch (UsernameAlreadyResponsibleException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.USERNAME_ALREADY_RESPONSIBLE).get(userService.getPreferredLanguage(principal))  + " " + username);

        }
    }

    @DeleteMapping(value = "/responsible")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeResponsible(@RequestParam(name = "username", defaultValue = "") String username,
                                               @RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                               Principal principal) {
        try {
            Course course = this.courseService.removeResponsible(uniqueId, username);
            return ResponseEntity.ok(Utils.languageDictionary.get(ApplicationConstants.COURSE_REMOVED_RESPONSIBLE).get(userService.getPreferredLanguage(principal)) + " " + course.getCompleteName());
        } catch (CourseNotFoundException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.COURSE_NOT_FOUND).get(userService.getPreferredLanguage(principal)));
        } catch (CourseNoResponsibleException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.COURSE_NO_RESPONSIBLE).get(userService.getPreferredLanguage(principal)) + " " + username);
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> addCourse(@Valid @RequestBody CourseAddRequest courseAddRequest) {
        return ResponseEntity.ok(this.courseService.addCourse(courseAddRequest));
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCourse(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId) {
        return ResponseEntity.ok(this.courseService.deleteCourse(uniqueId));
    }
}
