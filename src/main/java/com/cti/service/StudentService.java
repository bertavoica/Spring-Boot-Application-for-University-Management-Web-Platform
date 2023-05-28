package com.cti.service;

import com.cti.exception.*;
import com.cti.models.*;
import com.cti.payload.request.StudentAddRequest;
import com.cti.payload.request.StudentUpdateRequest;
import com.cti.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.*;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            UserRepository userRepository,
            TeacherRepository teacherRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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

    public void enrollStudent(String username, String courseId) throws UsernameNotExistsException, CourseNotFoundException, StudentAlreadyEnrolledException, StudentNotExistsException {
        Optional<Student> optionalStudent;
        Student student;
        Optional<Course> optionalCourse;


        if (username.equals("")) {
            throw new StudentNotExistsException();
        }

        optionalStudent = studentRepository.findByUsername(username);
        if (!optionalStudent.isPresent()) {
            throw new UsernameNotExistsException();
        }

        student = optionalStudent.get();

        optionalCourse = courseRepository.findByUniqueId(courseId);
        if (!optionalCourse.isPresent()) {
            throw new CourseNotFoundException();
        }

        if (student.getCoursesIds() == null)
            student.setCoursesIds(new ArrayList<>());

        if (student.getCoursesIds().contains(courseId)) {
            throw new StudentAlreadyEnrolledException();
        }

        student.getCoursesIds().add(courseId);
        studentRepository.save(student);
    }

    public void removeStudentFromCourse(String username, String courseId) throws StudentsAlreadyRegisteredException, UsernameNotExistsException, StudentNotEnrolledException {
        Optional<Student> optionalStudent;
        Student student;

        if (username.equals("")) {
            throw new StudentsAlreadyRegisteredException();
        }

        optionalStudent = studentRepository.findByUsername(username);
        if (!optionalStudent.isPresent()) {
            throw new UsernameNotExistsException();
        }

        student = optionalStudent.get();


        if (student.getCoursesIds() == null)
            student.setCoursesIds(new ArrayList<>());

        if (!student.getCoursesIds().contains(courseId)) {
            throw new StudentNotEnrolledException();
        }

        student.getCoursesIds().remove(courseId);
        studentRepository.save(student);
    }

    public List<Object> getStudentDetails(String username) {
        if (username.equals(""))
            return Collections.singletonList(studentRepository.findAll());
        else
            return Collections.singletonList(studentRepository.findByUsernameContainingIgnoreCase(username));
    }

    public List<Student> addStudent(StudentAddRequest studentAddRequest) throws UserExistException {
        Student student;
        User user;

        if (userRepository.findByUsername(studentAddRequest.getUsername()).isPresent()) {
            throw new UserExistException();
        }

        user = new User();
        user.setUsername(studentAddRequest.getUsername());
        user.setEmail(studentAddRequest.getEmailAddress());
        user.getRoles().add(new Role(ERole.ROLE_STUDENT));
        user.setPassword(passwordEncoder.encode(studentAddRequest.getPassword()));
        userRepository.save(user);

        student = new Student(studentAddRequest);
        studentRepository.save(student);

        return studentRepository.findAll();
    }

    public List<Student> updateStudent(StudentUpdateRequest studentUpdateRequest) throws StudentNotExistsException, RoleNotFoundException, TeacherExistException {
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
        if (!optionalStudent.isPresent() || !optionalUser.isPresent()) {
            throw new StudentNotExistsException();
        }

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
            if (optionalTeacher.isPresent()) {
                throw new TeacherExistException();
            }

            teacher = new Teacher();

            role = roleRepository.findByName(ERole.ROLE_TEACHER)
                    .orElseThrow(RoleNotFoundException::new);
            outputRoles.add(role);
            user.setRoles(outputRoles);
            teacher.setUsername(student.getUsername());
            teacher.setEmailAddress(student.getEmailAddress());
            teacherRepository.save(teacher);
            studentRepository.delete(student);

            userRepository.save(user);

        } else if (studentUpdateRequest.getRole().equals("Admin")) {
            role = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(RoleNotFoundException::new);
            outputRoles.add(role);
            user.setRoles(outputRoles);
            studentRepository.delete(student);
            userRepository.save(user);

        }

        if (studentUpdateRequest.getRole().equals("Student")) {
            studentRepository.save(student);
        }

        return studentRepository.findAll();
    }

    public List<Student> deleteStudent(String username) {
        userRepository.deleteByUsername(username);
        studentRepository.deleteByUsername(username);

        return studentRepository.findAll();
    }
}
