package org.todolist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Task {
    private int id; // Database ID for the task
    private String title;
    private String description;
    private java.sql.Date dueDate;
    private int completionStatus = 0; // Default to 0 (incomplete, as a TINYINT in the database)
    private String category;
    private String priority;
    private List<Task> dependencies = new ArrayList<>();

    private String recurrence;



    // Constructor without ID (for new tasks before saving to the database)
    public Task(String title, String description, java.sql.Date dueDate, String category, String priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.category = category;
        this.priority = priority;
        this.completionStatus = 0;
        this.dependencies = new ArrayList<>();
    }

    // Constructor with ID (for tasks fetched from the database)
    public Task(int id, String title, String description, java.sql.Date dueDate, String category, String priority, int completionStatus) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.category = category;
        this.priority = priority;
        this.completionStatus = completionStatus;
        this.dependencies = new ArrayList<>();
    }

    // Getter and Setter for ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Other getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(java.sql.Date dueDate) {
        this.dueDate = dueDate;
    }

    // Return the completion status as boolean (true for completed, false for incomplete)
    public boolean isCompleted() {
        return completionStatus == 1;
    }

    // Set the completion status, accepts boolean values
    //    public void setCompletionStatus(boolean isCompleted) {
    //        this.completionStatus = isCompleted ? 1 : 0;
    //    }

    // Getter and Setter for task completion status (0 = incomplete, 1 = complete)
    public int getIsCompleted() {
        return completionStatus;
    }

    public void setIsCompleted(int isCompleted) {
        this.completionStatus = isCompleted;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setDependencies(List<Task> dependencies) {
        this.dependencies = dependencies;
    }

    public List<Task> getDependencies() {
        return dependencies;
    }

    public void addDependency(Task task) {
        dependencies.add(task);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s - Due: %s, Priority: %s, Category: %s",
                (completionStatus == 1 ? "Completed" : "Incomplete"), title, description, dueDate, priority, category);
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

}
