package com.warmup.warmup_task.user.services;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.warmup.warmup_task.exceptions.UserNotFoundException;
import com.warmup.warmup_task.user.model.UserRepository;
import com.warmup.warmup_task.user.model.User;


@Service
public class DeactivateService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    public DeactivateService(UserRepository userRepository){
        this.userRepository= userRepository;
    }


    /**
     * Noraml deactivating user.
     */
//    public User deactivateUser(String username) throws UserNotFoundException {
//        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
//        user.setStatus("Inactive");
//        logger.info("User " + username + " has been inactivated successfully.");
//        return userRepository.save(user);
//    }

    /**
     * Auto deactivating user.
     */
    public User deactivateUser(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user.getLastRegLoginDate() != null && user.getLastRegLoginDate().isBefore(LocalDate.now().minusMonths(1).atStartOfDay())) {
            user.setStatus("Inactive");
            userRepository.save(user);
            logger.info("User " + username + " has been auto-inactivated successfully.");
        }
        return user;
    }


}
