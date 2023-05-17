package com.cti.service;

import com.cti.exception.*;
import com.cti.models.Course;
import com.cti.models.EProjectStatus;
import com.cti.models.Project;
import com.cti.models.Student;
import com.cti.payload.request.AssignmentUploadRequest;
import com.cti.payload.request.GradeFeedbackAssignmentRequest;
import com.cti.payload.request.ProjectAddRequest;
import com.cti.payload.request.ProjectUpdateRequest;
import com.cti.repository.CourseRepository;
import com.cti.repository.ProjectRepository;
import com.cti.repository.StudentRepository;
import com.cti.utils.Utils;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final UserService userService;
    private final EmailService emailService;

    public ProjectService(
            ProjectRepository projectRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            UserService userService,
            EmailService emailService
    ) {
        this.projectRepository = projectRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.emailService = emailService;
    }

    public File downloadProject(String studentName, String uniqueId) throws StudentNotExistsException, ProjectNotFoundUserException, ProjectNoAssignmentException {
        Optional<Student> optionalStudent;
        Student student;
        File assignment;
        Project selectedProject = null;

        optionalStudent = studentRepository.findByUsername(studentName);

        if (!optionalStudent.isPresent()) {
            throw new StudentNotExistsException();
        }

        student = optionalStudent.get();
        for (Project studentProject : student.getProjects()) {
            if (studentProject.getUniqueId().equals(uniqueId))
                selectedProject = studentProject;
        }
        if (selectedProject == null) {
            throw new ProjectNotFoundUserException();
        }

        if (selectedProject.getOutputLocation() == null) {
            throw new ProjectNoAssignmentException();
        }

        assignment = new File(selectedProject.getOutputLocation());
        if (!assignment.exists()) {
            throw new ProjectNoAssignmentException();
        }

        return assignment;
    }

    public void uploadProject(AssignmentUploadRequest assignmentUploadRequest) throws StudentNotExistsException, ProjectNotFoundUserException, IOException {
        String path;
        File destination;
        Optional<Student> optionalStudent;
        Student student;
        Project selectedProject = null;

        optionalStudent = studentRepository.findByUsername(assignmentUploadRequest.getUsername());
        if (!optionalStudent.isPresent()) {
            throw new StudentNotExistsException();
        }

        student = optionalStudent.get();

        for (Project studentProject : student.getProjects()) {
            if (studentProject.getUniqueId().equals(assignmentUploadRequest.getProjectId()))
                selectedProject = studentProject;
        }

        if (selectedProject == null) {
            throw new ProjectNotFoundUserException();
        }

        destination = new File(Utils.ASSIGNMENTS_FOLDER + File.separator + assignmentUploadRequest.getUsername());
        if (!destination.exists())
            destination.mkdirs();

        path = Utils.saveFile(assignmentUploadRequest.getAssignment(), destination.getAbsolutePath());

        selectedProject.setOutputLocation(path);
        selectedProject.setUploadDate(LocalDateTime.now());

        studentRepository.save(student);
    }

    public void reviewProject(GradeFeedbackAssignmentRequest gradeFeedback) throws StudentNotExistsException, NoProjectForStudentException {
        Optional<Student> optionalStudent;
        Student student;
        Project project = null;

        optionalStudent = studentRepository.findByUsername(gradeFeedback.getStudentName());
        if (!optionalStudent.isPresent()) {
            throw new StudentNotExistsException();
        }

        student = optionalStudent.get();
        for (Project studentProject : student.getProjects()) {
            if (studentProject.getUniqueId().equals(gradeFeedback.getProjectId()))
                project = studentProject;
        }

        if (project == null) {
            throw new NoProjectForStudentException();
        }

        project.setFeedback(gradeFeedback.getStudentFeedback());
        project.setGrade(gradeFeedback.getStudentGrade());

        emailService.sendMail(project);

        studentRepository.save(student);
    }

    public List<Project> getAllAssignedProjects(String owner) {
        List<Student> allStudents;
        List<Project> ownerAssignments;
        Optional<Student> optionalStudent;
        Student student;

        // Check if the owner is a student and return its assignments
        optionalStudent = studentRepository.findByUsername(owner);
        if (optionalStudent.isPresent()) {
            student = optionalStudent.get();
            return student.getProjects();
        }

        allStudents = studentRepository.findAll();
        ownerAssignments = new ArrayList<>();

        for (Student currentStudent : allStudents) {
            for (Project project : currentStudent.getProjects()) {
                if (project.getOwner().equals(owner))
                    ownerAssignments.add(project);
            }
        }

        return ownerAssignments;
    }

    public void deleteAssignment(String username, String assignmentId) throws StudentNotExistsException {
        Optional<Student> optionalStudent;
        Student student;

        optionalStudent = studentRepository.findByUsername(username);
        if (!optionalStudent.isPresent()) {
            throw new StudentNotExistsException();
        }

        student = optionalStudent.get();
        for (Project project : student.getProjects()) {
            if (project.getUniqueId().equals(assignmentId)) {
                student.getProjects().remove(project);
                break;
            }
        }
        studentRepository.save(student);
    }

    public String getReviewedStatistics(String uniqueId) throws ProjectNotFoundException {
        List<Student> students;
        Optional<Project> optionalProject;
        Project project;
        int passed = 0, failed = 0, totalNotReviewed = 0;
        JSONArray result, component;

        optionalProject = projectRepository.findByUniqueId(uniqueId);

        if (!optionalProject.isPresent()) {
            throw new ProjectNotFoundException();
        }

        project = optionalProject.get();
        students = studentRepository.findAll();

        result = new JSONArray();
        for (Student student : students) {
            if (student.getProjects() == null)
                continue;
            for (Project studentProject : student.getProjects()) {
                if (!studentProject.getProjectName().equals(project.getProjectName()))
                    continue;

                if (studentProject.getGrade() > 0 && studentProject.getFeedback() != null)
                    if (studentProject.getGrade() < 5)
                        failed++;
                    else
                        passed++;
                else
                    totalNotReviewed++;
            }
        }
        component = new JSONArray();
        component.put("Status");
        component.put("Total assignments");
        result.put(component);

        component = new JSONArray();
        component.put("Passed");
        component.put(passed);
        result.put(component);

        component = new JSONArray();
        component.put("Failed");
        component.put(failed);
        result.put(component);

        component = new JSONArray();
        component.put("Not reviewed");
        component.put(totalNotReviewed);
        result.put(component);

        return result.toString();
    }

    public String getDeadlineStatistics(String uniqueId) throws ProjectNotFoundException {
        List<Student> students;
        Optional<Project> optionalProject;
        Project project;
        int beforeDeadline = 0, afterDeadline = 0, notUploadedYet = 0;
        JSONArray result, component;

        optionalProject = projectRepository.findByUniqueId(uniqueId);

        if (!optionalProject.isPresent()) {
            throw new ProjectNotFoundException();
        }

        project = optionalProject.get();
        students = studentRepository.findAll();

        result = new JSONArray();
        for (Student student : students) {
            if (student.getProjects() == null)
                continue;
            for (Project studentProject : student.getProjects()) {
                if (!studentProject.getProjectName().equals(project.getProjectName()))
                    continue;
                if (studentProject.getDeadline() != null && studentProject.getUploadDate() != null) {
                    if (studentProject.getDeadline().isAfter(studentProject.getUploadDate()))
                        beforeDeadline++;
                    else
                        afterDeadline++;
                } else {
                    notUploadedYet++;
                }
            }
        }
        component = new JSONArray();
        component.put("Status");
        component.put("Total assignments");
        result.put(component);

        component = new JSONArray();
        component.put("Before deadline");
        component.put(beforeDeadline);
        result.put(component);

        component = new JSONArray();
        component.put("After deadline");
        component.put(afterDeadline);
        result.put(component);

        component = new JSONArray();
        component.put("Not uploaded");
        component.put(notUploadedYet);
        result.put(component);

        return result.toString();
    }

    public String getGradesStatistics(String uniqueId) throws ProjectNotFoundException {
        List<Student> students;
        Optional<Project> optionalProject;
        Project project;
        int grade;
        JSONArray result, component;
        Map<Integer, Integer> grades;

        optionalProject = projectRepository.findByUniqueId(uniqueId);

        if (!optionalProject.isPresent()) {
            throw new ProjectNotFoundException();
        }

        project = optionalProject.get();
        students = studentRepository.findAll();
        grades = new HashMap<>();
        for (int i = 0; i < 10; i++)
            grades.put(i + 1, 0);

        result = new JSONArray();
        for (Student student : students) {
            if (student.getProjects() == null)
                continue;
            for (Project studentProject : student.getProjects()) {
                if (!studentProject.getProjectName().equals(project.getProjectName()))
                    continue;

                if (studentProject.getGrade() > 0 && studentProject.getFeedback() != null) {
                    if (studentProject.getGrade() < 5) {
                        grade = (int) Math.floor(studentProject.getGrade());
                    } else {
                        grade = (int) Math.round(studentProject.getGrade());
                    }
                    grades.put(grade, grades.get(grade) + 1);
                }
            }
        }

        component = new JSONArray();
        component.put("Grades");
        component.put("Students");
        result.put(component);

        for (Map.Entry<Integer, Integer> set : grades.entrySet()) {
            component = new JSONArray();
            component.put(set.getKey());
            component.put(set.getValue());
            result.put(component);
        }

        return result.toString();
    }

    public void assignProjectToUser(String username, String uniqueId) throws StudentNotExistsException, ProjectNotFoundUserException, StudentAssignedProjectException {
        Project project;
        Optional<Project> optionalProject;
        Optional<Student> optionalStudent;
        Student student;

        optionalStudent = studentRepository.findByUsername(username);
        if (!optionalStudent.isPresent()) {
            throw new StudentNotExistsException();
        }

        student = optionalStudent.get();

        optionalProject = projectRepository.findByUniqueId(uniqueId);
        if (!optionalProject.isPresent()) {
            throw new ProjectNotFoundUserException();
        }
        project = optionalProject.get();

        for (Project activeProject : student.getProjects()) {
            if (activeProject.getUniqueId().equals(uniqueId)) {
                throw new StudentAssignedProjectException();
            }
        }

        project.setAssigned(LocalDateTime.now());
        project.setAssignee(student.getUsername());
        project.setStatus(EProjectStatus.ASSIGNED);
        project.setUniqueId(Utils.generateUniqueID());
        project.setAssigneeAddress(student.getEmailAddress());
        student.getProjects().add(project);

        studentRepository.save(student);
    }

    public void assignProjectToGroup(String groupName, String uniqueId) throws NoStudentsInGroupException, ProjectNotFoundException, StudentsGroupAlreadyAssignedException {
        Project project;
        Optional<Project> optionalProject;
        List<Student> students;
        int studentsAssigned = 0;
        boolean projectAssigned;

        students = studentRepository.findByGroup(groupName);
        if (students.size() == 0) {
            throw new NoStudentsInGroupException();
        }

        optionalProject = projectRepository.findByUniqueId(uniqueId);
        if (!optionalProject.isPresent()) {
            throw new ProjectNotFoundException();
        }

        project = optionalProject.get();

        for (Student student : students) {
            projectAssigned = false;
            for (Project activeProject : student.getProjects())
                if (activeProject.getUniqueId().equals(uniqueId))
                    projectAssigned = true;

            if (!projectAssigned) {
                studentsAssigned++;
                project.setAssigned(LocalDateTime.now());
                project.setAssigneeAddress(student.getEmailAddress());
                project.setAssignee(student.getUsername());
                project.setStatus(EProjectStatus.ASSIGNED);
                project.setUniqueId(Utils.generateUniqueID());
                student.getProjects().add(project);
                studentRepository.save(student);
            }
        }

        if (studentsAssigned == 0) {
            throw new StudentsGroupAlreadyAssignedException();
        }
    }

    public void addProjectTemplate(ProjectAddRequest projectAddRequest) throws CourseNotFoundException {
        Project project;
        Course course;
        Optional<Course> optionalCourse;
        DateTimeFormatter formatter;
        String inputTime;

        optionalCourse = courseRepository.findByUniqueId(projectAddRequest.getCourseUniqueId());
        if (!optionalCourse.isPresent()) {
            throw new CourseNotFoundException();
        }

        course = optionalCourse.get();

        project = new Project(projectAddRequest);
        project.setCourse(course);

        project.setOwner(projectAddRequest.getOwner());

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        inputTime = projectAddRequest.getInputDate() + " " + projectAddRequest.getInputTime();
        project.setDeadline(LocalDateTime.parse(inputTime, formatter));

        projectRepository.save(project);
    }

    public void updateProject(ProjectUpdateRequest projectUpdateRequest) throws ProjectNotFoundException, CourseNotFoundException {
        Project project;
        Optional<Project> optionalProject;
        Course course;
        Optional<Course> optionalCourse;
        DateTimeFormatter formatter;
        String inputTime;

        optionalProject = projectRepository.findByUniqueId(projectUpdateRequest.getUniqueId());
        if (!optionalProject.isPresent()) {
            throw new ProjectNotFoundException();
        }

        project = optionalProject.get();
        project.setProjectName(projectUpdateRequest.getProjectName());
        project.setDescription(projectUpdateRequest.getDescription());

        if (projectUpdateRequest.getCourseUniqueId() != null) {
            optionalCourse = courseRepository.findByUniqueId(projectUpdateRequest.getCourseUniqueId());
            if (!optionalCourse.isPresent()) {
                throw new CourseNotFoundException();
            }
            course = optionalCourse.get();
            project.setCourse(course);
        }

        if (projectUpdateRequest.getInputDate() != null && projectUpdateRequest.getInputTime() != null) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            inputTime = projectUpdateRequest.getInputDate() + " " + projectUpdateRequest.getInputTime();
            project.setDeadline(LocalDateTime.parse(inputTime, formatter));
        }

        projectRepository.save(project);
    }
}
