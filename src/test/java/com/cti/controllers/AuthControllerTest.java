package com.cti.controllers;

import com.cti.payload.request.LoginRequest;
import com.cti.payload.response.JwtResponse;
import com.cti.security.jwt.JwtUtils;
import com.cti.security.services.UserDetailsImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.testng.AssertJUnit.assertEquals;

@WebAppConfiguration
public class AuthControllerTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAuthenticateUser() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("username");
        loginRequest.setPassword("password");
        List<GrantedAuthority> authorities = new ArrayList<>();

        UserDetailsImpl userDetails = new UserDetailsImpl("1", "username", "email", "test", authorities, "en");

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        Mockito.when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())))
                .thenReturn(authentication);
        Mockito.when(jwtUtils.generateJwtToken(authentication)).thenReturn("token");

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        Mockito.verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        Mockito.verify(jwtUtils, times(1)).generateJwtToken(authentication);
        Mockito.verify(SecurityContextHolder.getContext(), times(1)).setAuthentication(authentication);

        assertEquals(response.getStatusCodeValue(), 200);
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals(jwtResponse.getTokenType(), "token");
        assertEquals(jwtResponse.getId(), 1L);
        assertEquals(jwtResponse.getUsername(), "username");
        assertEquals(jwtResponse.getEmail(), "email");
        assertEquals(jwtResponse.getRoles(), List.of("ROLE_USER"));
        assertEquals(jwtResponse.getLanguagePreference(), "language");
    }

}
