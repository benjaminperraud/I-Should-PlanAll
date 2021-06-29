package be.ac.ulb.infof307.g04.view.account;

import be.ac.ulb.infof307.g04.model.exceptions.AlertExceptions;
import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.util.TextFormat;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import javafx.scene.control.*;
import javafx.scene.text.Text;

/**
 * Class Account Creation Controller
 * **/
public class AccountCreationController {

    /** Instance private AccountCreationListener **/
    private AccountCreationListener listener;

    /** Instance of JavaFX FXML **/
    @FXML
    private TextField firstNameField;
    @FXML
    private Text firstNameTextError;
    @FXML
    private TextField lastNameField;
    @FXML
    private Text lastNameTextError;
    @FXML
    private TextField mailField;
    @FXML
    private Text mailTextError;
    @FXML
    private TextField usernameField;
    @FXML
    private Text usernameTextError;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Text passwordTextError;
    @FXML
    private PasswordField confirmPasswordField ;
    @FXML
    private Text confirmPasswordTextError ;
    @FXML
    private CheckBox conditionUtilisation;
    @FXML
    private Button registerButton ;


    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    public void initialize() {
        createTooltip();
        registerButton.setDisable(true);
        conditionUtilisation.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> registerButton.setDisable(!new_val));
    }

    /** JavaFX
     * Is called by user click on Cancel button
     */
    @FXML
    public void handleCancel() {
        listener.closeCurrentStage();
    }

    /** JavaFX
     * Is called by user click on Register button
     */
    @FXML
    public void handleConfirm() {
        TextField[] fieldArray = {firstNameField, lastNameField, mailField, usernameField, passwordField, confirmPasswordField};
        Text[] errorArray = {firstNameTextError, lastNameTextError, mailTextError, usernameTextError, passwordTextError, confirmPasswordTextError};
        if (TextFormat.isInputValid(fieldArray, errorArray)) {
            try{
                listener.register(firstNameField.getText(), usernameField.getText(), lastNameField.getText(), mailField.getText(), passwordField.getText());
                if (UserExceptions.getSuccess()) {
                    listener.closeCurrentStage();
                } else if (UserExceptions.getUserExistErr()) {
                    AlertExceptions alert = new AlertExceptions();
                    alert.throwAlert("Avertissement", "Ce nom d'utilisateur est déjà utilisé");
                }
            }catch (UserExceptions e){
                AlertExceptions alert = new AlertExceptions();
                alert.throwAlert("Avertissement", "Erreur lors de la sauvegarde de l'utilisateur");
            }
        }
    }


    /** JavaFX
     * Is called by user click on "Lire condition" button
     */
    @FXML
    public void handleCondition() {
        Alert alert  = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bienvenue");
        alert.setHeaderText("Voici les conditions d'utilisations");
        alert.setContentText("\nTout accès aux Services est subordonné à l’acceptation et au respect des présentes Conditions Générales par l’Utilisateur.\nL'utilisateur accepte que nous combinons les données recueillis sur nos services afin d'assurer une meilleur experience de notre produit.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK ) {
            alert.close();
        }
    }


    /**
     * Create a Tooltip to show user conditions
     * */
    public void createTooltip(){
        Tooltip tooltip = new Tooltip();
        tooltip.setText("\nVeuillez lire les conditions d'utilisations avant de vous inscrire.");
        tooltip.setStyle("-fx-font-size: 16");
        conditionUtilisation.setTooltip(tooltip);
    }

    /**
     * Sets the listener so the view controller can interact with it's controller
     * @param listener : listener for the UserProfileController
     */
    public void setListener(AccountCreationListener listener) {
        this.listener = listener;
    }

    /**
     *  Interface Listener AccountCreationListener
     *
     * Function : - closeCurrentStage : Close the current window (stage)
     *            - register : Register de connected User
     *
     */
    public interface AccountCreationListener{
        void closeCurrentStage();
        void register(String firstName, String userName, String lastName, String password, String mail) throws UserExceptions;
    }
}