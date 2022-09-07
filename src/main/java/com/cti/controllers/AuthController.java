package com.cti.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.cti.models.*;
import com.cti.payload.request.LoginRequest;
import com.cti.payload.request.SignupRequest;
import com.cti.payload.response.JwtResponse;
import com.cti.payload.response.MessageResponse;
import com.cti.repository.RoleRepository;
import com.cti.repository.StudentRepository;
import com.cti.repository.UserRepository;
import com.cti.security.jwt.JwtUtils;
import com.cti.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth-controller")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private StudentRepository studentRepository;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        String token;
        UserDetailsImpl user;
        List<String> roles;
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        token = jwtUtils.generateJwtToken(authentication);

        user = (UserDetailsImpl) authentication.getPrincipal();
        roles = user.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(token, user.getId(), user.getUsername(), user.getEmail(), roles, user.getLanguagePreference()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        Role role;
        Set<String> inputRoles;
        Set<Role> outputRoles;
        User user;
        Student student;

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        inputRoles = signUpRequest.getRole();
        outputRoles = new HashSet<>();

        if (inputRoles == null) {
            role = roleRepository.findByName(ERole.ROLE_STUDENT)
                    .orElseThrow(() -> new RuntimeException("Role is not found"));
            outputRoles.add(role);
        } else {
            for (String inputRole : inputRoles) {
                if (inputRole.equals("teacher")) {
                    role = roleRepository.findByName(ERole.ROLE_TEACHER)
                            .orElseThrow(() -> new RuntimeException("Role is not found"));
                    outputRoles.add(role);
                } else if (inputRole.equals("admin")) {
                    role = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Role is not found"));
                    outputRoles.add(role);
                } else {
                    role = roleRepository.findByName(ERole.ROLE_STUDENT)
                            .orElseThrow(() -> new RuntimeException("Role is not found"));
                    outputRoles.add(role);
                }
            }
        }

        // Create a new student instance without external info
        student = new Student();
        student.setEmailAddress(user.getEmail());
        student.setUsername(user.getUsername());
        studentRepository.save(student);

        user.setRoles(outputRoles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Student registered successfully!"));
    }
}