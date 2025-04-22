package com._data._data.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("{spring.mail.username}")
    private static String senderEmail;

    private final JavaMailSender javaMailSender;

    public String createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();
        
        // 인증코드 6자리 랜덤생성
        for (int i = 0; i < 6; i++) { 
            int index = random.nextInt(2); 

            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 65)); // 대문자
                case 1 -> key.append(random.nextInt(10)); // 숫자
            }
        }
        return key.toString();
    }

    public MimeMessage createMail(String mail, String authCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");
        String body = "";
        body += "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + authCode + "</h1>";
        body += "<h3>감사합니다.</h3>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    public String sendSimpleMessage(String sendEmail) throws MessagingException {
        String authCode = createCode();

        MimeMessage message = createMail(sendEmail, authCode);
        try {
            javaMailSender.send(message);
            return authCode;
        } catch (MailException e) {
            return null;
        }
    }
}
