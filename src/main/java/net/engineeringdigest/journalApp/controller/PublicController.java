package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.Service.UserDetailServiceImpl;
import net.engineeringdigest.journalApp.Service.UserService;
import net.engineeringdigest.journalApp.dto.AuthResponse;
import net.engineeringdigest.journalApp.dto.LoginRequest;
import net.engineeringdigest.journalApp.dto.SignupRequest;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;

@RestController
@RequestMapping("/public")
public class PublicController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    public PublicController(UserService userService, AuthenticationManager authenticationManager,
                            UserDetailServiceImpl userDetailsService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/health-check")
    public String healthCheck() { return "OK"; }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {
        if (userService.findByEmail(request.getEmail().trim()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }
        User user = User.builder()
                .userName(request.getUserName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(request.getPassword())
                .sentimentAnalysis(Boolean.FALSE)
                .roles(Collections.singletonList("USER"))
                .build();
        user.setJournalEntries(new ArrayList<>());
        if (!userService.saveNewUser(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Unable to create account. The username or email may already be registered.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Account created");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUserName(), request.getPassword()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUserName());
        return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(userDetails.getUsername()),
                userDetails.getUsername()));
    }
}