package com.warmup.warmup_task.user.services;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.warmup.warmup_task.exceptions.UserNotFoundException;
import com.warmup.warmup_task.exceptions.InvalidPasswordException;
import com.warmup.warmup_task.user.model.UserRepository;
import com.warmup.warmup_task.user.model.User;

@Service
public class LoginService {
    private final UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(LoginService.class);
    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * A login method that takes two parameters username and password and checks if
     * this user in the database or not.
     */
    public User login(String username, String password) throws UserNotFoundException, InvalidPasswordException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!user.getPassword().equals(password)) {
            throw new InvalidPasswordException("Invalid password");
        }
        // update last login date
        user.setLastRegLoginDate(LocalDateTime.now());
        userRepository.save(user);

        logger.info("User " + username + " has logged in successfully.");
        return user;
    }
}
