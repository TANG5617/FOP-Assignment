package controller;

import org.todolist.Task;
import org.todolist.TaskManager;
import service.EmailNotificationService;

import java.util.Scanner;
import java.util.Calendar;

public class TestEmail {
    public static void main(String[] args) {
        // Create a Scanner to get user input
        Scanner scanner = new Scanner(System.in);

        // Prompt for the user's email address
        System.out.print("Please enter your email address for task reminders: ");
        String userEmail = scanner.nextLine();

        // Create TaskManager and add some tasks for testing
        TaskManager taskManager = new TaskManager();

        // Task due in 1 day (should trigger email)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 24);  // Due in 24 hours
        java.util.Date utilDate = cal.getTime();
        java.sql.Date taskDue = new java.sql.Date(utilDate.getTime());
        Task task = new Task(1, "Test", "Complete the Java assignment.", taskDue, "Work", "High", 0);

        // Add tasks to the taskManager
        taskManager.addTask(task);

        // Now call the method to send email notifications
        taskManager.sendTaskReminderNotifications(userEmail); // Pass user email

//        EmailNotificationService emailService = new EmailNotificationService();
//        emailService.sendEmail("melodyzhu233@gmail.com", "测试邮件", "这是一个通过 Gmail 发送的测试邮件。");

    }
}
