package be.ac.ulb.infof307.g04.view.collaboration;

import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.view.popup.InfoPopup;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.Set;


/**
 * Class Collaboration View Controller
 * **/
public class CollaborationViewController {

    /** Instance private CollabViewListener **/
    private CollabViewListener listener;

    /** Instance of JavaFX FXML **/
    @FXML
    private TextField idTextField;
    @FXML
    private ComboBox<String> projectComboBox;

    /** JavaFX
     * Is called by user click on "Annuler" button
     */
    @FXML
    public void handleCancel() {
        listener.closeCurrentStage();
    }

    /** JavaFX
     * Is called by user click on "Ajouter" button
     */
    @FXML
    public void handleAddCollab() throws IOException, UserExceptions {
        String username = idTextField.getText();
        String projectTitle = projectComboBox.getSelectionModel().getSelectedItem();
        if (listener.sendCollaborationRequest(username, projectTitle)) {
            Alert alert = InfoPopup.showPopup("Invitation envoyée","Vous avez envoyé une invitation à " + username + " pour collaborer sur le projet " + projectTitle);
            alert.showAndWait();
        }
    }

    /**
     * Sets the project combo box with the user's projects
     * @param listProjectKeySet : a set of the connected user's projects
     */
    public void setProjectComboBox(Set<String> listProjectKeySet) {
        projectComboBox.getItems().setAll(listProjectKeySet);
    }

    /** Sets the listener so the view controller can interact with it's controller
     * @param listener : the listener for the controller
     */
    public void setListener(CollabViewListener listener) {
        this.listener = listener;
    }

    /**
     *  Interface Listener CollabViewListener
     *
     * Function : - sendCollaborationRequest : Send a request of collaboration for a project
     *            - closeCurrentStage : Close the current window (stage)
     */
    public interface CollabViewListener{
        boolean sendCollaborationRequest(String invitedUserName, String projectTitle) throws IOException, UserExceptions;
        void closeCurrentStage();
    }
}









