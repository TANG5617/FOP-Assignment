package org.todolist;

import service.EmailNotificationService;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TaskManager {
    private List<Task> tasks;

    public TaskManager() {
        this.tasks = new ArrayList<>();
    }

    // Add task
//    public void addTask(Task task) {
//        tasks.add(task);
//        System.out.println("Task added successfully: " + task.getTitle());
//    }
    public void addTask(Task task) {
        String sql = "INSERT INTO tasks (title, description, due_date, category, priority, completion_status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setDate(3, new java.sql.Date(task.getDueDate().getTime()));
            stmt.setString(4, task.getCategory());
            stmt.setString(5, task.getPriority());
            stmt.setBoolean(6, task.isCompleted());
            stmt.executeUpdate();
            System.out.println("Task added to database: " + task.getTitle());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete task
//    public void deleteTask(int index) {
//        if (index >= 0 && index < tasks.size()) {
//            System.out.println("Task deleted: " + tasks.get(index).getTitle());
//            tasks.remove(index);
//        } else {
//            System.out.println("Invalid task index.");
//        }
//    }

    public void deleteTask(int taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, taskId);  // Use the task ID to identify the task to delete
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Task with ID " + taskId + " deleted from database.");
            } else {
                System.out.println("No task found with ID " + taskId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Task> searchTasks(String keyword) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, title, description, due_date, category, priority, completion_status " +
                "FROM tasks WHERE title LIKE ? OR description LIKE ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchKeyword = "%" + keyword.toLowerCase() + "%";

            // Set parameter
            stmt.setString(1, searchKeyword);
            stmt.setString(2, searchKeyword);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String description = rs.getString("description");

                    java.sql.Date dueDate = rs.getDate("due_date");
                    int completionStatus = rs.getInt("completion_status");

                    String category = rs.getString("category");
                    String priority = rs.getString("priority");

                    Task task = new Task(id, title, description, dueDate, category, priority, completionStatus);
                    tasks.add(task);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }


    // Method to get all tasks
//    public List<Task> getTasks() {
//        return tasks;
//    }
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, title, description, due_date, category, priority, completion_status FROM tasks";  // Assuming you're querying a database
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");

                // Retrieve the due date and convert it to java.util.Date
                java.sql.Date dueDate = rs.getDate("due_date");

                String category = rs.getString("category");
                String priority = rs.getString("priority");

                // Retrieve completion status as an int (0 or 1), then convert to boolean
                int completionStatus = rs.getInt("completion_status");

                // Create the Task object with the retrieved data
                Task task = new Task(id, title, description, dueDate, category, priority, completionStatus);
                tasks.add(task);  // Add the task to the list
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }


    // Edit a task's details in the database
    public void editTask(int taskId, String title, String description, java.util.Date dueDate, String category, String priority, boolean completionStatus) {
        String sql = "UPDATE tasks SET title = ?, description = ?, due_date = ?, category = ?, priority = ?, completion_status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("Task ID: " + taskId);
            // Set the updated values for the task, excluding the taskId from modification
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setDate(3, new java.sql.Date(dueDate.getTime())); // Convert java.util.Date to java.sql.Date
            stmt.setString(4, category);
            stmt.setString(5, priority);

            // Fix the completion_status to use int (0 or 1) instead of boolean
            stmt.setInt(6, completionStatus ? 1 : 0);  // Store 1 for true (complete), 0 for false (incomplete)

            // Specify the condition to match the task by its ID (no change to taskId)
            stmt.setInt(7, taskId);

            int rowsUpdated = stmt.executeUpdate(); // Execute the update statement
            System.out.println("Rows updated: " + rowsUpdated);

            if (rowsUpdated > 0) {
                System.out.println("Task updated successfully in the database.");
            } else {
                System.out.println("Task update failed. Task ID may not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sort tasks by Due Date (Ascending)
    public List<Task> getSortedTasksByDueDateAsc() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, title, description, due_date, category, priority, completion_status " +
                "FROM tasks ORDER BY due_date ASC";  // SQL for ascending order

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                java.sql.Date dueDate = rs.getDate("due_date");
                String category = rs.getString("category");
                String priority = rs.getString("priority");
                int completionStatus = rs.getInt("completion_status");

                Task task = new Task(id, title, description, dueDate, category, priority, completionStatus);
                tasks.add(task);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // Sort tasks by Due Date (Descending)
    public List<Task> getSortedTasksByDueDateDesc() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, title, description, due_date, category, priority, completion_status " +
                "FROM tasks ORDER BY due_date DESC";  // SQL for descending order

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                java.sql.Date dueDate = rs.getDate("due_date");
                String category = rs.getString("category");
                String priority = rs.getString("priority");
                int completionStatus = rs.getInt("completion_status");

                Task task = new Task(id, title, description, dueDate, category, priority, completionStatus);
                tasks.add(task);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // Sort tasks by Priority (High to Low)
    public List<Task> getSortedTasksByPriorityHighLow() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, title, description, due_date, category, priority, completion_status " +
                "FROM tasks " +
                "ORDER BY CASE " +
                "WHEN priority = 'High' THEN 1 " +
                "WHEN priority = 'Medium' THEN 2 " +
                "WHEN priority = 'Low' THEN 3 " +
                "ELSE 4 END";  // Custom priority ordering

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                java.sql.Date dueDate = rs.getDate("due_date");
                String category = rs.getString("category");
                String priority = rs.getString("priority");
                int completionStatus = rs.getInt("completion_status");

                Task task = new Task(id, title, description, dueDate, category, priority, completionStatus);
                tasks.add(task);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }


    // Sort tasks by Priority (Low to High)
    public List<Task> getSortedTasksByPriorityLowHigh() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, title, description, due_date, category, priority, completion_status " +
                "FROM tasks " +
                "ORDER BY CASE " +
                "WHEN priority = 'Low' THEN 1 " +
                "WHEN priority = 'Medium' THEN 2 " +
                "WHEN priority = 'High' THEN 3 " +
                "ELSE 4 END";  // Custom priority ordering (Low first)

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                java.sql.Date dueDate = rs.getDate("due_date");
                String category = rs.getString("category");
                String priority = rs.getString("priority");
                int completionStatus = rs.getInt("completion_status");

                Task task = new Task(id, title, description, dueDate, category, priority, completionStatus);
                tasks.add(task);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }


    public void markTaskComplete(int taskId) {
        String sql = "UPDATE tasks SET completion_status = 1 WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, taskId);  // Set task ID to the prepared statement
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Task marked as complete in the database.");
            } else {
                System.out.println("Task update failed. Task ID may not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add dependency to a task (as shown in previous example)
    public boolean addDependency(int taskId, int dependencyId) {
        Task task = getTaskById(taskId);
        Task dependency = getTaskById(dependencyId);

        // Validate tasks
        if (task == null || dependency == null) {
            System.out.println("Error: Invalid task or dependency ID.");
            return false;
        }

        // Add the dependency to the task
        task.addDependency(dependency);
        System.out.println("Task \"" + task.getTitle() + "\" now depends on task \"" + dependency.getTitle() + "\".");
        return true;
    }

    private boolean checkForCircularDependency(int taskId, List<Integer> visitedTasks) {
        // Ensure taskId is within bounds before accessing
        if (taskId < 0 || taskId >= tasks.size()) {
            System.out.println("Invalid taskId: " + taskId);
            return false; // or handle this error appropriately
        }

        Task task = tasks.get(taskId); // Now safe to access

        // If we've already visited this task, it's a circular dependency
        if (visitedTasks.contains(taskId)) {
            return true; // Circular dependency detected
        }

        visitedTasks.add(taskId); // Add current task to visited list

        // Check all dependencies of the current task recursively
        for (Task dependency : task.getDependencies()) {
            int dependencyId = dependency.getId(); // Get the ID of the dependency
            if (checkForCircularDependency(dependencyId, visitedTasks)) {
                return true; // Circular dependency found
            }
        }

        // Backtrack (remove task from visited list)
        visitedTasks.remove(Integer.valueOf(taskId)); // Remove from visited list when backtracking
        return false; // No circular dependency found
    }

    public boolean isCircularDependency(int taskNumber) {
        List<Integer> visitedTasks = new ArrayList<>();
        return checkForCircularDependency(taskNumber, visitedTasks);
    }

    public List<Task> getIncompleteDependencies(Task task) {
        List<Task> incompleteDependencies = new ArrayList<>();
        for (Task dependency : task.getDependencies()) {
            if (!dependency.isCompleted()) {
                incompleteDependencies.add(dependency);
            }
        }
        return incompleteDependencies;
    }

    public Task getTaskById(int id) {
        Task task = null;
        String sql = "SELECT id, title, description, due_date, category, priority, completion_status FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);  // use task id ,do the search condition
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    task = new Task(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getDate("due_date"),
                            rs.getString("category"),
                            rs.getString("priority"),
                            rs.getInt("completion_status")
                    );
                    // Load dependencies from the database
                    List<Task> dependencies = getDependenciesForTask(id);
                    task.setDependencies(dependencies);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return task;
    }


    // Method to get tasks from the database
    public List<Task> getTasksFromDatabase() {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM tasks WHERE due_date >= CURDATE();";
        try (Connection connection = DatabaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                java.sql.Date dueDate = rs.getDate("due_date");
                String category = rs.getString("category");
                String priority = rs.getString("priority");
                int completionStatus = rs.getInt("completion_status");

                Task task = new Task(id, title, description, dueDate, category, priority, completionStatus);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // Send email reminders for tasks due in the next 24 hours
    public void sendTaskReminderNotifications(String userEmail) {
        Calendar now = Calendar.getInstance();
        long currentTimeMillis = now.getTimeInMillis();
        long twentyFourHoursInMillis = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

        List<Task> tasks = getTasksFromDatabase();  // Get tasks from the database

        // Iterate over each task and check if it is due within the next 24 hours
        for (Task task : tasks) {
            long dueTimeMillis = task.getDueDate().getTime();
            long timeDifference = dueTimeMillis - currentTimeMillis;

            if (timeDifference > 0 && timeDifference <= twentyFourHoursInMillis) {
                // If the task is due within the next 24 hours, send a reminder
                sendEmailReminder(userEmail, task);
            }
        }
    }

    private void sendEmailReminder(String userEmail, Task task) {
        // Use EmailNotificationService to send email reminder
        EmailNotificationService emailService = new EmailNotificationService();
        String subject = "Task Reminder: " + task.getTitle();
        String body = "Sending reminder email for task \"" + task.getTitle() + "\" due in 24 hours.";
        emailService.sendEmail(userEmail, subject, body);
    }

    // Set task dependency
    public boolean setTaskDependency(Task dependentTask, Task task) {
        // Ensure there's no circular dependency
        List<Integer> visitedTasks = new ArrayList<>();
        if (checkForCircularDependency(task.getId(), visitedTasks)) {
            System.out.println("Error: Circular dependency detected! Task cannot depend on this task.");
            return false;
        }

        // Add the dependency in the database
        if (addDependencyToDatabase(dependentTask.getId(), task.getId())) {
            System.out.println("Task \"" + dependentTask.getTitle() + "\" now depends on task \"" + task.getTitle() + "\".");
            return true; // Dependency added successfully
        }

        return false; // Failed to add dependency
    }

    private boolean addDependencyToDatabase(int dependentTaskId, int dependencyTaskId) {
        String sql = "INSERT INTO task_dependencies (dependent_task_id, dependency_task_id) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dependentTaskId);
            stmt.setInt(2, dependencyTaskId);

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Task> getDependenciesForTask(int taskId) {
        List<Task> dependencies = new ArrayList<>();
        String sql = "SELECT t.id, t.title, t.description, t.due_date, t.category, t.priority, t.completion_status " +
                "FROM tasks t " +
                "JOIN task_dependencies td ON t.id = td.dependency_task_id " +
                "WHERE td.dependent_task_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, taskId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getDate("due_date"),
                            rs.getString("category"),
                            rs.getString("priority"),
                            rs.getInt("completion_status")
                    );
                    dependencies.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dependencies;
    }

    public boolean canMarkTaskComplete(Task task) {
        List<Task> dependencies = getDependencies(task.getId());

        // check
        for (Task dependency : dependencies) {
            if (!dependency.isCompleted()) {
                return false;  // no
            }
        }
        return true;  // mark
    }

    public List<Task> getDependencies(int taskId) {
        String sql = "SELECT td.dependency_task_id FROM task_dependencies td WHERE td.dependent_task_id = ?";
        List<Task> dependencies = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId); // 设置 dependent_task_id 参数
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int dependencyTaskId = rs.getInt("dependency_task_id");
                    Task dependency = getTaskById(dependencyTaskId);
                    if (dependency != null) {
                        dependencies.add(dependency);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dependencies;
    }

}
