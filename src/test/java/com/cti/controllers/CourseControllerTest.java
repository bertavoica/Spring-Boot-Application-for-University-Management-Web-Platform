package com.cti.controllers;

import com.cti.exception.CourseNotFoundException;
import com.cti.exception.StudentAlreadyEnrolledException;
import com.cti.exception.StudentNotExistsException;
import com.cti.models.Course;
import com.cti.models.ELanguage;
import com.cti.service.CourseService;
import com.cti.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.Principal;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.AssertJUnit.assertTrue;

public class CourseControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CourseService courseService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CourseController courseController;

    private static final String URL = "/course-controller";

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
    }

    @Test
    public void getAllCoursesSuccessfully() throws Exception {
        Mockito.when(this.courseService.getAllCourses()).thenReturn(List.of(new Course()));

        this.mockMvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getUserCoursesSuccessfully() throws Exception {
        String username = "username";

        Mockito.when(this.courseService.getUserCourses(username)).thenReturn(List.of(new Course()));

        this.mockMvc.perform(get(URL + "/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", username))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getEnrolledStudentsSuccessfully() throws Exception {
        String uniqueId = "uniqueId";

        Mockito.when(this.courseService.getEnrolledStudents(uniqueId)).thenReturn(List.of("Student1", "Student2"));

        this.mockMvc.perform(get(URL + "/enrolled")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("uniqueId", uniqueId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void addUserCoursesSuccessfully() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Mockito.doNothing().when(this.courseService).addUserCourses(uniqueId, username);

        this.mockMvc.perform(put(URL + "/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("uniqueId", uniqueId)
                        .param("username", username))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void addCourseWithCourseNotFoundException() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(CourseNotFoundException.class).when(this.courseService).addUserCourses(uniqueId, username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/course-controller/user")
                .param("uniqueId", uniqueId)
                .param("username", username)
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
    public void addCourseWithStudentNotExistsException() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);

        Mockito.doThrow(StudentNotExistsException.class).when(this.courseService).addUserCourses(uniqueId, username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/course-controller/user")
                .param("uniqueId", uniqueId)
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        System.out.println(content);

        assertTrue(content.contains("Student with the following username does not exist: username"));
    }

    @Test
    public void addCourseWithStudentAlreadyEnrolledException() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(StudentAlreadyEnrolledException.class).when(this.courseService).addUserCourses(uniqueId, username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/course-controller/user")
                .param("uniqueId", uniqueId)
                .param("username", username)
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
    public void removeUserCourses() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.when(this.courseService.removeUserCourses(uniqueId, username)).thenReturn(new Course());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/course-controller/user")
                .param("uniqueId", uniqueId)
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Successfully removed student from course"));
    }
}
