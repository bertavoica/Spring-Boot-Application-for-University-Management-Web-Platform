package com.cti.service;

import com.cti.exception.*;
import com.cti.models.Course;
import com.cti.models.Project;
import com.cti.models.Student;
import com.cti.payload.request.AssignmentUploadRequest;
import com.cti.payload.request.GradeFeedbackAssignmentRequest;
import com.cti.payload.request.ProjectAddRequest;
import com.cti.payload.request.ProjectUpdateRequest;
import com.cti.repository.CourseRepository;
import com.cti.repository.ProjectRepository;
import com.cti.repository.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProjectServiceTests {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EmailService emailService;

    public static final String USERNAME = "username";

    public static final String UNIQUE_ID = "1";

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Download a project successfully.")
    public void downloadProject() throws ProjectNotFoundUserException, ProjectNoAssignmentException, StudentNotExistsException {
        Student student = new Student();
        student.setUsername(USERNAME);
        student.setCoursesIds(List.of(UNIQUE_ID));

        Project project1 = new Project();
        project1.setUniqueId("test_1");
        project1.setOutputLocation("test_1.txt");

        Project project2 = new Project();
        project2.setUniqueId("unique_id");
        project2.setOutputLocation("E:\\Facultate\\Cercetare\\VOICA_BERTA_IOANA_MASTER\\VOICA_BERTA_IOANA_MASTER\\Spring Boot Application Master\\src\\test\\java\\com\\cti\\service\\unique_id.txt");

        File mockedFile = new File("E:\\Facultate\\Cercetare\\VOICA_BERTA_IOANA_MASTER\\VOICA_BERTA_IOANA_MASTER\\Spring Boot Application Master\\src\\test\\java\\com\\cti\\service\\unique_id.txt");

        student.setProjects(List.of(project1, project2));

        when(this.studentRepository.findByUsername(USERNAME)).thenReturn(Optional.of(student));

        File result = projectService.downloadProject(USERNAME, UNIQUE_ID);

        assertTrue(result.exists());
        assertTrue(mockedFile.exists());
        assertTrue(mockedFile.isFile());
    }

    @Test
    @DisplayName("Upload a project successfully.")
    public void uploadProjectTest() throws ProjectNotFoundUserException, IOException, StudentNotExistsException {
        Student mockStudent = new Student();
        mockStudent.setUsername(USERNAME);
        mockStudent.setCoursesIds(List.of(UNIQUE_ID));

        Project project1 = new Project();
        project1.setUniqueId("test_1");
        project1.setOutputLocation("test1.txt");

        Project project2 = new Project();
        project2.setUniqueId(UNIQUE_ID);
        project2.setOutputLocation("E:\\\\Facultate\\\\Cercetare\\\\VOICA_BERTA_IOANA_MASTER\\\\VOICA_BERTA_IOANA_MASTER\\\\Spring Boot Application Master\\\\src\\\\test\\\\java\\\\com\\\\cti\\\\service\\\\unique_id.txt\"");

        AssignmentUploadRequest assignmentUploadRequest = new AssignmentUploadRequest();
        assignmentUploadRequest.setUsername(USERNAME);
        assignmentUploadRequest.setProjectId(UNIQUE_ID);

        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        assignmentUploadRequest.setAssignment(multipartFile);

        File mockedFile = new File("Assignments/" + USERNAME);

        mockStudent.setProjects(List.of(project1, project2));

        when(this.studentRepository.findByUsername(USERNAME)).thenReturn(Optional.of(mockStudent));

        this.projectService.uploadProject(assignmentUploadRequest);

        verify(studentRepository, times(1)).save(mockStudent);
    }

    @Test
    @DisplayName("Get all assigned projects successfully.")
    public void getAllAssignedProjects() throws StudentNotExistsException, NoProjectForStudentException {
        Project project = new Project();
        project.setAssignee(USERNAME);

        Student mockStudent = new Student();
        mockStudent.setUsername(USERNAME);
        mockStudent.setCoursesIds(List.of(UNIQUE_ID));
        mockStudent.setProjects(List.of(project));

        when(this.studentRepository.findByUsername(USERNAME)).thenReturn(Optional.of(mockStudent));

        when(this.studentRepository.findAll()).thenReturn(List.of(mockStudent));

        List<Project> projects = this.projectService.getAllAssignedProjects(USERNAME);

        assertEquals(projects.size(), 1);
    }

    @Test
    @DisplayName("Review a project successfully.")
    public void reviewProjectSuccessfully() throws StudentNotExistsException, NoProjectForStudentException {
        GradeFeedbackAssignmentRequest gradeFeedbackAssignmentRequest = new GradeFeedbackAssignmentRequest();
        gradeFeedbackAssignmentRequest.setProjectId("1");
        gradeFeedbackAssignmentRequest.setStudentGrade(9.8);
        gradeFeedbackAssignmentRequest.setStudentName(USERNAME);
        gradeFeedbackAssignmentRequest.setStudentFeedback("test");

        Project project = new Project();
        project.setAssignee(USERNAME);
        project.setUniqueId("1");

        Student mockStudent = new Student();
        mockStudent.setUsername(USERNAME);
        mockStudent.setCoursesIds(List.of(UNIQUE_ID));
        mockStudent.setProjects(List.of(project));

        when(this.studentRepository.findByUsername(gradeFeedbackAssignmentRequest.getStudentName())).thenReturn(Optional.of(mockStudent));

        projectService.reviewProject(gradeFeedbackAssignmentRequest);

        verify(emailService).sendMail(project);

        verify(this.studentRepository).save(mockStudent);
    }


    @Test
    @DisplayName("Delete an assignment successfully.")
    public void deleteAssignmentSuccessfully() throws StudentNotExistsException {
        Project project = new Project();
        project.setAssignee(USERNAME);
        project.setUniqueId("1");

        Student mockStudent = new Student();
        mockStudent.setUsername(USERNAME);
        mockStudent.setCoursesIds(new ArrayList<>(Collections.singleton(UNIQUE_ID)));
        mockStudent.setProjects(new ArrayList<>(Collections.singleton(project)));

        when(this.studentRepository.findByUsername(USERNAME)).thenReturn(Optional.of(mockStudent));

        projectService.deleteAssignment(USERNAME, "1");

        verify(this.studentRepository).save(mockStudent);
    }

    @Test
    @DisplayName("Get reviewed statistics successfully.")
    public void getReviewedStatisticsSuccessfully() throws ProjectNotFoundException {
        Project mockProject = new Project();
        mockProject.setAssignee(USERNAME);
        mockProject.setUniqueId("1");
        mockProject.setProjectName("test");
        mockProject.setGrade(8.9);
        mockProject.setFeedback("test");

        Student mockStudent = new Student();
        mockStudent.setUsername(USERNAME);
        mockStudent.setCoursesIds(List.of(UNIQUE_ID));
        mockStudent.setProjects(List.of(mockProject));

        when(this.projectRepository.findByUniqueId("1")).thenReturn(Optional.of(mockProject));

        when(this.studentRepository.findAll()).thenReturn(List.of(mockStudent));

        String result = projectService.getReviewedStatistics("1");

        assertEquals(result, "[[\"Status\",\"Total assignments\"],[\"Passed\",1],[\"Failed\",0],[\"Not reviewed\",0]]");
    }

    @Test
    @DisplayName("Get deadline statistics successfully.")
    public void getDeadlineStatisticsTest() throws ProjectNotFoundException {
        Project mockProject = new Project();
        mockProject.setAssignee(USERNAME);
        mockProject.setUniqueId("1");
        mockProject.setProjectName("test");
        mockProject.setGrade(8.9);
        mockProject.setFeedback("test");

        Student mockStudent = new Student();
        mockStudent.setUsername(USERNAME);
        mockStudent.setCoursesIds(List.of(UNIQUE_ID));
        mockStudent.setProjects(List.of(mockProject));

        when(this.projectRepository.findByUniqueId("1")).thenReturn(Optional.of(mockProject));

        when(this.studentRepository.findAll()).thenReturn(List.of(mockStudent));

        String result = projectService.getDeadlineStatistics("1");

        assertEquals(result, "[[\"Status\",\"Total assignments\"],[\"Before deadline\",0],[\"After deadline\",0],[\"Not uploaded\",1]]");
    }

    @Test
    @DisplayName("Get grade statistics successfully.")
    public void getGradesStatisticsTest() throws ProjectNotFoundException {
        Project mockProject = new Project();
        mockProject.setAssignee(USERNAME);
        mockProject.setUniqueId("1");
        mockProject.setProjectName("test");
        mockProject.setGrade(8.9);
        mockProject.setFeedback("test");

        Student mockStudent = new Student();
        mockStudent.setUsername(USERNAME);
        mockStudent.setCoursesIds(List.of(UNIQUE_ID));
        mockStudent.setProjects(List.of(mockProject));

        when(this.projectRepository.findByUniqueId("1")).thenReturn(Optional.of(mockProject));

        when(this.studentRepository.findAll()).thenReturn(List.of(mockStudent));

        String result = projectService.getGradesStatistics("1");

        assertEquals(result, "[[\"Grades\",\"Students\"],[1,0],[2,0],[3,0],[4,0],[5,0],[6,0],[7,0],[8,0],[9,1],[10,0]]");
    }


    @Test
    @DisplayName("Assign project to user successfully.")
    public void assignProjectToUserTest() throws ProjectNotFoundUserException, StudentNotExistsException, StudentAssignedProjectException {
        Project mockProject = new Project();
        mockProject.setAssignee(USERNAME);
        mockProject.setUniqueId("2");
        mockProject.setProjectName("test");
        mockProject.setGrade(8.9);
        mockProject.setFeedback("test");

        Project activeProject = new Project();
        activeProject.setUniqueId("1");

        Student mockStudent = new Student();
        mockStudent.setUsername(USERNAME);
        mockStudent.setCoursesIds(new ArrayList<>(Collections.singleton(UNIQUE_ID)));
        mockStudent.setProjects(new ArrayList<>(Collections.singleton(activeProject)));

        when(this.studentRepository.findByUsername(USERNAME)).thenReturn(Optional.of(mockStudent));

        when(this.projectRepository.findByUniqueId("2")).thenReturn(Optional.of(mockProject));

        this.projectService.assignProjectToUser(USERNAME, "2");

        verify(this.studentRepository).save(mockStudent);
    }

    @Test
    @DisplayName("Assign project to group successfully.")
    public void assignProjectToGroupTest() throws ProjectNotFoundException, NoStudentsInGroupException, StudentsGroupAlreadyAssignedException {
        Project mockProject = new Project();
        mockProject.setAssignee(USERNAME);
        mockProject.setUniqueId("2");
        mockProject.setProjectName("test");
        mockProject.setGrade(8.9);
        mockProject.setFeedback("test");

        Project activeProject = new Project();
        activeProject.setUniqueId("1");

        Student mockStudent = new Student();
        mockStudent.setUsername(USERNAME);
        mockStudent.setCoursesIds(new ArrayList<>(Collections.singleton(UNIQUE_ID)));
        mockStudent.setProjects(new ArrayList<>(Collections.singleton(activeProject)));

        when(this.studentRepository.findByGroup(USERNAME)).thenReturn(List.of(mockStudent));

        when(this.projectRepository.findByUniqueId("2")).thenReturn(Optional.of(mockProject));

        this.projectService.assignProjectToGroup(USERNAME, "2");

        verify(this.studentRepository).save(mockStudent);
    }

    @Test
    @DisplayName("Add project template successfully.")
    public void addProjectTemplateTest() throws CourseNotFoundException {
        ProjectAddRequest projectAddRequest = new ProjectAddRequest();
        projectAddRequest.setCourseUniqueId(UNIQUE_ID);
        projectAddRequest.setOwner(USERNAME);
        projectAddRequest.setInputDate("2023-05-06");
        projectAddRequest.setInputTime("23:59");

        Course mockCourse = new Course();
        mockCourse.setUniqueId(UNIQUE_ID);

        when(this.courseRepository.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(mockCourse));

//        Project project = new Project(projectAddRequest);
//        project.setCourse(mockCourse);
//        project.setOwner(USERNAME);
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        project.setDeadline(LocalDateTime.parse("2023-05-06 23:59", formatter));

        this.projectService.addProjectTemplate(projectAddRequest);

        verify(this.projectRepository).save(any(Project.class));
    }

    @Test
    @DisplayName("Update a project successfully.")
    public void updateProjectTest() throws ProjectNotFoundException, CourseNotFoundException {
        ProjectUpdateRequest projectUpdateRequest = new ProjectUpdateRequest();
        projectUpdateRequest.setCourseUniqueId(UNIQUE_ID);
        projectUpdateRequest.setUniqueId(UNIQUE_ID);

        Project mockProject = new Project();
        mockProject.setAssignee(USERNAME);
        mockProject.setUniqueId(UNIQUE_ID);
        mockProject.setProjectName("test");
        mockProject.setDescription("test");

        when(this.projectRepository.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(mockProject));

        Course mockCourse = new Course();
        mockCourse.setUniqueId(UNIQUE_ID);

        when(this.courseRepository.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(mockCourse));

        this.projectService.updateProject(projectUpdateRequest);

        verify(this.projectRepository).save(mockProject);
    }

}
