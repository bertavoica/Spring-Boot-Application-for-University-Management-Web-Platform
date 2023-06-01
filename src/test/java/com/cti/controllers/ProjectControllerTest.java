package com.cti.controllers;

import com.cti.exception.CourseNotFoundException;
import com.cti.exception.ProjectNotFoundUserException;
import com.cti.exception.StudentAssignedProjectException;
import com.cti.exception.StudentNotExistsException;
import com.cti.models.ELanguage;
import com.cti.models.Project;
import com.cti.payload.request.AssignmentUploadRequest;
import com.cti.service.ProjectService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.Principal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.AssertJUnit.assertTrue;

@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
public class ProjectControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectController projectController;

    private static final String URL = "/project-controller";

    private static final String ID = "test";
    private static final String USERNAME = "test";
    private static final String PURPOSE = "test";
    private static final String FILENAME = "test";
    private static final String OWNER = "owner";

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
    }


    //download project

    @Test
    public void uploadProjectTest() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("test", "test.txt",
                MediaType.ALL_VALUE, "test".getBytes());

        AssignmentUploadRequest assignmentUploadRequest = new AssignmentUploadRequest();
        assignmentUploadRequest.setProjectId(ID);
        assignmentUploadRequest.setUsername(USERNAME);
        assignmentUploadRequest.setPurpose(PURPOSE);
        assignmentUploadRequest.setFileName(FILENAME);
        assignmentUploadRequest.setAssignment(multipartFile);

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doNothing().when(this.projectService).uploadProject(assignmentUploadRequest);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(URL + "/upload")
                .content(multipartFile.getBytes())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("projectId", String.valueOf(assignmentUploadRequest.getProjectId()))
                .param("username", assignmentUploadRequest.getUsername())
                .param("purpose", assignmentUploadRequest.getPurpose())
                .param("fileName", assignmentUploadRequest.getFileName())
                .principal(principal);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Course not found"));
    }

    //review project
    @Test
    public void getAllAssignedProjectsTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        Project project = new Project();

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.when(this.projectService.getAllAssignedProjects(OWNER)).thenReturn(List.of(project));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL + "/assignments")
                .param("owner", OWNER)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[{\"uniqueId\":null,\"projectName\":null,\"description\":null,\"owner\":null,\"assignee\":null,\"assigneeAddress\":null,\"assigned\":null,\"deadline\":null,\"uploadDate\":null,\"outputLocation\":null,\"notifyUpdates\":false,\"grade\":0.0,\"feedback\":null,\"status\":null,\"course\":null}]"));
    }

    @Test
    public void deleteAssignmentTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doNothing().when(this.projectService).deleteAssignment(ID, USERNAME);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(URL + "/assignments")
                .param("assignmentId", ID)
                .param("username", USERNAME)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Successfully removed assignment from test"));
    }

    @Test
    public void deleteAssignmentWithStudentNotExistsExceptionTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(StudentNotExistsException.class).when(this.projectService).deleteAssignment(ID, USERNAME);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(URL + "/assignments")
                .param("assignmentId", ID)
                .param("username", USERNAME)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Student with the following username does not exist: test"));
    }

    @Test
    public void assignProjectToUserTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doNothing().when(this.projectService).assignProjectToUser(USERNAME, ID);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL + "/assign-user")
                .param("uniqueId", ID)
                .param("username", USERNAME)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Successfully assigned project to test"));
    }

    @Test
    public void assignProjectToUserWithStudentNotExistsExceptionTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(StudentNotExistsException.class).when(this.projectService).assignProjectToUser(USERNAME, ID);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL + "/assign-user")
                .param("uniqueId", ID)
                .param("username", USERNAME)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Student with the following username does not exist: test"));
    }

    @Test
    public void assignProjectToUserWithProjectNotFoundUserExceptionTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(ProjectNotFoundUserException.class).when(this.projectService).assignProjectToUser(USERNAME, ID);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL + "/assign-user")
                .param("uniqueId", ID)
                .param("username", USERNAME)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Project not found for user"));
    }

    @Test
    public void assignProjectToUserWithStudentAssignedProjectExceptionTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(StudentAssignedProjectException.class).when(this.projectService).assignProjectToUser(USERNAME, ID);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL + "/assign-user")
                .param("uniqueId", ID)
                .param("username", USERNAME)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Student already has the project assigned to him"));
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
