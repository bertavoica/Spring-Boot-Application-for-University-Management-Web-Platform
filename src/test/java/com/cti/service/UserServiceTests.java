package com.cti.service;

import com.cti.exception.UserNoLanguageException;
import com.cti.exception.UserNotFoundException;
import com.cti.exception.UsernameNotExistsException;
import com.cti.models.*;
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

import java.security.Principal;
import java.util.*;

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

    @Test
    @DisplayName("Update language preference successfully.")
    public void updateLanguagePreferenceTest() throws UserNotFoundException {
        User user = new User();
        user.setUsername("test");
        user.setLanguagePreference("romanian");

        Mockito.when(this.userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        this.userService.updateLanguagePreference("test", "english");

        Mockito.verify(this.userRepository, Mockito.times(1)).save(user);

        assertEquals("english", user.getLanguagePreference());
    }

    @Test
    @DisplayName("Update language preference successfully.")
    public void updateLanguagePreferenceWhenUserHasNoUsernameTest() throws UserNotFoundException {
        User user = new User();
        user.setUsername("test_1");
        user.setLanguagePreference("romanian");

        Mockito.when(this.userRepository.findByUsername("test")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> this.userService.updateLanguagePreference("test", "english"));
    }

    @Test
    @DisplayName("Update user rights when user is student.")
    public void updateUserRightsWhenUserIsStudent() throws UsernameNotExistsException {
        User user = new User();
        user.setUsername("test");
        user.setLanguagePreference("romanian");

        Role roleUser = new Role();
        roleUser.setName(ERole.ROLE_STUDENT);

        Set<Role> roleSet = new HashSet<>();
        roleSet.add(roleUser);
        user.setRoles(roleSet);


        Mockito.when(this.userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        Mockito.doNothing().when(this.studentRepository).deleteByUsername("test");

        User result = this.userService.updateUserRights(ERole.ROLE_TEACHER, "test");

        Mockito.verify(this.userRepository, Mockito.times(1)).save(user);

        assertEquals("test", result.getUsername());
        assertEquals(1, result.getRoles().size());
        assertEquals(roleSet, result.getRoles());
        assertEquals(ERole.ROLE_TEACHER, result.getRoles().iterator().next().getName());
    }

    @Test
    @DisplayName("Update user rights when user is student.")
    public void updateUserRightsWhenUserIsNotStudent() throws UsernameNotExistsException {
        User user = new User();
        user.setUsername("test");
        user.setLanguagePreference("romanian");

        Role roleUser = new Role();
        roleUser.setName(ERole.ROLE_TEACHER);

        Set<Role> roleSet = new HashSet<>();
        roleSet.add(roleUser);
        user.setRoles(roleSet);


        Mockito.when(this.userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        Mockito.doNothing().when(this.studentRepository).deleteByUsername("test");

        User result = this.userService.updateUserRights(ERole.ROLE_STUDENT, "test");

        Mockito.verify(this.userRepository, Mockito.times(1)).save(user);

        assertEquals("test", result.getUsername());
        assertEquals(1, result.getRoles().size());
        assertEquals(roleSet, result.getRoles());
        assertEquals(ERole.ROLE_STUDENT, result.getRoles().iterator().next().getName());
    }

    @Test
    @DisplayName("Get preferred language successfully.")
    public void getPreferredLanguageTest() {
        User user = new User();
        user.setUsername("test");
        user.setLanguagePreference("Romanian");

        Mockito.when(this.userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        ELanguage result = this.userService.getPreferredLanguage("test");

        assertEquals("ROMANIAN", result.name());
        assertNotNull(result);
    }

    @Test
    @DisplayName("Get preferred default language successfully.")
    public void getPreferredDefaultLanguageTest() {
        User user = new User();
        user.setUsername("test");

        Mockito.when(this.userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        ELanguage result = this.userService.getPreferredLanguage("test");

        assertEquals("ENGLISH", result.name());
        assertNotNull(result);
    }
    @Test
    @DisplayName("Get preferred default language when user not found successfully.")
    public void getPreferredDefaultLanguageWhenUserNotFoundTest() {
        Mockito.when(this.userRepository.findByUsername("test")).thenReturn(Optional.empty());

        ELanguage result = this.userService.getPreferredLanguage("test");

        assertEquals("ENGLISH", result.name());
        assertNotNull(result);
    }

    @Test
    @DisplayName("Get preferred language principal successfully.")
    public void getPreferredLanguagePrincipalTest() {
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "test";
            }
        };

        User user = new User();
        user.setUsername("test");
        user.setLanguagePreference("Romanian");

        Mockito.when(this.userRepository.findByUsername(principal.getName())).thenReturn(Optional.of(user));

        ELanguage result = this.userService.getPreferredLanguage("test");

        assertEquals("ROMANIAN", result.name());
        assertNotNull(result);
        assertEquals("test", principal.getName());
    }

    @Test
    @DisplayName("Get preferred default language principal successfully.")
    public void getPreferredDefaultLanguagePrincipalTest() {
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "test";
            }
        };

        User user = new User();
        user.setUsername("test");

        Mockito.when(this.userRepository.findByUsername(principal.getName())).thenReturn(Optional.of(user));

        ELanguage result = this.userService.getPreferredLanguage("test");

        assertEquals("ENGLISH", result.name());
        assertNotNull(result);
        assertEquals("test", principal.getName());
    }

    @Test
    @DisplayName("Get preferred default language principal with user not found successfully.")
    public void getPreferredDefaultLanguagePrincipalWithUserNotFoundTest() {
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "test";
            }
        };

        Mockito.when(this.userRepository.findByUsername(principal.getName())).thenReturn(Optional.empty());

        ELanguage result = this.userService.getPreferredLanguage("test");

        assertEquals("ENGLISH", result.name());
        assertNotNull(result);
        assertEquals("test", principal.getName());
    }
}
