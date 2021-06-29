package be.ac.ulb.infof307.g04.view.account;

import be.ac.ulb.infof307.g04.model.exceptions.AlertExceptions;
import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.util.TextFormat;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;


/**
 * Class User Connexion Controller
 * **/
public class UserConnexionController {

    /** Instance private UserConnexionListener **/
    private UserConnexionListener listener;

    /** Instance of JavaFX FXML **/
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label noUserFoundLabel;
    @FXML
    private Label wrongPasswordLabel;

    /** JavaFX
     * Is called by user click on 'Connexion' button
     * */
    @FXML void handleConnexion() {
        String password = TextFormat.deleteSpace(passwordField.getText()) ;
        String username = TextFormat.deleteSpace(usernameField.getText()) ;
        try {
            listener.login(username, password);
            if (UserExceptions.getSuccess()) {
                noUserFoundLabel.setText("");
                wrongPasswordLabel.setText("");
                listener.closeCurrentStage();
                listener.showMainWindow();
            } else if (UserExceptions.getUserExistErr()) {
                noUserFoundLabel.setText("Utilisateur inexistant");       // show "Utilisateur inexistant" to user
                noUserFoundLabel.setTextFill(Color.RED);
                wrongPasswordLabel.setText("");
            } else if (UserExceptions.getPasswordErr()) {
                wrongPasswordLabel.setText("Mot de passe incorrect");     // show "Mot de passe incorrect" to user
                wrongPasswordLabel.setTextFill(Color.RED);
                noUserFoundLabel.setText("");
            }
        } catch (UserExceptions e) {
            // Alert View Error
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert("Avertissement", "Erreur lors de la connexion, veuillez relancer l'application.");
        }

    }

    /** JavaFX
     * Is called by user click on 'Quitter' button
     * */
    @FXML void handleQuit(){
        listener.closeCurrentStage();
    }

    /** JavaFX
     * Called by user click on 'Créer un compte' button
     * */
    @FXML void handleCreateAccount() {
        listener.showCreateAccount();
    }

    /**
     * Sets the listener so the view controller can interact with it's controller
     * @param listener : listener for the UserConnexionListener
     */
    public void setListener(UserConnexionListener listener) {
        this.listener = listener;
    }


    /**
     *  Interface Listener UserConnexionListener
     *
     * Function : - closeCurrentStage : Close the current window (stage)
     *            - showCreateAccount : Show the Account creation Application window
     *            - showMainWindow : Show the Menu Application window
     *            - login : Log the connected User
     */
    public interface UserConnexionListener{
        void closeCurrentStage();
        void showCreateAccount();
        void showMainWindow();
        void login(String username, String password) throws UserExceptions;
    }
}