package com.cti.controllers;

import com.cti.exception.*;
import com.cti.models.Course;
import com.cti.models.ELanguage;
import com.cti.models.Student;
import com.cti.payload.request.StudentAddRequest;
import com.cti.repository.StudentRepository;
import com.cti.service.StudentService;
import com.cti.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.Principal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.AssertJUnit.assertTrue;

public class StudentControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private StudentController studentController;

    @Mock
    private UserService userService;

    @Mock
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    private static final String URL = "/student-controller";

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
    }

    @Test
    public void getStudentCoursesTest() throws Exception {
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);
        Course course = new Course();
        course.setCompleteName("test");

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.when(this.studentService.getStudentCourses(username)).thenReturn(List.of(course));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL + "/course")
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[{\"uniqueId\":null,\"completeName\":\"test\",\"abbreviation\":null,\"description\":null,\"responsible\":null,\"assignedUsers\":0}]"));
    }

    @Test
    public void getStudentCoursesWithUsernameNotFoundExceptionTest() throws Exception {
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(UsernameNotExistsException.class).when(this.studentService).getStudentCourses(username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL + "/course")
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Username does not exist"));
    }

    @Test
    public void enrollStudentTest() throws Exception {
        String username = "username";
        String courseId = "id";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doNothing().when(this.studentService).enrollStudent(username, courseId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(URL + "/course")
                .param("username", username)
                .param("courseId", courseId)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Successfully enrolled student in this course"));
    }

    @Test
    public void enrollStudentWithStudentNotExistsExceptionTest() throws Exception {
        String username = "username";
        String courseId = "id";

        Principal principal = Mockito.mock(Principal.class);

        Student student = new Student();
        student.setUsername("username");

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(StudentNotExistsException.class).when(this.studentService).enrollStudent(username, courseId);
        Mockito.when(this.studentRepository.findAll()).thenReturn(List.of(student));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(URL + "/course")
                .param("username", username)
                .param("courseId", courseId)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[{\"username\":\"username\",\"emailAddress\":null,\"educationCycle\":null,\"specialization\":null,\"projects\":[],\"coursesIds\":[],\"group\":null,\"superior\":null}]"));
    }

    @Test
    public void enrollStudentWithUsernameNotExistsExceptionTest() throws Exception {
        String username = "username";
        String courseId = "id";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(UsernameNotExistsException.class).when(this.studentService).enrollStudent(username, courseId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(URL + "/course")
                .param("username", username)
                .param("courseId", courseId)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Username does not exist"));
    }

    @Test
    public void enrollStudentWithCourseNotFoundExceptionTest() throws Exception {
        String username = "username";
        String courseId = "id";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(CourseNotFoundException.class).when(this.studentService).enrollStudent(username, courseId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(URL + "/course")
                .param("username", username)
                .param("courseId", courseId)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Course not found"));
    }

    @Test
    public void enrollStudentWithStudentAlreadyEnrolledExceptionTest() throws Exception {
        String username = "username";
        String courseId = "id";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(StudentAlreadyEnrolledException.class).when(this.studentService).enrollStudent(username, courseId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(URL + "/course")
                .param("username", username)
                .param("courseId", courseId)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Student is already enrolled in this course"));
    }

    @Test
    public void removeStudentFromCourseTest() throws Exception {
        String username = "username";
        String courseId = "id";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doNothing().when(this.studentService).removeStudentFromCourse(username, courseId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(URL + "/course")
                .param("username", username)
                .param("courseId", courseId)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Successfully removed student from course"));
    }

    @Test
    public void removeStudentFromCourseWithUsernameNotExistsExceptionTest() throws Exception {
        String username = "username";
        String courseId = "id";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(UsernameNotExistsException.class).when(this.studentService).removeStudentFromCourse(username, courseId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(URL + "/course")
                .param("username", username)
                .param("courseId", courseId)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Username does not exist"));
    }

    @Test
    public void removeStudentFromCourseWithStudentsAlreadyRegisteredExceptionTest() throws Exception {
        String username = "username";
        String courseId = "id";

        Principal principal = Mockito.mock(Principal.class);

        Student student = new Student();
        student.setUsername("test");

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(StudentsAlreadyRegisteredException.class).when(this.studentService).removeStudentFromCourse(username, courseId);
        Mockito.when(this.studentRepository.findAll()).thenReturn(List.of(student));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(URL + "/course")
                .param("username", username)
                .param("courseId", courseId)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[{\"username\":\"test\",\"emailAddress\":null,\"educationCycle\":null,\"specialization\":null,\"projects\":[],\"coursesIds\":[],\"group\":null,\"superior\":null}]"));
    }

    @Test
    public void removeStudentFromCourseWithStudentNotEnrolledExceptionTest() throws Exception {
        String username = "username";
        String courseId = "id";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(StudentNotEnrolledException.class).when(this.studentService).removeStudentFromCourse(username, courseId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(URL + "/course")
                .param("username", username)
                .param("courseId", courseId)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Student is not enrolled in this course"));
    }

    @Test
    public void getStudentDetailsWhenUsernameIsEmptyTest() throws Exception {
        String username = "";
        Principal principal = Mockito.mock(Principal.class);

        Student student = new Student();
        student.setUsername("username");

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.when(this.studentService.getStudentDetails(username)).thenReturn(List.of(student));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL)
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[{\"username\":\"username\",\"emailAddress\":null,\"educationCycle\":null,\"specialization\":null,\"projects\":[],\"coursesIds\":[],\"group\":null,\"superior\":null}]"));
    }

    @Test
    public void getStudentDetailsWhenUsernameIsNotEmptyTest() throws Exception {
        String username = "username";
        Principal principal = Mockito.mock(Principal.class);

        Student student = new Student();
        student.setUsername("username");

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.when(this.studentService.getStudentDetails(username)).thenReturn(List.of(student));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL)
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[{\"username\":\"username\",\"emailAddress\":null,\"educationCycle\":null,\"specialization\":null,\"projects\":[],\"coursesIds\":[],\"group\":null,\"superior\":null}]"));
    }

    @Test
    public void addStudentTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        Student student = new Student();
        student.setEmailAddress("test");
        student.setGroup("test");
        student.setUsername("username");
        student.setSpecialization("test");

        StudentAddRequest studentAddRequest = new StudentAddRequest();
        studentAddRequest.setEmailAddress("test");
        studentAddRequest.setPassword("test");
        studentAddRequest.setGroup("test");
        studentAddRequest.setCycle("test");
        studentAddRequest.setUsername("username");
        studentAddRequest.setSpecialization("test");

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.when(this.studentService.addStudent(studentAddRequest)).thenReturn(List.of(student));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL)
                .content(asJsonString(studentAddRequest))
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

//        content.contains("[{\"username\":\"username\",\"emailAddress\":null,\"educationCycle\":null,\"specialization\":null,\"projects\":[],\"coursesIds\":[],\"group\":null,\"superior\":null}]"));
    }



    private static String asJsonString(final Object obj) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }






}
