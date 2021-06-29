package be.ac.ulb.infof307.g04.view.project;

import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.view.popup.InfoPopup;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.List;

public class AddSubProjectDialogController {

    private SubProjectDialogListener listener;
    private ProjectListener projectListener;

    @FXML
    private TextArea descriptionArea;
    @FXML
    private DatePicker endDate;
    @FXML
    private TextField titleField;
    @FXML
    private Button applyChangesButton;

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        AddProjectDialogController.initializeFxml(endDate, applyChangesButton, titleField);
    }

    /**
     * Called by user click on 'Ajouter' button
     */
    @FXML
    public void handleAdd() throws UserExceptions {
        if (listener.checkValidity(endDate, titleField, descriptionArea)) {
            listener.createNewSubProject(titleField.getText(), descriptionArea.getText(), endDate.getValue());
            addPanelSub(titleField.getText(), descriptionArea.getText(), endDate.getValue());
            listener.closeCurrentStage();
        }
    }

    /**
     * Called by user click on 'Annuler' button
     */
    @FXML
    public void handleCancel() {
        listener.closeCurrentStage();
    }


    /**
     * adds subproject to the subprojects accordion
     */
    public void addPanelSub(String title, String description, LocalDate date) {
        ProjectPanel projectPanel = new ProjectPanel(title, description, date, listener.getParentLabels());
        projectListener.putPanelToMap(title, projectPanel);
        EventHandler<ActionEvent> modifyButtonHandler = event -> {
            Button btn = (Button) event.getSource();
            projectListener.showProjectInfoDisplay(btn.getAccessibleText());
            listener.refreshSubProject(projectPanel, btn.getAccessibleText());
        };
        EventHandler<ActionEvent> selectButtonHandler = event -> {
            Button btn = (Button) event.getSource();
            projectListener.showProjectContent(btn.getAccessibleText());
        };
        projectPanel.getModifyButton().setOnAction(modifyButtonHandler);
        projectPanel.getSelectButton().setOnAction(selectButtonHandler);
        projectListener.getProjectContentController().addSubPanelToAccordion(projectPanel.getPanel());
    }

    /**
     * Shows pop-up when the user creates a subproject with the title of an existing subproject
     */
    public void wrongTitle() {
        Alert alert = InfoPopup.showPopup("Avertissement", "Le titre du sous-projet est déjà attribué à un autre sous-projet.");
        alert.showAndWait();
    }

    /**
     * Shows pop-up when the user creates a subproject whose due date is after its parent project's one
     */
    public void wrongDate() {
        Alert alert = InfoPopup.showPopup("Avertissement", "La date de fin du sous-projet dépasse celle du projet parent.");
        alert.showAndWait();
    }

    /**
     * SetListener
     */
    public void setListener(SubProjectDialogListener listener) {
        this.listener = listener;
    }

    /**
     * SetProjectListener
     */
    public void setProjectListener(ProjectListener projectListener) {
        this.projectListener = projectListener;
    }

    public interface SubProjectDialogListener {
        void closeCurrentStage();
        boolean checkValidity(DatePicker endDate, TextField titleField, TextArea descriptionField);
        void createNewSubProject(String text, String text1, LocalDate value) throws UserExceptions;
        List<String> getParentLabels();
        void refreshSubProject(ProjectPanel projectPanel, String accessibleText);
    }

    public interface ProjectListener {
        void showProjectInfoDisplay(String accessibleText);
        void showProjectContent(String accessibleText);
        ProjectContentController getProjectContentController();
        void putPanelToMap(String title, ProjectPanel projectPanel);
        void closeCurrentStage();
    }
}