package com.cti.controllers;

import com.cti.exception.UserNoLanguageException;
import com.cti.exception.UserNotFoundException;
import com.cti.exception.UsernameNotExistsException;
import com.cti.models.ERole;
import com.cti.models.Role;
import com.cti.models.Student;
import com.cti.models.User;
import com.cti.repository.StudentRepository;
import com.cti.repository.UserRepository;
import com.cti.service.UserService;
import com.cti.utils.ApplicationConstants;
import com.cti.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user-controller")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRights(@RequestParam(name = "username", defaultValue = "") String username,
                                              @RequestParam(name = "role", defaultValue = "") ERole role) {
        try {
            return ResponseEntity.ok(this.userService.updateUserRights(role, username));
        } catch (UsernameNotExistsException e) {
            return ResponseEntity.badRequest().body("Error: User not found with username " + username);
        }
    }

    @PostMapping(value = "/language")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateLanguagePreference(@RequestParam(name = "username", defaultValue = "") String username,
                                                      @RequestParam(name = "language", defaultValue = "") String language,
                                                      Principal principal) {
        try {
            this.userService.updateLanguagePreference(username, language);
            return ResponseEntity.ok().body(Utils.languageDictionary.get(ApplicationConstants.UPDATED_LANGUAGE).get(userService.getPreferredLanguage(principal)) + " " + username);
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.USER_NOT_FOUND).get(userService.getPreferredLanguage(principal)) + " " + username);
        }
    }

    @GetMapping(value = "/language")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getLanguagePreference(@RequestParam(name = "username", defaultValue = "") String username,
                                                   Principal principal) {
        try {
            return ResponseEntity.ok().body(this.userService.getLanguagePreference(username));
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.USER_NOT_FOUND).get(userService.getPreferredLanguage(principal)) + " " + username);
        } catch (UserNoLanguageException e) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get(ApplicationConstants.USER_NO_LANGUAGE).get(userService.getPreferredLanguage(principal)));
        }
    }


    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(this.userService.getAllUsers());
    }
}
