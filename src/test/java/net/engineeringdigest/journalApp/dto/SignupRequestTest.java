package net.engineeringdigest.journalApp.dto;

import org.junit.jupiter.api.Test;
import javax.validation.Validation;
import javax.validation.Validator;
import static org.junit.jupiter.api.Assertions.*;

class SignupRequestTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validSignupRequestPassesValidation() {
        SignupRequest request = new SignupRequest();
        request.setUserName("jayendra");
        request.setEmail("jay@example.com");
        request.setPassword("a-long-test-password");
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void shortPasswordFailsValidation() {
        SignupRequest request = new SignupRequest();
        request.setUserName("jayendra");
        request.setEmail("jay@example.com");
        request.setPassword("short");
        assertFalse(validator.validate(request).isEmpty());
    }
}