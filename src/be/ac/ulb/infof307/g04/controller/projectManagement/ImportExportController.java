package be.ac.ulb.infof307.g04.controller.projectManagement;

import be.ac.ulb.infof307.g04.controller.Main;
import be.ac.ulb.infof307.g04.model.exceptions.*;
import be.ac.ulb.infof307.g04.model.Database;
import be.ac.ulb.infof307.g04.model.Project;
import be.ac.ulb.infof307.g04.model.User;
import be.ac.ulb.infof307.g04.model.stats.GlobalStats;
import be.ac.ulb.infof307.g04.util.Archivers;
import be.ac.ulb.infof307.g04.util.api.DriveAPI;
import be.ac.ulb.infof307.g04.util.api.DropboxAPI;
import be.ac.ulb.infof307.g04.util.FilesAndDirectory;
import be.ac.ulb.infof307.g04.util.exceptions.CompressException;
import be.ac.ulb.infof307.g04.util.exceptions.ExtractException;
import be.ac.ulb.infof307.g04.view.importExport.ImportExportViewController;
import com.dropbox.core.DbxException;
import com.google.api.services.drive.Drive;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.Vector;

public class ImportExportController implements ImportExportViewController.ImportExportListener {

    private static final String TEMP_DIRECTORY = "data/temp" ;
    private static final String JSON = ".json";
    private static final String TAR = ".tar.gz";
    private static final int tarStringSize = 7;

    private final Main main;

    public ImportExportController(Main main) {
        this.main = main;
    }

    /* Import/Export projects and stats from/to computer via file explorer */

    /** Compress global stats to a directory chosen with FileExplorer */
    public static String exportStats(String fileName, GlobalStats globalStats, boolean openExplorer) throws CompressException {
        File source = FilesAndDirectory.createDirectory(TEMP_DIRECTORY);  // create temporary dir to extract .json project
        createJsonStats(fileName, globalStats);
        File destination = openExplorer ? getFileFromExplorer(false) : new File("test/testDir/testDest");
        Archivers.compress(fileName, source, destination); // compress the json project into a new directory destination
        FilesAndDirectory.deleteDirectory(new File(TEMP_DIRECTORY));  // delete temporary dir
        return destination.getAbsolutePath();
    }

    /** Export a project in FileExplorer
     * @return return the path of the project if it was successfully exported, a null string otherwise
     */
    @Override
    public String exportProjectFromExplorer(String projectName) throws IOException, CompressException {
        File source = FilesAndDirectory.createDirectory(TEMP_DIRECTORY);  // create temporary dir to extract .json project
        createJsonProject(projectName, main.getConnectedUser());
        File destination = getFileFromExplorer(false) ;
        Archivers.compress(projectName, source, destination); // compress the json project into a new directory destination
        FilesAndDirectory.deleteDirectory(new File(TEMP_DIRECTORY));  // delete temporary dir
        return destination.getAbsolutePath();
    }


    /**
     * Import a project from FileExplorer into the application
     *
     * @return : boolean indicating whether project was successfully imported or failed
     */
    @Override
    public boolean importProjectFromExplorer() throws UserExceptions, IOException, ExtractException {
        Project p = null;
        File destination = FilesAndDirectory.createDirectory(TEMP_DIRECTORY);   // create temporary dir to extract .json project
        File projectJson = getFileFromExplorer(true ) ;
        if (projectJson != null ) {
            if (FilesAndDirectory.isFileTargz(projectJson.getName())) {
                FilesAndDirectory.copyFile(projectJson, new File(destination + "/" + projectJson.getName()));  // copy .json project to temp_directory
                Archivers.extract(projectJson, destination);    // extract the json project tar.gz from a directory source into the directory destination
                String jsonPath = TEMP_DIRECTORY + '/' + projectJson.getName().substring(0, projectJson.getName().length() - tarStringSize) + JSON;
                p = addProjectFromImport(jsonPath);
            } else {
                AlertExceptions alert = new AlertExceptions();
                alert.throwAlert("Mauvais fichier", "Le fichier sélectionné n'est pas une archive tar.gz");
            }
        }
        FilesAndDirectory.deleteDirectory(new File(TEMP_DIRECTORY));  // delete temporary dir
        String res = addProjectToUser(p);
        return !(res.equals("")) ;
    }

    /* DropBox operations */

    /** Import a project from DropBox into the application
     * @param project the projet to import
     * @return the title of the project if it was successfully imported, a empty string otherwise
     */
    @Override
    public boolean importProjectFromDropbox(String project) throws DbxException, IOException, UserExceptions, DropboxAPIException, ExtractException {
        DropboxAPI dropboxAPI = new DropboxAPI(main.getConnectedUser().getUsername());
        File destination = FilesAndDirectory.createDirectory(TEMP_DIRECTORY);   // create temporary dir to extract .json project
        FilesAndDirectory.createDirectory(TEMP_DIRECTORY + "2");
        String fileName = "/" + project + TAR;
        dropboxAPI.downloadFile("/" + main.getConnectedUser().getUsername() + fileName, TEMP_DIRECTORY+"2" + fileName);
        File projectJson = new File(TEMP_DIRECTORY+"2" + fileName) ;
            if (FilesAndDirectory.isFileTargz(projectJson.getName())) {
                FilesAndDirectory.copyFile(projectJson, new File(destination + "/" + projectJson.getName()));  // copy .json project to temp_directory
                Archivers.extract(projectJson, destination);    // extract the json project tar.gz from a directory source into the directory destination
                String jsonPath = TEMP_DIRECTORY + '/' + projectJson.getName().substring(0, projectJson.getName().length() - tarStringSize) + JSON;
                Project p = addProjectFromImport(jsonPath);
                addProjectToUser(p);
                return !(p == null);
            } else {
                AlertExceptions alert = new AlertExceptions();
                alert.throwAlert("Mauvais fichier", "Le fichier sélectionné n'est pas une archive tar.gz");
            }
        FilesAndDirectory.deleteDirectory(new File(TEMP_DIRECTORY));  // delete temporary dir
        FilesAndDirectory.deleteDirectory(new File(TEMP_DIRECTORY + "2"));  // delete temporary dir
        return false;
    }


    /** Connexion between view and model for project exportation to DropBox
     * @param projectName : name of project getting exported
     * @return boolean indicating whether exportation succeeded or failed */
    @Override
    public boolean exportProjectToDropbox(String projectName) throws IOException, DropboxAPIException, CompressException {
        File source = FilesAndDirectory.createDirectory(TEMP_DIRECTORY);  // create temporary dir to extract .json project
        File source2 = FilesAndDirectory.createDirectory(TEMP_DIRECTORY+"2"); //répertoire où ira le fichier compressé
        createJsonProject(projectName, main.getConnectedUser());
        Archivers.compress(projectName, source, source2); // compress the json project and let it at the same place
        DropboxAPI dropboxAPI = new DropboxAPI(main.getConnectedUser().getUsername());
        String filePath = TEMP_DIRECTORY+"2" + (System.getProperty("os.name").equals("Linux") ? "/" : "\\") +projectName+ TAR;
        dropboxAPI.uploadFile(filePath, "/" + main.getConnectedUser().getUsername() + "/" + projectName+ TAR);
        FilesAndDirectory.deleteDirectory(new File(TEMP_DIRECTORY));  // delete temporary dir
        FilesAndDirectory.deleteDirectory(new File(TEMP_DIRECTORY+"2"));
        return true;
    }


    /**  */
    @Override
    public List<String> getAvailableDropboxProjects() throws IOException, DropboxAPIException {
        List<String> fileNamesList;
        List<String> fileNamesListReturn = new Vector<>();
        DropboxAPI dropboxAPI = new DropboxAPI(main.getConnectedUser().getUsername());
        String userName = main.getConnectedUser().getUsername();
        try {
            fileNamesList = dropboxAPI.getFiles("/" + userName);
            String projectName;
            String subString;
            for (String s: fileNamesList) {
                projectName = s;
                subString = projectName.substring(projectName.length() - tarStringSize);
                if (subString.equals(TAR)) {
                    projectName = projectName.replace(TAR, "");
                    projectName = projectName.replace("/" + userName + "/", "");
                    fileNamesListReturn.add(projectName);
                }
            }
        } catch(Exception ignored) {}
        return fileNamesListReturn;
    }

    /* Drive operations */

    /** Connexion between view and model for project importation from Google Drive
     *
     * @param projectName : name of project getting imported
     * @throws DriveAPIException to report exception occurred during Drive operations
     * @throws UserExceptions to report error occurred while adding imported project to user's
     * @throws ImportExportException to report other exceptions occurred in this method */
    @Override
    public void importProjectFromDrive(String projectName) throws DriveAPIException, ImportExportException, UserExceptions, ExtractException {
        Drive service;
        String userDirID, fileID, fileName;
        File temp = null, tempDir = null;

        try {
            service = DriveAPI.startService(main.getConnectedUser().getUsername());
            userDirID = DriveAPI.manageRemoteUserDirectory(service, main.getConnectedUser().getUsername());
            fileID = DriveAPI.getIDFromName(service, userDirID, projectName);

            // Create temporary dir to extract .json project
            temp = FilesAndDirectory.createDirectory(TEMP_DIRECTORY);
            tempDir = FilesAndDirectory.createDirectory(TEMP_DIRECTORY + "2");
            if (temp == null || tempDir == null){ // Errors are handled in FilesAndDirectory class but they make further operations impossible
                throw new IOException();
            }

            fileName = "/" + projectName + TAR;
            DriveAPI.downloadFile(service, fileID, TEMP_DIRECTORY + fileName);

            // file already exists due to downloadFile method but we need a reference to it for Archivers
            File f = new File(TEMP_DIRECTORY + fileName);

            Archivers.extract(f, tempDir);

            Project p = addProjectFromImport(TEMP_DIRECTORY + "2/" + projectName + JSON);
            addProjectToUser(p);

        } catch (IOException e) {
            if (temp != null){
                FilesAndDirectory.deleteDirectory(new File(TEMP_DIRECTORY));
            }
            if (tempDir != null){
                FilesAndDirectory.deleteDirectory(new File(TEMP_DIRECTORY + "2"));
            }
            throw new ImportExportException("Une erreur est survenue lors du traitement du fichier à télécharger.", e);
        }
        // delete temporary dirs
        FilesAndDirectory.deleteDirectory(new File(TEMP_DIRECTORY));  // delete temporary dir
        FilesAndDirectory.deleteDirectory(new File(TEMP_DIRECTORY + "2"));
    }


    /** Implementation of exportation to Google Drive operations
     *
     * @param projectName : name of project getting exported
     * @throws DriveAPIException if error occurred during Drive operations
     * @throws ImportExportException report other errors occurred in this method */
    @Override
    public void exportProjectToDrive(String projectName) throws DriveAPIException, ImportExportException, CompressException {
        File source = null, source2 = null;
        try {
            Drive service = DriveAPI.startService(main.getConnectedUser().getUsername());

            // Check or create user directory on the cloud
            String userDirID = DriveAPI.manageRemoteUserDirectory(service, main.getConnectedUser().getUsername());

            // Upload file
            source = FilesAndDirectory.createDirectory(TEMP_DIRECTORY);  // create temporary dir to extract .json project
            source2 = FilesAndDirectory.createDirectory(TEMP_DIRECTORY + "2"); // directory to store compressed file
            if (source == null || source2 == null){ // Errors are handled in FilesAndDirectory class but they make further operations impossible
                throw new IOException();
            }
            createJsonProject(projectName, main.getConnectedUser());
            Archivers.compress(projectName, source, source2); // compress the json project and let it at the same place
            String filePath = TEMP_DIRECTORY + "2" + (System.getProperty("os.name").equals("Linux") ? "/" : "\\") + projectName + TAR;
            DriveAPI.uploadFile(service, projectName, filePath, userDirID);

        } catch (IOException e) {
            if (source != null){
                FilesAndDirectory.deleteDirectory(new File(TEMP_DIRECTORY));  // delete temporary dir
            }
            if (source2 != null){
                FilesAndDirectory.deleteDirectory(new File(TEMP_DIRECTORY + "2"));
            }
            throw new ImportExportException("Une erreur est survenue lors du traitement du fichier à exporter.", e);
        }
        FilesAndDirectory.deleteDirectory(new File(TEMP_DIRECTORY));  // delete temporary dir
        FilesAndDirectory.deleteDirectory(new File(TEMP_DIRECTORY + "2"));
    }

    /** Display files available for download
     *
     * @return List of strings containing name of files on Drive available for download
     * @throws DriveAPIException to report exception occurred in Drive operations */
    @Override
    public List<String> getAvailableDriveProjects() throws DriveAPIException {
        Drive service = DriveAPI.startService(main.getConnectedUser().getUsername());
        String userDirID = DriveAPI.manageRemoteUserDirectory(service, main.getConnectedUser().getUsername());
        return DriveAPI.getListFiles(service, userDirID);
    }


    /** Close the current Stage */
    @Override
    public void closeCurrentStage() {
        main.closeCurrentStage();
    }

    /** Return File selected with FileExplorer */
    public static File getFileFromExplorer(boolean filesOnly) {
        JFileChooser f = new JFileChooser();
        if (filesOnly) {
            f.setFileSelectionMode(JFileChooser.FILES_ONLY);  // show files only in File Explorer
        }
        else {
            f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        f.showSaveDialog(null);
        return f.getSelectedFile();
    }


    /** Create a .json file for a given project */
    public static void createJsonProject(String projectName, User user) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Project project = user.getProject(projectName);
        FileWriter fileWriter = new FileWriter(TEMP_DIRECTORY + "/" + projectName + JSON);
        fileWriter.write(gson.toJson(project));
        fileWriter.close();
    }

    /** Create a .json file for a given set of global stats */
    public static void createJsonStats(String fileName, GlobalStats globalStats)  {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter fileWriter = new FileWriter(TEMP_DIRECTORY + "/" + fileName + JSON);
            fileWriter.write(gson.toJson(globalStats));
            fileWriter.close();
        }
        catch (IOException e) {
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert("Erreur d'écriture de fichier", "Le fichier n'a pas pu être écrit correctement");
        }
    }

    /** Return Project object from json file in path */
    public static Project addProjectFromImport(String path) throws IOException {
        Gson gson = createGson();
        FileReader read = new FileReader(path);
        Project newProject = gson.fromJson(read, Project.class);
        read.close();
        return newProject ;
    }

    /**
     * Add a Project to the list of Project of user and to the view
     *
     * @param newProject : the project to be add
     * @return : the new Project title
     */
    public String addProjectToUser(Project newProject) throws UserExceptions {
        if (newProject != null) {
            if (main.getConnectedUser().getListProject().containsKey(newProject.getTitle())){
                AlertExceptions alert = new AlertExceptions();
                alert.throwAlert("Projet existant", "Un projet du même nom existe déjà" );
                return "";
            }
            main.getConnectedUser().addProject(newProject.getTitle(), newProject);
            Database ddb = new Database(main.getConnectedUser());
            ddb.updateDetails();
            main.getMenuController().addProjectToPanel(main.getMenuController().createNewProjectPanel(newProject));
            return newProject.getTitle();
        } else {
            return "";
        }
    }

    /** Create a gson object */
    public static Gson createGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }
}