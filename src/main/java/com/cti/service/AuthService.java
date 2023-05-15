package com.cti.service;

import com.cti.exception.EmailAlreadyInUseException;
import com.cti.exception.UsernameAlreadyTakenException;
import com.cti.models.ERole;
import com.cti.models.Role;
import com.cti.models.Student;
import com.cti.models.User;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            RoleRepository roleRepository,
            StudentRepository studentRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.encoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.studentRepository = studentRepository;
    }

    public JwtResponse authenticateUser(@Valid LoginRequest loginRequest) {
        String token;
        UserDetailsImpl user;
        List<String> roles;
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        token = jwtUtils.generateJwtToken(authentication);

        user = (UserDetailsImpl) authentication.getPrincipal();
        roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(token, user.getId(), user.getUsername(), user.getEmail(), roles, user.getLanguagePreference());
    }

    public void registerUser(@Valid SignupRequest signUpRequest) throws UsernameAlreadyTakenException, EmailAlreadyInUseException {
        Role role;
        Set<String> inputRoles;
        Set<Role> outputRoles;
        User user;
        Student student;

        if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
            throw new UsernameAlreadyTakenException();
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            throw new EmailAlreadyInUseException();
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
    }
}
