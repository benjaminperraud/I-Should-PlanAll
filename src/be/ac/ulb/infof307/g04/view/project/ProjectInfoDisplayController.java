package be.ac.ulb.infof307.g04.view.project;

import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.view.popup.InfoPopup;
import be.ac.ulb.infof307.g04.util.ToolDate;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.*;

public class ProjectInfoDisplayController {

    private static final String MODIFIED = "Modifié !";
    private static final String MODIFY = "Modifier";

    private ProjectInfoListener mainListener;
    private boolean modified ;

    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private DatePicker dueDate;
    @FXML
    private ComboBox<String> labelComboBox;
    @FXML
    private Button modifyButton;


    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        ToolDate.disablePastDates(dueDate);
        modified = false;
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            modifyButton.setDisable(newValue.equals(""));
            if (modified){
                if (mainListener.isNewProjectTitle(newValue)) {
                    modifyButton.setText(MODIFY);
                }
                else {
                    modifyButton.setText(MODIFIED);
                }
            }
        });
        descriptionArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (modified) {
                if (mainListener.isNewProjectDescription(newValue)){
                    modifyButton.setText(MODIFY);
                }
                else {
                    modifyButton.setText(MODIFIED);
                }
            }
        });
    }

    /**
     * Called by user click on 'Retirer' button
     * */
    @FXML
    public void handleRemoveLabel() {
        String value = labelComboBox.getValue();
        labelComboBox.getItems().remove(value);
        mainListener.removeLabel(value);
        mainListener.removeChildrenLabel(value);
    }

    /**
     * Called by user click on 'Ajouter' button
     * */
    @FXML
    public void handleAddLabel(){
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Ajouter une étiquette");
        textInputDialog.setContentText("Insérer une nouvelle étiquette  :");
        textInputDialog.setHeaderText(null);
        textInputDialog.setGraphic(null);
        Optional<String> res = textInputDialog.showAndWait();
        if (res.isPresent()) {
            String newLab = res.get();
            if (mainListener.addLabel(newLab)) {
                labelComboBox.getItems().add(newLab);
                mainListener.addChildrenLabel(newLab);
            } else {
                Alert alert = InfoPopup.showPopup("Avertissement", "Ce label est vide ou a déjà été ajouté.");
                alert.showAndWait();
            }
        }
    }

    /**
     * Called by user click on 'Retour' button
     * */
    @FXML
    void handleBack()  {
        mainListener.closeCurrentStage();
    }

    /**
     * Called by user click on 'Supprimer' button
     * */
    @FXML
    void handleDelete() throws UserExceptions { //when the project is deleted
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Voulez-vous vraiment supprimer le projet ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            //delete from database
            mainListener.deleteProject();
            mainListener.deleteProjectFromPanel();
            mainListener.deleteUpdateDetailsUser();
            handleBack();  //go back to menu
        }
    }

    /**
     * Called by user click on 'Modifier' button
     * */
    @FXML
    void handleModify() throws UserExceptions {
        if (mainListener.isNewProjectDescription(descriptionArea.getText()) | mainListener.isNewProjectTitle(titleField.getText()) | !dueDate.getValue().equals(mainListener.getProjectEndDate())) {
            modifyButton.setText("Modifié !");
            modified = true;
        }
        String oldName = mainListener.getCurrentProjectTitle();
        String newTitle = titleField.getText();
        if (!newTitle.equals(oldName)){
            if (mainListener.isNewProjectTitle(newTitle)){
                mainListener.updateProjectMapPanel(oldName, newTitle);  // update the map
                mainListener.refreshProjectName(oldName, newTitle);     // refresh projectPane title
                mainListener.deleteProject();
            }
            else{
                Alert alert = InfoPopup.showPopup("Avertissement", "Ce titre est déjà attribué à un projet ou un sous-projet");
                alert.showAndWait();
                return ;
            }
        }
        mainListener.createNewProject(newTitle, descriptionArea.getText(), dueDate.getValue());
        mainListener.updateCollaborator(oldName, newTitle);
        mainListener.updateDetailUser();
        modifyButton.setText("Modifié !");
    }

    /**
     * Initialize the display of the project information
     * */
    public void displayProjectInfo(String title, String description, LocalDate date, List<String> items) {
        titleField.setText(title);
        descriptionArea.setText(description);
        dueDate.setValue(date);
        labelComboBox.getItems().setAll(items);
    }

    /** Sets the listener so the view controller can interact with it's controller
     * @param mainListener : the listener for the controller
     */
    public void setMainListener(ProjectInfoListener mainListener){ this.mainListener = mainListener; }

    public interface ProjectInfoListener {
        void closeCurrentStage();
        void updateDetailUser() throws UserExceptions;
        void deleteUpdateDetailsUser() throws UserExceptions;
        void refreshProjectName(String oldName, String newName) throws UserExceptions;
        void deleteProject() throws UserExceptions;
        void createNewProject(String newName, String text, LocalDate value);
        void deleteProjectFromPanel();
        void updateProjectMapPanel(String oldName, String newName) throws UserExceptions;
        String getCurrentProjectTitle();
        boolean addLabel(String newLab);
        void removeLabel(String lab);
        void addChildrenLabel(String label);
        void removeChildrenLabel(String label);
        boolean isNewProjectTitle(String newValue);
        boolean isNewProjectDescription(String newValue);
        LocalDate getProjectEndDate();
        void updateCollaborator(String oldName, String newName) throws UserExceptions;
    }
}