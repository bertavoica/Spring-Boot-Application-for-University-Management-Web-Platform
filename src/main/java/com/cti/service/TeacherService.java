package com.cti.service;

import com.cti.exception.StudentExistException;
import com.cti.exception.TeacherNotExistsException;
import com.cti.exception.TitleNotExistsException;
import com.cti.exception.UserExistException;
import com.cti.models.*;
import com.cti.payload.request.TeacherAddRequest;
import com.cti.payload.request.TeacherUpdateRequest;
import com.cti.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.*;

@Service
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final TitleRepository titleRepository;
    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public TeacherService(
            TeacherRepository teacherRepository,
            UserRepository userRepository,
            TitleRepository titleRepository,
            StudentRepository studentRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.titleRepository = titleRepository;
        this.studentRepository = studentRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Teacher> getAllTeachers(
            String username,
            String available,
            String specialization
    ) {
        List<Teacher> teacherList = teacherRepository.findAll();
        List<Teacher> result;

        if (!specialization.equals("")) {
            result = new ArrayList<>();
            for (Teacher teacher : teacherList) {
                if (!teacher.getSpecialization().isEmpty() && teacher.getSpecialization().equals(specialization))
                    result.add(teacher);
            }

            return result;
        } else if (available.equals("true")) {
            result = new ArrayList<>();
            for (Teacher teacher : teacherList) {
                if (teacher.getSpecialization().equals("")) {
                    result.add(teacher);
                }
            }

            return result;
        } else if (username.equals(""))
            return teacherList;
        else
            return teacherRepository.findByUsernameContainingIgnoreCase(username);
    }

    public List<Teacher> addTeacher(TeacherAddRequest teacherAddRequest) throws UserExistException, TitleNotExistsException {
        Teacher teacher;
        User user;

        if (userRepository.findByUsername(teacherAddRequest.getUsername()).isPresent()) {
            throw new UserExistException();
        }

        if (!titleRepository.findByName(teacherAddRequest.getTitle()).isPresent()) {
            throw new TitleNotExistsException();
        }

        user = new User();
        user.setUsername(teacherAddRequest.getUsername());
        user.setEmail(teacherAddRequest.getEmailAddress());
        user.getRoles().add(new Role(ERole.ROLE_TEACHER));
        user.setPassword(passwordEncoder.encode(teacherAddRequest.getPassword()));

        userRepository.save(user);

        teacher = new Teacher(teacherAddRequest);

        teacherRepository.save(teacher);

        return teacherRepository.findAll();
    }

    public List<Teacher> deleteTeacher(String username) {
        userRepository.deleteByUsername(username);
        teacherRepository.deleteByUsername(username);

        return teacherRepository.findAll();
    }

    public List<Teacher> updateTeacher(TeacherUpdateRequest teacherUpdateRequest) throws TeacherNotExistsException, TitleNotExistsException, StudentExistException, RoleNotFoundException {
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
        if (!optionalTeacher.isPresent() || !optionalUser.isPresent()) {
            throw new TeacherNotExistsException();
        }

        if (!titleRepository.findByName(teacherUpdateRequest.getTitle()).isPresent() && teacherUpdateRequest.getRole().equals("Teacher")) {
            throw new TitleNotExistsException();
        }

        teacher = optionalTeacher.get();

        user = optionalUser.get();
        outputRoles = new HashSet<>();
        if (teacherUpdateRequest.getRole().equals("Student")) {
            optionalStudent = studentRepository.findByUsername(teacherUpdateRequest.getUsername());
            if (optionalStudent.isPresent()) {
                throw new StudentExistException();
            }

            student = new Student();

            role = roleRepository.findByName(ERole.ROLE_STUDENT)
                    .orElseThrow(RoleNotFoundException::new);
            outputRoles.add(role);
            user.setRoles(outputRoles);
            student.setUsername(teacher.getUsername());
            student.setEmailAddress(teacher.getEmailAddress());
            studentRepository.save(student);
            teacherRepository.delete(teacher);

            userRepository.save(user);

        } else if (teacherUpdateRequest.getRole().equals("Admin")) {
            role = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(RoleNotFoundException::new);
            outputRoles.add(role);
            user.setRoles(outputRoles);
            teacherRepository.delete(teacher);
            userRepository.save(user);
        }

        if (teacherUpdateRequest.getRole().equals("Teacher") && teacherUpdateRequest.getTitle() != null) {
            teacher.setTitle(teacherUpdateRequest.getTitle());
            teacherRepository.save(teacher);
        }

        return teacherRepository.findAll();
    }
}
