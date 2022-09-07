package com.cti.controllers;

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
import com.cti.service.UserService;
import com.cti.service.Utils;
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

    @GetMapping(value = "/download")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> downloadProject(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                             @RequestParam(name = "studentName", defaultValue = "") String studentName,
                                             Principal principal) throws IOException {

        Optional<Student> optionalStudent;
        Student student;
        File assignment;
        Project selectedProject = null;
        String filename;

        optionalStudent = studentRepository.findByUsername(studentName);

        if (!optionalStudent.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("StudentNotExist").get(userService.getPreferredLanguage(principal)) + " " + studentName);

        student = optionalStudent.get();
        for (Project studentProject : student.getProjects()) {
            if (studentProject.getUniqueId().equals(uniqueId))
                selectedProject = studentProject;
        }
        if (selectedProject == null)
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("ProjectNotFoundUser").get(userService.getPreferredLanguage(principal)) + " " + studentName);

        if (selectedProject.getOutputLocation() == null)
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("ProjectNoAssignment").get(userService.getPreferredLanguage(principal)));

        assignment = new File(selectedProject.getOutputLocation());
        if (!assignment.exists())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("ProjectNoAssignment").get(userService.getPreferredLanguage(principal)));

        try {
            filename = assignment.getName();
            ByteArrayInputStream in = new ByteArrayInputStream(Files.readAllBytes(assignment.toPath()));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + filename);
            headers.add("X-Suggested-Filename", filename);

            Path path = Paths.get(assignment.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(assignment.length())
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body(Utils.languageDictionary.get("ErrorDownloadingAssignment").get(userService.getPreferredLanguage(principal)));
    }

    @PutMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadProject(@Valid @ModelAttribute AssignmentUploadRequest assignmentUploadRequest,
                                           Principal principal) throws IOException {

        String path;
        File destination;
        Optional<Student> optionalStudent;
        Student student;
        Project selectedProject = null;



        optionalStudent = studentRepository.findByUsername(assignmentUploadRequest.getUsername());
        if (!optionalStudent.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("StudentNotExists").get(userService.getPreferredLanguage(principal)) + " " + assignmentUploadRequest.getUsername());

        student = optionalStudent.get();

        for (Project studentProject : student.getProjects()) {
            if (studentProject.getUniqueId().equals(assignmentUploadRequest.getProjectId()))
                selectedProject = studentProject;
        }

        if (selectedProject == null)
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("ProjectNotFoundUser").get(userService.getPreferredLanguage(principal)) + " " + assignmentUploadRequest.getUsername());

        destination = new File(Utils.ASSIGNMENTS_FOLDER + File.separator + assignmentUploadRequest.getUsername());
        if (!destination.exists())
            destination.mkdirs();

        path = Utils.saveFile(assignmentUploadRequest.getAssignment(), destination.getAbsolutePath());

        selectedProject.setOutputLocation(path);
        selectedProject.setUploadDate(LocalDateTime.now());

        studentRepository.save(student);

        return ResponseEntity.ok(Utils.languageDictionary.get("ProjectUpdated").get(userService.getPreferredLanguage(principal)));
    }


    @PutMapping(value = "/review")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> reviewProject(@Valid @RequestBody GradeFeedbackAssignmentRequest gradeFeedback,
                                           Principal principal) {

        Optional<Student> optionalStudent;
        Student student;
        Project project = null;

        optionalStudent = studentRepository.findByUsername(gradeFeedback.getStudentName());
        if (!optionalStudent.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("StudentNotExists").get(userService.getPreferredLanguage(principal)) + " " + gradeFeedback.getStudentName());

        student = optionalStudent.get();
        for (Project studentProject : student.getProjects()) {
            if (studentProject.getUniqueId().equals(gradeFeedback.getProjectId()))
                project = studentProject;
        }

        if (project == null)
            return ResponseEntity.badRequest().body((Utils.languageDictionary.get("NoProjectForStudent").get(userService.getPreferredLanguage(principal)) + " " + gradeFeedback.getStudentName()));

        project.setFeedback(gradeFeedback.getStudentFeedback());
        project.setGrade(gradeFeedback.getStudentGrade());

        emailService.sendMail(project);

        studentRepository.save(student);

        return ResponseEntity.ok(Utils.languageDictionary.get("ProjectUpdated").get(userService.getPreferredLanguage(principal)));
    }

    @GetMapping(value = "/assignments")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllAssignedProjects(@RequestParam(name = "owner", defaultValue = "") String owner) {
        List<Student> allStudents;
        List<Project> ownerAssignments;
        Optional<Student> optionalStudent;
        Student student;

        // Check if the owner is a student and return its assignments
        optionalStudent = studentRepository.findByUsername(owner);
        if (optionalStudent.isPresent()) {
            student = optionalStudent.get();
            return ResponseEntity.ok(student.getProjects());
        }

        allStudents = studentRepository.findAll();
        ownerAssignments = new ArrayList<>();

        for (Student currentStudent : allStudents) {
            for (Project project : currentStudent.getProjects()) {
                if (project.getOwner().equals(owner))
                    ownerAssignments.add(project);
            }
        }

        return ResponseEntity.ok(ownerAssignments);
    }

    @DeleteMapping(value = "/assignments")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteAssignment(@RequestParam(name = "assignmentId", defaultValue = "") String assignmentId,
                                              @RequestParam(name = "username", defaultValue = "") String username,
                                              Principal principal) {

        Optional<Student> optionalStudent;
        Student student;

        optionalStudent = studentRepository.findByUsername(username);
        if (!optionalStudent.isPresent()) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("StudentNotExists").get(userService.getPreferredLanguage(principal)) + " " + username);
        }

        student = optionalStudent.get();
        for (Project project : student.getProjects()) {
            if (project.getUniqueId().equals(assignmentId)) {
                student.getProjects().remove(project);
                break;
            }
        }
        studentRepository.save(student);

        return ResponseEntity.ok(Utils.languageDictionary.get("AssignmentRemoved").get(userService.getPreferredLanguage(principal)) + " " + username);
    }

    @PostMapping(value = "/assign-user")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> assignProjectToUser(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                                 @RequestParam(name = "username", defaultValue = "") String username,
                                                 Principal principal) {
        Project project;
        Optional<Project> optionalProject;
        Optional<Student> optionalStudent;
        Student student;

        optionalStudent = studentRepository.findByUsername(username);
        if (!optionalStudent.isPresent()) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("StudentNotExists").get(userService.getPreferredLanguage(principal)) + " " + username);
        }

        student = optionalStudent.get();

        optionalProject = projectRepository.findByUniqueId(uniqueId);
        if (!optionalProject.isPresent()) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("ProjectNotFoundUser").get(userService.getPreferredLanguage(principal)));
        }
        project = optionalProject.get();

        for (Project activeProject : student.getProjects()) {
            if (activeProject.getUniqueId().equals(uniqueId))
                return ResponseEntity.badRequest().body(Utils.languageDictionary.get("StudentAssignedProject").get(userService.getPreferredLanguage(principal)));
        }

        project.setAssigned(LocalDateTime.now());
        project.setAssignee(student.getUsername());
        project.setStatus(EProjectStatus.ASSIGNED);
        project.setUniqueId(Utils.generateUniqueID());
        project.setAssigneeAddress(student.getEmailAddress());
        student.getProjects().add(project);

        studentRepository.save(student);

        return ResponseEntity.ok(Utils.languageDictionary.get("AssignedProject").get(userService.getPreferredLanguage(principal)) + " " + username);
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
                                                   Principal principal) throws IOException {
        List<Student> students;
        Optional<Project> optionalProject;
        Project project;
        int passed = 0, failed = 0, totalNotReviewed = 0;
        JSONArray result, component;

        optionalProject = projectRepository.findByUniqueId(uniqueId);

        if (!optionalProject.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("ProjectNotFound").get(userService.getPreferredLanguage(principal)));

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

        return ResponseEntity.ok(result.toString());
    }

    @GetMapping(value = "/statistics/deadline")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getDeadlineStatistics(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                                   Principal principal) throws IOException {
        List<Student> students;
        Optional<Project> optionalProject;
        Project project;
        int beforeDeadline = 0, afterDeadline = 0, notUploadedYet = 0;
        JSONArray result, component;

        optionalProject = projectRepository.findByUniqueId(uniqueId);

        if (!optionalProject.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("ProjectNotFound").get(userService.getPreferredLanguage(principal)));

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

        return ResponseEntity.ok(result.toString());
    }

    @GetMapping(value = "/statistics/grades")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getGradesStatistics(@RequestParam(name = "uniqueId", defaultValue = "") String uniqueId,
                                                 Principal principal) throws IOException {
        List<Student> students;
        Optional<Project> optionalProject;
        Project project;
        int grade;
        JSONArray result, component;
        Map<Integer, Integer> grades;

        optionalProject = projectRepository.findByUniqueId(uniqueId);

        if (!optionalProject.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("ProjectNotFound").get(userService.getPreferredLanguage(principal)));

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

        return ResponseEntity.ok(result.toString());
    }
}

