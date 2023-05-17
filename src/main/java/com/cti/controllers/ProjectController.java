package com.cti.controllers;

import com.cti.exception.*;
import com.cti.models.EProjectStatus;
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
import com.cti.service.EmailService;
import com.cti.service.ProjectService;
import com.cti.service.UserService;
import com.cti.utils.ApplicationConstants;
import com.cti.utils.Utils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/project-controller")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ProjectService projectService;

    @GetMapping(value = "/download")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> downloadProject(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                             @RequestParam(name = "studentName", defaultValue = "") String studentName,
                                             Principal principal) {
        try {
            File assignment = this.projectService.downloadProject(studentName, uniqueId);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + assignment.getName());
            headers.add("X-Suggested-Filename", assignment.getName());

            Path path = Paths.get(assignment.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(assignment.length())
                    .body(resource);
        } catch (StudentNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.STUDENT_NOT_EXISTS).get(userService.getPreferredLanguage(principal)) + " " + studentName);
        } catch (ProjectNotFoundUserException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.PROJECT_NOT_FOUND_USER).get(userService.getPreferredLanguage(principal)) + " " + studentName);
        } catch (IOException | ProjectNoAssignmentException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.ERROR_DOWNLOADING_ASSIGNMENT).get(userService.getPreferredLanguage(principal)));
        }
    }

    @PutMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadProject(@Valid @ModelAttribute AssignmentUploadRequest assignmentUploadRequest,
                                           Principal principal) throws IOException {
        try {
            this.projectService.uploadProject(assignmentUploadRequest);
            return ResponseEntity.ok(Utils.languageDictionary.get("ProjectUpdated").get(userService.getPreferredLanguage(principal)));
        } catch (StudentNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.STUDENT_NOT_EXISTS).get(userService.getPreferredLanguage(principal)) + " " + assignmentUploadRequest.getUsername());
        } catch (ProjectNotFoundUserException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.PROJECT_NOT_FOUND_USER).get(userService.getPreferredLanguage(principal)) + " " + assignmentUploadRequest.getUsername());
        }
    }


    @PutMapping(value = "/review")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> reviewProject(@Valid @RequestBody GradeFeedbackAssignmentRequest gradeFeedback,
                                           Principal principal) {
        try {
            this.projectService.reviewProject(gradeFeedback);
            return ResponseEntity.ok(Utils.languageDictionary.get("ProjectUpdated").get(userService.getPreferredLanguage(principal)));
        } catch (StudentNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.STUDENT_NOT_EXISTS).get(userService.getPreferredLanguage(principal)) + " " + gradeFeedback.getStudentName());
        } catch (NoProjectForStudentException e) {
            return ResponseEntity.badRequest().body((Utils.languageDictionary.get(ApplicationConstants.NO_PROJECT_FOR_STUDENT).get(userService.getPreferredLanguage(principal)) + " " + gradeFeedback.getStudentName()));
        }
    }

    @GetMapping(value = "/assignments")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllAssignedProjects(@RequestParam(name = "owner", defaultValue = "") String owner) {
        return ResponseEntity.ok(this.projectService.getAllAssignedProjects(owner));
    }

    @DeleteMapping(value = "/assignments")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteAssignment(@RequestParam(name = "assignmentId", defaultValue = "") String assignmentId,
                                              @RequestParam(name = "username", defaultValue = "") String username,
                                              Principal principal) {

        try {
            this.projectService.deleteAssignment(username, assignmentId);
            return ResponseEntity.ok(Utils.languageDictionary.get(ApplicationConstants.ASSIGNMENT_REMOVED).get(userService.getPreferredLanguage(principal)) + " " + username);
        } catch (StudentNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.STUDENT_NOT_EXISTS).get(userService.getPreferredLanguage(principal)) + " " + username);

        }
    }

    @PostMapping(value = "/assign-user")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> assignProjectToUser(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                                 @RequestParam(name = "username", defaultValue = "") String username,
                                                 Principal principal) {
        try {
            this.projectService.assignProjectToUser(username, uniqueId);
            return ResponseEntity.ok(Utils.languageDictionary.get(ApplicationConstants.ASSIGNED_PROJECT).get(userService.getPreferredLanguage(principal)) + " " + username);
        } catch (StudentNotExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.STUDENT_NOT_EXISTS).get(userService.getPreferredLanguage(principal)) + " " + username);
        } catch (ProjectNotFoundUserException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.PROJECT_NOT_FOUND_USER).get(userService.getPreferredLanguage(principal)));
        } catch (StudentAssignedProjectException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.STUDENT_ASSIGNED_PROJECT).get(userService.getPreferredLanguage(principal)));
        }
    }

    @PostMapping(value = "/assign-group")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> assignProjectToGroup(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                                  @RequestParam(name = "groupName", defaultValue = "") String groupName,
                                                  Principal principal) {
        Project project;
        Optional<Project> optionalProject;
        List<Student> students;
        int studentsAssigned = 0;
        boolean projectAssigned;

        students = studentRepository.findByGroup(groupName);
        if (students.size() == 0) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("NoStudentsInGroup").get(userService.getPreferredLanguage(principal)) + " " + groupName);
        }

        optionalProject = projectRepository.findByUniqueId(uniqueId);
        if (!optionalProject.isPresent()) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("ProjectNotFound").get(userService.getPreferredLanguage(principal)));
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
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("StudentsGroupAlreadyAssigned").get(userService.getPreferredLanguage(principal)) + " " + project.getProjectName());
        }

        return ResponseEntity.ok(Utils.languageDictionary.get("StudentsGroupAssigned").get(userService.getPreferredLanguage(principal)) + " " + studentsAssigned);
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> addProjectTemplate(@Valid @RequestBody ProjectAddRequest projectAddRequest,
                                                Principal principal) {
        Project project;
        Course course;
        Optional<Course> optionalCourse;
        DateTimeFormatter formatter;
        String inputTime;

        optionalCourse = courseRepository.findByUniqueId(projectAddRequest.getCourseUniqueId());
        if (!optionalCourse.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("CourseNotFound").get(userService.getPreferredLanguage(principal)));

        course = optionalCourse.get();

        project = new Project(projectAddRequest);
        project.setCourse(course);

        project.setOwner(projectAddRequest.getOwner());

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        inputTime = projectAddRequest.getInputDate() + " " + projectAddRequest.getInputTime();
        project.setDeadline(LocalDateTime.parse(inputTime, formatter));

        projectRepository.save(project);

        return ResponseEntity.ok(Utils.languageDictionary.get("ProjectTemplateAdded").get(userService.getPreferredLanguage(principal)) + " " + projectAddRequest.getProjectName());
    }

    @PutMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateProject(@Valid @RequestBody ProjectUpdateRequest projectUpdateRequest,
                                           Principal principal) {
        Project project;
        Optional<Project> optionalProject;
        Course course;
        Optional<Course> optionalCourse;
        DateTimeFormatter formatter;
        String inputTime;

        optionalProject = projectRepository.findByUniqueId(projectUpdateRequest.getUniqueId());
        if (!optionalProject.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("ProjectNotFound").get(userService.getPreferredLanguage(principal)));

        project = optionalProject.get();
        project.setProjectName(projectUpdateRequest.getProjectName());
        project.setDescription(projectUpdateRequest.getDescription());

        if (projectUpdateRequest.getCourseUniqueId() != null) {
            optionalCourse = courseRepository.findByUniqueId(projectUpdateRequest.getCourseUniqueId());
            if (!optionalCourse.isPresent())
                return ResponseEntity.badRequest().body(Utils.languageDictionary.get("CourseNotFound").get(userService.getPreferredLanguage(principal)));
            course = optionalCourse.get();
            project.setCourse(course);
        }

        if (projectUpdateRequest.getInputDate() != null && projectUpdateRequest.getInputTime() != null) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            inputTime = projectUpdateRequest.getInputDate() + " " + projectUpdateRequest.getInputTime();
            project.setDeadline(LocalDateTime.parse(inputTime, formatter));
        }

        projectRepository.save(project);

        return ResponseEntity.ok(Utils.languageDictionary.get("ProjectTemplateUpdated").get(userService.getPreferredLanguage(principal)));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteProject(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                           Principal principal) {

        projectRepository.deleteByUniqueId(uniqueId);

        return ResponseEntity.ok(Utils.languageDictionary.get("ProjectTemplateDeleted").get(userService.getPreferredLanguage(principal)));
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllProjects(@RequestParam(name = "owner", defaultValue = "", required = false) String owner) {
        if (owner.equals(""))
            return ResponseEntity.ok(projectRepository.findAll());
        else
            return ResponseEntity.ok(projectRepository.findByOwner(owner));
    }

    @GetMapping(value = "/statistics/reviewed")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getReviewedStatistics(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                                   Principal principal) {
        try {
            String result = this.projectService.getReviewedStatistics(uniqueId);
            return ResponseEntity.ok(result);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.PROJECT_NOT_FOUND).get(userService.getPreferredLanguage(principal)));
        }
    }

    @GetMapping(value = "/statistics/deadline")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getDeadlineStatistics(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                                   Principal principal) {
        try {
            String result = this.projectService.getDeadlineStatistics(uniqueId);
            return ResponseEntity.ok(result);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.PROJECT_NOT_FOUND).get(userService.getPreferredLanguage(principal)));
        }
    }

    @GetMapping(value = "/statistics/grades")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getGradesStatistics(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                                 Principal principal) throws IOException {
        try {
            String result = this.projectService.getGradesStatistics(uniqueId);
            return ResponseEntity.ok(result);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.PROJECT_NOT_FOUND).get(userService.getPreferredLanguage(principal)));
        }
    }
}

