package com.example.phase_04.service.impl;

import com.example.phase_04.entity.Person;
import com.example.phase_04.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    public String sendEmail(Person person){
        String to = person.getEmail();
        String subject = "Test Email";
        String text = "Hello " + person.getFirstName() +"!\nThis email is only for testing the API";
        sendSimpleMessage(to,subject,text);

        return "Sent";
    }

    public void sendSimpleMessage(String to, String subject, String text){
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("amir.ahmadi9034@gmail.com");
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(text);
        javaMailSender.send(mail);
    }

    public void sendActivationLink(){}

}
