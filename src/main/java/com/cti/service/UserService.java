package com.cti.service;

import com.cti.exception.UserNoLanguageException;
import com.cti.exception.UserNotFoundException;
import com.cti.exception.UsernameNotExistsException;
import com.cti.models.*;
import com.cti.repository.StudentRepository;
import com.cti.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    public ELanguage getPreferredLanguage(Principal principal) {
        ELanguage result = ELanguage.ENGLISH;

        Optional<User> optionalUser;
        User user;

        optionalUser = userRepository.findByUsername(principal.getName());
        if (!optionalUser.isPresent()) {
            return result;
        }

        user = optionalUser.get();
        if (user.getLanguagePreference() == null)
            return result;
        if (user.getLanguagePreference().equals("Romanian"))
            return ELanguage.ROMANIAN;
        return ELanguage.ENGLISH;
    }

    public ELanguage getPreferredLanguage(String username) {
        ELanguage result = ELanguage.ENGLISH;

        Optional<User> optionalUser;
        User user;

        optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            return result;
        }

        user = optionalUser.get();
        if (user.getLanguagePreference() == null)
            return result;
        if (user.getLanguagePreference().equals("Romanian"))
            return ELanguage.ROMANIAN;
        return ELanguage.ENGLISH;
    }

    public User updateUserRights(ERole role, String username) throws UsernameNotExistsException {
        User user;
        List<Role> roles;
        Optional<User> optionalUser;
        optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new UsernameNotExistsException();
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

        return user;
    }

    public void updateLanguagePreference(String username, String language) throws UserNotFoundException {
        User user;
        Optional<User> optionalUser;

        optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException();
        }

        user = optionalUser.get();
        user.setLanguagePreference(language);
        userRepository.save(user);
    }

    public String getLanguagePreference(String username) throws UserNotFoundException, UserNoLanguageException {
        User user;
        Optional<User> optionalUser;

        optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException();
        }

        user = optionalUser.get();
        if (user.getLanguagePreference() == null) {
            throw new UserNoLanguageException();
        }

        return user.getLanguagePreference();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
