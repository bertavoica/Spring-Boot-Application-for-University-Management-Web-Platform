package com.cti.controllers;

import com.cti.models.Admin;
import com.cti.models.User;
import com.cti.payload.request.AdminUpdateRequest;
import com.cti.service.AdminService;
import com.google.gson.Gson;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.Principal;
import java.util.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private static final String URL = "/admin-controller";

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    public void getAllAdminsSuccessfully() throws Exception {
        String username = "username";

        Mockito.when(this.adminService.prepareAdminList(username)).thenReturn(List.of(new Admin(new User())));

        this.mockMvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", username))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteAdminSuccessfully() throws Exception {
        String username = "admin_username";

        Mockito.doNothing().when(this.adminService).deleteByUsername(username);

        mockMvc.perform(delete(URL)
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    public void updateAdminSuccessfully() throws Exception {
        AdminUpdateRequest adminUpdateRequest = new AdminUpdateRequest();
        adminUpdateRequest.setRole("role");
        adminUpdateRequest.setUsername("username");

        Mockito.when(this.adminService.updateAdmin(eq(adminUpdateRequest), Mockito.any(Principal.class))).thenReturn(List.of(new Admin(new User())));

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(adminUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}