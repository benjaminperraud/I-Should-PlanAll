package be.ac.ulb.infof307.g04.controller.projectManagement;

import be.ac.ulb.infof307.g04.controller.Main;
import be.ac.ulb.infof307.g04.model.Database;
import be.ac.ulb.infof307.g04.model.Project;
import be.ac.ulb.infof307.g04.model.Task;
import be.ac.ulb.infof307.g04.view.project.*;
import be.ac.ulb.infof307.g04.model.SubProject;
import be.ac.ulb.infof307.g04.model.User;
import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.view.MenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.*;


/**
 * Controller class for the ProjectInfoDisplayController view controller, allows to control the display of projects
 *
 * Listener : ProjectInfoDisplayController, MenuController, AddSubProjectDialogController, ProjectContentController,
 *            AddProjectDialogController
 */
public class ProjectController implements ProjectInfoDisplayController.ProjectInfoListener, MenuController.ProjectListener, AddSubProjectDialogController.ProjectListener, ProjectContentController.ProjectListener, AddProjectDialogController.ProjectListener {

    /** Instance private final Main **/
    private final Main main;

    /** The current Project being display with showProjectInfoDisplay() or showProjectContent() */
    private Project currentProject;

    /** Instance private ProjectContentController **/
    private ProjectContentController projectContentController;

    /** Instance final private TaskController **/
    private final TaskController taskController;

    /** Instance final private SubProjectController **/
    private final SubProjectController subProjectController;

    /** Instance final private Map<String,ProjectPanel **/
    private final Map<String,ProjectPanel> mapProjectPanel;


    /** Constructor ProjectController **/
    public ProjectController(Main mainController) {
        main = mainController;
        mapProjectPanel = new HashMap<>();
        taskController = new TaskController(main, this);
        subProjectController = new SubProjectController(main, this);
    }

    /**
     * Show all information concerning a project (task, subproject)
     * @param accessibleText :
     */
    public void showProjectInfoDisplay(String accessibleText){
        Project project = main.getConnectedUser().getProject(accessibleText);
        FXMLLoader loader = new FXMLLoader();
        Parent rootPane = Main.loadFXML(ProjectInfoDisplayController.class, loader);
        ProjectInfoDisplayController controller = loader.getController();
        main.setCurrentProject(project);
        currentProject = project;
        controller.setMainListener(this);
        controller.displayProjectInfo(project.getTitle(), project.getDescription(), project.getDueDate(), project.getLabels());

        refactoring("analytics.png", "Projet", rootPane, false);

//        Stage stage = new Stage() ;
//        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/analytics.png"));
//        stage.setTitle("Projet");
//        main.getUnclosedStage().add(stage);
//        main.setNonReleasable(stage);
//        assert rootPane != null;
//        Scene scene = new Scene(rootPane);
//        stage.setScene(scene);
//        stage.showAndWait(); // show the stage and wait for closing
    }

    /**
     * Show all information concerning a project (task, subproject)
     * @param accessibleText :
     */
    public void showProjectContent(String accessibleText){
        Project project = main.getConnectedUser().getProject(accessibleText);
        FXMLLoader loader = new FXMLLoader();
        Parent rootPane = Main.loadFXML(ProjectContentController.class, loader);
        projectContentController = loader.getController();
        main.setCurrentProject(project);
        currentProject = project;
        projectContentController.setListeners(taskController, subProjectController, this);
        initializeTaskPanel();
        initializeSubProjectPanels();

        refactoring("Tâches et sous-projets de " + accessibleText, "analytics.png", rootPane,false);

//        Stage stage = new Stage() ;
//        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/analytics.png"));
//        stage.setTitle("Tâches et sous-projets de " + accessibleText);
//        main.getUnclosedStage().add(stage);
//        main.setNonReleasable(stage);
//        assert rootPane != null;
//        Scene scene = new Scene(rootPane);
//        stage.setScene(scene);
//        stage.showAndWait(); // show the stage and wait for closing

        main.closeLastStage();
    }


    /** Show the scene to add a new project to the main window */
    public void showAddProject() {
        FXMLLoader loader = new FXMLLoader();
        AnchorPane rootPane = (AnchorPane) Main.loadFXML(AddProjectDialogController.class, loader);
        AddProjectDialogController controller = loader.getController();
        controller.setListener(this);
        refactoring("Ajouter un projet", "analytics.png", rootPane, true);

//        Stage stage = new Stage() ;
//        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/analytics.png"));
//        stage.setTitle("Ajouter un projet");
//        assert rootPane != null;
//        Scene scene = new Scene(rootPane);
//        stage.setScene(scene);
//        main.getUnclosedStage().add(stage);
//        main.setNonReleasable(stage);
//        stage.show();
    }


    /**
     *  Cette méthode permet d'alléger le code et de la réutiliser dans toutes les méthodes de type "show" qui créent puis lancent les fenêtres"
     * @param image : le nom de l'icône qui est ajouté à la fenêtre
     * @param title : le titre de la fenêtre
     * @param rootPane : le panneau
     * @param show : true si on veut faire un .show(), false pour un showAndWait()
     */
    public void refactoring(String image, String title, Parent rootPane, boolean show){
        Stage stage = new Stage();
        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/" + image));
        stage.setTitle(title);
        main.getUnclosedStage().add(stage);
        main.setNonReleasable(stage);
        assert rootPane != null;
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        if (show){
            stage.show();
        }
        else {
            stage.showAndWait(); // show the stage and wait for closing
        }
    }

    /** Initalize the Pannel of Sub Project into the Project Content window */
    private void initializeSubProjectPanels() {
        for (SubProject subPro : main.getCurrentProject().getSubProjectList().values()) {
            projectContentController.addPanelSub(subPro.getTitle(), subPro.getDescription(), subPro.getDueDate(), subPro.getLabels());
        }
    }

    /**
     * Initialize every tasks of connected user on the panel displayed
     */
    public void initializeTaskPanel() {
        List<Task> tasks = new LinkedList<>(currentProject.getTasksMap().values());
        Collections.sort(tasks);
        for (Task task : tasks) {
            projectContentController.addPanelTask(task.getTitle(), task.getDescription(), task.getStartDate(), task.getEndDate(), task.getCollaborator());
        }
    }

    /** Delete the current Project for the collaborators of the current User  */
    @Override
    public void deleteProject(){
        main.getUserController().deleteProjectCollaborator(currentProject.getTitle(), main.getConnectedUser());
    }

    /** Delete the Project Panel on the window  */
    @Override
    public void deleteProjectFromPanel(){
        if (currentProject instanceof SubProject){
            projectContentController.deleteSubprojectPanel(currentProject.getTitle());
        }
        else{
            main.getMenuController().deletePanel(currentProject.getTitle()) ;
        }
    }

    /**
     * Call the controller view method to refresh project on panel display
     *
     * @param oldName old project name
     * @param newName new project name
     */
    public void refreshProjectName(String oldName, String newName) {
        if (!(currentProject instanceof SubProject)){
            main.getMenuController().refreshProjectName(oldName, newName);
            // Modify project for All collaborator
        }
    }

    /**
     * Update the map with new project name
     *
     * @param oldName old project name
     * @param newName new project name
     */
    @Override
    public void updateProjectMapPanel(String oldName, String newName){
        mapProjectPanel.put(newName, mapProjectPanel.get(oldName));
        mapProjectPanel.remove(oldName);
        mapProjectPanel.get(newName).setText(newName);
    }

    /**
     * Create a new project and add it to user
     *
     * @param newTitle new project title
     * @param description description of the project
     * @param dueDate due date of the project
     */
    @Override
    public void createNewProject(String newTitle, String description, LocalDate dueDate) {
        currentProject.setDescription(description) ;
        currentProject.setDueDate(dueDate);
        if (!currentProject.getTitle().equals(newTitle)){
            currentProject.setTitle(newTitle);
            if (currentProject instanceof SubProject){
                Project project = main.getConnectedUser().getProject(((SubProject) currentProject).getProjectParentName());
                project.getSubProjectList().put(newTitle, (SubProject) currentProject);
            }
            else{
                main.getConnectedUser().getListProject().put(newTitle, currentProject);
            }
            main.getConnectedUser().getProject(newTitle).updateSubParentName(newTitle);
        }
    }

    /**
     * Update information in all collaborator when project title change
     *
     * @param oldName : the last name of the current project
     * @param newName : the new name of the current project
     */
    @Override
    public void updateCollaborator(String oldName, String newName) throws UserExceptions {
        // Modify project for All collaborator
        if (currentProject.getCollaborators()!=null && !currentProject.getCollaborators().isEmpty()) {
            for (String collaborator : currentProject.getCollaborators()) {
                if (main.getConnectedUser().getUsername().equals(collaborator)) {
                    continue;
                }
                User userCollab = Database.getUserFromDatabase(collaborator);
                main.getUserController().deleteProjectCollaborator(oldName, userCollab);
                if (currentProject instanceof SubProject){
                    Project project = userCollab.getProject(((SubProject) currentProject).getProjectParentName());
                    project.getSubProjectList().put(newName, (SubProject) currentProject);
                }
                else{
                    userCollab.addProject(newName, currentProject);
                }
                Database ddb = new Database(userCollab);
                ddb.updateDetails();
            }
        }
    }

    /**
     * Add a new project on the window and database
     * @param title new project title
     * @param description description of the project
     * @param endDate due date of the project
     */
    @Override
    public void addNewProject(String title, String description, LocalDate endDate) throws UserExceptions {
        main.getMenuController().addNewProject(title, description, endDate);
    }

    /** Close the current screen (Stage) **/
    @Override
    public void closeCurrentStage() {
        main.closeCurrentStage();
    }

    /** Update the Project of the User and his collaborators if they exist **/
    @Override
    public void updateDetailUser() throws UserExceptions {
        main.updateDetailsUser();
    }

    /** Delete the Project of the User and his collaborators if they exist **/
    @Override
    public void deleteUpdateDetailsUser() throws UserExceptions {
        main.deleteUpdateDetailsUser();
    }

    /** Add label on the current Project **/
    @Override
    public boolean addLabel(String newLab) {
        return currentProject.addLabel(newLab);}

    /** Delete label on the current Project **/
    @Override
    public void removeLabel(String lab) {
        currentProject.removeLabel(lab);
    }

    /**
     * Add a label to each subproject of a project
     * @param label : label to be added
     */
    @Override
    public void addChildrenLabel(String label) {
        Project project = currentProject;
        for (SubProject subProject : project.getSubProjectList().values()){
            subProject.addLabel(label);
        }
    }

    /**
     * Remove a label to each subproject of a project
     * @param label : label to be removed
     */
    @Override
    public void removeChildrenLabel(String label) {
        Project project = currentProject;
        for (SubProject subProject : project.getSubProjectList().values()){
            subProject.removeLabel(label);
        }
    }

    /** Check if project titles match */
    @Override
    public boolean isNewProjectTitle(String newValue) {
        return !main.getConnectedUser().getListProject().containsKey(newValue);
    }

    /** Check if project descriptions match */
    @Override
    public boolean isNewProjectDescription(String newValue) {
        return main.getConnectedUser().isProjectTitleUnique(newValue);
    }

    @Override
    public void putPanelToMap(String title, ProjectPanel projectPanel) {
        mapProjectPanel.put(title, projectPanel);
    }

    public Map<String,ProjectPanel> getMapProjectPanel() { return mapProjectPanel; }

    /**
     * Check if the Title is a unique title Project on the database of the current User
     *
     * @param title : title Project to check
     * @return boolean : return true if the title is unique
     *
     * */
    @Override
    public boolean isProjectTitleUnique(String title) {
        return main.getConnectedUser().isProjectTitleUnique(title);
    }

    /** Getters and Setters **/
    @Override
    public LocalDate getProjectEndDate() {
        return currentProject.getDueDate();
    }

    public String getCurrentProjectTitle(){ return currentProject.getTitle(); }

    public ProjectContentController getProjectContentController() {
        return projectContentController;
    }
}
