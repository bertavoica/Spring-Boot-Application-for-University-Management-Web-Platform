package com.cti.controllers;

import com.cti.controllers.AdminController;
import com.cti.models.Admin;
import com.cti.models.ERole;
import com.cti.models.Role;
import com.cti.models.Student;
import com.cti.models.Teacher;
import com.cti.models.User;
import com.cti.payload.request.AdminUpdateRequest;
import com.cti.repository.RoleRepository;
import com.cti.repository.StudentRepository;
import com.cti.repository.TeacherRepository;
import com.cti.repository.UserRepository;
import com.cti.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

public class AdminControllerTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserService userService;

    @Mock
    private Principal principal;

    @InjectMocks
    private AdminController adminController;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAllAdmins_WithUsername_ShouldReturnResponseEntityWithListOfAdmins() {
        // Arrange
    }
}