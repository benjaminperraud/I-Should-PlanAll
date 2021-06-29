package be.ac.ulb.infof307.g04.view.project;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class SubProjectDialogController {

    private SubProjectDialogListener listener;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private DatePicker endDate;
    @FXML
    private TextField titleField;

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        endDate.setValue(LocalDate.now());
    }

    /**
     * Called by user click on 'Ajouter' button
     * */
    @FXML
    public void handleAdd() {
        listener.checkValidity(endDate, titleField, descriptionArea);
    }

    /**
     * Called by user click on 'Annuler' button
     * */
    @FXML
    public void handleCancel() {
        listener.closeCurrentStage();
    }

    /**
     * SetListener
     */
    public void setListener(SubProjectDialogListener listener) { this.listener = listener; }

    public interface SubProjectDialogListener {
        void closeCurrentStage();
        void checkValidity(DatePicker endDate, TextField titleField, TextArea descriptionField);
    }
}
