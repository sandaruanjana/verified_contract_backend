package com.wixis360.verifiedcontractingbackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final ResourceLoader resourceLoader;
    private final JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async
    public void sendPlain(String to, String title, String msg) {
        log.info("Sending email to {} with title {} and message {}", to, title, msg);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject(title);
        message.setText(msg);
        emailSender.send(message);
    }

    @Async
    public void send(String to, String subject, String body) throws MessagingException, IOException {
        log.info("Sending email to " + to);
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(senderEmail, "Verified Contracting");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        Resource resource = resourceLoader.getResource("file:src/main/resources/static/img/logo.png");
        FileSystemResource res = new FileSystemResource(resource.getFile());
        helper.addInline("identifier1234", res);
        emailSender.send(message);
    }
}
