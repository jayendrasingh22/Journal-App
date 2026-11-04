package net.engineeringdigest.journalApp.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class JournalEntryRequest {
    @NotBlank @Size(max = 120)
    private String title;
    @Size(max = 20000)
    private String content;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}