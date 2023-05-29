package com.cti.service;

import com.cti.exception.UserNoLanguageException;
import com.cti.exception.UserNotFoundException;
import com.cti.models.Title;
import com.cti.models.User;
import com.cti.repository.StudentRepository;
import com.cti.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTests {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentRepository studentRepository;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    @DisplayName("Get all users successfully.")
    public void getAllUsersTest() {
        Mockito.when(this.userRepository.findAll()).thenReturn(List.of(new User(), new User()));

        List<User> result = this.userService.getAllUsers();

        assertEquals(2, result.size());
    }


    @Test
    @DisplayName("Get language preference successfully.")
    public void getLanguagePreferenceTest() throws UserNotFoundException, UserNoLanguageException {
        User user = new User();
        user.setUsername("test");
        user.setLanguagePreference("romanian");

        Mockito.when(this.userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        String result = this.userService.getLanguagePreference("test");

        assertEquals("romanian", result);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Get language preference when user not exists.")
    public void getLanguagePreferenceWhenUserNotExistsTest() {
        User user = new User();
        user.setUsername("test_1");
        user.setLanguagePreference("romanian");

        Mockito.when(this.userRepository.findByUsername("test")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> this.userService.getLanguagePreference("test"));
    }

    @Test
    @DisplayName("Get language preference when user has no language successfully.")
    public void getLanguagePreferenceWhenUserHasNoLanguageTest() {
        User user = new User();
        user.setUsername("test");
        user.setLanguagePreference(null);

        Mockito.when(this.userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        assertThrows(UserNoLanguageException.class, () -> this.userService.getLanguagePreference("test"));

    }
}
