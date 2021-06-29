package be.ac.ulb.infof307.g04.controller.projectManagement;

import be.ac.ulb.infof307.g04.controller.Main;
import be.ac.ulb.infof307.g04.model.Database;
import be.ac.ulb.infof307.g04.model.Project;
import be.ac.ulb.infof307.g04.model.exceptions.AlertExceptions;
import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.view.MenuController;
import be.ac.ulb.infof307.g04.view.collaboration.CollaborationViewController;
import be.ac.ulb.infof307.g04.view.collaboration.WaitingCollaborationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/** Class Collaboration Controller
 *
 * Listener : WaitingCollaborationController, CollaborationViewController, MenuController
 *
 * **/
public class CollaborationController implements WaitingCollaborationController.WaitingCollaborationListener, CollaborationViewController.CollabViewListener, MenuController.CollabListener {

    /** Instance private final Main **/
    private final Main main;

    /** Constructor CollaborationController **/
    public CollaborationController(Main main) {
        this.main = main;
    }

    /**
     * Show Collaboration scene
     * */
    public void showCollaboration(){
        FXMLLoader loader = new FXMLLoader();
        Parent rootPane = Main.loadFXML(CollaborationViewController.class, loader);
        CollaborationViewController controller = loader.getController();
        controller.setListener(this);
        controller.setProjectComboBox(main.getListProjectKeySet());
        Stage stage = new Stage() ;
        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/collaboration.png"));
        stage.setTitle("Demande de collaboration");
        main.getUnclosedStage().add(stage);
        main.setNonReleasable(stage);
        assert rootPane != null;
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.showAndWait(); // Show the stage
    }

    /**
     * Show Waiting Collaboration scene
     * */
    public void showWaitingCollaborations(){
        FXMLLoader loader = new FXMLLoader();
        Parent rootPane = Main.loadFXML(WaitingCollaborationController.class, loader);
        WaitingCollaborationController controller = loader.getController();
        controller.setListener(this);
        controller.setListView(main.getConnectedUser().getListWaitingCollab());
        Stage stage = new Stage() ;
        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/collaboration.png"));
        stage.setTitle("Collaboration en attente, veuillez patientez s'il vous plait !");
        main.getUnclosedStage().add(stage);
        main.setNonReleasable(stage);
        assert rootPane != null;
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.showAndWait(); // Show stage
    }


    /**
     * Send a collaboration request to a specified user
     * @param invitedUserName : the username of the invited user
     * @param projectTitle : the title of the project for which collaboration is desired
     * @return boolean true if success, false otherwise
     */
    @Override
    public boolean sendCollaborationRequest(String invitedUserName, String projectTitle) throws UserExceptions {
        if (main.getConnectedUser().getUsername().equals(invitedUserName)) {
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert("Invitation impossible", "Vous ne pouvez pas vous inviter !");
            return false;
        } else if (projectTitle == null) {
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert("Avertissement", "Vous n'avez pas sélectionné de projet");
            return false;
        } else if (Database.userExists(invitedUserName)) {
            main.getConnectedUser().sendCollaboration(invitedUserName, projectTitle);
            return true;
        } else {
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert("Avertissement", "Cet utilisateur n'existe pas.");
            return false;
        }
    }

    /**
     * Add a collaborator to a project
     * @param hostUserName : the username of the user who initiated the invitation to collaborate
     * @param projectName : the name of the project for which collaboration is requested
     */
    @Override
    public void addCollaborator(String hostUserName, String projectName) throws UserExceptions {
        if (Database.userExists(hostUserName)){
            Project project = main.getConnectedUser().addCollaborator(hostUserName, projectName);
            main.getMenuController().addProjectToPanel(main.getMenuController().createNewProjectPanel(project));
            main.getMenuController().setNoProjectEmpty();
        }
    }

    /**
     * Delete a collaborator to a project
     * @param hostUserName : the username of the user who initiated the invitation to collaborate
     * @param projectName : the name of the project for which collaboration is requested
     */
    @Override
    public void deleteCollaborationRequest(String hostUserName, String projectName) throws UserExceptions {
        main.getConnectedUser().removeInvitation(hostUserName, projectName);
    }

    /** Close the current screen (Stage) **/
    @Override
    public void closeCurrentStage() {
        main.closeCurrentStage();
    }
}
