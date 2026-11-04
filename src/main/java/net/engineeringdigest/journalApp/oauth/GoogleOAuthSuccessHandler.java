package net.engineeringdigest.journalApp.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.engineeringdigest.journalApp.Repository.UserRepository;
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

@Component
public class GoogleOAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public GoogleOAuthSuccessHandler(UserRepository userRepository, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = principal.getAttribute("email");
        Boolean emailVerified = principal.getAttribute("email_verified");

        if (email == null || !Boolean.TRUE.equals(emailVerified)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Google account email is not verified");
            return;
        }

        User user = userRepository.findByEmailIgnoreCase(email);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "No local account is linked to this verified Google email. Sign in with your password first.");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(),
                new AuthResponse(jwtUtil.generateToken(user.getUserName()), user.getUserName()));
    }
}