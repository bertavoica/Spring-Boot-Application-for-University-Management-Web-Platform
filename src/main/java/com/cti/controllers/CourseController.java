package com.cti.controllers;

import com.cti.models.Course;
import com.cti.models.Student;
import com.cti.payload.request.CourseAddRequest;
import com.cti.repository.CourseRepository;
import com.cti.repository.StudentRepository;
import com.cti.repository.UserRepository;
import com.cti.service.UserService;
import com.cti.service.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/course-controller")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    @GetMapping(value = "/user")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserCourses(@RequestParam(name = "username", defaultValue = "") String username) {
        Optional<Course> optionalCourse;
        List<Course> allCourses;
        List<Course> result;
        Student student;
        Optional<Student> optionalStudent;

        optionalStudent = studentRepository.findByUsername(username);
        result = new ArrayList<>();
        if (optionalStudent.isPresent()) {
            student = optionalStudent.get();
            if (student.getCoursesIds() == null)
                return ResponseEntity.ok(result);
            for (String courseId : student.getCoursesIds()) {
                optionalCourse = courseRepository.findByUniqueId(courseId);
                optionalCourse.ifPresent(result::add);
            }
        } else {
            allCourses = courseRepository.findAll();
            for (Course course : allCourses) {
                if (course.getResponsible() != null && course.getResponsible().contains(username))
                    result.add(course);
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/enrolled")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getEnrolledStudents(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId) {

        List<Student> students;
        List<String> result;

        students = studentRepository.findByCourseId(uniqueId);
        result = new ArrayList<>();
        for (Student student : students) {
            result.add(student.getUsername());
        }

        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/user")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> addUserCourses(@RequestParam(name = "username", defaultValue = "") String username,
                                            @RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                            Principal principal) {

        Optional<Course> optionalCourse;
        Course course;
        Student student;
        Optional<Student> optionalStudent;

        optionalCourse = courseRepository.findByUniqueId(uniqueId);
        if (!optionalCourse.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("CourseNotFound").get(userService.getPreferredLanguage(principal)));

        course = optionalCourse.get();

        optionalStudent = studentRepository.findByUsername(username);
        if (!optionalStudent.isPresent())
            return ResponseEntity.badRequest().body((Utils.languageDictionary.get("StudentNotExists").get(userService.getPreferredLanguage(principal)) + " " + username));

        student = optionalStudent.get();

        if (student.getCoursesIds().contains(uniqueId))
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("StudentAlreadyEnrolled").get(userService.getPreferredLanguage(principal)) + " " + username);

        student.getCoursesIds().add(uniqueId);
        studentRepository.save(student);

        course.setAssignedUsers(course.getAssignedUsers() + 1);
        courseRepository.save(course);

        return ResponseEntity.ok(Utils.languageDictionary.get("StudentEnrolled").get(userService.getPreferredLanguage(principal)) + " " + username);
    }

    @DeleteMapping(value = "/user")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> removeUserCourses(@RequestParam(name = "username", defaultValue = "") String username,
                                               @RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                               Principal principal) {

        Optional<Course> optionalCourse;
        Course course;
        Student student;
        Optional<Student> optionalStudent;

        optionalCourse = courseRepository.findByUniqueId(uniqueId);
        if (!optionalCourse.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("CourseNotFound").get(userService.getPreferredLanguage(principal)));

        course = optionalCourse.get();

        optionalStudent = studentRepository.findByUsername(username);
        if (!optionalStudent.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("StudentNotExists").get(userService.getPreferredLanguage(principal)) + " " + username);

        student = optionalStudent.get();

        if (!student.getCoursesIds().contains(uniqueId))
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("StudentAlreadyEnrolled").get(userService.getPreferredLanguage(principal)) + " " + course.getCompleteName());

        student.getCoursesIds().remove(uniqueId);
        studentRepository.save(student);

        course.setAssignedUsers(course.getAssignedUsers() - 1);
        courseRepository.save(course);

        return ResponseEntity.ok(Utils.languageDictionary.get("StudentRemovedFromCourse").get(userService.getPreferredLanguage(principal)) + " " + course.getCompleteName());
    }

    @GetMapping(value = "/responsible")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getResponsible(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                            Principal principal) {
        Course course;
        Optional<Course> optionalCourse;

        optionalCourse = courseRepository.findByUniqueId(uniqueId);
        if (!optionalCourse.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("CourseNotFound").get(userService.getPreferredLanguage(principal)));

        course = optionalCourse.get();
        if (course.getResponsible() == null)
            return ResponseEntity.ok(new ArrayList<>());

        return ResponseEntity.ok(course.getResponsible());
    }

    @PutMapping(value = "/responsible")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addResponsible(@RequestParam(name = "username", defaultValue = "") String username,
                                            @RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                            Principal principal) {
        Course course;
        Optional<Course> optionalCourse;

        optionalCourse = courseRepository.findByUniqueId(uniqueId);
        if (!optionalCourse.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("CourseNotFound").get(userService.getPreferredLanguage(principal)));

        course = optionalCourse.get();

        if (studentRepository.findByUsername(username).isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("UsernameIsStudent").get(userService.getPreferredLanguage(principal)) + " " + username);

        if (!userRepository.findByUsername(username).isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("UsernameNotExist").get(userService.getPreferredLanguage(principal)) + " " + username);

        if (course.getResponsible() == null)
            course.setResponsible(new ArrayList<>());

        if (course.getResponsible().contains(username))
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("UsernameAlreadyResponsible").get(userService.getPreferredLanguage(principal))  + " " + username);

        course.getResponsible().add(username);
        courseRepository.save(course);

        return ResponseEntity.ok(Utils.languageDictionary.get("TeacherAddedResponsible").get(userService.getPreferredLanguage(principal)) + " " + course.getCompleteName());
    }

    @DeleteMapping(value = "/responsible")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeResponsible(@RequestParam(name = "username", defaultValue = "") String username,
                                               @RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                               Principal principal) {
        Course course;
        Optional<Course> optionalCourse;

        optionalCourse = courseRepository.findByUniqueId(uniqueId);
        if (!optionalCourse.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("CourseNotFound").get(userService.getPreferredLanguage(principal)));

        course = optionalCourse.get();

        if (course.getResponsible() == null)
            course.setResponsible(new ArrayList<>());

        if (!course.getResponsible().contains(username))
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("CourseNoResponsible").get(userService.getPreferredLanguage(principal)) + " " + username);

        course.getResponsible().remove(username);
        courseRepository.save(course);

        return ResponseEntity.ok(Utils.languageDictionary.get("CourseRemovedResponsible").get(userService.getPreferredLanguage(principal)) + " " + course.getCompleteName());
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> addCourse(@Valid @RequestBody CourseAddRequest courseAddRequest) {
        Course course;

        course = new Course(courseAddRequest);
        courseRepository.save(course);

        return ResponseEntity.ok(courseRepository.findAll());
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCourse(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId) {

        courseRepository.deleteByUniqueId(uniqueId);

        return ResponseEntity.ok(courseRepository.findAll());
    }
}
