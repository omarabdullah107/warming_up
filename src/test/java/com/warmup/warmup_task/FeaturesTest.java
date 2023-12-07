package com.warmup.warmup_task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.warmup.warmup_task.jwt.config.JwtRequestFilter;
import com.warmup.warmup_task.jwt.config.JwtTokenUtil;
import com.warmup.warmup_task.jwt.config.WebSecurityConfig;
import com.warmup.warmup_task.jwt.service.JwtUserDetailsService;
import com.warmup.warmup_task.exceptions.UsernameAlreadyExistsException;
import com.warmup.warmup_task.exceptions.UserNotFoundException;
import com.warmup.warmup_task.exceptions.InvalidPasswordException;
import com.warmup.warmup_task.user.services.LoginService;
import com.warmup.warmup_task.user.services.RegisterService;
import com.warmup.warmup_task.user.model.User;
import com.warmup.warmup_task.user.model.UserRepository;
import com.warmup.warmup_task.jwt.config.JwtAuthenticationEntryPoint;
import com.warmup.warmup_task.jwt.controller.JwtAuthenticationController;
import com.warmup.warmup_task.jwt.model.JwtResponse;
import com.warmup.warmup_task.jwt.model.JwtRequest;
import com.warmup.warmup_task.user.services.ListService;
import com.warmup.warmup_task.exceptions.ResourceNotFoundException;
import com.warmup.warmup_task.user.services.DeactivateService;
import com.warmup.warmup_task.user.services.DeleteService;


@ExtendWith(MockitoExtension.class)
class FeaturesTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUserDetailsService jwtUserDetailsService;
    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private UserDetails userDetails;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private RegisterService registerService;
    @InjectMocks
    private LoginService loginService;
    @InjectMocks
    private ListService listService;
    @InjectMocks
    private DeactivateService deactivateService;
    @InjectMocks
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @InjectMocks
    private DeleteService deleteService;
    private static final String USERNAME = "username";
    private static final String SECRET = "secret";
    private final JwtAuthenticationController jwtAuthenticationController =
            new JwtAuthenticationController(authenticationManager, jwtTokenUtil, jwtUserDetailsService);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(userDetails.getUsername()).thenReturn(USERNAME);
    }


    @Test
    public void registerNewUserTest() throws UsernameAlreadyExistsException {
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@john.com");
        user.setPassword("password123");

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        User savedUser = registerService.register(user);

        assertNotNull(savedUser);
        assertEquals(user.getUsername(), savedUser.getUsername());
    }

    @Test
    public void registerExistingUserTest() {
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@john.com");
        user.setPassword("password123");

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        assertThrows(UsernameAlreadyExistsException.class, () -> registerService.register(user));
    }

    @Test
    public void loginSuccessTest() throws UserNotFoundException, InvalidPasswordException {
        String username = "john_doe";
        String password = "password123";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        User result = loginService.login(username, password);

        assertEquals(user, result);
    }

    @Test()
    public void loginUserNotFoundTest() {
        String username = "john_doe";
        String password = "password123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> loginService.login(username, password));
    }

    @Test()
    public void loginInvalidPasswordTest() {
        String username = "john_doe";
        String password = "password123";
        User user = new User();
        user.setUsername(username);
        user.setPassword("wrong_password");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertThrows(InvalidPasswordException.class, () -> loginService.login(username, password));
    }

    @Test
    public void commenceReturnUnauthorizedResponseTest() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);

        jwtAuthenticationEntryPoint.commence(request, response, authException);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }


    @Test
    public void passwordEncoderTest() {
        WebSecurityConfig config = new WebSecurityConfig(mock(JwtAuthenticationEntryPoint.class), mock(UserDetailsService.class), mock(JwtRequestFilter.class));
        PasswordEncoder encoder = config.passwordEncoder();
        assert(encoder != null);
    }


    @Test
    public void validateExpiredTokenTest() {
        Date expiredDate = new Date(System.currentTimeMillis() - 1000);
        String expiredToken = Jwts.builder().setSubject(USERNAME).setExpiration(expiredDate).signWith(SignatureAlgorithm.HS512, SECRET).compact();
        jwtTokenUtil.validateToken(expiredToken, userDetails);
    }



    @Test
    public void getAllUsersTest() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 3, 26, 14, 30, 0);
        User user1 = new User(1, "John", "johndoe@example.com", "active",dateTime,"123");

        List<User> userList = new ArrayList<>();
        userList.add(user1);

        Mockito.when(userRepository.findAll()).thenReturn(userList);

        List<User> result = listService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getUsername());
        assertEquals("johndoe@example.com", result.get(0).getEmail());
        assertEquals("active", result.get(0).getStatus());
        assertEquals("123", result.get(0).getPassword());
    }

    @Test
    public void getUsersByUsernameWithValidUsernameTest() throws ResourceNotFoundException {
        String username = "johndoe";
        LocalDateTime dateTime = LocalDateTime.of(2023, 3, 26, 14, 30, 0);
        User user1 = new User(1, "johndoe", "Johndoe@example.com", "active",dateTime,"123");

        List<User> userList = new ArrayList<>();
        userList.add(user1);

        Mockito.when(userRepository.findUsersByUsername(username)).thenReturn(userList);

        List<User> result = listService.getUsersByUsername(username);

        assertEquals(1, result.size());
        assertEquals("johndoe", result.get(0).getUsername());
        assertEquals("Johndoe@example.com", result.get(0).getEmail());
        assertEquals("active", result.get(0).getStatus());
        assertEquals("123", result.get(0).getPassword());
    }

    @Test()
    public void getUsersByUsernameWithInvalidUsernameTest() {
        String username = "invalidusername";

        Mockito.when(userRepository.findUsersByUsername(username)).thenReturn(new ArrayList<>());


        assertThrows(ResourceNotFoundException.class, () -> {
            listService.getUsersByUsername(username);
        });

    }


    @Test
    public void createAuthenticationTokenTest() throws Exception {
        JwtRequest authenticationRequest = new JwtRequest("testuser", "testpassword");
        UserDetails userDetails = mock(UserDetails.class);
        String jwtToken = "testjwt";
        when(jwtUserDetailsService.loadUserByUsername(authenticationRequest.getUsername())).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn(jwtToken);

        ResponseEntity<JwtResponse> response = (ResponseEntity<JwtResponse>) jwtAuthenticationController.createAuthenticationToken(authenticationRequest);

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        verify(jwtUserDetailsService).loadUserByUsername(authenticationRequest.getUsername());
        verify(jwtTokenUtil).generateToken(userDetails);
        JwtResponse responseBody = response.getBody();
        assert responseBody != null;
        assertEquals(jwtToken, responseBody.getToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }



    @Test
    void loadUserByUsernameTest() {
        String username = "testuser";
        String password = "testpassword";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
    }

    /**
     * Test of the normal deactivating of the user.
     */

//    @Test
//    public void deactivateUserTest() throws UserNotFoundException {
//        String username = "johndoe";
//        User user = new User();
//        user.setUsername(username);
//        user.setStatus("active");
//
//
//        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
//        when(userRepository.save(user)).thenReturn(user);
//
//        User result = deactivateService.deactivateUser(username);
//
//        verify(userRepository, times(1)).findByUsername(username);
//        verify(userRepository, times(1)).save(user);
//
//        assertEquals("Inactive", result.getStatus());
//    }

    /**
     * Test for auto deactivating user
     */
    @Test
    void autoDeactivateUserTest() throws UserNotFoundException {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(1, "testuser", "testuser@test.com","Active",now.minusMonths(2),"123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User deactivatedUser = deactivateService.deactivateUser("testuser");

        assertEquals("Inactive", deactivatedUser.getStatus());
    }

    @Test
    public void deactivateUserNotFoundTest() {
        String username = "testUser";
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            deactivateService.deactivateUser(username);
        });
    }


    @Test
    public void deleteUserByUsernameTest() throws UserNotFoundException {
        String username = "john123";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        deleteService.deleteUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).deleteByUsername(username);
    }

    @Test()
    public void deleteUserByUsername_UserNotFoundTest() {
        String username = "john123";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            deleteService.deleteUserByUsername(username);
        });
    }

}


