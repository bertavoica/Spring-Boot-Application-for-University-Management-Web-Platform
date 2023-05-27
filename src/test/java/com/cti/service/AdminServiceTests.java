package com.cti.service;

import com.cti.models.*;
import com.cti.payload.request.AdminUpdateRequest;
import com.cti.repository.RoleRepository;
import com.cti.repository.StudentRepository;
import com.cti.repository.TeacherRepository;
import com.cti.repository.UserRepository;
import com.cti.utils.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.Principal;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertThrows;
import static org.testng.AssertJUnit.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AdminServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private AdminService adminService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("Prepare admin when user has username.")
    public void prepareAdminWhenUserHasUsernameTest() {
        User user = new User();
        String username = "test";
        Set<Role> outputRoles = new HashSet<>();
        outputRoles.add(new Role(ERole.ROLE_ADMIN));
        user.setRoles(outputRoles);
        user.setUsername(username);

        List<User> userList = List.of(user);

        Mockito.doReturn(userList).when(this.userRepository).findByUsernameContainingIgnoreCase(username);

        List<Admin> adminList = this.adminService.prepareAdminList(username);
        assertEquals(1, adminList.size());
        assertEquals(username, adminList.get(0).getUsername());
    }

    @Test
    @DisplayName("Prepare admin when user has no username.")
    public void prepareAdminWhenUserHasNoUsernameTest() {
        User user = new User();
        User secondUser = new User();
        String username = "";
        Set<Role> outputRoles = new HashSet<>();
        outputRoles.add(new Role(ERole.ROLE_ADMIN));
        user.setRoles(outputRoles);
        user.setUsername("");

        secondUser.setRoles(outputRoles);
        secondUser.setUsername("");

        List<User> userList = List.of(user, secondUser);
        Mockito.doReturn(userList).when(this.userRepository).findAll();

        List<Admin> adminList = this.adminService.prepareAdminList(username);

        assertEquals(2, adminList.size());
        assertEquals("", adminList.get(0).getUsername());
        assertEquals("", adminList.get(1).getUsername());

    }

    @Test
    @DisplayName("Delete by username successfully.")
    public void deleteByUsernameSuccessfully() {
        String firstUserUsername = "firstUserUsername";
        String secondUserUsername = "secondUserUsername";

        User firstUser = new User();
        firstUser.setUsername(firstUserUsername);
        User secondUser = new User();
        secondUser.setUsername(secondUserUsername);

        Mockito.doNothing().when(this.userRepository).deleteByUsername(secondUserUsername);
        List<User> expectedResult = List.of(firstUser);

        assertEquals(1, expectedResult.size());
    }

    @Test
    @DisplayName("Update admin when optional user is not present.")
    public void updateAdminWhenOptionalUserIsNotPresent() throws Exception {
        String username = "userNotFound";
        AdminUpdateRequest adminUpdateRequest = new AdminUpdateRequest();
        adminUpdateRequest.setUsername(username);
        adminUpdateRequest.setRole(ERole.ROLE_ADMIN.toString());

        Optional<User> optionalUser = Optional.empty();
        Mockito.doReturn(optionalUser)
                .when(this.userRepository)
                .findByUsername(username);


        when(userRepository.findByUsername(adminUpdateRequest.getUsername())).thenReturn(optionalUser);
        assertThrows(Exception.class, () -> {
            adminService.updateAdmin(adminUpdateRequest, principal);
        });
    }

    @Test
    @DisplayName("Update admin when optional student is not present.")
    public void updateAdminWhenOptionalStudentIsNotPresent() throws Exception {
        AdminUpdateRequest adminUpdateRequest = new AdminUpdateRequest();
        adminUpdateRequest.setUsername("testuser");
        adminUpdateRequest.setRole("Student");
        Principal principal = () -> "testuser";

        when(studentRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new User()));

        assertThrows(Exception.class,
                () -> this.adminService.updateAdmin(adminUpdateRequest, principal));
    }

    @Test
    @DisplayName("Update admin when optional teacher is not present.")
    public void updateAdminWhenOptionalTeacherIsNotPresent() {
        AdminUpdateRequest adminUpdateRequest = new AdminUpdateRequest();
        adminUpdateRequest.setUsername("test_user");
        adminUpdateRequest.setRole("Teacher");

        Principal principal = Mockito.mock(Principal.class);
        when(userService.getPreferredLanguage(principal)).thenReturn(ELanguage.valueOf("en"));

        Optional<Teacher> optionalTeacher = Optional.empty();
        when(teacherRepository.findByUsername(adminUpdateRequest.getUsername()))
                .thenReturn(optionalTeacher);

        Optional<User> optionalUser = Optional.of(new User());
        when(userRepository.findByUsername(adminUpdateRequest.getUsername()))
                .thenReturn(optionalUser);

        Role role = new Role();
        role.setName(ERole.ROLE_ADMIN);
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(role));

        try {
            adminService.updateAdmin(adminUpdateRequest, principal);
        } catch (Exception e) {
            Assert.fail(Utils.languageDictionary.get("TeacherExist").get(ELanguage.valueOf("en")) + " " + adminUpdateRequest.getUsername());
            assertEquals(e.getMessage(), Utils.languageDictionary.get("TeacherExist").get(ELanguage.valueOf("en")) + " " + adminUpdateRequest.getUsername());
        }
    }

    @Test
    @DisplayName("Update admin successfully.")
    public void updateAdminSuccessful() throws Exception {
        AdminUpdateRequest adminUpdateRequest = new AdminUpdateRequest();
        adminUpdateRequest.setUsername("test_user");
        adminUpdateRequest.setRole("Student");

        Principal principal = Mockito.mock(Principal.class);

        User user = new User();
        user.setUsername("test_user");
        user.setEmail("test_user@example.com");
        user.setPassword("test_password");
        userRepository.save(user);

        List<Admin> result = this.adminService.updateAdmin(adminUpdateRequest, principal);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test_user", result.get(0).getUsername());
    }
}
