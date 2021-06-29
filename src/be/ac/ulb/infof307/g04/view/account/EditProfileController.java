package be.ac.ulb.infof307.g04.view.account;

import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.util.TextFormat;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import java.util.Optional;

/**
 * Class Edit Profile Controller
 * **/
public class EditProfileController {

    /** Instance private EditProfileListener **/
    private EditProfileListener listener ;

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
    private PasswordField confirmPasswordField;
    @FXML
    private Text confirmPasswordTextError;

    /** JavaFX
     * Is called by user click on Cancel button
     * */
    @FXML
    void handleCancel() {
        listener.closeCurrentStage();
    }

    /** JavaFX
     * Is called by user click on Confirm button
     */
    @FXML
    void handleConfirm() throws UserExceptions {
        TextField[] fieldArray = {firstNameField, lastNameField, mailField, usernameField, passwordField, confirmPasswordField};
        Text[] errorArray = {firstNameTextError, lastNameTextError, mailTextError, usernameTextError, passwordTextError, confirmPasswordTextError};
        if (TextFormat.isInputValid(fieldArray, errorArray)) {
            boolean isEdit = isEditAccepted();
            if (isEdit) {
                listener.updateUserInfo(firstNameField.getText(), usernameField.getText(), lastNameField.getText(), mailField.getText(), passwordField.getText());
            }
            listener.updateUsername();
            listener.closeCurrentStage();
        }
    }

    /**
     * Set user informations on every field
     *
     * @param name : the name of the searched user
     * @param userName : the username of the searched user
     * @param lastName : the lastname of the searched user
     * @param mail : the email of the searched user
     * @param password : the password of the searched user
     * */
    public void setUserInformation(String name, String userName, String lastName, String mail, String password){
        mailField.setText(mail);
        firstNameField.setText(name);
        lastNameField.setText(lastName);
        passwordField.setText(password);
        confirmPasswordField.setText(password);
        usernameField.setText(userName);
    }

    /**
     * Dialog to confirm or not the profile modification
     * @return true if confirm, false otherwise
     */
    public boolean isEditAccepted(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Voulez-vous vraiment modifier vos données ?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.filter(buttonType -> buttonType == ButtonType.OK).isPresent();
    }

    /**
     * Sets the listener so the view controller can interact with it's controller
     * @param listener : listener for the UserProfileController
     */
    public void setListener(EditProfileListener listener) {
        this.listener = listener;
    }


    /**
     *  Interface Listener EditProfileListener
     *
     * Function : - closeCurrentStage : Close the current window (stage)
     *            - updateUsername : Update the Username window
     *            - updateUserInfo : Update the info of the connected User
     */
    public interface EditProfileListener {
        void closeCurrentStage();
        void updateUsername();
        void updateUserInfo(String firstName, String userName, String lastName, String mail, String password) throws UserExceptions;
    }
}
