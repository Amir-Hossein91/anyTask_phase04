package com.example.phase_04.service.impl;

import com.example.phase_04.entity.Person;
import com.example.phase_04.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    public void sendActivationLink(Person person) {

        String text = "<p>Dear " + person.getFirstName() + " " + person.getLastName() + "!</p><br>" +
                "<p>Thank you for registering in 'anyTask'.</p><br>" +
                "<P>To complete your registration process, please click the link bellow:</P><br>" +
                "<a href=\"http://localhost:8080/person/verifyEmailAddress/"+person.getUsername()+"\">click here to finalize your registration</a>";
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("amir.ahmadi9034@gmail.com");
            helper.setTo(person.getEmail());
            helper.setSubject("EMAIL VERIFICATION");
            helper.setText(text,true);
            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
