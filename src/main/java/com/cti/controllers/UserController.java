package com.cti.controllers;

import com.cti.models.ERole;
import com.cti.models.Role;
import com.cti.models.Student;
import com.cti.models.User;
import com.cti.repository.StudentRepository;
import com.cti.repository.UserRepository;
import com.cti.service.UserService;
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
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserService userService;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRights(@RequestParam(name = "username", defaultValue = "") String username,
                                              @RequestParam(name = "role", defaultValue = "") ERole role) {
        User user;
        List<Role> roles;
        Optional<User> optionalUser;
        optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.badRequest().body("Error: User not found with username " + username);
        }

        user = optionalUser.get();
        if (user.getRoles().size() == 1) {
            roles = new ArrayList<>(user.getRoles());
            if (roles.get(0).getName().equals(ERole.ROLE_STUDENT) && !role.equals(ERole.ROLE_STUDENT)) {
                studentRepository.deleteByUsername(user.getUsername());
            }
            if (!roles.get(0).getName().equals(ERole.ROLE_STUDENT) && role.equals(ERole.ROLE_STUDENT))
                studentRepository.save(new Student(user.getUsername(), user.getEmail()));

        }
        user.getRoles().clear();
        user.getRoles().add(new Role(role));

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    @PostMapping(value = "/language")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateLanguagePreference(@RequestParam(name = "username", defaultValue = "") String username,
                                                      @RequestParam(name = "language", defaultValue = "") String language,
                                                      Principal principal) {
        User user;
        Optional<User> optionalUser;

        ;
        optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("UserNotFound").get(userService.getPreferredLanguage(principal)) + " " + username);
        }

        user = optionalUser.get();
        user.setLanguagePreference(language);
        userRepository.save(user);

        return ResponseEntity.ok().body(Utils.languageDictionary.get("UpdatedLanguage").get(userService.getPreferredLanguage(principal)) + " " + username);
    }

    @GetMapping(value = "/language")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getLanguagePreference(@RequestParam(name = "username", defaultValue = "") String username,
                                                   Principal principal) {
        User user;
        Optional<User> optionalUser;

        optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent())
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("UserNotFound").get(userService.getPreferredLanguage(principal)) + " " + username);

        user = optionalUser.get();
        if (user.getLanguagePreference() == null)
            return ResponseEntity.badRequest().body(Utils.languageDictionary.get("UserNoLanguage").get(userService.getPreferredLanguage(principal)));

        return ResponseEntity.ok().body(user.getLanguagePreference());
    }


    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
