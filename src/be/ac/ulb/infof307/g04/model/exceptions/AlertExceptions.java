package be.ac.ulb.infof307.g04.model.exceptions;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;


/**
 * Class Alert Exception
 * **/
public class AlertExceptions {

    /** this shows an alert to the user, so that he can understand if there's an error
     *
     * @param errorTitle : The title for the Popup
     * @param errorDesc : The Description for the Popup
     *
     * **/
    public void throwAlert(String errorTitle, String errorDesc){
        Alert alert  = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(errorTitle);
        alert.setHeaderText(errorTitle);
        alert.setContentText(errorDesc);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            alert.close();
        }
    }
}
