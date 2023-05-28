package com.cti.service;

import com.cti.exception.*;
import com.cti.models.Course;
import com.cti.models.Student;
import com.cti.models.User;
import com.cti.payload.request.CourseAddRequest;
import com.cti.repository.CourseRepository;
import com.cti.repository.StudentRepository;
import com.cti.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CourseServiceTests {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    public static final String USERNAME = "username";
    public static final String UNIQUE_ID = "unique_id";

    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Get all courses successfully.")
    public void getAllCoursesTest() {
        List<Course> courses = List.of(new Course(), new Course());
        when(this.courseRepository.findAll()).thenReturn(courses);

        assertEquals(this.courseService.getAllCourses(), courses);
        assertEquals(2, courses.size());
    }

    @Test
    @DisplayName("Get user courses successfully.")
    public void getUserCoursesTest() {
        Course course1 = new Course();
        course1.setCompleteName("Course1");
        course1.setUniqueId(UNIQUE_ID);

        Student mockStudent = new Student();
        mockStudent.setUsername(USERNAME);
        mockStudent.setCoursesIds(List.of(UNIQUE_ID));

        Course mockCourse = new Course();
        mockCourse.setAssignedUsers(1);
        mockCourse.setCompleteName("Course1");

        Optional<Student> optionalMockStudent = Optional.of(mockStudent);
        when(this.studentRepository.findByUsername(USERNAME)).thenReturn(optionalMockStudent);

        Optional<Course> optionalMockCourse = Optional.of(mockCourse);
        when(this.courseRepository.findByUniqueId(UNIQUE_ID)).thenReturn(optionalMockCourse);

        when(this.courseRepository.findAll()).thenReturn(List.of(course1));

        assertEquals(this.courseService.getUserCourses(USERNAME).size(), 1);
    }

    @Test
    @DisplayName("Get enrolled students successfully.")
    public void getEnrolledStudentsTest() {
        Student student = new Student();
        student.setCoursesIds(List.of(UNIQUE_ID));
        student.setUsername(USERNAME);
        List<Student> students = List.of(student);

        when(this.studentRepository.findByCourseId(UNIQUE_ID)).thenReturn(students);

        assertEquals(this.courseService.getEnrolledStudents(UNIQUE_ID).size(), 1);
    }

    @Test
    @DisplayName("Add user courses successfully.")
    public void addUserCoursesTest() throws CourseNotFoundException, StudentNotExistsException, StudentAlreadyEnrolledException {
        Course mockCourse = new Course();
        mockCourse.setUniqueId(UNIQUE_ID);
        mockCourse.setAssignedUsers(1);

        Student mockStudent = new Student();
        mockStudent.setUsername(USERNAME);
        mockStudent.setCoursesIds(new ArrayList<>());

        Optional<Course> optionalMockCourse = Optional.of(mockCourse);
        when(courseRepository.findByUniqueId(UNIQUE_ID)).thenReturn(optionalMockCourse);

        Optional<Student> optionalMockStudent = Optional.of(mockStudent);
        when(studentRepository.findByUsername(USERNAME)).thenReturn(optionalMockStudent);

        courseService.addUserCourses(UNIQUE_ID, USERNAME);

        verify(studentRepository, times(1)).save(mockStudent);
        verify(courseRepository, times(1)).save(mockCourse);

        assertEquals(1, mockStudent.getCoursesIds().size());
        assertTrue(mockStudent.getCoursesIds().contains(UNIQUE_ID));
        assertEquals(2, mockCourse.getAssignedUsers());
    }

    @Test
    @DisplayName("Remove user courses successfully.")
    public void removeUserCoursesTest() throws StudentAlreadyEnrolledException, CourseNotFoundException, StudentNotExistsException {
        Course mockCourse = new Course();
        mockCourse.setUniqueId(UNIQUE_ID);
        mockCourse.setAssignedUsers(1);

        Student mockStudent = new Student();
        mockStudent.setUsername(USERNAME);
        mockStudent.setCoursesIds(new ArrayList<>(Collections.singletonList(UNIQUE_ID)));

        Optional<Course> optionalMockCourse = Optional.of(mockCourse);
        when(courseRepository.findByUniqueId(UNIQUE_ID)).thenReturn(optionalMockCourse);

        Optional<Student> optionalMockStudent = Optional.of(mockStudent);
        when(studentRepository.findByUsername(USERNAME)).thenReturn(optionalMockStudent);

        courseService.removeUserCourses(UNIQUE_ID, USERNAME);

        verify(studentRepository, times(1)).save(mockStudent);
        verify(courseRepository, times(1)).save(mockCourse);

        assertTrue(mockStudent.getCoursesIds().isEmpty());
        assertEquals(0, mockCourse.getAssignedUsers());
    }

    @Test
    @DisplayName("Add responsible successfully.")
    public void addResponsibleTest() throws UsernameNotExistsException, CourseNotFoundException, UsernameIsStudentException, UsernameAlreadyResponsibleException {
        Course mockCourse = new Course();
        mockCourse.setUniqueId(UNIQUE_ID);
        mockCourse.setAssignedUsers(1);

        Optional<Course> optionalMockCourse = Optional.of(mockCourse);
        when(courseRepository.findByUniqueId(UNIQUE_ID)).thenReturn(optionalMockCourse);

        when(studentRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(new User()));

        Course result = courseService.addResponsible(UNIQUE_ID, USERNAME);

        verify(courseRepository, times(1)).save(mockCourse);

        assertTrue(mockCourse.getResponsible().contains(USERNAME));
        assertEquals(mockCourse, result);
    }

    @Test
    @DisplayName("Get responsible successfully.")
    public void getResponsibleTest() throws CourseNotFoundException {
        Course mockCourse = new Course();
        mockCourse.setUniqueId(UNIQUE_ID);
        mockCourse.setResponsible(List.of(USERNAME));

        Optional<Course> optionalMockCourse = Optional.of(mockCourse);
        when(courseRepository.findByUniqueId(UNIQUE_ID)).thenReturn(optionalMockCourse);

        List<String> responsible = courseService.getResponsible(UNIQUE_ID);

        assertEquals(responsible.size(), 1);
    }

    @Test
    @DisplayName("Remove responsible successfully.")
    public void removeResponsible() throws CourseNotFoundException, CourseNoResponsibleException {
        Course mockCourse = new Course();
        mockCourse.setUniqueId(UNIQUE_ID);
        mockCourse.setResponsible(new ArrayList<>(Collections.singletonList(USERNAME)));

        Optional<Course> optionalMockCourse = Optional.of(mockCourse);
        when(courseRepository.findByUniqueId(UNIQUE_ID)).thenReturn(optionalMockCourse);

        Course course = courseService.removeResponsible(UNIQUE_ID, USERNAME);

        assertTrue(course.getResponsible().isEmpty());
    }

    @Test
    @DisplayName("Add course successfully.")
    public void addCourseTest() {
        CourseAddRequest courseAddRequest = new CourseAddRequest();
        Course course = new Course(courseAddRequest);

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        Mockito.when(courseRepository.save(courseCaptor.capture())).thenReturn(course);
        Mockito.when(courseRepository.findAll()).thenReturn(List.of(course));

        List<Course> result = this.courseService.addCourse(courseAddRequest);

        Mockito.verify(this.courseRepository).save(courseCaptor.getValue());
        assertEquals(result.size(), 1);
        assertEquals(courseCaptor.getValue(), course);
    }

    @Test
    @DisplayName("Delete a course successfully.")
    public void deleteCourseTest() {
        Mockito.doNothing().when(this.courseRepository).deleteByUniqueId(UNIQUE_ID);

    }
}
