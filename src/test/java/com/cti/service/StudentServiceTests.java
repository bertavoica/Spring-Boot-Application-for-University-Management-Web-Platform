package com.cti.service;

import com.cti.exception.*;
import com.cti.models.*;
import com.cti.payload.request.StudentAddRequest;
import com.cti.payload.request.StudentUpdateRequest;
import com.cti.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.management.relation.RoleNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertThrows;
import static org.testng.AssertJUnit.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StudentServiceTests {

    @InjectMocks
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    public static final String USERNAME = "username";

    public static final String UNIQUE_ID = "1";

    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Get student courses test.")
    public void getStudentCoursesTest() throws UsernameNotExistsException {
        Student student = new Student();
        student.setUsername(USERNAME);
        student.setCoursesIds(List.of(UNIQUE_ID));

        Mockito.when(this.studentRepository.findByUsername(USERNAME)).thenReturn(Optional.of(student));

        Course mockCourse = new Course();
        mockCourse.setUniqueId(UNIQUE_ID);
        mockCourse.setResponsible(new ArrayList<>(Collections.singletonList(USERNAME)));

        Optional<Course> optionalMockCourse = Optional.of(mockCourse);
        when(courseRepository.findByUniqueId(UNIQUE_ID)).thenReturn(optionalMockCourse);

        List<Object> result = this.studentService.getStudentCourses(USERNAME);

        assertEquals(result.size(), 1);
    }

    @Test
    @DisplayName("Enroll a student successfully.")
    public void enrollStudentTest() throws UsernameNotExistsException, StudentAlreadyEnrolledException, CourseNotFoundException, StudentNotExistsException {
        Student student = new Student();
        student.setUsername(USERNAME);
        student.setCoursesIds(new ArrayList<>(Collections.singleton("2")));

        Mockito.when(this.studentRepository.findByUsername(USERNAME)).thenReturn(Optional.of(student));

        Course mockCourse = new Course();
        mockCourse.setUniqueId(UNIQUE_ID);
        mockCourse.setResponsible(new ArrayList<>(Collections.singletonList(USERNAME)));

        Optional<Course> optionalMockCourse = Optional.of(mockCourse);
        when(courseRepository.findByUniqueId(UNIQUE_ID)).thenReturn(optionalMockCourse);

        this.studentService.enrollStudent(USERNAME, UNIQUE_ID);

        verify(this.studentRepository).save(student);
    }

    @Test
    @DisplayName("Remove a student from course successfully.")
    public void removeStudentFromCourseTest() throws UsernameNotExistsException, StudentsAlreadyRegisteredException, StudentNotEnrolledException {
        Student student = new Student();
        student.setUsername(USERNAME);
        student.setCoursesIds(new ArrayList<>(Collections.singleton(UNIQUE_ID)));

        Mockito.when(this.studentRepository.findByUsername(USERNAME)).thenReturn(Optional.of(student));

        this.studentService.removeStudentFromCourse(USERNAME, UNIQUE_ID);

        verify(this.studentRepository).save(student);
    }

    @Test
    @DisplayName("Get student details successfully.")
    public void getStudentDetailsTest() {
        Student student_1 = new Student();
        student_1.setUsername("test_1");

        List<Object> students = List.of(student_1);

        List<Object> result = this.studentService.getStudentDetails("test_1");

        assertEquals(result.size(), students.size());
    }

    @Test
    @DisplayName("Add student successfully.")
    public void addStudentWithNonExistingUsernameTest() throws UserExistException {
        StudentAddRequest request = new StudentAddRequest();
        request.setUsername("test");
        request.setPassword("pass");
        request.setEmailAddress("email");

        Student student = new Student();
        student.setUsername("test");
        student.setEmailAddress("email");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenReturn(null);

        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        when(studentRepository.save(studentCaptor.capture())).thenReturn(null);

        List<Student> students = List.of(student);
        when(studentRepository.findAll()).thenReturn(students);

        List<Student> result = studentService.addStudent(request);

        verify(userRepository, times(1)).save(any(User.class));
        verify(studentRepository, times(1)).save(any(Student.class));
        verify(studentRepository, times(1)).findAll();

        assertFalse(result.isEmpty());

        User savedUser = userCaptor.getValue();
        assertEquals(request.getUsername(), savedUser.getUsername());
        assertEquals(request.getEmailAddress(), savedUser.getEmail());

        Student savedStudent = studentCaptor.getValue();
        assertEquals(request.getUsername(), savedStudent.getUsername());
        assertEquals(request.getEmailAddress(), savedStudent.getEmailAddress());
    }

    @Test
    @DisplayName("Add student with existing username successfully.")
    public void addStudentWithExistingUsernameTest() {
        StudentAddRequest request = new StudentAddRequest();
        request.setUsername("test");
        request.setPassword("pass");
        request.setEmailAddress("email");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(new User()));

        assertThrows(UserExistException.class, () -> studentService.addStudent(request));
    }

    @Test
    @DisplayName("Update student when role is Teacher successfully.")
    public void updateStudentWithRoleTeacherTest() throws TeacherExistException, RoleNotFoundException, StudentNotExistsException {
        StudentUpdateRequest request = new StudentUpdateRequest();
        request.setUsername(USERNAME);
        request.setRole("Teacher");
        request.setSpecialization("specialization");
        request.setCycle("cycle");
        request.setGroup("group");

        Student student = new Student();
        student.setUsername(USERNAME);
        student.setEmailAddress("test");

        User user = new User();
        user.setUsername(USERNAME);

        Teacher teacher = new Teacher();
        Role role = new Role();

        Mockito.when(this.studentRepository.findByUsername(USERNAME)).thenReturn(Optional.of(student));
        Mockito.when(this.userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        Mockito.when(this.teacherRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        Mockito.when(this.roleRepository.findByName(ERole.ROLE_TEACHER)).thenReturn(Optional.of(role));

        Mockito.when(this.teacherRepository.save(Mockito.any(Teacher.class))).thenReturn(teacher);

        this.studentService.updateStudent(request);

        verify(teacherRepository, times(1)).save(any(Teacher.class));
        verify(studentRepository, times(1)).delete(student);
        verify(userRepository, times(1)).save(user);
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Update student when role is Admin successfully.")
    public void updateStudentWithRoleAdminTest() throws TeacherExistException, RoleNotFoundException, StudentNotExistsException {
        StudentUpdateRequest request = new StudentUpdateRequest();
        request.setUsername(USERNAME);
        request.setRole("Admin");
        request.setSpecialization("specialization");
        request.setCycle("cycle");
        request.setGroup("group");

        Student student = new Student();
        student.setUsername(USERNAME);
        student.setEmailAddress("test");

        User user = new User();
        user.setUsername(USERNAME);

        Role role = new Role();

        Mockito.when(this.studentRepository.findByUsername(USERNAME)).thenReturn(Optional.of(student));
        Mockito.when(this.userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        Mockito.when(this.teacherRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        Mockito.when(this.roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(role));

        this.studentService.updateStudent(request);

        verify(studentRepository, times(1)).delete(student);
        verify(userRepository, times(1)).save(user);
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Update student when role is Student successfully.")
    public void updateStudentWithRoleStudentTest() throws TeacherExistException, RoleNotFoundException, StudentNotExistsException {
        StudentUpdateRequest request = new StudentUpdateRequest();
        request.setUsername(USERNAME);
        request.setRole("Student");
        request.setSpecialization("specialization");
        request.setCycle("cycle");
        request.setGroup("group");

        Student student = new Student();
        student.setUsername(USERNAME);
        student.setEmailAddress("test");

        User user = new User();
        user.setUsername(USERNAME);

        Mockito.when(this.studentRepository.findByUsername(USERNAME)).thenReturn(Optional.of(student));
        Mockito.when(this.userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        this.studentService.updateStudent(request);

        verify(studentRepository, times(1)).save(student);
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Delete a student successfully.")
    public void deleteStudentTest() {
        Mockito.doNothing().when(userRepository).deleteByUsername(USERNAME);
        Mockito.doNothing().when(studentRepository).deleteByUsername(USERNAME);

        Student student = new Student();
        student.setUsername(USERNAME);


        List<Student> students = List.of(student);
        when(studentRepository.findAll()).thenReturn(students);

        List<Student> result = this.studentService.deleteStudent(USERNAME);

        verify(userRepository, times(1)).deleteByUsername(USERNAME);
        verify(studentRepository, times(1)).deleteByUsername(USERNAME);
        verify(studentRepository, times(1)).findAll();

        Assert.assertNotNull(result);
    }
}
