package com.cti.service;

import com.cti.exception.UsernameNotExistsException;
import com.cti.models.Course;
import com.cti.models.Student;
import com.cti.repository.CourseRepository;
import com.cti.repository.StudentRepository;
import com.cti.utils.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final UserService userService;

    public StudentService(
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            UserService userService
    ) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    public List<Object> getStudentCourses(String username) throws UsernameNotExistsException {
        List<Course> courseList;
        Optional<Student> optionalStudent;
        Student student;
        Optional<Course> optionalCourse;

        if (username.equals(""))
            return Collections.singletonList(studentRepository.findAll());

        optionalStudent = studentRepository.findByUsername(username);
        if (!optionalStudent.isPresent()) {
            throw new UsernameNotExistsException();
        }

        student = optionalStudent.get();
        courseList = new ArrayList<>();

        if (student.getCoursesIds() == null)
            return Collections.singletonList(courseList);

        for (String courseId : student.getCoursesIds()) {
            optionalCourse = courseRepository.findByUniqueId(courseId);
            optionalCourse.ifPresent(courseList::add);
        }

        return Collections.singletonList(courseList);
    }
}
