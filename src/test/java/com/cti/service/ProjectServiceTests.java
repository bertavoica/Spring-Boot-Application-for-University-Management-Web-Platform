package com.cti.service;

import com.cti.exception.ProjectNoAssignmentException;
import com.cti.exception.ProjectNotFoundUserException;
import com.cti.exception.StudentNotExistsException;
import com.cti.models.Project;
import com.cti.models.Student;
import com.cti.payload.request.AssignmentUploadRequest;
import com.cti.repository.ProjectRepository;
import com.cti.repository.StudentRepository;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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
    private UserService userService;

    @Mock
    private EmailService emailService;

    public static final String USERNAME = "username";

    public static final String UNIQUE_ID = "unique_id";

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
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
    public void uploadProject() {
        Student mockStudent = new Student();
        mockStudent.setUsername(USERNAME);
        mockStudent.setCoursesIds(List.of(UNIQUE_ID));

        Project project1 = new Project();
        project1.setUniqueId("test_1");
        project1.setOutputLocation("test1.txt");

        Project project2 = new Project();
        project2.setUniqueId("unique_id");
        project2.setOutputLocation("E:\\\\Facultate\\\\Cercetare\\\\VOICA_BERTA_IOANA_MASTER\\\\VOICA_BERTA_IOANA_MASTER\\\\Spring Boot Application Master\\\\src\\\\test\\\\java\\\\com\\\\cti\\\\service\\\\unique_id.txt\"");

        AssignmentUploadRequest assignmentUploadRequest = new AssignmentUploadRequest();
        assignmentUploadRequest.setUsername(USERNAME);
//        assignmentUploadRequest.setAssignment();

//        File mockedFile = new File("Assignments/" + USERNAME);
//        String path =
//
//        mockStudent.setProjects(List.of(project1, project2));
//
//        when(this.studentRepository.findByUsername(USERNAME)).thenReturn(Optional.of(mockStudent));
//
//        verify(studentRepository, times(1)).save(mockStudent);

    }
}
