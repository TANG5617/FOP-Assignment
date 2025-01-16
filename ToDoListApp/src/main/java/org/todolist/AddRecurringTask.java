package org.todolist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class AddRecurringTask {
    static class Task {
        String title;
        String description;
        String dueDate;
        boolean isComplete;
        Task dependency;
        String recurrence;

        Task(String title, String description, String dueDate) {
            this.title = title;
            this.description = description;
            this.dueDate = dueDate;
            this.isComplete = false;
            this.dependency = null;
            this.recurrence = null;
        }

        void setDependency(Task dependency) {
            this.dependency = dependency;
        }

        void setRecurrence(String recurrence) {
            this.recurrence = recurrence;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[" + (isComplete ? "Complete" : "Incomplete") + "] ");
            sb.append(title + ": " + description);
            if (dueDate != null && !dueDate.isEmpty()) sb.append(" - Due: " + dueDate);
            if (dependency != null) sb.append(" (Depends on " + dependency.title + ")");
            return sb.toString();
        }
    }

    private static final List<Task> tasks = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== To-Do List App ===");
            System.out.println("1. Add a Recurring Task");
            System.out.println("2. Edit a Task");
            System.out.println("3. Exit");
            System.out.print("> ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    addRecurringTask();
                    break;
                case 2:
                    editTask();
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void addRecurringTask() {
        System.out.println("=== Add a Recurring Task ===");
        System.out.print("Enter task title: ");
        String title = scanner.nextLine();
        System.out.print("Enter task description: ");
        String description = scanner.nextLine();
        System.out.print("Enter recurrence interval (daily, weekly, monthly): ");
        String recurrence = scanner.nextLine().toLowerCase();

        String dueDate = calculateNextDueDate(recurrence);
        if (dueDate == null) {
            System.out.println("Invalid recurrence interval.");
            return;
        }

        Task task = new Task(title, description, dueDate);
        task.setRecurrence(recurrence);
        tasks.add(task);
        System.out.println("Recurring Task \"" + title + "\" created successfully!");
    }

    private static String calculateNextDueDate(String recurrence) {
        Calendar calendar = Calendar.getInstance();
        switch (recurrence) {
            case "daily":
                calendar.add(Calendar.DATE, 1);
                break;
            case "weekly":
                calendar.add(Calendar.DATE, 7);
                break;
            case "monthly":
                calendar.add(Calendar.MONTH, 1);
                break;
            default:
                return null;
        }
        return dateFormat.format(calendar.getTime());
    }

    private static void editTask() {
        System.out.println("=== View All Tasks ===");
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }

        System.out.println("\n=== Edit Task ===");
        System.out.print("Enter the task number you want to edit: ");
        int taskNumber = Integer.parseInt(scanner.nextLine()) - 1;
        if (taskNumber < 0 || taskNumber >= tasks.size()) {
            System.out.println("Invalid task number.");
            return;
        }

        Task task = tasks.get(taskNumber);

        System.out.println("What would you like to edit?");
        System.out.println("1. Title");
        System.out.println("2. Description");
        System.out.println("3. Due Date");
        System.out.println("4. Category");
        System.out.println("5. Priority");
        System.out.println("6. Set Task Dependency");
        System.out.println("7. Cancel");
        System.out.print("> ");
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 1:
                System.out.print("Enter the new title: ");
                task.title = scanner.nextLine();
                break;
            case 2:
                System.out.print("Enter the new description: ");
                task.description = scanner.nextLine();
                break;
            case 3:
                System.out.print("Enter the new due date (YYYY-MM-DD): ");
                task.dueDate = scanner.nextLine();
                break;
            case 6:
                System.out.println("=== View All Tasks ===");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println((i + 1) + ". " + tasks.get(i));
                }
                System.out.print("Enter the task number to set as dependency: ");
                int dependencyNumber = Integer.parseInt(scanner.nextLine()) - 1;
                if (dependencyNumber < 0 || dependencyNumber >= tasks.size()) {
                    System.out.println("Invalid task number.");
                    return;
                }
                task.setDependency(tasks.get(dependencyNumber));
                break;
            case 7:
                System.out.println("Edit cancelled.");
                return;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        System.out.println("Task \"" + task.title + "\" has been updated.");
        System.out.println("\n=== View All Tasks ===");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }
}
