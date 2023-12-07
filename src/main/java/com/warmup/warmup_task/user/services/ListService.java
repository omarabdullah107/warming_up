package com.warmup.warmup_task.user.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.warmup.warmup_task.user.model.UserRepository;
import com.warmup.warmup_task.user.model.User;
import com.warmup.warmup_task.exceptions.ResourceNotFoundException;
@Service
public class ListService {
    private final UserRepository userRepository;
    public ListService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }
    public List<User> getUsersByUsername(String username) throws ResourceNotFoundException {
        List<User> users = userRepository.findUsersByUsername(username);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
        return users;
    }

}
