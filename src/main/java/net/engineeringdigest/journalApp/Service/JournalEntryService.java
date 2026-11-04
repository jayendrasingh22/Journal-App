package net.engineeringdigest.journalApp.Service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.Repository.JournalEntryRepo;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class JournalEntryService {
    private final JournalEntryRepo journalEntryRepo;
    private final UserService userService;

    public JournalEntryService(JournalEntryRepo journalEntryRepo, UserService userService) {
        this.journalEntryRepo = journalEntryRepo;
        this.userService = userService;
    }

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String userName) {
        User user = userService.findByUserName(userName);
        if (user == null) throw new IllegalArgumentException("User not found");
        journalEntry.setDate(LocalDateTime.now());
        JournalEntry saved = journalEntryRepo.save(journalEntry);
        if (user.getJournalEntries() == null) user.setJournalEntries(new ArrayList<>());
        user.getJournalEntries().add(saved);
        userService.saveUser(user);
    }

    public void saveEntry(JournalEntry journalEntry) {
        journalEntryRepo.save(journalEntry);
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepo.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalEntryRepo.findById(id);
    }

    @Transactional
    public boolean deleteById(ObjectId id, String userName) {
        User user = userService.findByUserName(userName);
        if (user == null || user.getJournalEntries() == null) return false;
        boolean removed = user.getJournalEntries().removeIf(entry -> entry.getId().equals(id));
        if (removed) {
            userService.saveUser(user);
            journalEntryRepo.deleteById(id);
        }
        return removed;
    }
}