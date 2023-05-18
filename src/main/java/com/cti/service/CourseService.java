package com.cti.service;

import com.cti.exception.*;
import com.cti.models.Course;
import com.cti.models.Student;
import com.cti.payload.request.CourseAddRequest;
import com.cti.repository.CourseRepository;
import com.cti.repository.StudentRepository;
import com.cti.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public CourseService(
            CourseRepository courseRepository,
            StudentRepository studentRepository,
            UserRepository userRepository
    ) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    public List<Course> getAllCourses() {
        return this.courseRepository.findAll();
    }

    public List<Course> getUserCourses(String username) {
        Optional<Course> optionalCourse;
        List<Course> allCourses;
        List<Course> result;
        Student student;
        Optional<Student> optionalStudent;

        optionalStudent = studentRepository.findByUsername(username);
        result = new ArrayList<>();
        if (optionalStudent.isPresent()) {
            student = optionalStudent.get();
            if (student.getCoursesIds() == null) {
                return result;
            }

            for (String courseId : student.getCoursesIds()) {
                optionalCourse = courseRepository.findByUniqueId(courseId);
                optionalCourse.ifPresent(result::add);
            }
        } else {
            allCourses = courseRepository.findAll();
            for (Course course : allCourses) {
                if (course.getResponsible() != null && course.getResponsible().contains(username)) {
                    result.add(course);
                }
            }
        }

        return result;
    }

    public List<String> getEnrolledStudents(String uniqueId) {
        List<Student> students;
        List<String> result;

        students = studentRepository.findByCourseId(uniqueId);
        result = new ArrayList<>();
        for (Student student : students) {
            result.add(student.getUsername());
        }

        return result;
    }

    public void addUserCourses(String uniqueId, String username) throws CourseNotFoundException, StudentNotExistsException, StudentAlreadyEnrolledException {
        Optional<Course> optionalCourse;
        Course course;
        Student student;
        Optional<Student> optionalStudent;

        optionalCourse = courseRepository.findByUniqueId(uniqueId);
        if (!optionalCourse.isPresent()) {
            throw new CourseNotFoundException();
        }

        course = optionalCourse.get();

        optionalStudent = studentRepository.findByUsername(username);
        if (!optionalStudent.isPresent()) {
            throw new StudentNotExistsException();
        }

        student = optionalStudent.get();

        if (student.getCoursesIds().contains(uniqueId)) {
            throw new StudentAlreadyEnrolledException();
        }

        student.getCoursesIds().add(uniqueId);
        studentRepository.save(student);

        course.setAssignedUsers(course.getAssignedUsers() + 1);
        courseRepository.save(course);
    }

    public Course removeUserCourses(String uniqueId, String username) throws CourseNotFoundException, StudentNotExistsException, StudentAlreadyEnrolledException {
        Optional<Course> optionalCourse;
        Course course;
        Student student;
        Optional<Student> optionalStudent;

        optionalCourse = courseRepository.findByUniqueId(uniqueId);
        if (!optionalCourse.isPresent()) {
            throw new CourseNotFoundException();
        }

        course = optionalCourse.get();

        optionalStudent = studentRepository.findByUsername(username);
        if (!optionalStudent.isPresent()) {
            throw new StudentNotExistsException();
        }

        student = optionalStudent.get();

        if (!student.getCoursesIds().contains(uniqueId)) {
            throw new StudentAlreadyEnrolledException();
        }

        student.getCoursesIds().remove(uniqueId);
        studentRepository.save(student);

        course.setAssignedUsers(course.getAssignedUsers() - 1);
        courseRepository.save(course);

        return course;
    }

    public Course addResponsible(String uniqueId, String username) throws UsernameAlreadyResponsibleException, UsernameNotExistsException, UsernameIsStudentException, CourseNotFoundException {
        Course course;
        Optional<Course> optionalCourse;

        optionalCourse = courseRepository.findByUniqueId(uniqueId);
        if (!optionalCourse.isPresent()) {
            throw new CourseNotFoundException();
        }

        course = optionalCourse.get();

        if (studentRepository.findByUsername(username).isPresent()) {
            throw new UsernameIsStudentException();
        }

        if (!userRepository.findByUsername(username).isPresent()) {
            throw new UsernameNotExistsException();
        }

        if (course.getResponsible() == null)
            course.setResponsible(new ArrayList<>());

        if (course.getResponsible().contains(username)) {
            throw new UsernameAlreadyResponsibleException();
        }

        course.getResponsible().add(username);
        courseRepository.save(course);

        return course;
    }

    public List<String> getResponsible(String uniqueId) throws CourseNotFoundException {
        Course course;
        Optional<Course> optionalCourse;

        optionalCourse = courseRepository.findByUniqueId(uniqueId);
        if (!optionalCourse.isPresent()) {
            throw new CourseNotFoundException();
        }

        course = optionalCourse.get();
        if (course.getResponsible() == null) {
            return new ArrayList<>();
        }

        return course.getResponsible();
    }

    public Course removeResponsible(String uniqueId, String username) throws CourseNotFoundException, CourseNoResponsibleException {
        Course course;
        Optional<Course> optionalCourse;

        optionalCourse = courseRepository.findByUniqueId(uniqueId);
        if (!optionalCourse.isPresent()) {
            throw new CourseNotFoundException();
        }

        course = optionalCourse.get();

        if (course.getResponsible() == null)
            course.setResponsible(new ArrayList<>());

        if (!course.getResponsible().contains(username)) {
            throw new CourseNoResponsibleException();
        }

        course.getResponsible().remove(username);
        courseRepository.save(course);

        return course;
    }

    public List<Course> addCourse(CourseAddRequest courseAddRequest) {
        Course course;

        course = new Course(courseAddRequest);
        courseRepository.save(course);

        return courseRepository.findAll();
    }

    public List<Course> deleteCourse(String uniqueId) {
        courseRepository.deleteByUniqueId(uniqueId);

        return courseRepository.findAll();
    }
}
