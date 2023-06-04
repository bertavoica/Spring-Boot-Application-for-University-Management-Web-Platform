package com.cti.controllers;

import com.cti.exception.TitleNotExistsException;
import com.cti.exception.UserExistException;
import com.cti.models.Course;
import com.cti.models.ELanguage;
import com.cti.models.Teacher;
import com.cti.payload.request.TeacherAddRequest;
import com.cti.payload.request.TeacherUpdateRequest;
import com.cti.service.TeacherService;
import com.cti.service.UserService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
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

@AutoConfigureJson
public class TeacherControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private TeacherController teacherController;

    @Mock
    private UserService userService;

    @Mock
    private TeacherService teacherService;

    private static final String URL = "/teacher-controller";

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(teacherController).build();
    }

    @Test
    public void getAllTeachersTest() throws Exception {
        String username = "test";
        String available = "test";
        String specialization = "test";

        Teacher teacher = new Teacher();
        teacher.setUsername("test");
        teacher.setSpecialization("test");
        teacher.setEmailAddress("test");
        teacher.setTitle("test");
        teacher.setSuperior("test");

        Mockito.when(this.teacherService.getAllTeachers(username, available, specialization)).thenReturn(List.of(teacher));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL)
                .param("username", username)
                .param("available", available)
                .param("specialization", specialization)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[{\"username\":\"test\",\"emailAddress\":\"test\",\"title\":\"test\",\"superior\":\"test\",\"specialization\":\"test\"}]"));
    }

    @Test
    public void addTeacherTest() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setUsername("test");
        teacher.setSpecialization("test");
        teacher.setEmailAddress("test");
        teacher.setTitle("test");
        teacher.setSuperior("test");

        TeacherAddRequest teacherAddRequest = new TeacherAddRequest();
        teacherAddRequest.setPassword("test");
        teacherAddRequest.setTitle("title");
        teacherAddRequest.setUsername("test");
        teacherAddRequest.setEmailAddress("test");

        Mockito.when(this.teacherService.addTeacher(teacherAddRequest)).thenReturn(List.of(teacher));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL)
                .content(asJsonString(teacherAddRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("test"));
    }

    @Test
    public void deleteTeacherTest() throws Exception {
        String username = "test";

        Teacher teacher = new Teacher();
        teacher.setUsername("test");
        teacher.setSpecialization("test");
        teacher.setEmailAddress("test");
        teacher.setTitle("test");
        teacher.setSuperior("test");

        Mockito.when(this.teacherService.deleteTeacher(username)).thenReturn(List.of(teacher));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(URL)
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[{\"username\":\"test\",\"emailAddress\":\"test\",\"title\":\"test\",\"superior\":\"test\",\"specialization\":\"test\"}]"));
    }

    @Test
    public void updateTeacherTest() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setUsername("test");
        teacher.setSpecialization("test");
        teacher.setEmailAddress("test");
        teacher.setTitle("test");
        teacher.setSuperior("test");

        TeacherUpdateRequest teacherUpdateRequest = new TeacherUpdateRequest();
        teacherUpdateRequest.setRole("test");
        teacherUpdateRequest.setUsername("test1");
        teacherUpdateRequest.setTitle("test1");

        Mockito.when(this.teacherService.updateTeacher(teacherUpdateRequest)).thenReturn(List.of(teacher));

        System.out.println(asJsonString(teacherUpdateRequest));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(URL)
                .content(asJsonString(teacherUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("test"));
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
