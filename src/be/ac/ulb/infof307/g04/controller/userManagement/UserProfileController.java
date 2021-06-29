package be.ac.ulb.infof307.g04.controller.userManagement;

import be.ac.ulb.infof307.g04.controller.Main;
import be.ac.ulb.infof307.g04.model.Database;
import be.ac.ulb.infof307.g04.model.Project;
import be.ac.ulb.infof307.g04.model.SubProject;
import be.ac.ulb.infof307.g04.model.User;
import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.view.MenuController;
import be.ac.ulb.infof307.g04.view.account.AccountCreationController;
import be.ac.ulb.infof307.g04.view.account.EditProfileController;
import be.ac.ulb.infof307.g04.view.account.UserConnexionController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.Map;


/**
 * Controller class for the AccountCreationController, EditProfileController and UserConnexionController view controller, allows to control stage related to user information
 *
 * Listener : AccountCreationListener, EditProfileListener, UserConnexionListener, UserListener
 */
public class UserProfileController implements AccountCreationController.AccountCreationListener, EditProfileController.EditProfileListener, UserConnexionController.UserConnexionListener, MenuController.UserListener {

    /** Instance Variable Main */
    private final Main main;

    /** Constructor */
    public UserProfileController(Main mainController) {
        main = mainController;
    }

    /** Show the UserConnexion scene */
    public void showUserConnexion() {
        FXMLLoader loader = new FXMLLoader();
        Parent rootPane = Main.loadFXML(UserConnexionController.class, loader);
        UserConnexionController controller = loader.getController();
        controller.setListener(this);
        Stage stage = new Stage();
        stage.setTitle("Connexion");
        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/lock.png"));
        stage.setResizable(false);
        main.getUnclosedStage().add(stage);
        assert rootPane != null;
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.show(); // Show the stage
    }

    /** Show the createAccount scene */
    public void showCreateAccount() {
        FXMLLoader loader = new FXMLLoader();
        Parent rootPane = Main.loadFXML(AccountCreationController.class, loader);
        AccountCreationController controller = loader.getController();
        controller.setListener(this);
        Stage stage = new Stage();
        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/user.png"));
        stage.setTitle("Inscription");
        main.getUnclosedStage().add(stage);
        main.setNonReleasable(stage);
        assert rootPane != null;
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.show(); // Show the stage
    }

    /** Show the MenuController scene */
    public void showMainWindow() {
        main.showMainWindow();
    }

    /** Show the EditProfile scene */
    public void showEditProfile() {
        FXMLLoader loader = new FXMLLoader();
        Parent rootPane = Main.loadFXML(EditProfileController.class, loader);
        EditProfileController controller = loader.getController();
        controller.setListener(this);
        User u = main.getConnectedUser();
        controller.setUserInformation(u.getName(), u.getUsername(), u.getLastname(), u.getEmail(), u.getPassword());
        Stage stage = new Stage();
        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/user.png"));
        stage.setTitle("Edition du profil");
        main.getUnclosedStage().add(stage);
        main.setNonReleasable(stage);
        assert rootPane != null;
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.show(); // Show the stage
    }

    /**
     * Logs a user in the application
     *
     * @param username : the username of the user
     * @param password : the password of the user
     * @throws UserExceptions to report error occurred while the user is logging in
     */
    @Override
    public void login(String username, String password) throws UserExceptions {
        User user = new User();
        Database ddb = new Database(user);
        ddb.login(username, password);
    }

    /**
     * Updates the information of the connected user
     *
     * @param firstName : the first name of the user
     * @param userName : the username of the user
     * @param lastName : the last name of the user
     * @param mail : the mail of the user
     * @param password : the password of the user
     * @throws UserExceptions to report error occurred while the user's information is being updated
     */
    @Override
    public void updateUserInfo(String firstName, String userName, String lastName, String mail, String password) throws UserExceptions {
        User user = main.getConnectedUser();
        Database dbc = new Database(user);
        dbc.editUserInformations(firstName, userName, lastName, mail, password, main.getConnectedUser().getListProject());
    }

    /**
     * Register a user in the application
     *
     * @param firstName : the first name of the user
     * @param userName : the user name of the user
     * @param lastName : the last name of the user
     * @param mail : the mail of the user
     * @param password : the password of the user
     * @throws UserExceptions to report error occurred while the user is being registered
     */
    @Override
    public void register(String firstName, String userName, String lastName, String mail, String password) throws UserExceptions {
        Database.register(firstName, userName, lastName, mail, password);
    }

    /** Close the current stage */
    @Override
    public void closeCurrentStage() {
        main.closeCurrentStage();
    }

    /**
     * Update the username connected to the application
     */
    @Override
    public void updateUsername() {
        main.updateUsername();
    }

    /** Delete a project of the current User */
    public void deleteProjectCollaborator(String projectName, User userCollaborator){
        if (userCollaborator.getListProject().remove(projectName) == null){
            for (Project project : userCollaborator.getListProject().values()) {
                deleteSubProjectGlobal(project.getSubProjectList(), projectName);
            }
        }
    }

    /** Recursive function : Delete a subproject of the current User */
    public void deleteSubProjectGlobal(Map<String, SubProject> subProjectList, String projectName) {
        for (Project project : subProjectList.values()) {
            if (project.getTitle().equals(projectName)) {
                subProjectList.remove(projectName);
            } else {
                deleteSubProjectGlobal(project.getSubProjectList(), projectName);
            }
        }
    }
}
