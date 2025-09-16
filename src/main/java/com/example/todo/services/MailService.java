package com.example.todo.services;

import com.example.todo.models.TaskModel;
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

    public void sendTaskDueDateReminder(String to, TaskModel task) {
        String subject = "Reminder: Task due soon - " + task.getTitle();
        StringBuilder htmlText = new StringBuilder();
        htmlText.append("<!DOCTYPE html>")
                .append("<html lang=\"en\">")
                .append("<head>")
                .append("    <meta charset=\"UTF-8\">")
                .append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
                .append("    <title>Task Due Soon</title>")
                .append("    <style>")
                .append("        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; }")
                .append("        .container { max-width: 600px; margin: 0 auto; background-color: #fff8f8; padding: 30px; border-radius: 10px; border-left: 4px solid #ff6b6b; }")
                .append("        .header { text-align: center; margin-bottom: 25px; }")
                .append("        .header h1 { color: #ff6b6b; margin: 0; }")
                .append("        .content { background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }")
                .append("        .task-info { background: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0; }")
                .append("        .task-detail { margin: 8px 0; }")
                .append("        .priority-high { color: #ff6b6b; font-weight: bold; }")
                .append("        .priority-medium { color: #f39c12; font-weight: bold; }")
                .append("        .priority-low { color: #27ae60; font-weight: bold; }")
                .append("        .button { display: inline-block; padding: 12px 24px; background-color: #ff6b6b; color: white; text-decoration: none; border-radius: 5px; margin-top: 15px; }")
                .append("        .footer { text-align: center; margin-top: 30px; color: #666; font-size: 14px; }")
                .append("    </style>")
                .append("</head>")
                .append("<body>")
                .append("    <div class=\"container\">")
                .append("        <div class=\"header\">")
                .append("            <h1>⏰ Task Due Soon</h1>")
                .append("        </div>")
                .append("        <div class=\"content\">")
                .append("            <h2>Friendly Reminder</h2>")
                .append("            <p>Hello there!</p>")
                .append("            <p>This is a reminder that your task <strong>\"").append(task.getTitle()).append("\"</strong> is due soon.</p>")
                .append("            ")
                .append("            <div class=\"task-info\">")
                .append("                <h3>Task Details:</h3>")
                .append("                <div class=\"task-detail\"><strong>Title:</strong> ").append(task.getTitle()).append("</div>");

        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            htmlText.append("                <div class=\"task-detail\"><strong>Description:</strong> ").append(task.getDescription()).append("</div>");
        }

        htmlText.append("                <div class=\"task-detail\"><strong>Due Date:</strong> ").append(task.getDueDate()).append("</div>")
                .append("                <div class=\"task-detail\"><strong>Priority:</strong> <span class=\"priority-").append(task.getPriority().toString().toLowerCase()).append("\">").append(task.getPriority()).append("</span></div>")
                .append("                <div class=\"task-detail\"><strong>Status:</strong> ").append(task.getStatus()).append("</div>")
                .append("            </div>")
                .append("            ")
                .append("            <p>Don't forget to complete your task on time! You can update the status or due date if needed.</p>")
                .append("            <a href=\"").append(pageLink).append("/tasks/").append(task.getId()).append("\" class=\"button\">View Task</a>")
                .append("        </div>")
                .append("        <div class=\"footer\">")
                .append("            <p>This is an automated reminder. You can manage your notification settings in your account preferences.</p>")
                .append("            <p>Stay productive!<br>The Todo App Team</p>")
                .append("        </div>")
                .append("    </div>")
                .append("</body>")
                .append("</html>");

        sendMail(to, subject, htmlText.toString());
    }
}
