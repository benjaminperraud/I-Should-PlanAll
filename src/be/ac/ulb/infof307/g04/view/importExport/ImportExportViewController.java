package be.ac.ulb.infof307.g04.view.importExport;

import be.ac.ulb.infof307.g04.model.exceptions.DriveAPIException;
import be.ac.ulb.infof307.g04.model.exceptions.DropboxAPIException;
import be.ac.ulb.infof307.g04.model.exceptions.ImportExportException;
import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.util.exceptions.CompressException;
import be.ac.ulb.infof307.g04.util.exceptions.ExtractException;
import be.ac.ulb.infof307.g04.view.popup.InfoPopup;
import be.ac.ulb.infof307.g04.view.help.HelpViewController;
import com.dropbox.core.DbxException;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ImportExportViewController {

    private static final String WARNING = "Avertissement";
    private ImportExportListener listener;
    private HelpListener helpListener;

    @FXML
    private ListView<String> listProjectsImport;
    @FXML
    private ListView<String> listProjectsExport;
    @FXML
    private ToggleButton driveImportButton;
    @FXML
    private ToggleButton dropboxImportButton;

    /* Class utilities */

    /**
     * SetListener
     */
    public void setListener(ImportExportListener listener, HelpListener helpListener) {
        this.listener = listener;
        this.helpListener = helpListener;
    }

    private void popUpException(String title, String message) {
        InfoPopup.showPopup(title, message).showAndWait();
    }

    /**
     * Add all local projects of the user in the widget listProjectsExport
     * */
    public void setListProjectsExport(Set<String> listProjects){
        listProjectsExport.getItems().addAll(listProjects);
    }

    /* View methods related to importation/exportation of projects and stats from/to computer */

    /**
     * Is called by user click on 'Importer depuis l'ordinateur' button
     * */
    @FXML
    public void handleImportFromComputer() throws UserExceptions, IOException {
        try {
            listener.importProjectFromExplorer();
        } catch (ExtractException e) {
            InfoPopup.showPopup(WARNING, e.getMessage()).showAndWait();
        }
        listener.closeCurrentStage();
    }

    /**
     * Is called by user click on 'Exporter sur l'ordinateur' button from 'Exportation' tab.
     * */
    @FXML
    public void handleExportToComputer() throws IOException {
        String selection = listProjectsExport.getSelectionModel().getSelectedItem();
        if (selection != null) {
            try {
                listener.exportProjectFromExplorer(selection);
            } catch (CompressException e) {
                InfoPopup.showPopup(WARNING, e.getMessage()).showAndWait();
            }
            listener.closeCurrentStage();
        } else {
            InfoPopup.showPopup(WARNING, "Veuillez sélectionner un projet avant de l'exporter.").showAndWait();
        }
    }

    /* General cloud importation handler */

    /**
     * Is called by user click on 'Importer' button from 'Importation' tab
     * The method first check whether an element is selected, and then imports it with the selected cloud service.
     * */
    @FXML
    public void handleImport() throws IOException, DbxException, UserExceptions {
        String executionMessage = "", executionMessageTitle = "Info";
        boolean execution = false;
        try {
            if (listProjectsImport.getSelectionModel().getSelectedItem() != null) {
                if (driveImportButton.isSelected()) {
                    listener.importProjectFromDrive(listProjectsImport.getSelectionModel().getSelectedItem());
                    execution = true;  // reaching this line means no error has been thrown
                } else if (dropboxImportButton.isSelected()) {
                    execution = listener.importProjectFromDropbox(listProjectsImport.getSelectionModel().getSelectedItem());
                } else {
                    executionMessage = "Aucun service de cloud n'a été sélectionné";
                    executionMessageTitle = WARNING;
                }
            } else {
                executionMessage = "Aucun projet n'a été sélectionné";
                executionMessageTitle = WARNING;
            }
            if (executionMessage.equals("")){
                executionMessage = !execution ? "L'importation depuis le cloud a échoué" : "L'importation depuis le cloud a été correctement effectuée";
            }
        } catch (DriveAPIException | ImportExportException e) {
            executionMessage = e.getMessage();
        } catch (DropboxAPIException | ExtractException e) {
            InfoPopup.showPopup(WARNING, e.getMessage()).showAndWait();
        }
        popUpException(executionMessageTitle, executionMessage);
    }

    /* DropBox operations */

    /**
     * Is called by user click on 'Dropbox' button from 'Importation' tab
     * */
    @FXML
    public void displayAvailableDropboxFiles() throws DbxException, IOException {
        try {
            driveImportButton.setSelected(false);
            dropboxImportButton.setSelected(true);
            List<String> fileNamesList = listener.getAvailableDropboxProjects();
            listProjectsImport.getItems().clear();
            if (fileNamesList.size() == 0) {
                InfoPopup.showPopup(WARNING, "Pas de projets dans DropBox.").showAndWait();
            } else {
                for (String s : fileNamesList) {
                    listProjectsImport.getItems().add(s);
                }
            }
        } catch (DropboxAPIException e) {
            InfoPopup.showPopup(WARNING, e.getMessage()).showAndWait();
        }
    }

    /**
     * Is called by user click on 'Dropbox' button from 'Exportation' tab.
     * */
    @FXML
    public void handleExportToDropbox() {
        try {
            String selection = listProjectsExport.getSelectionModel().getSelectedItem();
            if (selection != null) {
                listener.exportProjectToDropbox(selection);
                listener.closeCurrentStage();
            } else {
                InfoPopup.showPopup(WARNING, "Veuillez sélectionner un projet avant de l'exporter.").showAndWait();
            }
        } catch (DropboxAPIException | CompressException | DbxException | IOException e) {
            InfoPopup.showPopup(WARNING, e.getMessage()).showAndWait();
        }
    }

    /* Google Drive operations */

    /**
     * Called when user clicks on 'Google Drive' button from 'Importation' tab.
     * Displays available projects found on Google Drive in the listProjectsImport.
     * */
    @FXML
    public void displayAvailableDriveFiles() {
        driveImportButton.setSelected(true);
        dropboxImportButton.setSelected(false);

        listProjectsImport.getItems().clear();
        try {
            List<String> projectsName = listener.getAvailableDriveProjects();
            if (projectsName.size() == 0) {
                popUpException(WARNING, "Aucun projet n'a été trouvé sur Drive.");
            } else {
                listProjectsImport.getItems().addAll(projectsName);
            }
        } catch (DriveAPIException e) {
            popUpException(WARNING, e.getMessage());
        }
    }

    /**
     * Called user clicks on 'Google Drive' button from 'Exportation' tab.
     * */
    @FXML
    public void handleExportToGoogleDrive() {
        try {
            String selection = listProjectsExport.getSelectionModel().getSelectedItem();
            if (selection != null) {
                listener.exportProjectToDrive(selection);  // if anything goes wrong, error is thrown
                popUpException("Info", "Le projet a été exporté avec succès.");
            } else {
                popUpException(WARNING, "Veuillez sélectionner un projet avant de l'exporter.");
            }
        } catch (DriveAPIException | ImportExportException e) {
            popUpException(WARNING, e.getMessage());
        } catch (CompressException e) {
            InfoPopup.showPopup(WARNING, e.getMessage()).showAndWait();
        }
    }

    /* Other operations */

    /**
     * Is called when user clicks the help button
     * Opens help window on the right tab
     */
    @FXML void handleHelp() {
        helpListener.showHelp(HelpViewController.getImportExport());
    }

    /**
     * Is called when user clicks on 'Annuler' button from both 'Importation' and 'Exportation' tabs.
     * */
    @FXML
    public void handleCancel() {
        listener.closeCurrentStage();
    }


    public interface ImportExportListener {
        void closeCurrentStage();
        boolean importProjectFromExplorer() throws UserExceptions, IOException, ExtractException;
        String exportProjectFromExplorer(String project) throws IOException, CompressException;
        void exportProjectToDrive(String selectedItem) throws DriveAPIException, ImportExportException, CompressException;
        boolean exportProjectToDropbox(String project) throws DbxException, IOException, DropboxAPIException, CompressException;
        void importProjectFromDrive(String project) throws DriveAPIException, ImportExportException, UserExceptions, ExtractException;
        boolean importProjectFromDropbox(String project) throws UserExceptions, IOException, DbxException, DropboxAPIException, ImportExportException, ExtractException;
        List<String> getAvailableDriveProjects() throws DriveAPIException;
        List<String> getAvailableDropboxProjects() throws IOException, DbxException, DropboxAPIException;
    }

    public interface HelpListener{
        void showHelp(int tab);
    }
}
