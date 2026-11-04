package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.Repository.UserRepository;
import net.engineeringdigest.journalApp.Service.UserService;
import net.engineeringdigest.journalApp.Service.WeatherService;
import net.engineeringdigest.journalApp.api_response.WeatherResponse;
import net.engineeringdigest.journalApp.dto.ProfileUpdateRequest;
import net.engineeringdigest.journalApp.dto.UserResponse;
import net.engineeringdigest.journalApp.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;\nimport javax.validation.constraints.Email;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final WeatherService weatherService;

    public UserController(UserService userService, UserRepository userRepository, WeatherService weatherService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.weatherService = weatherService;
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody ProfileUpdateRequest request) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUserName(currentUserName);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        User duplicate = userService.findByUserName(request.getUserName().trim());
        if (duplicate != null && !duplicate.getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already in use");
        }
        user.setUserName(request.getUserName().trim());
        if (request.getEmail() != null) {\n            User emailOwner = userService.findByEmail(request.getEmail().trim());\n            if (emailOwner != null && !emailOwner.getId().equals(user.getId())) {\n                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");\n            }\n            user.setEmail(request.getEmail().trim().toLowerCase());\n        }
        userRepository.save(user);
        return ResponseEntity.ok(new UserResponse(user));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUserById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByUserName(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<String> greeting() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WeatherResponse weatherResponse = weatherService.getWeather("Mumbai");
        String greeting = "";
        if (weatherResponse != null && weatherResponse.getCurrent() != null
                && weatherResponse.getCurrent().getFeelslike() != null) {
            greeting = ", Weather feels like " + weatherResponse.getCurrent().getFeelslike();
        }
        return ResponseEntity.ok("Hi " + authentication.getName() + greeting);
    }
}