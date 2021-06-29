package be.ac.ulb.infof307.g04.view.task;

import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.view.popup.InfoPopup;
import be.ac.ulb.infof307.g04.util.ToolDate;
import be.ac.ulb.infof307.g04.view.project.ProjectContentController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

public class AddTaskDialogController {

    private TaskDialogListener listener;

    @FXML
    private TextField titleField;
    @FXML
    private TextField taskDescriptionField;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private ComboBox<String> projectComboBox;
    @FXML
    private Button applyChangesButton;

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        startDate.setValue(LocalDate.now());
        endDate.setValue(LocalDate.now());
        ToolDate.disablePastDates(endDate);
        ToolDate.disablePastDates(startDate);
        applyChangesButton.setDisable(true);
        titleField.textProperty().addListener((observable, newValue, oldValue) -> {
            applyChangesButton.setDisable(newValue.equals(""));
            applyChangesButton.setDisable(oldValue.equals(""));
        });
    }

    /**
     * Is called by user click on 'Ajouter' button
     */
    @FXML
    public void handleAdd() throws UserExceptions {
        if(ToolDate.isEarlierThanDate(startDate.getValue(), endDate.getValue())) {
            if (!listener.isTaskDateValid(startDate.getValue(), endDate.getValue())){
                Alert alert = InfoPopup.showPopup("Avertissement", "La date de fin de tâche dépasse celle de fin de projet");
                alert.showAndWait();
            } else {
                if (listener.taskTitleExists(titleField.getText())) {
                    Alert alert = InfoPopup.showPopup("Avertissement", "Le titre de cette tâche existe déjà !");
                    alert.showAndWait();
                } else {
                    listener.addTask(titleField.getText(),  startDate.getValue(), endDate.getValue(), taskDescriptionField.getText(), projectComboBox.getValue());
                    TaskPanel taskPanel = createPanelTask(titleField.getText(), taskDescriptionField.getText(), startDate.getValue(), endDate.getValue(), projectComboBox.getValue());
                    listener.getProjectContentController().addTaskPanelToAccordion(taskPanel.getPanel());
                }
            }
        } else {
            Alert alert = InfoPopup.showPopup("Avertissement", "La date de début dépasse celle de fin");
            alert.showAndWait();
        }

    }

    /**
     * Is called by user click on 'Annuler' button
     */
    @FXML
    public void handleCancel() {
        listener.closeCurrentStage();
    }

    /**
     * Adds a panel containing all details of a Task given in parameter
     *
     * @param title : title of task
     * @param description : description of task
     * @param startDate : date of start of task
     * @param endDate : date of end of task
     * @param collaborator : collaborator assigned to the task
     */
    private TaskPanel createPanelTask(String title, String description, LocalDate startDate, LocalDate endDate, String collaborator) {
        TaskPanel taskPanel = new TaskPanel(title, description, startDate, endDate, Collections.singleton(collaborator)) ;
        listener.putPanelToMap(title, taskPanel);
        // Handle edit button
        EventHandler<ActionEvent> modifyButtonHandler = event ->{
            Button btn = (Button) event.getSource();
            listener.showEditTask(btn.getAccessibleText());
            listener.refreshTask(taskPanel, btn.getAccessibleText());
        };
        taskPanel.getModifyButton().setOnAction(modifyButtonHandler);
        return taskPanel;
    }

    /**
     * SetProjectComboBox
     */
    public void setProjectComboBox(Set<String> listProjectKeySet) {
        projectComboBox.getItems().setAll(listProjectKeySet);
    }

    /**
     * SetListener
     */
    public void setListener(TaskDialogListener listener) { this.listener = listener; }

    public interface TaskDialogListener {
        void closeCurrentStage();
        void addTask(String title, LocalDate startDate, LocalDate endDate, String description, String collabo) throws UserExceptions;
        void showEditTask(String taskName);
        ProjectContentController getProjectContentController();
        void putPanelToMap(String title, TaskPanel taskPanel);
        void refreshTask(TaskPanel taskPanel, String taskTitle);
        boolean taskTitleExists(String taskTitle);
        boolean isTaskDateValid(LocalDate startDate, LocalDate endDate);
    }
}
