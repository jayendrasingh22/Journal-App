package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.Service.JournalEntryService;
import net.engineeringdigest.journalApp.Service.UserService;
import net.engineeringdigest.journalApp.dto.JournalEntryRequest;
import net.engineeringdigest.journalApp.dto.JournalEntryResponse;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournelEntryV2Controller {
    private final JournalEntryService journalEntryService;
    private final UserService userService;

    public JournelEntryV2Controller(JournalEntryService journalEntryService, UserService userService) {
        this.journalEntryService = journalEntryService;
        this.userService = userService;
    }

    private String currentUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private User currentUser() {
        User user = userService.findByUserName(currentUserName());
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        return user;
    }

    @GetMapping
    public ResponseEntity<List<JournalEntryResponse>> getAllJournalEntriesOfUser() {
        List<JournalEntryResponse> entries = currentUser().getJournalEntries().stream()
                .map(JournalEntryResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(entries);
    }

    @PostMapping
    public ResponseEntity<JournalEntryResponse> createEntry(@Valid @RequestBody JournalEntryRequest request) {
        User user = currentUser();
        JournalEntry entry = new JournalEntry();
        entry.setTitle(request.getTitle().trim());
        entry.setContent(request.getContent());
        entry.setDate(LocalDateTime.now());
        journalEntryService.saveEntry(entry, user.getUserName());
        return ResponseEntity.status(HttpStatus.CREATED).body(new JournalEntryResponse(entry));
    }

    @GetMapping("/id/{myId}")
    public ResponseEntity<JournalEntryResponse> getJournalEntryById(@PathVariable ObjectId myId) {
        User user = currentUser();
        boolean belongsToUser = user.getJournalEntries().stream()
                .anyMatch(entry -> myId.equals(entry.getId()));
        if (!belongsToUser) return ResponseEntity.notFound().build();
        Optional<JournalEntry> entry = journalEntryService.findById(myId);
        return entry.map(value -> ResponseEntity.ok(new JournalEntryResponse(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/id/{myId}")
    public ResponseEntity<Void> deleteJournalEntryById(@PathVariable ObjectId myId) {
        boolean removed = journalEntryService.deleteById(myId, currentUserName());
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/id/{myId}")
    public ResponseEntity<JournalEntryResponse> updateJournalEntryById(
            @PathVariable ObjectId myId, @Valid @RequestBody JournalEntryRequest request) {
        User user = currentUser();
        boolean belongsToUser = user.getJournalEntries().stream()
                .anyMatch(entry -> myId.equals(entry.getId()));
        if (!belongsToUser) return ResponseEntity.notFound().build();

        JournalEntry entry = journalEntryService.findById(myId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Journal entry not found"));
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            entry.setTitle(request.getTitle().trim());
        }
        if (request.getContent() != null) entry.setContent(request.getContent());
        journalEntryService.saveEntry(entry);
        return ResponseEntity.ok(new JournalEntryResponse(entry));
    }
}