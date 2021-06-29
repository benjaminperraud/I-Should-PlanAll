package be.ac.ulb.infof307.g04.controller;

import be.ac.ulb.infof307.g04.controller.projectManagement.CollaborationController;
import be.ac.ulb.infof307.g04.controller.projectManagement.ImportExportController;
import be.ac.ulb.infof307.g04.controller.projectManagement.ProjectController;
import be.ac.ulb.infof307.g04.controller.toolsManagement.HelpController;
import be.ac.ulb.infof307.g04.controller.toolsManagement.ReminderController;
import be.ac.ulb.infof307.g04.controller.toolsManagement.StatsController;
import be.ac.ulb.infof307.g04.controller.userManagement.UserProfileController;
import be.ac.ulb.infof307.g04.model.*;
import be.ac.ulb.infof307.g04.model.exceptions.AlertExceptions;
import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.view.MenuController;
import be.ac.ulb.infof307.g04.view.importExport.ImportExportViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


/** Class Main Controller
 *
 * Listener : MenuController,
 *
 * **/
public class Main extends Application implements
        MenuController.MenuListener
{
    public static final int CONTROLLER_LENGTH = 10;
    
    /** Instance static current User **/
    private static User connectedUser;

    /** Instance Variable User **/
    private UserProfileController userController ;

    /** Instance Variable ProjectController **/
    private ProjectController projectController;

    /** Instance Variable MenuController **/
    private MenuController menuController;

    /** Instance Variable Collaboration Controller */
    private CollaborationController collabController;

    /** Instance Variable Stats Controller */
    private StatsController statsController;

    /** Instance Variable Help Controller */
    private HelpController helpController;

    /** List Stage Controller **/
    private LinkedList<Stage> unclosedStage ;

    /** Instance Variable current Project **/
    private Project currentProject; // the current project's content displayed on screen

    /** Sub controller for cloud tasks **/
    private ImportExportController importExportController;

    public static void main(String[] args) {
        launch(args);
    }

    /** Initalization attributes and launch First Screen of the Application **/
    @Override
    public void start(Stage primaryStage) { //Start page of the program : Connexion
        primaryStage.setTitle("Connexion");
        unclosedStage = new LinkedList<>();
        userController = new UserProfileController(this);
        projectController = new ProjectController(this);
        userController.showUserConnexion();
        importExportController = new ImportExportController(this);
        collabController = new CollaborationController(this);
        statsController = new StatsController(this);
        helpController = new HelpController(this);
    }

    /** Show the showMainWindow scene */
    public void showMainWindow(){
        FXMLLoader loader = new FXMLLoader();
        AnchorPane menu = (AnchorPane) loadFXML(MenuController.class, loader);
        menuController = loader.getController();
        menuController.setListeners(this, userController, projectController, collabController, statsController, helpController);
        initialisePanel();
        menuController.displayUserName(connectedUser.getUsername());
        Stage stage = new Stage() ;
        stage.setTitle("Bienvenue " + connectedUser.getUsername() );
        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/project.png"));
        assert menu != null;
        Scene scene = new Scene(menu);
        stage.setScene(scene);
        unclosedStage.add(stage);
        stage.show();
        if (connectedUser.hasUrgentTasks()) {
            new ReminderController(this).showSceneReminder();
        }
    }

    /**
     * Makes the stage non releasable
     * @param stage the stage
     * */
    public void setNonReleasable(Stage stage){
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(unclosedStage.get(unclosedStage.size()-2));   // makes previousStage unreachable until closing stage
        stage.setOnCloseRequest(we -> unclosedStage.removeLast());   // call if the stage is closed with the Close Button(X)
    }


    /**
     * Show Import Export project scene
     * */
    @Override
    public void showImportExport() {
        FXMLLoader loader = new FXMLLoader();
        Parent rootPane = loadFXML(ImportExportViewController.class, loader);
        ImportExportViewController controller = loader.getController();
        controller.setListener(importExportController, helpController);
        controller.setListProjectsExport(getListProjectKeySet());
        Stage stage = new Stage() ;
        stage.setTitle("Importation/Exportation d'un projet");
        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/cloud-computing.png"));
        unclosedStage.add(stage);
        setNonReleasable(stage);
        assert rootPane != null;
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.showAndWait(); // Show stage
    }

    /**
     * Initialise the Panel of projects in the MainWindow
     * */
    public void initialisePanel() {
        if (connectedUser.getListProject().size() == 0) {
            menuController.noProject();
        } else {
            for (String key : connectedUser.getListProject().keySet()) {
                menuController.addProjectToPanel(menuController.createNewProjectPanel(connectedUser.getListProject().get(key)));
            }
        }
    }

    /**
     * Call updateDetails on the Database from the Current User and Update Json file from All colaborators
     * */
    public void updateDetailsUser() throws UserExceptions {
        Database ddb = new Database(connectedUser);
        ddb.updateDetails();
        // Modify project for All collaborator
        if (getCurrentProject().getCollaborators()!=null && !getCurrentProject().getCollaborators().isEmpty()) {
                for (String collaborator : getCurrentProject().getCollaborators()) {
                    if (getConnectedUser().getUsername().equals(collaborator)) {
                        continue;
                    }
                    User userCollab = Database.getUserFromDatabase(collaborator);
                    if (getCurrentProject() instanceof SubProject) {
                        SubProject subProject = (SubProject) getCurrentProject();
                        Project projectParent = connectedUser.getProject(subProject.getProjectParentName());
                        userController.deleteProjectCollaborator(subProject.getTitle(), userCollab);
                        userCollab.getProject(projectParent.getTitle()).addSubProject((SubProject) getCurrentProject());
                    }
                    else{
                        userController.deleteProjectCollaborator(getCurrentProject().getTitle(), userCollab);
                        userCollab.addProject(getCurrentProject().getTitle(), getCurrentProject());
                        userCollab.getListProject().get(getCurrentProject().getTitle()).setTitle(getCurrentProject().getTitle());
                    }
                    Database ddbCollab = new Database(userCollab);
                    ddbCollab.updateDetails();
                }
            }
    }

    /**
     * Call updateDetails after a delete Instance of Project or Sub Project
     * from the Current User and Update Json file from All colaborators
     * */
    public void deleteUpdateDetailsUser() throws UserExceptions {
        Database ddb = new Database(connectedUser);
        ddb.updateDetails();
        // Modify project for All collaborator
        if (getCurrentProject().getCollaborators()!=null && !getCurrentProject().getCollaborators().isEmpty()) {
            for (String collaborator : getCurrentProject().getCollaborators()) {
                if (getConnectedUser().getUsername().equals(collaborator)) {
                    continue;
                }
                User userCollab = Database.getUserFromDatabase(collaborator);
                userController.deleteProjectCollaborator(getCurrentProject().getTitle(), userCollab);
                Database ddbCollab = new Database(userCollab);
                ddbCollab.updateDetails();
            }
        }
    }

    /**
     * Close the current stage
     */
    @Override
    public void closeCurrentStage() {
        unclosedStage.getLast().close();
        unclosedStage.removeLast();
    }

    /**
     * Close the last Stage, except if it is the mainStage
     */
    public void closeLastStage(){
        if (unclosedStage.size() > 1){
            closeCurrentStage();
        }
    }

    /**
     * Return a new pane for a new project
     * @return TiltedPane corresponding to the new project created
     * */
    @Override
    public TitledPane createNewPaneProject(String title, String description, LocalDate endDate) throws UserExceptions {
        Project newProject = new Project(title,description, endDate);
        connectedUser.addProject(newProject.getTitle(), newProject);
        return menuController.createNewProjectPanel(newProject);
    }

    /**
     * Load the FXML file and return the corresponding Pane
     * @param clazz : the class of the view controller corresponding to the fxml file to load
     * @param loader : FXML loader
     * @return loader of the fxml class
     */
    public static Pane loadFXML(Class<?> clazz, FXMLLoader loader) {
        try {
            String name = clazz.getSimpleName().substring(0,clazz.getSimpleName().length()- CONTROLLER_LENGTH);
            loader.setLocation(clazz.getResource( name+ ".fxml"));
            return loader.load();
        }
        catch (IOException e) {
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert("Erreur lors de l'ouverture de la page FXML", "Veuillez relancer l'application");
            return null;
        }
    }

    /** Update the display Usernemane **/
    public void updateUsername() {
        menuController.displayUserName(connectedUser.getUsername());
    }

    /**
     *
     * Getters and Setters
     *
     */
    public static void setConnectedUser(User user) { connectedUser = user ;}

    public void setCurrentProject(Project project) {
        currentProject = project;
    }

    public Project getCurrentProject(){
        return currentProject;
    }

    public User getConnectedUser() {
        return connectedUser;
    }

    public  UserProfileController getUserController() {return userController;}

    public MenuController getMenuController() {
        return menuController;
    }

    public LinkedList<Stage> getUnclosedStage() {
        return unclosedStage;
    }

    /**
     * Report the project reminder
     * @param projectTitle: name of the project
     */

    public void reportProject(String projectTitle) throws UserExceptions {
        connectedUser.getProject(projectTitle).report();
        new Database(connectedUser).updateDetails();
    }

    /**
     * Report the task reminder
     * @param projectTitle: name of the project
     * @param taskTitle: name of the task
     */

    public void reportTask(String projectTitle, String taskTitle) throws UserExceptions {
        connectedUser.getProject(projectTitle).getTasksMap().get(taskTitle).report();
        new Database(connectedUser).updateDetails();
    }

    @Override
    public Set<String> getListProjectKeySet() {
        return connectedUser.getListProject().keySet();
    }

    @Override
    public String getProjectDescription(String text) {
        return connectedUser.getListProject().get(text).getDescription();
    }

    @Override
    public List<String> getProjectLabel(String text) {
        return connectedUser.getListProject().get(text).getLabels();
    }

    @Override
    public LocalDate getProjectDueDate(String text) {
        return connectedUser.getListProject().get(text).getDueDate();
    }
}