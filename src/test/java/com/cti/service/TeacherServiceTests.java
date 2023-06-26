package com.cti.service;

import com.cti.exception.StudentExistException;
import com.cti.exception.TeacherNotExistsException;
import com.cti.exception.TitleNotExistsException;
import com.cti.exception.UserExistException;
import com.cti.models.*;
import com.cti.payload.request.TeacherAddRequest;
import com.cti.payload.request.TeacherUpdateRequest;
import com.cti.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TeacherServiceTests {

    @InjectMocks
    private TeacherService teacherService;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TitleRepository titleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private RoleRepository roleRepository;

    public static final String USERNAME = "username";

    public static final String UNIQUE_ID = "1";

    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Get all teachers with specialization successfully.")
    public void getAllTeachersWithSpecializationTest() {
        String username = "";
        String available = "";
        String specialization = "Math";

        Teacher teacher = new Teacher();
        teacher.setUsername(USERNAME);
        teacher.setSpecialization("Math");

        List<Teacher> expectedResult = List.of(teacher);

        when(teacherRepository.findAll()).thenReturn(expectedResult);

        List<Teacher> result = teacherService.getAllTeachers(username, available, specialization);

        Assert.assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("Get all teachers with available successfully.")
    public void getAllTeachersWithAvailableTest() {
        String username = "";
        String available = "true";
        String specialization = "";

        Teacher teacher = new Teacher();
        teacher.setUsername(USERNAME);
        teacher.setSpecialization("");

        List<Teacher> expectedResult = List.of(teacher);

        when(teacherRepository.findAll()).thenReturn(expectedResult);

        List<Teacher> result = teacherService.getAllTeachers(username, available, specialization);

        Assert.assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("Get all teachers with username successfully.")
    public void getAllTeacherWithUsernameTest() {
        String username = "john";
        String available = "";
        String specialization = "";

        Teacher teacher = new Teacher();
        teacher.setUsername(USERNAME);
        teacher.setSpecialization("");

        List<Teacher> expectedResult = List.of(teacher);

        when(teacherRepository.findByUsernameContainingIgnoreCase(username)).thenReturn(expectedResult);

        List<Teacher> result = teacherService.getAllTeachers(username, available, specialization);

        Assert.assertEquals(expectedResult, result);
        verify(teacherRepository, times(1)).findByUsernameContainingIgnoreCase(username);
    }

    @Test
    @DisplayName("Get all teachers without filter successfully.")
    public void getAllTeachersWithoutFilterTest() {
        String username = "";
        String available = "";
        String specialization = "";

        Teacher teacher = new Teacher();
        teacher.setUsername(USERNAME);
        teacher.setSpecialization("Math");

        List<Teacher> expectedResult = List.of(teacher);

        when(teacherRepository.findAll()).thenReturn(expectedResult);

        List<Teacher> result = teacherService.getAllTeachers(username, available, specialization);

        Assert.assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("Add teacher successfully.")
    public void addTeacherTest() throws UserExistException, TitleNotExistsException {
        TeacherAddRequest teacherAddRequest = new TeacherAddRequest();
        teacherAddRequest.setUsername("test");
        teacherAddRequest.setPassword("pass");
        teacherAddRequest.setTitle("title");

        Title title = new Title();
        title.setName("title");

        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());
        when(titleRepository.findByName("title")).thenReturn(Optional.of(title));

        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(teacherRepository.save(any(Teacher.class))).thenReturn(new Teacher());

        List<Teacher> result = this.teacherService.addTeacher(teacherAddRequest);

        verify(userRepository).save(any(User.class));
        verify(teacherRepository).save(any(Teacher.class));

        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Delete teacher successfully.")
    public void deleteTeacher() {
        List<Teacher> result = this.teacherService.deleteTeacher(USERNAME);

        assertNotNull(result);

        verify(this.userRepository).deleteByUsername(USERNAME);
        verify(this.teacherRepository).deleteByUsername(USERNAME);
    }

    @Test
    public void updateTeacherWhenRoleIsStudent() throws StudentExistException, RoleNotFoundException, TeacherNotExistsException, TitleNotExistsException {
        TeacherUpdateRequest teacherUpdateRequest = new TeacherUpdateRequest();
        teacherUpdateRequest.setUsername("test");
        teacherUpdateRequest.setTitle("title");
        teacherUpdateRequest.setRole("Student");

        Teacher teacher = new Teacher();
        teacher.setUsername(USERNAME);

        User user = new User();
        user.setUsername(USERNAME);

        when(this.teacherRepository.findByUsername("test")).thenReturn(Optional.of(teacher));
        when(this.userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        when(this.titleRepository.findByName("title")).thenReturn(Optional.empty());

        when(this.studentRepository.findByUsername("test")).thenReturn(Optional.empty());
        Role role = new Role();
        role.setName(ERole.ROLE_STUDENT);

        when(this.roleRepository.findByName(ERole.ROLE_STUDENT)).thenReturn(Optional.of(role));


        List<Teacher> result = this.teacherService.updateTeacher(teacherUpdateRequest);

        verify(studentRepository, times(1)).save(any(Student.class));
        verify(teacherRepository, times(1)).save(any(Teacher.class));
//        verify(userRepository, times(1)).save(any(User.class));
    }

}
