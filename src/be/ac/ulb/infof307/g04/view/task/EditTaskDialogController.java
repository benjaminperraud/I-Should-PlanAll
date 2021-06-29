package be.ac.ulb.infof307.g04.view.task;

import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.util.ToolDate;
import be.ac.ulb.infof307.g04.view.popup.InfoPopup;
import be.ac.ulb.infof307.g04.view.project.ProjectContentController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.*;

public class EditTaskDialogController {

    private EditTaskDialogListener listener;

    @FXML
    private TextField titleField;
    @FXML
    private TextArea taskDescriptionArea;
    @FXML
    private Button applyChangesButton;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private ComboBox<String> projectComboBox;


    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     * */
    @FXML
    public void initialise(String title, String description, LocalDate start, LocalDate end){
        applyChangesButton.setText("Appliquer les changements");
        titleField.setText(title);
        taskDescriptionArea.setText(description);
        startDate.setValue(start);
        endDate.setValue(end);
        ToolDate.disablePastDates(startDate);
        ToolDate.disablePastDates(endDate);
        // disable button if title is empty
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyChangesButton.setDisable(newValue.equals(""));
        });
    }

    /**
     * Called by user click on 'Supprimer' button
     * */
    @FXML
    public void handleDelete() throws UserExceptions {
        listener.deleteTask();
        listener.closeCurrentStage();
    }

    /**
     * Called by user click on 'Retirer' button
     * */
    @FXML
    public void handleRemoveLabel() {
        String value = projectComboBox.getValue();
        projectComboBox.getItems().remove(value);
    }

    /**
     * Called by user click on 'Ajouter' button
     * */
    @FXML
    public void handleAddLabel(){
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Confirmation");
        textInputDialog.setHeaderText("Entrer le nom du collaborateur à ajouter :");
        textInputDialog.setContentText("Collaborateur:");
        Optional<String> res = textInputDialog.showAndWait();
        if (res.isPresent()) {
            String newLab = res.get();
            // Check if the new collaborator exist Collaborator List of the current project
            if (listener.isCollaboratorAssigned(newLab) && !projectComboBox.getItems().contains(newLab)) {
                projectComboBox.getItems().add(newLab);
            } else {
                Alert alert = InfoPopup.showPopup("Avertissement", "Ce collaborateur n'existe pas ou a déjà été ajouté.");
                alert.showAndWait();
            }
        }
    }

    /**
     * Called by user click on 'Appliquer les modifications' button
     * */
    @FXML
    public void handleApply() throws UserExceptions {
        // Update the current project with New Update for the current Task
        listener.applyProjectChanges(titleField.getText(), taskDescriptionArea.getText(), projectComboBox.getItems());
        if(ToolDate.isEarlierThanDate(startDate.getValue(), endDate.getValue())) {
            listener.applyProjectChanges(titleField.getText(), startDate.getValue(),  endDate.getValue());
            listener.closeCurrentStage();
        } else {
            Alert alert = InfoPopup.showPopup("Avertissement", "La date de début dépasse celle de fin");
            alert.showAndWait();
        }
        if (!listener.getCurrentTaskName().equals(titleField.getText())){
            if (listener.isTaskTitleUnique(titleField.getText())){ // si nouveau titre valide
                listener.updateMapTaskPanel(listener.getCurrentTaskName(), titleField.getText());
            }
            else {
                Alert alert = InfoPopup.showPopup("Avertissement", "Ce nom est déjà attribué à une tâche");
                alert.showAndWait();
            }
        }

    }

    /**
     * Called by user click on 'Annuler' button
     * */
    @FXML
    public void handleCancel() {
        listener.closeCurrentStage();
    }

    /**
     * SetProjectComboBox
     */
    public void setProjectComboBox(Set<String> listCollaborator) {
        projectComboBox.getItems().setAll(listCollaborator);
    }

    /**
     * SetListener
     */
    public void setListener(EditTaskDialogListener listener) { this.listener = listener; }

    public interface EditTaskDialogListener {
        void closeCurrentStage();
        boolean isCollaboratorAssigned(String collaborator);
        void deleteTask() throws UserExceptions;
        void applyProjectChanges(String taskName, LocalDate start, LocalDate end) throws UserExceptions;
        void applyProjectChanges(String title, String description, ObservableList<String> items) throws UserExceptions;
        String getCurrentTaskName();
        void updateMapTaskPanel(String currentTaskName, String text);
        boolean isTaskTitleUnique(String text);
        ProjectContentController getProjectContentController();
    }
}
