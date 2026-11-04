package net.engineeringdigest.journalApp.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ProfileUpdateRequest {
    @NotBlank
    @Size(min = 3, max = 40)
    private String userName;

    @Email
    @Size(max = 254)
    private String email;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}