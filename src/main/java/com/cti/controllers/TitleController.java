package com.cti.controllers;

import com.cti.exception.TitleExistsException;
import com.cti.models.Teacher;
import com.cti.models.Title;
import com.cti.payload.request.TitleAddRequest;
import com.cti.repository.TeacherRepository;
import com.cti.repository.TitleRepository;
import com.cti.service.TitleService;
import com.cti.service.UserService;
import com.cti.utils.ApplicationConstants;
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
    private TitleService titleService;

    @Autowired
    private UserService userService;

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllTitles() {

        return ResponseEntity.ok(this.titleService.getAllTitles());
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> addTitle(@Valid @RequestBody TitleAddRequest titleAddRequest,
                                      Principal principal) {
        try {
            return ResponseEntity.ok(this.titleService.addTitle(titleAddRequest));
        } catch (TitleExistsException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.TITLE_EXISTS).get(userService.getPreferredLanguage(principal)) + " " + titleAddRequest.getName());
        }
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTitle(@RequestParam(name = "name", defaultValue = "") String name) {
        return ResponseEntity.ok(this.titleService.deleteTitle(name));
    }
}
