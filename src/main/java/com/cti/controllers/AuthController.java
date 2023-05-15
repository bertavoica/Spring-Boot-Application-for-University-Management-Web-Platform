package com.cti.controllers;

import javax.validation.Valid;

import com.cti.exception.EmailAlreadyInUseException;
import com.cti.exception.UsernameAlreadyTakenException;
import com.cti.payload.request.LoginRequest;
import com.cti.payload.request.SignupRequest;
import com.cti.payload.response.MessageResponse;
import com.cti.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth-controller")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(this.authService.authenticateUser(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            this.authService.registerUser(signUpRequest);
            return ResponseEntity.ok(new MessageResponse("Student registered successfully!"));
        } catch (EmailAlreadyInUseException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        } catch (UsernameAlreadyTakenException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
    }
}