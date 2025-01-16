package controller;

import org.todolist.Task;
import org.todolist.TaskManager;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TaskTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager();

        while (true) {
            // Display menu options
            System.out.println("=== To-Do List App ===");
            System.out.println("1. Add task");
            System.out.println("2. Mark task as complete");
            System.out.println("3. Delete task");
            System.out.println("4. Edit task");
            System.out.println("5. Search task");
            System.out.println("6. Sort tasks");
            System.out.println("7. Recurring task");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {

                case 1:
                    // Add a new task
                    System.out.println("=== Add a New Task ===");
                    System.out.print("Enter task title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter task description: ");
                    String description = scanner.nextLine();
                    System.out.print("Enter due date (YYYY-MM-DD): ");
                    String dueDateInput = scanner.nextLine();
                    Date dueDate = Date.valueOf(dueDateInput);
                    System.out.print("Enter task category (Homework, Personal, Work): ");
                    String category = scanner.nextLine();
                    System.out.print("Priority level (Low, Medium, High): ");
                    String priority = scanner.nextLine();

                    Task newTask = new Task(title, description, dueDate, category, priority);
                    taskManager.addTask(newTask);
                    System.out.println("Task \"" + title + "\" added successfully!");
                    break;

                case 2:
//                    System.out.print("\n=== Mark Task as Complete ===\n");
//                    System.out.print("Enter the task ID you want to mark as complete: ");
//                    int taskId = scanner.nextInt();
//                    taskManager.markTaskComplete(taskId);
//                    Task task = taskManager.getTaskById(taskId);
//                    // Assuming we fetch task details by ID, you'd display the task's title here.
//                    System.out.println("Task \"" + task.getTitle() + "\" marked as complete!");
//                    break;
                    markTaskComplete(scanner);
                    break;

                case 3:
                    // Delete a task
                    System.out.println("=== Delete a Task ===");
                    System.out.print("Enter the task ID you want to delete: ");
                    int deleteTaskId = scanner.nextInt();
                    Task task1 = taskManager.getTaskById(deleteTaskId);
                    scanner.nextLine();  // Consume newline
                    taskManager.deleteTask(deleteTaskId);
                    System.out.println("Task \"" + task1.getTitle() + "\" deleted successfully.");
                    break;

                case 4:
                    // Edit task
                    TaskManager taskManager1 = new TaskManager();

                    // Fetch all tasks from the database
                    List<Task> tasks = taskManager1.getTasks();

                    // Display all tasks
                    if (tasks.isEmpty()) {
                        System.out.println("No tasks available.");
                        return;
                    }
                    System.out.println("\n=== View All Tasks ===");
                    for (Task task : tasks) {
                        String completionStatus = task.isCompleted() ? "Complete" : "Incomplete";
                        System.out.println("ID: " + task.getId() + ", Title: " + task.getTitle() + ", Status: " + completionStatus + ", Description: " + task.getDescription() + " - Due: " + task.getDueDate() + " - Category: " + task.getCategory() + " - Priority: " + task.getPriority());
                    }

                    // Allow the user to edit a task
//                    System.out.println("\n=== Edit Task ===");
//                    Scanner scanner2 = new Scanner(System.in);
//                    System.out.print("Enter the task number you want to edit: ");
//                    int taskNumber = Integer.parseInt(scanner2.nextLine()) - 1;
//
//                    if (taskNumber < 0 || taskNumber >= tasks.size()) {
//                        System.out.println("Invalid task number.");
//                        return;
//                    }
//
//                    Task taskToEdit = tasks.get(taskNumber);
                    System.out.println("\n=== Edit Task ===");
                    Scanner scanner2 = new Scanner(System.in);
                    System.out.print("Enter the task ID you want to edit: ");
                    int taskId = Integer.parseInt(scanner2.nextLine());  //let the user type id

                    // Allow the user to edit a task by id
                    Task taskToEdit = taskManager.getTaskById(taskId);

                    if (taskToEdit == null) {
                        System.out.println("Task with ID " + taskId + " not found.");
                        return;
                    }

                    System.out.println("What would you like to edit?");
                    System.out.println("1. Title");
                    System.out.println("2. Description");
                    System.out.println("3. Due Date");
                    System.out.println("4. Category");
                    System.out.println("5. Priority");
                    System.out.println("6. Set Task Dependency");
                    System.out.println("7. Cancel");
                    System.out.print("> ");
                    int choice2 = Integer.parseInt(scanner.nextLine());

                    // Handle user input for editing
                    switch (choice2) {
                        case 1: // Edit Title
                            System.out.print("Enter the new title: ");
                            String newTitle = scanner.nextLine();
                            taskManager.editTask(taskToEdit.getId(), newTitle, taskToEdit.getDescription(), taskToEdit.getDueDate(), taskToEdit.getCategory(), taskToEdit.getPriority(), taskToEdit.isCompleted());
                            System.out.println("Task title has been updated to \"" + newTitle + "\".");
                            break;

                        case 2: // Edit Description
                            System.out.print("Enter the new description: ");
                            String newDescription = scanner.nextLine();
                            taskManager.editTask(taskToEdit.getId(), taskToEdit.getTitle(), newDescription, taskToEdit.getDueDate(), taskToEdit.getCategory(), taskToEdit.getPriority(), taskToEdit.isCompleted());
                            System.out.println("Task \"" + taskToEdit.getTitle() + "\" has been updated to \"" + newDescription + "\".");
                            break;

                        case 3: // Edit Due Date
                            System.out.print("Enter the new due date (YYYY-MM-DD): ");
                            String newDueDateStr = scanner.nextLine();
                            java.util.Date newDueDate = null;
                            try {
                                newDueDate = new SimpleDateFormat("yyyy-MM-dd").parse(newDueDateStr);
                            } catch (ParseException e) {
                                System.out.println("Invalid date format. Please enter the date in the format YYYY-MM-DD.");
                                return;
                            }
                            taskManager.editTask(taskToEdit.getId(), taskToEdit.getTitle(), taskToEdit.getDescription(), newDueDate, taskToEdit.getCategory(), taskToEdit.getPriority(), taskToEdit.isCompleted());
                            System.out.println("Task \"" + taskToEdit.getTitle() + "\" has been updated with a new due date: " + newDueDateStr);
                            break;

                        case 4: // Edit Category
                            System.out.print("Enter the new category (Homework, Personal, Work): ");
                            String newCategory = scanner.nextLine();
                            taskManager.editTask(taskToEdit.getId(), taskToEdit.getTitle(), taskToEdit.getDescription(), taskToEdit.getDueDate(), newCategory, taskToEdit.getPriority(), taskToEdit.isCompleted());
                            System.out.println("Task \"" + taskToEdit.getTitle() + "\" has been updated to category: " + newCategory);
                            break;

                        case 5: // Edit Priority
                            System.out.print("Enter the new priority (Low, Medium, High): ");
                            String newPriority = scanner.nextLine();
                            taskManager.editTask(taskToEdit.getId(), taskToEdit.getTitle(), taskToEdit.getDescription(), taskToEdit.getDueDate(), taskToEdit.getCategory(), newPriority, taskToEdit.isCompleted());
                            System.out.println("Task \"" + taskToEdit.getTitle() + "\" has been updated with priority: " + newPriority);
                            break;

                        case 6: // Set Dependency
                            setTaskDependency(scanner);
                            break;

                        case 7: // Cancel Edit
                            System.out.println("Edit cancelled.");
                            return;

                        default:
                            System.out.println("Invalid choice. Please select a valid option.");
                            return;
                    }

                    // Display all tasks after the update
//                    System.out.println("\n=== View All Tasks ===");
//                    tasks = taskManager.getTasks();  // Fetch updated list from DB
//                    for (int i = 0; i < tasks.size(); i++) {
//                        Task task3 = tasks.get(i);
//                        String completionStatus = task3.isCompleted() ? "[Completed]" : "[Incomplete]";
//                        System.out.println((i + 1) + ". " + completionStatus + " " + task3.getTitle() + ": " + task3.getDescription() + " - Due: " + task3.getDueDate() + "- Category: " + task3.getCategory() + "- Priority: " + task3.getPriority());
//                    }
                    // Display all tasks after the update
                    System.out.println("\n=== Tasks after updating ===");
                    List<Task> tasks3 = taskManager.getTasks();
                    for (Task task : tasks3) {
                        String completionStatus = task.isCompleted() ? "Complete" : "Incomplete";
                        System.out.println("ID: " + task.getId() + ", Title: " + task.getTitle() + ", Status: " + completionStatus + ", Description: " + task.getDescription() + " - Due: " + task.getDueDate() + " - Category: " + task.getCategory() + " - Priority: " + task.getPriority());
                    }
                    break;

                case 5:
                    // Search task
                    System.out.println("=== Search Tasks ===");
                    System.out.print("Enter a keyword to search by title or description: ");
                    String keyword = scanner.nextLine();
                    List<Task> searchResults = taskManager.searchTasks(keyword);

                    System.out.println("=== Search Results ===");
                    if (searchResults.isEmpty()) {
                        System.out.println("No tasks found matching the keyword \"" + keyword + "\".");
                    } else {
                        for (int i = 0; i < searchResults.size(); i++) {
                            System.out.println((i + 1) + ". " + searchResults.get(i));
                        }
                    }
                    break;

                case 6:
                    // Sort tasks
                    System.out.println("=== Sort Tasks ===");
                    System.out.println("Sort by:");
                    System.out.println("1. Due Date (Ascending)");
                    System.out.println("2. Due Date (Descending)");
                    System.out.println("3. Priority (High to Low)");
                    System.out.println("4. Priority (Low to High)");
                    System.out.print("> ");
                    int sortChoice = scanner.nextInt();
                    scanner.nextLine();  // Consume newline

                    switch (sortChoice) {
                        case 1:
                            taskManager.getSortedTasksByDueDateAsc();
                            System.out.println("Tasks sorted by Due Date (Ascending)!");
                            break;
                        case 2:
                            taskManager.getSortedTasksByDueDateDesc();
                            System.out.println("Tasks sorted by Due Date (Descending)!");
                            break;
                        case 3:
                            taskManager.getSortedTasksByPriorityHighLow();
                            System.out.println("Tasks sorted by Priority (High to Low)!");
                            break;
                        case 4:
                            taskManager.getSortedTasksByPriorityLowHigh();
                            System.out.println("Tasks sorted by Priority (Low to High)!");
                            break;
                        default:
                            System.out.println("Invalid option! Please try again.");
                            break;
                    }
                    break;

                case 7:
                    System.out.println("=== Add a Recurring Task ===");
                    System.out.print("Enter task title: ");
                    String title1 = scanner.nextLine();
                    System.out.print("Enter task description: ");
                    String description1 = scanner.nextLine();
                    System.out.print("Enter recurrence interval (daily, weekly, monthly): ");
                    String recurrence = scanner.nextLine().toLowerCase();
                    System.out.print("Enter task category (Homework, Personal, Work): ");
                    String category1 = scanner.nextLine();
                    System.out.print("Priority level (Low, Medium, High): ");
                    String priority1 = scanner.nextLine();

                    // Calculate the next due date based on recurrence
                    String dueDate1 = calculateNextDueDate(recurrence);
                    if (dueDate1 == null) {
                        System.out.println("Invalid recurrence interval.");
                        return;
                    }

                    // Create new task with the calculated due date
                    Task newTask1 = new Task(title1, description1, java.sql.Date.valueOf(dueDate1), category1, priority1); // Convert to java.sql.Date
                    newTask1.setRecurrence(recurrence); // Set the recurrence for the new task

                    taskManager.addTask(newTask1);  // Assuming addTask() is a method in TaskManager to add the task to the database or list
                    System.out.println("Recurring Task \"" + title1 + "\" created successfully!");
                    break;

                case 8:
                    // Exit
                    System.out.println("Exiting the application.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please choose again.");
            }
        }
    }

    public static String calculateNextDueDate(String recurrence) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        switch (recurrence) {
            case "daily":
                calendar.add(Calendar.DAY_OF_YEAR, 1);  // Add 1 day for daily recurrence
                break;
            case "weekly":
                calendar.add(Calendar.WEEK_OF_YEAR, 1); // Add 1 week for weekly recurrence
                break;
            case "monthly":
                calendar.add(Calendar.MONTH, 1);        // Add 1 month for monthly recurrence
                break;
            default:
                return null; // Invalid recurrence interval
        }

        return sdf.format(calendar.getTime()); // Return date as a string in the format yyyy-MM-dd
    }

    public static void markTaskComplete(Scanner scanner) {
        TaskManager taskManager = new TaskManager();

        List<Task> tasks = taskManager.getTasks();

        if (tasks.isEmpty()) {
            System.out.println("\nNo tasks available.");
            return;
        }

        System.out.println("\n=== Mark Task as Complete ===");
        System.out.print("Enter the task ID you want to mark as complete: ");
        int taskId = Integer.parseInt(scanner.nextLine());

        // Get object
        Task task = taskManager.getTaskById(taskId);

        if (task == null) {
            System.out.println("Task not found with ID: " + taskId);
        } else {
            // canMarkTaskComplete
            if (taskManager.canMarkTaskComplete(task)) {
                taskManager.markTaskComplete(taskId);
                System.out.println("Task \"" + task.getTitle() + "\" marked as complete.");
            } else {
                System.out.println("Task \"" + task.getTitle() + "\" cannot be marked as complete due to incomplete dependencies.");
            }
        }
    }




    public static void setTaskDependency(Scanner scanner) {
        TaskManager taskManager = new TaskManager();

        List<Task> tasks = taskManager.getTasks();

        if (tasks.isEmpty()) {
            System.out.println("\nNo tasks available.");
            return;
        }

        System.out.println("\n=== Set Task Dependency ===");

        // Display available tasks
        System.out.println("Available tasks:");
        for (Task task : tasks) {
            String completionStatus = task.isCompleted() ? "Complete" : "Incomplete";
            System.out.println("ID: " + task.getId() + ", Title: " + task.getTitle() + ", Status: " + completionStatus);
        }

        try {
            // Get dependent task
            System.out.print("Enter the task ID that depends on another task: ");
            int dependentTaskId = Integer.parseInt(scanner.nextLine());

            Task dependentTask = taskManager.getTaskById(dependentTaskId);
            if (dependentTask == null) {
                System.out.println("Task not found with ID: " + dependentTaskId);
                return;
            }

            // Get the task it depends on
            System.out.print("Enter the task ID it depends on: ");
            int taskId = Integer.parseInt(scanner.nextLine());

            Task task = taskManager.getTaskById(taskId);
            if (task == null) {
                System.out.println("Task not found with ID: " + taskId);
                return;
            }

            // Set the dependency
            if (taskManager.setTaskDependency(dependentTask, task)) {
                System.out.println("Task \"" + dependentTask.getTitle() + "\" now depends on task \"" + task.getTitle() + "\".");
            } else {
                System.out.println("Error: Could not set dependency (circular dependency or invalid task).");
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input format.");
        }
    }


}
