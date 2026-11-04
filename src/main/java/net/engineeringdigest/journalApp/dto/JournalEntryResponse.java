package net.engineeringdigest.journalApp.dto;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.enums.Sentiment;
import java.time.LocalDateTime;

public class JournalEntryResponse {
    private final String id;
    private final String title;
    private final String content;
    private final LocalDateTime date;
    private final Sentiment sentiment;

    public JournalEntryResponse(JournalEntry entry) {
        this.id = entry.getId() == null ? null : entry.getId().toHexString();
        this.title = entry.getTitle();
        this.content = entry.getContent();
        this.date = entry.getDate();
        this.sentiment = entry.getSentiment();
    }
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getDate() { return date; }
    public Sentiment getSentiment() { return sentiment; }
}