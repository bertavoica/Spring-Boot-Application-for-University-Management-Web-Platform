package com.cti.controllers;

import com.cti.exception.*;
import com.cti.models.ELanguage;
import com.cti.models.Project;
import com.cti.payload.request.AssignmentUploadRequest;
import com.cti.payload.request.ProjectUpdateRequest;
import com.cti.repository.ProjectRepository;
import com.cti.service.ProjectService;
import com.cti.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

public class ProjectControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    @Mock
    private ProjectRepository projectRepository;

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

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(URL + "/upload")
                .file("assignment", multipartFile.getBytes())
                .param("projectId", assignmentUploadRequest.getProjectId())
                .param("username", assignmentUploadRequest.getUsername())
                .param("purpose", assignmentUploadRequest.getPurpose())
                .param("fileName", assignmentUploadRequest.getFileName())
                .principal(principal)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Project was successfully updated"));
    }

    @Test
    public void uploadProjectWithStudentNotExistsExceptionTest() throws Exception {
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
        Mockito.doThrow(StudentNotExistsException.class).when(this.projectService).uploadProject(assignmentUploadRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(URL + "/upload")
                .file("assignment", multipartFile.getBytes())
                .param("projectId", assignmentUploadRequest.getProjectId())
                .param("username", assignmentUploadRequest.getUsername())
                .param("purpose", assignmentUploadRequest.getPurpose())
                .param("fileName", assignmentUploadRequest.getFileName())
                .principal(principal)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

//        assertTrue(content.contains("Project was successfully updated"));
    }

    @Test
    public void uploadProjectWithProjectNotFoundUserExceptionTest() throws Exception {
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
        Mockito.doThrow(ProjectNotFoundUserException.class).when(this.projectService).uploadProject(assignmentUploadRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(URL + "/upload")
                .file("assignment", multipartFile.getBytes())
                .param("projectId", assignmentUploadRequest.getProjectId())
                .param("username", assignmentUploadRequest.getUsername())
                .param("purpose", assignmentUploadRequest.getPurpose())
                .param("fileName", assignmentUploadRequest.getFileName())
                .principal(principal)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

//        assertTrue(content.contains("Project was successfully updated"));
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

    @Test
    public void assignProjectToGroupTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doNothing().when(this.projectService).assignProjectToGroup(USERNAME, ID);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL + "/assign-group")
                .param("uniqueId", ID)
                .param("username", USERNAME)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Successfully assigned project to the following number of students"));
    }

    @Test
    public void assignProjectToGroupWithNoStudentsInGroupExceptionTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(NoStudentsInGroupException.class).when(this.projectService).assignProjectToGroup(USERNAME, ID);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL + "/assign-group")
                .param("uniqueId", ID)
                .param("username", USERNAME)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("students"));
    }

    @Test
    public void assignProjectToGroupWitProjectNotFoundExceptionTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(ProjectNotFoundException.class).when(this.projectService).assignProjectToGroup(USERNAME, ID);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL + "/assign-group")
                .param("uniqueId", ID)
                .param("username", USERNAME)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("students"));
    }

//    @Test
//    public void assignProjectToUserWithProjectNotFoundUserExceptionTest() throws Exception {
//        Principal principal = Mockito.mock(Principal.class);
//
//        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
//        Mockito.doThrow(ProjectNotFoundUserException.class).when(this.projectService).assignProjectToUser(USERNAME, ID);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL + "/assign-user")
//                .param("uniqueId", ID)
//                .param("username", USERNAME)
//                .principal(principal)
//                .contentType(MediaType.APPLICATION_JSON);
//
//
//        MvcResult result = mockMvc.perform(requestBuilder)
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andReturn();
//
//        String content = result.getResponse().getContentAsString();
//
//        assertTrue(content.contains("Project not found for user"));
//    }

    @Test
    public void assignProjectToGroupWithStudentsGroupAlreadyAssignedExceptionTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doThrow(StudentsGroupAlreadyAssignedException.class).when(this.projectService).assignProjectToGroup(USERNAME, ID);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL + "/assign-group")
                .param("uniqueId", ID)
                .param("username", USERNAME)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("students"));
    }

//    @Test
//    public void addProjectTemplateTest() throws Exception {
//        Principal principal = Mockito.mock(Principal.class);
//
//        ProjectAddRequest projectAddRequest = new ProjectAddRequest();
//        projectAddRequest.setInputTime("test");
//        projectAddRequest.setProjectName("test");
//        projectAddRequest.setOwner(OWNER);
//        projectAddRequest.setCourseUniqueId(ID);
//        projectAddRequest.setDescription("test");
//        projectAddRequest.setInputDate("test");
//
//        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
//        Mockito.doNothing().when(this.projectService).addProjectTemplate(projectAddRequest);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL)
//                .content(asJsonString(projectAddRequest))
//                .principal(principal)
//                .contentType(MediaType.APPLICATION_JSON);
//
//
//        MvcResult result = mockMvc.perform(requestBuilder)
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String content = result.getResponse().getContentAsString();
//
//        assertTrue(content.contains("Successfully added project template test"));
//    }

    @Test
    public void updateProjectTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        ProjectUpdateRequest projectUpdateRequest = new ProjectUpdateRequest();
        projectUpdateRequest.setUniqueId(ID);
        projectUpdateRequest.setProjectName("test");
        projectUpdateRequest.setDescription("test");
        projectUpdateRequest.setInputTime("time");
        projectUpdateRequest.setInputDate("date");

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doNothing().when(this.projectService).updateProject(projectUpdateRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(URL)
                .content(asJsonString(projectUpdateRequest))
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Successfully updated project template"));
    }

    @Test
    public void deleteProjectTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.doNothing().when(this.projectRepository).deleteByUniqueId(ID);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(URL)
                .param("uniqueId", ID)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Successfully deleted project template"));
    }

    @Test
    public void getAllProjectsWhenOwnerIsNullTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        Project project = new Project();
        project.setProjectName("test");
        project.setOwner("test");

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.when(this.projectRepository.findAll()).thenReturn(List.of(project));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL)
                .param("owner", "")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[{\"uniqueId\":null,\"projectName\":\"test\",\"description\":null,\"owner\":\"test\",\"assignee\":null,\"assigneeAddress\":null,\"assigned\":null,\"deadline\":null,\"uploadDate\":null,\"outputLocation\":null,\"notifyUpdates\":false,\"grade\":0.0,\"feedback\":null,\"status\":null,\"course\":null}]"));
    }

    @Test
    public void getAllProjectsWhenOwnerIsGivenTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        Project project = new Project();
        project.setProjectName("test");
        project.setOwner("owner");

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.when(this.projectRepository.findByOwner("owner")).thenReturn(List.of(project));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL)
                .param("owner", OWNER)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[{\"uniqueId\":null,\"projectName\":\"test\",\"description\":null,\"owner\":\"owner\",\"assignee\":null,\"assigneeAddress\":null,\"assigned\":null,\"deadline\":null,\"uploadDate\":null,\"outputLocation\":null,\"notifyUpdates\":false,\"grade\":0.0,\"feedback\":null,\"status\":null,\"course\":null}]"));
    }

    @Test
    public void getReviewedStatisticsTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        String resultString = "[[\"Status\",\"Total assignments\"],[\"Passed\",1],[\"Failed\",0],[\"Not reviewed\",0]]";

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.when(this.projectService.getReviewedStatistics(ID)).thenReturn(resultString);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL + "/statistics/reviewed")
                .param("uniqueId", ID)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[[\"Status\",\"Total assignments\"],[\"Passed\",1],[\"Failed\",0],[\"Not reviewed\",0]]"));
    }

    @Test
    public void getDeadlineStatisticsTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        String resultString = "[[\"Status\",\"Total assignments\"],[\"Before deadline\",0],[\"After deadline\",0],[\"Not uploaded\",1]]";

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.when(this.projectService.getDeadlineStatistics(ID)).thenReturn(resultString);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL + "/statistics/deadline")
                .param("uniqueId", ID)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[[\"Status\",\"Total assignments\"],[\"Before deadline\",0],[\"After deadline\",0],[\"Not uploaded\",1]]"));
    }


    @Test
    public void getGradesStatisticsTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        String resultString = "[[\"Grades\",\"Students\"],[1,0],[2,0],[3,0],[4,0],[5,0],[6,0],[7,0],[8,0],[9,1],[10,0]]";

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ENGLISH);
        Mockito.when(this.projectService.getGradesStatistics(ID)).thenReturn(resultString);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL + "/statistics/grades")
                .param("uniqueId", ID)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[[\"Grades\",\"Students\"],[1,0],[2,0],[3,0],[4,0],[5,0],[6,0],[7,0],[8,0],[9,1],[10,0]]"));
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
