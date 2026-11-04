package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.Service.UserService;
import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.dto.AdminCreateRequest;
import net.engineeringdigest.journalApp.dto.UserResponse;
import net.engineeringdigest.journalApp.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final AppCache appCache;

    public AdminController(UserService userService, AppCache appCache) {
        this.userService = userService;
        this.appCache = appCache;
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<UserResponse>> getAllUser() {
        List<UserResponse> users = userService.getAll().stream().map(UserResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PostMapping("/create-admin-user")
    public ResponseEntity<String> createUser(@Valid @RequestBody AdminCreateRequest request) {
        if (userService.findByUserName(request.getUserName()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already in use");
        }
        User user = User.builder().userName(request.getUserName().trim())
                .email(request.getEmail().trim().toLowerCase()).password(request.getPassword())
                .sentimentAnalysis(Boolean.FALSE).roles(Collections.singletonList("ADMIN"))
                .JournalEntries(new ArrayList<>()).build();
        userService.saveAdmin(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Admin account created");
    }

    @GetMapping("/clear-app-cache")
    public ResponseEntity<Void> clearAppCache() {
        appCache.init();
        return ResponseEntity.noContent().build();
    }
}