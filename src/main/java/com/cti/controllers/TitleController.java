package com.cti.controllers;

import com.cti.models.Teacher;
import com.cti.models.Title;
import com.cti.payload.request.TitleAddRequest;
import com.cti.repository.TeacherRepository;
import com.cti.repository.TitleRepository;
import com.cti.service.UserService;
import com.cti.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/title-controller")
public class TitleController {

    @Autowired
    private TitleRepository titleRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserService userService;

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllTitles() {

        return ResponseEntity.ok(titleRepository.findAll());
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> addTitle(@Valid @RequestBody TitleAddRequest titleAddRequest,
                                      Principal principal) {
        Title title;

        if (titleRepository.findByName(titleAddRequest.getName()).isPresent()) {
            System.out.println("test");
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("TitleExists").get(userService.getPreferredLanguage(principal)) + " " + titleAddRequest.getName());
        }


        System.out.println("bhdsjd");
        title = new Title();
        title.setName(titleAddRequest.getName());
        titleRepository.save(title);

        System.out.println("dsbhg");

        return ResponseEntity.ok(Utils.languageDictionary.get("TitleAdded").get(userService.getPreferredLanguage(principal)) + " " + titleAddRequest.getName());
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTitle(@RequestParam(name = "name", defaultValue = "") String name) {

        List<Teacher> teachers;

        // Set as null as teacher titles that have this name
        teachers = teacherRepository.findByTitle(name);
        for (Teacher teacher : teachers) {
            teacher.setTitle(null);
            teacherRepository.save(teacher);
        }

        titleRepository.deleteByName(name);

        return ResponseEntity.ok(titleRepository.findAll());
    }
}
