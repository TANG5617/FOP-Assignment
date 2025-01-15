package service;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailNotificationService {

    public void sendEmail(String toEmail, String subject, String body) {
        String fromEmail = "melodyzhu233@gmail.com";  // Change to your email address
        String host = "smtp.gmail.com";  // SMTP server address (e.g., Gmail's SMTP server)

        // Set up the email properties
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");  // Use 465 for SSL, 587 for TLS
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Get session and authenticate
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, "rhmk vdsx sjcf yjwv");  // Use the correct password
            }
        });

        try {
            // Create the email message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject);
            message.setText(body);

            // Send the email
            Transport.send(message);
            System.out.println("Email sent successfully.");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
