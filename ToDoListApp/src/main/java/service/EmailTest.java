package service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

public class EmailTest {
    /**
     * Sends an email notification.
     *
     * @param recipientEmail the recipient's email address
     * @param subject        the subject of the email
     * @param content        the content of the email
     */
    public static void sendEmail(String recipientEmail, String subject, String content) {
        // Load HTML template for email content
        String emailContent = loadEmailTemplate();

        if (emailContent == null) {
            System.out.println("Error: Unable to load email template.");
            return;
        }

        // Set SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "false");
        properties.put("mail.smtp.starttls.enable", "false");
        properties.put("mail.smtp.host", "localhost");
        properties.put("mail.smtp.port", "1025");

        // Create email session with authentication
        Session session = Session.getInstance(properties);

        try {
            // Create and configure the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("hello@example.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);

            String modifiedEmailContent = emailContent
                    .replaceAll("\\{\\{ subject }}", subject)
                    .replace("{{ content }}", content);

            message.setContent(modifiedEmailContent, "text/html");

            // Send the email
            Transport.send(message);
            System.out.println("Email sent successfully to " + recipientEmail + "!");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sending email: " + e.getMessage());
        }
    }

    /**
     * Loads the HTML email template file.
     *
     * @return String containing the HTML code. Null when failed to read file.
     */
    private static String loadEmailTemplate() {
        URI templateFilePath;
        try {
            templateFilePath = Objects.requireNonNull(
                            EmailTest.class
                                    .getClassLoader()
                                    .getResource("EmailTemplate.html"))
                    .toURI();
        } catch (URISyntaxException e) {
            return null;
        }

        try {
            return new String(Files.readAllBytes(Paths.get(templateFilePath)));
        } catch (IOException e) {
            return null;
        }
    }
}