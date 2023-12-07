package com.warmup.warmup_task.user.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.warmup.warmup_task.exceptions.InvalidPasswordException;
import com.warmup.warmup_task.exceptions.ResourceNotFoundException;
import com.warmup.warmup_task.exceptions.UsernameAlreadyExistsException;
import com.warmup.warmup_task.exceptions.UserNotFoundException;
import com.warmup.warmup_task.user.model.User;
import com.warmup.warmup_task.user.services.LoginService;
import com.warmup.warmup_task.user.services.RegisterService;
import com.warmup.warmup_task.user.services.ListService;
import com.warmup.warmup_task.user.services.DeactivateService;
import com.warmup.warmup_task.user.services.DeleteService;


@Controller
@RequestMapping(path="/demo")
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private final ListService userService;
    private final RegisterService registerService;
    private final LoginService loginService;
    private final DeactivateService deactivateService;
    private final DeleteService deleteService;

    /**
     * Using constructor injection method to
     * make the code more robust.
     */
    public MainController(ListService userService, RegisterService registerService, LoginService loginService,
                          DeactivateService deactivateService, DeleteService deleteService) {
        this.userService = userService;
        this.registerService = registerService;
        this.loginService = loginService;
        this.deactivateService = deactivateService;
        this.deleteService = deleteService;
    }

    /**
     * Registering users using JSON object and recording
     * them into the DB and returning JSON object as an output.
     */
    @PostMapping(path = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<User> register(@RequestBody User user) {
        try {
            User savedUser = registerService.register(user);
            return ResponseEntity.ok(savedUser);
        } catch (UsernameAlreadyExistsException e) {
            logger.error("Error occurred while registering user with username " + user.getUsername(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Logging users and checking if they are already registered or not.
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        try {
            user = loginService.login(user.getUsername(), user.getPassword());
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException | InvalidPasswordException e) {
            logger.error("Error occurred while logging in user with username " + user.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    /**
     * Implementing the Get API method that will list all the users in the database + using authentication.
     */

    //Getting list of users
    @GetMapping("/list")
    public ResponseEntity getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    //Listing users by username
    @GetMapping("/list/{username}")
    public ResponseEntity<?> getUsersByUsername(@PathVariable String username) {
        try {
            List<User> users = userService.getUsersByUsername(username);
            return ResponseEntity.ok(users);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * implementing an api that will deactivate user status by username with jwt token authentication.
     */

    @PutMapping("/{username}/deactivate")
    public ResponseEntity<User> deactivateUser(@PathVariable String username) {
        try{
            User user = deactivateService.deactivateUser(username);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e){
            logger.error("User not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    /**
     * implementing an api for deleting a user by username.
     */

    @DeleteMapping("/{username}/delete")
    public ResponseEntity<String> deleteUserByUsername(@PathVariable String username) {
        try{
            deleteService.deleteUserByUsername(username);
            return ResponseEntity.ok("User deleted successfully");
        } catch(UserNotFoundException e){
            logger.error("User not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
