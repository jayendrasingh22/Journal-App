package net.engineeringdigest.journalApp.dto;

public class AuthResponse {
    private final String tokenType = "Bearer";
    private final String accessToken;
    private final String userName;

    public AuthResponse(String accessToken, String userName) {
        this.accessToken = accessToken;
        this.userName = userName;
    }
    public String getTokenType() { return tokenType; }
    public String getAccessToken() { return accessToken; }
    public String getUserName() { return userName; }
}