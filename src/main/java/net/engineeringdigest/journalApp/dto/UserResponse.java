package net.engineeringdigest.journalApp.dto;

import net.engineeringdigest.journalApp.entity.User;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserResponse {
    private final String id;
    private final String userName;
    private final String email;
    private final Boolean sentimentAnalysis;
    private final List<String> roles;

    public UserResponse(User user) {
        this.id = user.getId() == null ? null : user.getId().toHexString();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.sentimentAnalysis = user.getSentimentAnalysis();
        this.roles = user.getRoles() == null ? Collections.emptyList() :
                user.getRoles().stream().collect(Collectors.toList());
    }
    public String getId() { return id; }
    public String getUserName() { return userName; }
    public String getEmail() { return email; }
    public Boolean getSentimentAnalysis() { return sentimentAnalysis; }
    public List<String> getRoles() { return roles; }
}