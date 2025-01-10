package service;

import java.util.Scanner;

public class EmailNotificationService {
    public static void main(String[] args) {
        String recipientEmail;

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter recipient email: ");
            recipientEmail = scanner.nextLine();
        }

        // Test sending an email
        EmailTest.sendEmail(
                recipientEmail,
                "Test Subject",
                "This is a test email.");
    }
}