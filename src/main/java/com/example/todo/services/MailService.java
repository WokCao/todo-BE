package com.example.todo.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.page-link}")
    private String pageLink;

    private void sendMail(String to, String subject, String htmlText) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(htmlText, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendWelcomeMail(String to) {
        String subject = "Welcome to Todo App";
        String htmlText = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>Welcome to Todo App</title>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; }" +
                "        .container { max-width: 600px; margin: 0 auto; background-color: #f9f9f9; padding: 30px; border-radius: 10px; }" +
                "        .header { text-align: center; margin-bottom: 30px; }" +
                "        .header h1 { color: #4CAF50; margin: 0; }" +
                "        .content { background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }" +
                "        .button { display: inline-block; padding: 12px 24px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }" +
                "        .footer { text-align: center; margin-top: 30px; color: #666; font-size: 14px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">" +
                "            <h1>🎯 Todo App</h1>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <h2>Welcome aboard!</h2>" +
                "            <p>Hello there!</p>" +
                "            <p>Thank you for registering with Todo App. We're excited to help you stay organized and productive!</p>" +
                "            <p>With our app, you can:</p>" +
                "            <ul>" +
                "                <li>Create and manage your tasks efficiently</li>" +
                "                <li>Set priorities and due dates</li>" +
                "                <li>Organize tasks into categories</li>" +
                "                <li>Track your progress and stay on top of your goals</li>" +
                "            </ul>" +
                "            <p>Ready to get started?</p>" +
                "            <a href=\"" + pageLink + "/login\" class=\"button\">Start Organizing Now</a>" +
                "        </div>" +
                "        <div class=\"footer\">" +
                "            <p>If you have any questions, feel free to reply to this email or contact our support team.</p>" +
                "            <p>Happy organizing!<br>The Todo App Team</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";

        sendMail(to, subject, htmlText);
    }
}
