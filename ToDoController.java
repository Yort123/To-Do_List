package com.example.toDoList;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import org.controlsfx.control.PropertySheet;
import java.net.URL;
import java.time.LocalDate;
import javafx.scene.control.TableCell;
import java.util.ResourceBundle;


public class ToDoController implements Initializable {

    @FXML
    public TableView<NewTask> tableView;
    @FXML
    public DatePicker date;

    @FXML
    private Button addButton;

    @FXML
    private TableColumn<NewTask, String> completed;

    @FXML
    private TextField taskName;

    @FXML
    private TableColumn<NewTask, String> toDo;
    private ObservableList<NewTask> temp;
    private TableRow<NewTask> draggedRow;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        toDo.setCellValueFactory(new PropertyValueFactory<NewTask, String>("combo")); // initially put the task in the to do column
        completed.setCellValueFactory(new PropertyValueFactory<>(""));
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        temp = tableView.getSelectionModel().getSelectedItems();



        toDo.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                } else {
                    NewTask task = getTableView().getItems().get(getIndex());

                    LocalDate currentDate = LocalDate.now();

                    if (task.getDueDate() != null && task.getDueDate().isBefore(currentDate)){  // If the due date is past then the task will show up red
                        setStyle("-fx-background-color: red;");
                    } else {
                        setStyle("");
                    }
                    setText(item);
                }
            }
        });


        completed.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                } else {
                    NewTask task = getTableView().getItems().get(getIndex());

                    LocalDate currentDate = LocalDate.now();

                    if (task.getDueDate() != null && task.getDueDate().isBefore(currentDate)){  // If the due date is past then the task will show up red
                        setStyle("-fx-background-color: red;");
                    } else {
                        setStyle("");
                    }
                    setText(item);
                }
            }
        });

    }

    private void setDragAndDropHandlers(TableCell<NewTask, String> cell) {
        cell.setOnDragDetected(event -> {
            if (!cell.isEmpty()) {
                Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(cell.getItem());
                db.setContent(content);
                event.consume();
            }
        });

        cell.setOnDragOver(event -> {
            if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        cell.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                success = true;

                // Move the task to the new column
                moveTaskToColumn(db.getString(), cell.getTableColumn());
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void moveTaskToColumn(String taskName, TableColumn<NewTask, String> targetColumn) {
        NewTask taskToMove = tableView.getItems().stream()
                .filter(task -> task.getTaskName().equals(taskName))
                .findFirst()
                .orElse(null);

        if (taskToMove != null) {
            // Update the task's status based on the target column
            taskToMove.setStatus(targetColumn.getText());

            // Remove the task from its current position
            tableView.getItems().remove(taskToMove);

            // Add the task to the new position
            tableView.getItems().add(taskToMove);

            // Refresh the TableView
            tableView.refresh();
        }
    }
    private TableColumn<NewTask, String> getColumnForTableRow(TableRow<NewTask> row) {
        // Determine the target column based on the TableView columns
        if (row.getIndex() >= 0 && row.getIndex() < tableView.getItems().size()) {
            NewTask task = tableView.getItems().get(row.getIndex());
            String taskStatus = task.getStatus();

            switch (taskStatus) {
                case "To-Do":
                    return toDo;
                case "Completed":
                    return completed;

            }
        }

        return null;
    }

    @FXML
    public void addButton() {
        /*
         parameters: None
         returns: Nothing
         When the add button is hit, what is in the taskName text field and in the date field are placed into the
         to-do column
         */
        NewTask test = new NewTask(taskName.getText(), date.getValue());

        if (taskName != null && date != null) {

            toDo.getTableView().getItems().add(test);
            clearFields();
        }
    }

    private void clearFields() {
        taskName.clear();
        date.setValue(null);
    }
}


