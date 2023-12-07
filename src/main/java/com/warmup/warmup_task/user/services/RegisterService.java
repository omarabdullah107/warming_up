package com.warmup.warmup_task.user.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.warmup.warmup_task.exceptions.UsernameAlreadyExistsException;
import com.warmup.warmup_task.user.model.User;
import com.warmup.warmup_task.user.model.UserRepository;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);
    public RegisterService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    /**
     * A register method that register users and checks if this user is already existing or not.
     */
    public User register(User user) throws UsernameAlreadyExistsException {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        // Set the current Datetime
        user.setLastRegLoginDate(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        logger.info("User with username " + savedUser.getUsername() + " has been registered.");
        return savedUser;
    }
}
