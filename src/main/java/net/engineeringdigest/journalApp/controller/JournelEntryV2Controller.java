package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.Service.JournalEntryService;
import net.engineeringdigest.journalApp.Service.UserService;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/journal")
public class JournelEntryV2Controller {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;


    // GET ALL JOURNAL ENTRIES OF A USER
    // GET /journal/{userName}
    @GetMapping()
    public ResponseEntity<?> getAllJournalEntriesOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User user = userService.findByUserName(userName);
        List<JournalEntry> all = user.getJournalEntries();

        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>( HttpStatus.NOT_FOUND);
    }


    // CREATE JOURNAL ENTRY
    // POST /journal/{userName}
    @PostMapping()
    public ResponseEntity<?> createEntry(
            @RequestBody JournalEntry myentry) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            // Check whether user exists BEFORE saving journal entry
            User user = userService.findByUserName(userName);

            if (user == null) {
                return new ResponseEntity<>( "User not found: " + userName, HttpStatus.NOT_FOUND);
            }
            myentry.setDate(LocalDateTime.now());
            journalEntryService.saveEntry(myentry, userName);

            return new ResponseEntity<>(
                    myentry,
                    HttpStatus.CREATED
            );

        } catch (Exception e) {
            e.printStackTrace();

            return new ResponseEntity<>( "Error while creating journal entry: " + e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // GET JOURNAL ENTRY BY ID
    // GET /journal/id/{myId}

    @GetMapping("id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById( @PathVariable ObjectId myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user=userService.findByUserName(userName);

        List<JournalEntry> collect =user.getJournalEntries().stream().filter(x-> x.getId().equals(myId)).collect(Collectors.toList());
        if(!collect.isEmpty()) {
            Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);

            if (journalEntry.isPresent()) {
                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
            }

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }



    // DELETE JOURNAL ENTRY
    // DELETE /journal/id/{userName}/{myId}
    @DeleteMapping("id/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        boolean removed = journalEntryService.deleteById(myId, userName);
        if (removed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
    }

    // UPDATE JOURNAL ENTRY
    // PUT /journal/id/{userName}/{id}
    @PutMapping("id/{myId}")
    public ResponseEntity<?> updateJournalEntryById(@PathVariable ObjectId myId, @RequestBody JournalEntry newEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user=userService.findByUserName(userName);

        List<JournalEntry> collect =user.getJournalEntries().stream().filter(x-> x.getId().equals(myId)).collect(Collectors.toList());
        if(!collect.isEmpty()) {
            Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);

            if (journalEntry.isPresent()) {
                JournalEntry old = journalEntryService.findById(myId).orElse(null);
//
                old.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().equals("") ? newEntry.getTitle() : old.getTitle());
                 old.setContent(newEntry.getContent() != null && !newEntry.getContent().equals("") ? newEntry.getContent() : old.getContent());
                    old.setContent(newEntry.getContent());
                    journalEntryService.saveEntry(old);

                    return new ResponseEntity<>(old, HttpStatus.OK);


            }

        }return new ResponseEntity<>( HttpStatus.NOT_FOUND);
    }
}