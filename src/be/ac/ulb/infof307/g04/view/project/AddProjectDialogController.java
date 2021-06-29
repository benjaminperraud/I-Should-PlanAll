package be.ac.ulb.infof307.g04.view.project;

import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.view.popup.InfoPopup;
import be.ac.ulb.infof307.g04.util.ToolDate;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class AddProjectDialogController {

    @FXML
    private TextArea descriptionArea;
    @FXML
    private DatePicker endDate;
    @FXML
    private TextField titleField;
    @FXML
    private Button applyChangesButton;

    private ProjectListener projectListener;

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        initializeFxml(endDate, applyChangesButton, titleField);
    }

    public static void initializeFxml(DatePicker endDate, Button applyChangesButton, TextField titleField) {
        endDate.setValue(LocalDate.now());
        ToolDate.disablePastDates(endDate);
        applyChangesButton.setDisable(true);
        titleField.textProperty().addListener((observable, newValue, oldValue) -> {
            applyChangesButton.setDisable(newValue.equals(""));
            applyChangesButton.setDisable(oldValue.equals(""));
        });
    }

    /**
     * Called by user click on 'Ajouter' button
     */
    @FXML
    public void handleAdd() throws UserExceptions {
        if (!projectListener.isProjectTitleUnique(titleField.getText())){
            projectListener.addNewProject(titleField.getText(), descriptionArea.getText(), endDate.getValue());
            projectListener.closeCurrentStage();
        }
        else{
            Alert alert = InfoPopup.showPopup("Avertissement", "Le titre du projet est déjà attribué à un autre projet.");
            alert.showAndWait();
        }
    }

    /**
     * Called by user click on 'Annuler' button
     */
    @FXML
    public void handleCancel() {
        projectListener.closeCurrentStage();
    }

    /**
     * SetListener
     */
    public void setListener(ProjectListener projectListener) {
        this.projectListener = projectListener;
    }

    public interface ProjectListener {
        void closeCurrentStage();
        void addNewProject(String title, String description, LocalDate endDate) throws UserExceptions;
        boolean isProjectTitleUnique(String title);
    }
}
