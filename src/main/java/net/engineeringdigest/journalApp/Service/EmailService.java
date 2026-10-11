package net.engineeringdigest.journalApp.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String body) {

        log.info("1. sendEmail() called");

        try {
            SimpleMailMessage mail = new SimpleMailMessage();

            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);

            log.info("2. Sending email to {}", to);

            javaMailSender.send(mail);

            log.info("3. EMAIL SENT SUCCESSFULLY");

        } catch (Exception e) {
            log.error("4. ERROR WHILE SENDING MAIL", e);
            throw new RuntimeException("Email sending failed", e);
        }
    }
}