package com.warmup.warmup_task.user.services;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.warmup.warmup_task.exceptions.UserNotFoundException;
import com.warmup.warmup_task.user.model.UserRepository;

@Service
public class DeleteService {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);
    public DeleteService(UserRepository userRepository){
        this.userRepository= userRepository;
    }
    @Transactional
    public void deleteUserByUsername(String username) throws UserNotFoundException {
        userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.deleteByUsername(username);
        logger.info("User " + username + " has been deleted successfully.");
    }
}