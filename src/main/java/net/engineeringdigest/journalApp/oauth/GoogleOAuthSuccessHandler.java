package net.engineeringdigest.journalApp.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.engineeringdigest.journalApp.Repository.UserRepository;
import net.engineeringdigest.journalApp.Service.UserService;
import net.engineeringdigest.journalApp.dto.AuthResponse;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.utils.JwtUtil;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Component
public class GoogleOAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public GoogleOAuthSuccessHandler(UserRepository userRepository, UserService userService,
                                     JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = principal.getAttribute("email");
        String subject = principal.getAttribute("sub");
        Boolean emailVerified = principal.getAttribute("email_verified");

        if (email == null || subject == null || !Boolean.TRUE.equals(emailVerified)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Google account email is not verified");
            return;
        }

        User user = userRepository.findByGoogleSubject(subject);
        if (user == null) {
            // Never link a Google identity to an existing local account based on email alone.
            if (userRepository.findByEmailIgnoreCase(email) != null) {
                response.sendError(HttpServletResponse.SC_CONFLICT,
                        "A local account already uses this email. Automatic account linking is disabled.");
                return;
            }

            String username = "google_" + subject.substring(0, Math.min(subject.length(), 24));
            user = User.builder().userName(username).email(email.toLowerCase())
                    .password(UUID.randomUUID().toString()).sentimentAnalysis(Boolean.FALSE)
                    .roles(Collections.singletonList("USER")).build();
            user.setGoogleSubject(subject);
            user.setJournalEntries(new ArrayList<>());
            if (!userService.saveNewUser(user)) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Unable to create Google account");
                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(),
                new AuthResponse(jwtUtil.generateToken(user.getUserName()), user.getUserName()));
    }
}