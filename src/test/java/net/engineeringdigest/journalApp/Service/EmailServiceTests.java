package net.engineeringdigest.journalApp.Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

//@SpringBootTest
public class EmailServiceTests {

    @Autowired
    private  EmailService emailService;



    @Test
    void testSendEmail(){
        emailService.sendEmail("nehaasingh201204@gmail.com","hahahha first email hai beta through project gnggggg","oyeee mareezzzzzzz mueheheheh, GET WELL SOOONNNNNNNNNNNNNNNNN !))");

    }
}
