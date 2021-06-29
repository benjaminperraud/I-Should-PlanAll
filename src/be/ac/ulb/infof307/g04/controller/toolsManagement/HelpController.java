package be.ac.ulb.infof307.g04.controller.toolsManagement;

import be.ac.ulb.infof307.g04.controller.Main;
import be.ac.ulb.infof307.g04.view.MenuController;
import be.ac.ulb.infof307.g04.view.help.HelpViewController;
import be.ac.ulb.infof307.g04.view.importExport.ImportExportViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Class Help Controller
 *
 * Listener : HelpViewController, MenuController, ImportExportViewController
 *
 * **/
public class HelpController implements HelpViewController.HelpViewListener, MenuController.HelpListener, ImportExportViewController.HelpListener {

    /** Instance private final Main **/
    private final Main main;

    /** Constructor HelpController **/
    public HelpController(Main main) {
        this.main = main;
    }

    /**
     * Show Help section scene
     * */
    @Override
    public void showHelp(int tab) {
        FXMLLoader loader = new FXMLLoader();
        Parent rootPane = Main.loadFXML(HelpViewController.class, loader);
        HelpViewController controller = loader.getController();
        controller.setListener(this);
        controller.setTab(tab);
        Stage stage = new Stage() ;
        stage.setTitle("Section d'aide");
        main.getUnclosedStage().add(stage);
        main.setNonReleasable(stage);
        assert rootPane != null;
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.showAndWait(); // Show stage
    }

    /**
     * Close the current screen (Stage)
     */
    @Override
    public void closeCurrentStage() {
        main.closeCurrentStage();
    }
}
