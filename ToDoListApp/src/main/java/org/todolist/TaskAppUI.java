package org.todolist;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskAppUI extends Application {
    private TaskManager taskManager = new TaskManager();
    private ListView<String> taskListView = new ListView<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("To-Do List App");

        // Initialize the task manager and load tasks from the database at startup
        updateTaskList();

        // Main layout
        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 20; -fx-background-color: #F5F5F5; -fx-alignment: center;");

        // Title label
        Label title = new Label("To-Do List");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: black;");
        root.getChildren().add(title);

        // Search bar for searching tasks
        TextField searchField = new TextField();
        searchField.setPromptText("Enter a keyword to search by title or description");

        Button searchButton = new Button("Search");
        Button clearSearchButton = new Button("Clear Search");

        // Define the event for search
        searchButton.setOnAction(e -> searchTasks(searchField.getText()));

        // Define the event for clearing the search
        clearSearchButton.setOnAction(e -> restoreOriginalTasks());

        // Layout for the search bar and buttons
        VBox searchLayout = new VBox(10, searchField, searchButton, clearSearchButton);
        root.getChildren().add(searchLayout);

        // Task List View
        taskListView.setPrefHeight(300);
        taskListView.setStyle("-fx-background-color: white; -fx-text-fill: #2C2C2C;");
        root.getChildren().add(taskListView);

        // Button layout (two lines of buttons)
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        // First row of buttons
        HBox firstRow = new HBox(10);
        firstRow.setAlignment(Pos.CENTER);
        Button addButton = new Button("Add Task");
        Button deleteButton = new Button("Delete Task");
        Button recurringButton = new Button("Recurring Task");
        firstRow.getChildren().addAll(addButton, deleteButton, recurringButton);

        // Second row of buttons
        HBox secondRow = new HBox(10);
        secondRow.setAlignment(Pos.CENTER);
        Button markCompleteButton = new Button("Mark Complete");
        Button sortButton = new Button("Sort Tasks");
        Button analyticButton = new Button("Data Analytics");
        secondRow.getChildren().addAll(markCompleteButton, sortButton, analyticButton);

        // Add the rows to the button box
        buttonBox.getChildren().addAll(firstRow, secondRow);
        root.getChildren().add(buttonBox);

        // Event handling for task management buttons
        addButton.setOnAction(e -> showAddTaskDialog());
        deleteButton.setOnAction(e -> deleteSelectedTask());
        markCompleteButton.setOnAction(e -> markSelectedTaskComplete());
        sortButton.setOnAction(e -> showSortOptionsDialog());
        recurringButton.setOnAction(e -> showRecurringTaskDialog());
        analyticButton.setOnAction(e -> showAnalyticsDashboard());

        // Task List Double-Click Event (for editing)
        List<Task> tasks = taskManager.getTasks();  // Initial list of tasks
        taskListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {  // Double-click check
                int selectedIndex = taskListView.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    Task selectedTask = tasks.get(selectedIndex);
                    showEditOptionsDialog(selectedTask);  // Open edit dialog for the selected task
                }
            }
        });

        // Set up scene and stage
        Scene scene = new Scene(root, 600, 700, Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Show Add Task dialog
    private void showAddTaskDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add New Task");

        VBox dialogRoot = new VBox(15);
        dialogRoot.setStyle("-fx-padding: 20;");
        dialogRoot.setAlignment(Pos.CENTER);

        TextField titleField = new TextField();
        titleField.setPromptText("Enter task title:");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Enter task description:");

        // DatePicker for due date instead of a TextField
        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Select due date");

        ComboBox<String> categoryField = new ComboBox<>();
        categoryField.getItems().addAll("Homework", "Personal", "Work");
        categoryField.setValue("Homework");
        categoryField.setPromptText("Category");

        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll("Low", "Medium", "High");
        priorityBox.setValue("Low");
        priorityBox.setPromptText("Priority");

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            try {
                String title = titleField.getText();
                String description = descriptionField.getText();
                LocalDate localDueDate = dueDatePicker.getValue();
                String category = categoryField.getValue();
                String priority = priorityBox.getValue();

                java.sql.Date dueDate = java.sql.Date.valueOf(localDueDate);

                Task newTask = new Task(title, description, dueDate, category, priority);
                taskManager.addTask(newTask);
                updateTaskList();
                dialog.close();
                showAlert("Task Added", "Task \"" + title + "\" added successfully!");

            } catch (Exception ex) {
                showAlert("Invalid Input", "Please enter valid task details.");
            }
        });

        dialogRoot.getChildren().addAll(titleField, descriptionField, dueDatePicker, categoryField, priorityBox, saveButton);

        Scene dialogScene = new Scene(dialogRoot, 300, 400, Color.BLACK);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    // Search tasks based on the keyword in the title or description
//    private void searchTasks(String keyword) {
//        List<Task> filteredTasks = taskManager.searchTasks(keyword);  // Call searchTasks in TaskManager
//        updateTaskList(filteredTasks);
//    }

    // Delete selected task
    private void deleteSelectedTask() {
        int selectedIndex = taskListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            String taskName = taskManager.getTasks().get(selectedIndex).getTitle();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Task");
            alert.setContentText("Are you sure you want to delete task \"" + taskName + "\"?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    taskManager.deleteTask(selectedIndex);
                    updateTaskList();
                    showAlert("Task Deleted", "Task \"" + taskName + "\" deleted successfully!");
                }
            });
        } else {
            showAlert("No Selection", "Please select a task to delete.");
        }
    }

    private void markSelectedTaskComplete() {
        // Check if there are any tasks in the list
        List<Task> tasks = taskManager.getTasks();
        if (tasks.isEmpty()) {
            showAlert("No Tasks", "There are no tasks to mark as complete.");
            return;
        }

        // Get the selected index from the ListView
        int selectedIndex = taskListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0) {
            Task selectedTask = tasks.get(selectedIndex);

            // Check for incomplete dependencies
            List<Task> incompleteDependencies = taskManager.getIncompleteDependencies(selectedTask);

            if (!incompleteDependencies.isEmpty()) {
                // Gather the names of the incomplete dependencies
                StringBuilder dependenciesNames = new StringBuilder();
                for (Task dependency : incompleteDependencies) {
                    dependenciesNames.append("\"").append(dependency.getTitle()).append("\"").append("\n");
                }

                // Show the custom warning alert with the incomplete dependencies
                showAlert("Warning",
                        "Task \"" + selectedTask.getTitle() + "\" cannot be marked as complete because it depends on:" +
                                dependenciesNames + "Please complete these tasks first.");
            } else {
                // If no incomplete dependencies, mark the task as complete
                taskManager.markTaskComplete(selectedTask.getId()); // Pass taskId instead of index

                // Update the task list in the UI to reflect the completion status
                updateTaskList();

                // Show success message
                showAlert("Task Complete", "Task \"" + selectedTask.getTitle() + "\" marked as complete!");
            }
        } else {
            showAlert("No Selection", "Please select a task to mark as complete.");
        }
    }




    // Edit selected task
//    private void showEditTaskDialog() {
//        Stage dialog = new Stage();
//        dialog.setTitle("Edit Task");
//
//        VBox layout = new VBox(15);
//        layout.setStyle("-fx-padding: 20;");
//        layout.setAlignment(Pos.CENTER);
//
//        Label titleLabel = new Label("Task List:");
//        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
//        layout.getChildren().add(titleLabel);
//
//        ListView<String> taskListView = new ListView<>();
//        List<Task> tasks = taskManager.getTasks();
//
//        for (Task task : tasks) {
//            String status = task.isCompleted() ? "[Complete]" : "[Incomplete]";
//            String displayText = String.format(
//                    "%s - %s - %s",
//                    status,
//                    task.getTitle(),
//                    task.getDescription()
//            );
//            taskListView.getItems().add(displayText);
//        }
//
//        layout.getChildren().add(taskListView);
//
//        taskListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
//            int selectedIndex = newValue.intValue();
//            if (selectedIndex >= 0) {
//                Task selectedTask = tasks.get(selectedIndex);
//                showEditOptionsDialog(selectedTask);  // Modify this method to accept a taskId
//            }
//        });
//
//        Scene scene = new Scene(layout, 400, 500);
//        dialog.setScene(scene);
//        dialog.show();
//    }

    // Show the task edit options (Title, Description, Due Date, etc.)
    private void showEditOptionsDialog(Task taskToEdit) {
        Stage editDialog = new Stage();
        editDialog.setTitle("Edit Task: " + taskToEdit.getTitle());

        VBox editDialogRoot = new VBox(15);
        editDialogRoot.setStyle("-fx-padding: 20;");
        editDialogRoot.setAlignment(Pos.CENTER);

        Label editLabel = new Label("What would you like to edit?");
        editDialogRoot.getChildren().add(editLabel);

        Button editTitleButton = new Button("1. Title");
        Button editDescriptionButton = new Button("2. Description");
        Button editDueDateButton = new Button("3. Due Date");
        Button editCategoryButton = new Button("4. Category");
        Button editPriorityButton = new Button("5. Priority");
        Button setDependencyButton = new Button("6. Set Task Dependency");
        Button cancelButton = new Button("7. Cancel");

        editTitleButton.setOnAction(e -> showEditTitleDialog(taskToEdit));
        editDescriptionButton.setOnAction(e -> showEditDescriptionDialog(taskToEdit));
        editDueDateButton.setOnAction(e -> showEditDueDateDialog(taskToEdit));
        editCategoryButton.setOnAction(e -> showEditCategoryDialog(taskToEdit));
        editPriorityButton.setOnAction(e -> showEditPriorityDialog(taskToEdit));
        setDependencyButton.setOnAction(e -> showSetDependencyDialog());
        cancelButton.setOnAction(e -> editDialog.close());

        editDialogRoot.getChildren().addAll(
                editTitleButton,
                editDescriptionButton,
                editDueDateButton,
                editCategoryButton,
                editPriorityButton,
                setDependencyButton,
                cancelButton
        );

        Scene editScene = new Scene(editDialogRoot, 300, 400);
        editDialog.setScene(editScene);
        editDialog.show();
    }


    // Edit Title
    private void showEditTitleDialog(Task taskToEdit) {
        Stage titleDialog = new Stage();
        titleDialog.setTitle("Edit Title");

        VBox titleDialogRoot = new VBox(15);
        titleDialogRoot.setStyle("-fx-padding: 20;");
        titleDialogRoot.setAlignment(Pos.CENTER);

        TextField titleField = new TextField(taskToEdit.getTitle());
        titleField.setPromptText("Enter new title:");
        Button saveButton = new Button("Save Title");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        saveButton.setOnAction(e -> {
            String newTitle = titleField.getText();
            taskToEdit.setTitle(newTitle); // Update in-memory object
            taskManager.editTask(taskToEdit.getId(), newTitle, taskToEdit.getDescription(), taskToEdit.getDueDate(),
                    taskToEdit.getCategory(), taskToEdit.getPriority(), taskToEdit.isCompleted());
            updateTaskList();
            showAlert("Task Updated", "Task title updated successfully!");
            titleDialog.close();
        });

        titleDialogRoot.getChildren().addAll(titleField, saveButton);

        Scene titleScene = new Scene(titleDialogRoot, 300, 200, Color.BLACK);
        titleDialog.setScene(titleScene);
        titleDialog.show();
    }

    // Edit Description
    private void showEditDescriptionDialog(Task taskToEdit) {
        Stage descriptionDialog = new Stage();
        descriptionDialog.setTitle("Edit Description");

        VBox descriptionDialogRoot = new VBox(15);
        descriptionDialogRoot.setStyle("-fx-padding: 20;");
        descriptionDialogRoot.setAlignment(Pos.CENTER);

        TextField descriptionField = new TextField(taskToEdit.getDescription());
        descriptionField.setPromptText("Enter new description:");
        Button saveButton = new Button("Save Description");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            String newDescription = descriptionField.getText();
            taskToEdit.setDescription(newDescription);
            taskManager.editTask(taskToEdit.getId(), newDescription, taskToEdit.getDescription(), taskToEdit.getDueDate(),
                    taskToEdit.getCategory(), taskToEdit.getPriority(), taskToEdit.isCompleted());
            updateTaskList();
            showAlert("Task Updated", "Task description updated successfully!");
            descriptionDialog.close();
        });

        descriptionDialogRoot.getChildren().addAll(descriptionField, saveButton);

        Scene descriptionScene = new Scene(descriptionDialogRoot, 300, 200, Color.BLACK);
        descriptionDialog.setScene(descriptionScene);
        descriptionDialog.show();
    }


    // Edit Due Date
    private void showEditDueDateDialog(Task taskToEdit) {
        Stage dueDateDialog = new Stage();
        dueDateDialog.setTitle("Edit Due Date");

        VBox dueDateDialogRoot = new VBox(15);
        dueDateDialogRoot.setStyle("-fx-padding: 20;");
        dueDateDialogRoot.setAlignment(Pos.CENTER);

        DatePicker dueDatePicker = new DatePicker();

        // Pre-fill the DatePicker with the current due date
        java.util.Date utilDueDate = taskToEdit.getDueDate();  // Get the java.util.Date
        if (utilDueDate != null) {
            java.sql.Date sqlDueDate = new java.sql.Date(utilDueDate.getTime()); // Convert to java.sql.Date
            dueDatePicker.setValue(sqlDueDate.toLocalDate());  // Set it in DatePicker
        } else {
            dueDatePicker.setValue(null);  // If no due date, set it to null
        }

        Button saveButton = new Button("Save Due Date");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        saveButton.setOnAction(e -> {
            try {
                // Get the selected date from the DatePicker
                LocalDate newDueDate = dueDatePicker.getValue();
                if (newDueDate != null) {
                    java.sql.Date updatedDueDate = java.sql.Date.valueOf(newDueDate); // Convert back to java.sql.Date
                    taskToEdit.setDueDate(updatedDueDate);  // Update the task's due date
                    taskManager.editTask(
                            taskToEdit.getId(),
                            taskToEdit.getTitle(),
                            taskToEdit.getDescription(),
                            updatedDueDate,
                            taskToEdit.getCategory(),
                            taskToEdit.getPriority(),
                            taskToEdit.isCompleted()
                    );
                } else {
                    showAlert("Invalid Date", "Please select a valid due date.");
                }

                // Update the task list in the UI
                updateTaskList();

                // Show success alert and close the dialog
                showAlert("Task Updated", "Task due date updated successfully!");
                dueDateDialog.close();
            } catch (Exception ex) {
                showAlert("Error", "An error occurred while updating the due date.");
                ex.printStackTrace();
            }
        });

        // Add components to the layout
        dueDateDialogRoot.getChildren().addAll(dueDatePicker, saveButton);

        // Set the scene and show the dialog
        Scene dueDateScene = new Scene(dueDateDialogRoot, 300, 200, Color.BLACK);
        dueDateDialog.setScene(dueDateScene);
        dueDateDialog.show();
    }



    // Edit Category
    private void showEditCategoryDialog(Task taskToEdit) {
        Stage categoryDialog = new Stage();
        categoryDialog.setTitle("Edit Category");

        VBox categoryDialogRoot = new VBox(15);
        categoryDialogRoot.setStyle("-fx-padding: 20;");
        categoryDialogRoot.setAlignment(Pos.CENTER);

        // Create a combobox for the user to enter a new category
        ComboBox<String> categoryField = new ComboBox<>();
        categoryField.getItems().addAll("Homework", "Personal", "Work");
        categoryField.setValue(taskToEdit.getCategory());

        Button saveButton = new Button("Save Category");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            String newCategory = categoryField.getValue();
            if (!newCategory.isEmpty()) {
                taskToEdit.setCategory(newCategory); // Set the new category
                taskManager.editTask(taskToEdit.getId(), newCategory, taskToEdit.getDescription(), taskToEdit.getDueDate(),
                        taskToEdit.getCategory(), taskToEdit.getPriority(), taskToEdit.isCompleted());
                updateTaskList();
                showAlert("Task Updated", "Task category updated successfully!");
                categoryDialog.close();
            } else {
                showAlert("Invalid Category", "Please select a valid category");
            }
        });

        categoryDialogRoot.getChildren().addAll(categoryField, saveButton);

        Scene categoryScene = new Scene(categoryDialogRoot, 300, 200, Color.BLACK);
        categoryDialog.setScene(categoryScene);
        categoryDialog.show();
    }

    // Edit Priority
    private void showEditPriorityDialog(Task taskToEdit) {
        Stage priorityDialog = new Stage();
        priorityDialog.setTitle("Edit Priority");

        VBox priorityDialogRoot = new VBox(15);
        priorityDialogRoot.setStyle("-fx-padding: 20;");
        priorityDialogRoot.setAlignment(Pos.CENTER);

        // Create a combo box to select the priority
        ComboBox<String> priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll("Low", "Medium", "High");
        priorityComboBox.setValue(taskToEdit.getPriority()); // Set current priority if available

        Button saveButton = new Button("Save Priority");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            String newPriority = priorityComboBox.getValue();
            if (newPriority != null && !newPriority.isEmpty()) {
                taskToEdit.setPriority(newPriority); // Set the new priority
                taskManager.editTask(taskToEdit.getId(), newPriority, taskToEdit.getDescription(), taskToEdit.getDueDate(),
                        taskToEdit.getCategory(), taskToEdit.getPriority(), taskToEdit.isCompleted());
                updateTaskList();
                showAlert("Task Updated", "Task priority updated successfully!");
                priorityDialog.close();
            } else {
                showAlert("Invalid Priority", "Please select a valid priority.");
            }
        });

        priorityDialogRoot.getChildren().addAll(priorityComboBox, saveButton);

        Scene priorityScene = new Scene(priorityDialogRoot, 300, 200, Color.BLACK);
        priorityDialog.setScene(priorityScene);
        priorityDialog.show();
    }

    // Set Task Dependency (within Edit Task)
    private void showSetDependencyDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Set Task Dependency");

        VBox dialogRoot = new VBox(15);
        dialogRoot.setStyle("-fx-padding: 20;");
        dialogRoot.setAlignment(Pos.CENTER);

        // Display all tasks with their task numbers
        Label taskListLabel = new Label("Select a task number and the task it depends on.\n\n");
        StringBuilder taskListString = new StringBuilder("Task List:\n");
        int taskNumber = 1;
        for (Task task : taskManager.getTasks()) {
            taskListString.append(taskNumber).append(". ").append(task.getTitle()).append("\n");
            taskNumber++;
        }
        taskListLabel.setText(taskListString.toString());

        // Text fields to input task numbers
        TextField taskField = new TextField();
        taskField.setPromptText("Enter task number that depends on another task:");

        TextField dependencyField = new TextField();
        dependencyField.setPromptText("Enter the task number it depends on:");

        Button saveButton = new Button("Set Dependency");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            try {
                int taskNumberInput = Integer.parseInt(taskField.getText());
                int dependencyNumberInput = Integer.parseInt(dependencyField.getText());

                // Check for valid task numbers
                if (taskNumberInput <= 0 || taskNumberInput > taskManager.getTasks().size() ||
                        dependencyNumberInput <= 0 || dependencyNumberInput > taskManager.getTasks().size()) {
                    showAlert("Invalid Input", "Please enter valid task numbers from the list.");
                    return;
                }

                Task task = taskManager.getTasks().get(taskNumberInput - 1); // Get task by its number
                Task dependency = taskManager.getTasks().get(dependencyNumberInput - 1); // Get dependency task

                // Check if task is trying to depend on itself
                if (taskNumberInput == dependencyNumberInput) {
                    showAlert("Invalid Dependency", "A task cannot depend on itself.");
                    return;
                }

                // Check for circular dependency using the public method
                if (taskManager.isCircularDependency(dependencyNumberInput - 1)) {
                    showAlert("Circular Dependency", "Cannot add this dependency due to a circular reference.");
                    return;
                }

                // Add the dependency if valid
                boolean isDependencyAdded = taskManager.addDependency(taskNumberInput, dependencyNumberInput);
                if (isDependencyAdded) {
                    showAlert("Dependency Set", "Task \"" + task.getTitle() + "\" now depends on task \"" + dependency.getTitle() + "\".");
                }

                dialog.close();
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter valid numeric task numbers.");
            } catch (IndexOutOfBoundsException ex) {
                showAlert("Invalid Task Number", "Please enter a valid task number.");
            }
        });

        dialogRoot.getChildren().addAll(taskListLabel, taskField, dependencyField, saveButton);

        Scene dialogScene = new Scene(dialogRoot, 300, 300, Color.BLACK);
        dialog.setScene(dialogScene);
        dialog.show();
    }


    // Show Recurring Task dialog
    private void showRecurringTaskDialog() {
        // You can add functionality for recurring tasks here
        showAlert("Recurring Task", "Recurring Task functionality will be added here.");
    }

    // Sort tasks
    private void showSortOptionsDialog() {
        // Create a new dialog for sorting options
        Stage sortDialog = new Stage();
        sortDialog.setTitle("Sort Tasks");

        VBox sortDialogRoot = new VBox(15);
        sortDialogRoot.setAlignment(Pos.CENTER);
        sortDialogRoot.setStyle("-fx-padding: 20;");

        // Label for instructions
        Label sortLabel = new Label("Sort by:");
        sortDialogRoot.getChildren().add(sortLabel);

        // Sort options
        Button sortByDueDateAscButton = new Button("Due Date (Ascending)");
        Button sortByDueDateDescButton = new Button("Due Date (Descending)");
        Button sortByPriorityHighLowButton = new Button("Priority (High to Low)");
        Button sortByPriorityLowHighButton = new Button("Priority (Low to High)");

        // Button actions to trigger sorting
        sortByDueDateAscButton.setOnAction(e -> {
            List<Task> sortedTasks = taskManager.getSortedTasksByDueDateAsc();
            updateTaskList(sortedTasks);
            sortDialog.close();
            showAlert("Sorting", "Tasks sorted by Due Date (Ascending)!");
        });
        sortByDueDateDescButton.setOnAction(e -> {
            List<Task> sortedTasks = taskManager.getSortedTasksByDueDateDesc();
            updateTaskList(sortedTasks);
            sortDialog.close();
            showAlert("Sorting", "Tasks sorted by Due Date (Descending)!");
        });
        sortByPriorityHighLowButton.setOnAction(e -> {
            List<Task> sortedTasks = taskManager.getSortedTasksByPriorityHighLow();
            updateTaskList(sortedTasks);
            sortDialog.close();
            showAlert("Sorting", "Tasks sorted by Priority (High to Low)!");
        });
        sortByPriorityLowHighButton.setOnAction(e -> {
            List<Task> sortedTasks = taskManager.getSortedTasksByPriorityLowHigh();
            updateTaskList(sortedTasks);
            sortDialog.close();
            showAlert("Sorting", "Tasks sorted by Priority (Low to High)!");
        });

        // Add the buttons to the dialog layout
        sortDialogRoot.getChildren().addAll(sortByDueDateAscButton, sortByDueDateDescButton, sortByPriorityHighLowButton, sortByPriorityLowHighButton);

        // Show the sorting dialog
        Scene sortDialogScene = new Scene(sortDialogRoot, 300, 200, Color.BLACK);
        sortDialog.setScene(sortDialogScene);
        sortDialog.show();
    }


    private void showAnalyticsDashboard() {
        Stage analyticsDialog = new Stage();
        analyticsDialog.setTitle("Task Analytics Dashboard");

        VBox analyticsDialogRoot = new VBox(15);
        analyticsDialogRoot.setStyle("-fx-padding: 20;");
        analyticsDialogRoot.setAlignment(Pos.CENTER);

        // Calculate the analytics data
        int totalTasks = taskManager.getTasks().size();
        int completedTasks = 0;
        int pendingTasks = 0;
        int homeworkCount = 0;
        int personalCount = 0;
        int workCount = 0;

        for (Task task : taskManager.getTasks()) {
            if (task.isCompleted()) {
                completedTasks++;
            } else {
                pendingTasks++;
            }

            switch (task.getCategory()) {
                case "Homework":
                    homeworkCount++;
                    break;
                case "Personal":
                    personalCount++;
                    break;
                case "Work":
                    workCount++;
                    break;
            }
        }

        double completionRate = (totalTasks == 0) ? 0 : (double) completedTasks / totalTasks * 100;

        // Create labels for the statistics
        Label totalTasksLabel = new Label("Total Tasks: " + totalTasks);
        Label completedLabel = new Label("Completed: " + completedTasks);
        Label pendingLabel = new Label("Pending: " + pendingTasks);
        Label completionRateLabel = new Label(String.format("Completion Rate: %.2f%%", completionRate));
        Label categorizedTasksLabel = new Label(String.format("Task Categories:\nHomework: %d\nPersonal: %d\nWork: %d", homeworkCount, personalCount, workCount));

        // Add the labels to the layout
        analyticsDialogRoot.getChildren().addAll(
                totalTasksLabel,
                completedLabel,
                pendingLabel,
                completionRateLabel,
                categorizedTasksLabel
        );

        // Set up the scene
        Scene analyticsScene = new Scene(analyticsDialogRoot, 300, 300, Color.BLACK);
        analyticsDialog.setScene(analyticsScene);
        analyticsDialog.show();
    }

//    // Update the task list view
//    private void updateTaskList() {
//        // Refresh the ListView with the updated tasks
//        taskListView.getItems().clear();
//        for (Task task : taskManager.getTasks()) {
//            taskListView.getItems().add(task.toString());
//        }
//    }

    private void searchTasks(String keyword) {
        // Filter tasks based on the keyword
        List<Task> filteredTasks = taskManager.searchTasks(keyword);

        // If no tasks are found, show "No results found"
        if (filteredTasks.isEmpty()) {
            ObservableList<String> noResultsMessage = FXCollections.observableArrayList("No results found.");
            taskListView.setItems(noResultsMessage);  // Set the ListView to show the "No results" message
        } else {
            updateTaskList(filteredTasks);  // Update task list to show the filtered tasks
        }
    }

    // Update the task list in the UI based on the filtered tasks
    private void updateTaskList(List<Task> tasks) {
//        taskListView.getItems().clear();
//        for (Task task : tasks) {
//            taskListView.getItems().add(task.toString());
//        }
        ObservableList<String> taskStrings = FXCollections.observableArrayList();

            for (Task task : tasks) {
                // Ensure the format is consistent: [complete] - title - description - due date - priority - category
                taskStrings.add(task.toString());
            }

            taskListView.setItems(taskStrings);  // Update ListView with the formatted tasks
    }

    private void restoreOriginalTasks() {
        updateTaskList();
    }

    // Overloaded updateTaskList to update all tasks (no search filter)
    private void updateTaskList() {
        if (taskListView == null) {
            throw new IllegalStateException("Task list view is not initialized.");
        }

        taskListView.getItems().clear();
        List<Task> tasks = taskManager.getTasks();// Fetch tasks from the database

        if (tasks == null) {
            showAlert("Error", "Failed to load tasks. Please try again.");
            return;
        }

        for (Task task : tasks) {
            // 检查各字段是否为 null，提供默认值
            String title = task.getTitle() != null ? task.getTitle() : "No Title";
            String description = task.getDescription() != null ? task.getDescription() : "No Description";
            String dueDate = task.getDueDate() != null ? task.getDueDate().toString() : "No Due Date";
            String priority = task.getPriority() != null ? task.getPriority() : "No Priority";
            String category = task.getCategory() != null ? task.getCategory() : "No Category";
            String status = task.isCompleted() ? "[Complete]" : "[Incomplete]";

            // 格式化任务信息
            String displayText = String.format(
                    "%s - %s - %s - Due: %s, Priority: %s, Category: %s",
                    status, title, description, dueDate, priority, category
            );

            taskListView.getItems().add(displayText); // 添加到任务列表
        }
    }


    // Show alert messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
