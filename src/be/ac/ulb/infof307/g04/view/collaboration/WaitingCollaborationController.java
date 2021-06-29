package be.ac.ulb.infof307.g04.view.collaboration;

import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.view.popup.InfoPopup;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Map;
import java.util.Set;

/**
 * Class Waiting Collaboration Controller
 * **/
public class WaitingCollaborationController {

    /** Instance private WaitingCollaborationListener **/
    private WaitingCollaborationListener listener;

    /** Instance of JavaFX FXML **/
    @FXML
    private ListView<String> projectListView;
    @FXML
    private ListView<String> hostListUsername;

    /** JavaFX
     * Is called by user click on "Accepter" button
     */
    @FXML
    public void handleAccept() throws UserExceptions {
        MultipleSelectionModel<String> selectedModel = projectListView.getSelectionModel();
        if (projectListView.getItems().isEmpty()){
            Alert alert = InfoPopup.showPopup("Aucune demande en attente","Vous n'avez aucune demande de collaboration en attente !");
            alert.showAndWait();
        } else if (selectedModel.getSelectedItem() == null) {
            Alert alert = InfoPopup.showPopup("Aucune demande sélectionné","Vous n'avez sélectionné aucune demande de collaboration!");
            alert.showAndWait();
        } else {
            String projectName = selectedModel.getSelectedItem();
            int projectIndex = selectedModel.getSelectedIndex();
            String hostUsername = hostListUsername.getItems().get(projectIndex);
            listener.addCollaborator(hostUsername, projectName);
            listener.deleteCollaborationRequest(hostUsername, projectName);
            projectListView.getItems().remove(projectName);
            hostListUsername.getItems().remove(hostUsername);
            Alert alert = InfoPopup.showPopup("Invitation acceptée","Vous avez bien accepté l'invitation de " + hostUsername + " pour le projet " + projectName);
            alert.showAndWait();
        }
    }

    /** JavaFX
     * Is called by user click on "Refuser" button
     */
    @FXML
    public void handleRefuse() throws UserExceptions {
        MultipleSelectionModel<String> selectedModel = projectListView.getSelectionModel();
        if (projectListView.getItems().isEmpty()){
            Alert alert = InfoPopup.showPopup("Aucune demande en attente","Vous n'avez aucune demande de collaboration en attente !");
            alert.showAndWait();
        } else if (selectedModel.getSelectedItem() == null) {
            Alert alert = InfoPopup.showPopup("Aucune demande sélectionné","Vous n'avez sélectionné aucune demande de collaboration!");
            alert.showAndWait();
        } else {
            String projectName = selectedModel.getSelectedItem();
            int projectIndex = selectedModel.getSelectedIndex();
            String hostUsername = hostListUsername.getItems().get(projectIndex);
            listener.deleteCollaborationRequest(hostUsername, projectName);
            projectListView.getItems().remove(projectName);
            hostListUsername.getItems().remove(hostUsername);
            Alert alert = InfoPopup.showPopup("Invitation refusée", "Vous avez bien refusé l'invitation de " + hostUsername + " pour le projet " + projectName);
            alert.showAndWait();
        }
    }


    /**
     * Sets the project listview with the user's waiting collaboration request
     * @param waitingCollaboration : a map of the connected user's waiting collaboration request
     */
    public void setListView(Map<String, Set<String>> waitingCollaboration){
        waitingCollaboration.forEach((key, val) -> val.forEach((v) -> {
            projectListView.getItems().add(v);
            hostListUsername.getItems().add(key);
        }));
    }

    /**
     * Sets the listener so the view controller can interact with it's controller
     * @param listener : listener for the main controller
     */
    public void setListener(WaitingCollaborationListener listener) {
        this.listener = listener;
    }

    /**
     *  Interface Listener WaitingCollaborationListener
     *
     * Function : - addCollaborator : Add a collaborator
     *            - deleteCollaborationRequest : Delete a request of collaboration
     */
    public interface WaitingCollaborationListener {
        void addCollaborator(String hostUserName, String projectName) throws UserExceptions;
        void deleteCollaborationRequest(String hostUserName, String projectName) throws UserExceptions;
    }
}
