package com.cti.service;

import com.cti.models.ELanguage;
import com.cti.models.User;
import com.cti.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
}
