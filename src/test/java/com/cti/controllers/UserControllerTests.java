package com.cti.controllers;

import com.cti.exception.UserNoLanguageException;
import com.cti.exception.UserNotFoundException;
import com.cti.exception.UsernameNotExistsException;
import com.cti.models.*;
import com.cti.service.TitleService;
import com.cti.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.AssertJUnit.assertTrue;

public class UserControllerTests {
    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;


    @Mock
    private UserService userService;

    private static final String URL = "/user-controller";

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void updateUserRightsWhenUserIsStudentTest() throws Exception {
        String username = "test";

        Role roleUser = new Role();
        roleUser.setName(ERole.ROLE_STUDENT);

        Set<Role> roleSet = new HashSet<>();
        roleSet.add(roleUser);

        User user = new User();
        user.setUsername(username);
        user.setLanguagePreference("romanian");
        user.setRoles(roleSet);

        Mockito.when(this.userService.updateUserRights(ERole.ROLE_TEACHER, username)).thenReturn(user);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL)
                .param("username", username)
                .param("role", String.valueOf(ERole.ROLE_TEACHER))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("{\"id\":null,\"username\":\"test\",\"email\":null,\"password\":null,\"languagePreference\":\"romanian\",\"roles\":[{\"id\":null,\"name\":\"ROLE_STUDENT\"}]}"));
    }

    @Test
    public void updateUserRightsWhenUserIsNotStudentTest() throws Exception {
        String username = "test";

        Role roleUser = new Role();
        roleUser.setName(ERole.ROLE_TEACHER);

        Set<Role> roleSet = new HashSet<>();
        roleSet.add(roleUser);

        User user = new User();
        user.setUsername(username);
        user.setLanguagePreference("romanian");
        user.setRoles(roleSet);

        Mockito.when(this.userService.updateUserRights(ERole.ROLE_STUDENT, username)).thenReturn(user);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL)
                .param("username", username)
                .param("role", String.valueOf(ERole.ROLE_STUDENT))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("{\"id\":null,\"username\":\"test\",\"email\":null,\"password\":null,\"languagePreference\":\"romanian\",\"roles\":[{\"id\":null,\"name\":\"ROLE_TEACHER\"}]}"));
    }

    @Test
    public void updateUserRightsWhenUsernameNotExistsTest() throws Exception {
        String username = "test";

        Mockito.doThrow(UsernameNotExistsException.class).when(this.userService).updateUserRights(ERole.ROLE_STUDENT, username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL)
                .param("username", username)
                .param("role", String.valueOf(ERole.ROLE_STUDENT))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Error: User not found with username test"));
    }

    @Test
    public void updateLanguagePreferenceTest() throws Exception {
        String username = "test";
        String language = ELanguage.ROMANIAN.toString();

        Principal principal = Mockito.mock(Principal.class);

        Mockito.doNothing().when(this.userService).updateLanguagePreference(username, language);
        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ROMANIAN);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL + "/language")
                .param("username", username)
                .param("language", language)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Actualizare cu succes a limbii pentru test"));
    }

    @Test
    public void updateLanguagePreferenceWhenUserNotFoundTest() throws Exception {
        String username = "test";
        String language = ELanguage.ROMANIAN.toString();

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ROMANIAN);
        Mockito.doThrow(UserNotFoundException.class).when(this.userService).updateLanguagePreference(username, language);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL + "/language")
                .param("username", username)
                .param("language", language)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Error: User not found with username test"));
    }

    @Test
    public void getLanguagePreferenceTest() throws Exception {
        String username = "test";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ROMANIAN);
        Mockito.when(this.userService.getLanguagePreference(username)).thenReturn(String.valueOf(ELanguage.ROMANIAN));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL + "/language")
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("ROMANIAN"));
    }

    @Test
    public void getLanguagePreferenceWhenUserNotFoundTest() throws Exception {
        String username = "test";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ROMANIAN);
        Mockito.doThrow(UserNotFoundException.class).when(this.userService).getLanguagePreference(username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL + "/language")
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Error: User not found with username test"));
    }

    @Test
    public void getLanguagePreferenceWhenUserNoLanguageTest() throws Exception {
        String username = "test";

        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(this.userService.getPreferredLanguage(principal)).thenReturn(ELanguage.ROMANIAN);
        Mockito.doThrow(UserNoLanguageException.class).when(this.userService).getLanguagePreference(username);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL + "/language")
                .param("username", username)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("Utilizatorul nu are setata o limba preferata"));
    }

    @Test
    public void getAllUsersTest() throws Exception {
        User user = new User();
        user.setUsername("test");

        Mockito.when(this.userService.getAllUsers()).thenReturn(List.of(user));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[{\"id\":null,\"username\":\"test\",\"email\":null,\"password\":null,\"languagePreference\":\"English\",\"roles\":[]}]"));
    }


}
