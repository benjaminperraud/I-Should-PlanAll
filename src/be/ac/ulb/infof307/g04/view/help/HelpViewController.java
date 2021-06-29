package be.ac.ulb.infof307.g04.view.help;


import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

/**
 * This class controls the view of the help windows
 */
public class HelpViewController {

    /** Instance private HelpViewListener **/
    private HelpViewListener listener;

    /** These attributes allows the window to launch on a predetermined pane */
    private static final int OVERVIEW = 0;
    private static final int IMPORT_EXPORT = 3;

    /** Instance of JavaFX FXML **/
    @FXML
    private TabPane tabPane;

    /** JavaFX
     * Is called by user click on "Annuler" button
     */
    public void handleCancel() {
        listener.closeCurrentStage();
    }

    /* Getters */

    public static int getImportExport() { return IMPORT_EXPORT; }
    public static int getOverview() { return OVERVIEW; }


    /** Sets the listener so the view controller can interact with it's controller
     * @param listener : the listener for the controller
     */
    public void setListener(HelpViewListener listener) { this.listener = listener; }

    /** Sets the number(index) of the tab who is selected
     * @param tab : the number(index) of the tab selected
     */
    public void setTab (int tab) {tabPane.getSelectionModel().select(tab); }

    /**
     *  Interface Listener HelpViewListener
     *
     * Function : - closeCurrentStage : Close the current window (stage)
     */
    public interface HelpViewListener {
        void closeCurrentStage();
    }
}
