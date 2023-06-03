package com.cti.controllers;

import com.cti.exception.*;
import com.cti.models.Course;
import com.cti.models.ELanguage;
import com.cti.payload.request.CourseAddRequest;
import com.cti.service.CourseService;
import com.cti.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.Principal;
import java.util.List;

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
    public void getAllCoursesSuccessfullyTest() throws Exception {
        Mockito.when(this.courseService.getAllCourses()).thenReturn(List.of(new Course()));

        this.mockMvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getUserCoursesSuccessfullyTest() throws Exception {
        String username = "username";

        Mockito.when(this.courseService.getUserCourses(username)).thenReturn(List.of(new Course()));

        this.mockMvc.perform(get(URL + "/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", username))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getEnrolledStudentsSuccessfullyTest() throws Exception {
        String uniqueId = "uniqueId";

        Mockito.when(this.courseService.getEnrolledStudents(uniqueId)).thenReturn(List.of("Student1", "Student2"));

        this.mockMvc.perform(get(URL + "/enrolled")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("uniqueId", uniqueId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void addUserCoursesSuccessfullyTest() throws Exception {
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
    public void addCourseWithCourseNotFoundExceptionTest() throws Exception {
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
    public void addCourseWithStudentNotExistsExceptionTest() throws Exception {
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
    public void addCourseWithStudentAlreadyEnrolledExceptionTest() throws Exception {
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
    public void removeUserCoursesTest() throws Exception {
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

    @Test
    public void removeUserCoursesWhenCourseNotFoundTest() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(CourseNotFoundException.class).when(this.courseService).removeUserCourses(uniqueId, username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/course-controller/user")
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
    public void removeUserCoursesWhenStudentNotExistsTest() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(StudentNotExistsException.class).when(this.courseService).removeUserCourses(uniqueId, username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/course-controller/user")
                .param("uniqueId", uniqueId)
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Student with the following username does not exist: username"));
    }

    @Test
    public void removeUserCoursesWhenStudentAlreadyEnrolledTest() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(StudentAlreadyEnrolledException.class).when(this.courseService).removeUserCourses(uniqueId, username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/course-controller/user")
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
    public void getResponsibleSuccessfullyTest() throws Exception {
        String uniqueId = "uniqueId";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.courseService.getResponsible(uniqueId)).thenReturn(List.of("responsible"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/course-controller/responsible")
                .param("uniqueId", uniqueId)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[\"responsible\"]"));
    }

    @Test
    public void addResponsibleSuccessfullyTest() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);

        Course course = new Course();
        course.setCompleteName("course");

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.when(this.courseService.addResponsible(uniqueId, username)).thenReturn(course);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/course-controller/responsible")
                .param("uniqueId", uniqueId)
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Successfully added teacher as responsible for course"));
    }

    @Test
    public void addResponsibleWithCourseNotFoundTest() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(CourseNotFoundException.class).when(this.courseService).addResponsible(uniqueId, username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/course-controller/responsible")
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
    public void addResponsibleWithUsernameIStudent() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(UsernameIsStudentException.class).when(this.courseService).addResponsible(uniqueId, username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/course-controller/responsible")
                .param("uniqueId", uniqueId)
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Username is a student username"));
    }

    @Test
    public void addResponsibleWhenUsernameNotExists() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(UsernameNotExistsException.class).when(this.courseService).addResponsible(uniqueId, username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/course-controller/responsible")
                .param("uniqueId", uniqueId)
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Username does not exist username"));
    }

    @Test
    public void addResponsibleWhenUsernameAlreadyResponsible() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(UsernameAlreadyResponsibleException.class).when(this.courseService).addResponsible(uniqueId, username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/course-controller/responsible")
                .param("uniqueId", uniqueId)
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Username is already responsible for this course username"));
    }

    @Test
    public void removeResponsibleSuccessfullyTest() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);

        Course course = new Course();
        course.setCompleteName("course");

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.when(this.courseService.removeResponsible(uniqueId, username)).thenReturn(course);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/course-controller/responsible")
                .param("uniqueId", uniqueId)
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Successfully removed teacher as responsible from course"));
    }

    @Test
    public void removeResponsibleWhenCourseNotFoundTest() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(CourseNotFoundException.class).when(this.courseService).removeResponsible(uniqueId, username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/course-controller/responsible")
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
    public void removeResponsibleWhenCourseNoResponsibleExceptionTest() throws Exception {
        String uniqueId = "uniqueId";
        String username = "username";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(CourseNoResponsibleException.class).when(this.courseService).removeResponsible(uniqueId, username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/course-controller/responsible")
                .param("uniqueId", uniqueId)
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Course does not have teacher as responsible username"));
    }

    @Test
    public void addCourseSuccessfullyTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        Course course = new Course();
        course.setCompleteName("test");
        course.setAbbreviation("test");
        course.setDescription("test");

        CourseAddRequest courseAddRequest = new CourseAddRequest();
        courseAddRequest.setCompleteName("test");
        courseAddRequest.setAbbreviation("test");
        courseAddRequest.setDescription("test");

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.when(this.courseService.addCourse(courseAddRequest)).thenReturn(List.of(course));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL)
                .content(asJsonString(courseAddRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Successfully added teacher as responsible for course"));
    }

    @Test
    public void deleteCourseSuccessfullyTest() throws Exception {
        String uniqueId = "uniqueId";

        Course course = new Course();
        course.setCompleteName("course");
        course.setAbbreviation("abbreviation");
        course.setDescription("description");

        Mockito.when(this.courseService.deleteCourse(uniqueId)).thenReturn(List.of(course));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(URL)
                .param("uniqueId", uniqueId)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[{\"uniqueId\":null,\"completeName\":\"course\",\"abbreviation\":\"abbreviation\",\"description\":\"description\",\"responsible\":null,\"assignedUsers\":0}]"));
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
