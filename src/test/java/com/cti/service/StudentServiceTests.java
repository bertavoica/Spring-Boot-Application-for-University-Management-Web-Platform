package com.cti.service;

import com.cti.exception.UsernameNotExistsException;
import com.cti.models.Course;
import com.cti.models.Student;
import com.cti.repository.CourseRepository;
import com.cti.repository.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StudentServiceTests {

    @InjectMocks
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

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
}
