package be.ac.ulb.infof307.g04.view.popup;

import javafx.scene.control.Alert;

/**
 * Class InfoPopup
 * **/
public final class InfoPopup {

    private InfoPopup() {}

    /**
     * Used to make creation of dialogs easier (and more easily modifiable
     * in the future)
     * @param title: title of the popup
     * @param message: message of the popup
     * @return popupBox: the popup
     */
    public static Alert showPopup(String title, String message) {
        Alert popupBox = new Alert(Alert.AlertType.INFORMATION);
        popupBox.setTitle(title);
        popupBox.setHeaderText(message);
        return popupBox;
    }

}
