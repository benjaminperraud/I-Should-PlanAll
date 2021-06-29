package be.ac.ulb.infof307.g04.controller.projectManagement;

import be.ac.ulb.infof307.g04.controller.Main;
import be.ac.ulb.infof307.g04.controller.projectManagement.ProjectController;
import be.ac.ulb.infof307.g04.model.Database;
import be.ac.ulb.infof307.g04.model.Task;
import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.view.project.ProjectContentController;
import be.ac.ulb.infof307.g04.view.task.EditTaskDialogController;
import be.ac.ulb.infof307.g04.view.task.AddTaskDialogController;
import be.ac.ulb.infof307.g04.view.task.TaskPanel;
import be.ac.ulb.infof307.g04.view.task.TaskView;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Controller class for the AddTaskDialogController, EditTaskDialogController and ProjectContentController view controller, allows to control the display of tasks
 *
 * Listener : TaskDialogListener, EditTaskDialogListener, TaskListener
 */
public class TaskController implements AddTaskDialogController.TaskDialogListener, EditTaskDialogController.EditTaskDialogListener, ProjectContentController.TaskListener {

    /** Instance Variable Main */
    private final Main main;

    /** Instance Variable Project Controller */
    private final ProjectController projectController;

    /** The task being displayed on screen with showEditTask() */
    private Task currentTask;

    private final Map<String, TaskPanel> mapTaskPanel;

    /** Constructor */
    public TaskController(Main mainController, ProjectController projectController){
        main = mainController;
        this.projectController = projectController;
        mapTaskPanel = new HashMap<>();
    }

    /**
     * Show the scene to add a new Task to a project
     */
    public void showAddTask() {
        FXMLLoader loader = new FXMLLoader();
        AnchorPane rootPane = (AnchorPane) Main.loadFXML(AddTaskDialogController.class, loader);
        AddTaskDialogController controller = loader.getController();
        controller.setListener(this);
        // set last change list of Collaborators
        if (main.getCurrentProject().getCollaborators() != null) {
            controller.setProjectComboBox(main.getCurrentProject().getCollaborators());
        }
        Stage stage = new Stage();
        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/task.png"));
        stage.setTitle("Ajouter une tâche");
        main.getUnclosedStage().add(stage);
        main.setNonReleasable(stage);
        assert rootPane != null;
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.showAndWait(); // show the stage and wait for closing
    }

    /**
     * Show the editTask scene
     * @param taskName : the title of the task
     */
    @Override
    public void showEditTask(String taskName){
        Task task = main.getConnectedUser().getTask(taskName);
        FXMLLoader loader = new FXMLLoader();
        Parent rootPane = Main.loadFXML(EditTaskDialogController.class, loader);
        EditTaskDialogController controller = loader.getController();
        currentTask = task;
        controller.initialise(task.getTitle(), task.getDescription(), task.getStartDate(), task.getEndDate());
        controller.setListener(this);
        Stage stage = new Stage();
        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/task.png"));
        stage.setTitle("Modifier une tâche");
        main.getUnclosedStage().add(stage);
        main.setNonReleasable(stage);
        // set current List of Collaborators
        controller.setProjectComboBox(task.getCollaborator());
        assert rootPane != null;
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.showAndWait(); // Show the stage
    }

    /**
     * Apply date changes on tasks in current project
     *
     * @param start starting date of the task
     * @param end   ending date of the task
     */
    @Override
    public void applyProjectChanges(String taskName, LocalDate start, LocalDate end) throws UserExceptions {
        main.getCurrentProject().getTask(taskName).setStartDate(start);
        main.getCurrentProject().getTask(taskName).setEndDate(end);
        main.updateDetailsUser();
    }

    /**
     * Apply date changes on tasks in current project
     *
     * @param title       title of the task
     * @param description description of the task
     * @param collabo     list of collaborators
     */
    @Override
    public void applyProjectChanges(String title, String description, ObservableList<String> collabo) throws UserExceptions {
        Set<String> collaborators = new HashSet<>();
        for (String collaborator : collabo) {
            if (Database.userExists(collaborator) && main.getCurrentProject().getCollaborators().contains(collaborator)) {
                collaborators.add(collaborator);
            }
        }
        main.getCurrentProject().removeTask(currentTask);
        Task task = new Task(title, description, collaborators);
        main.getCurrentProject().addTask(task);
        main.updateDetailsUser();
    }

    /**
     * Update the Map matching Task title and the Task Panel
     *
     * @param oldName : old title of the current Task
     * @param newName : new title of the current Task
     */
    public void updateMapTaskPanel(String oldName, String newName) {
        mapTaskPanel.put(newName, mapTaskPanel.get(oldName));
        mapTaskPanel.remove(oldName);
        mapTaskPanel.get(newName).setText(newName);
    }

    @Override
    public boolean isTaskTitleUnique(String taskTitle) {
        return main.getCurrentProject().getTasks().containsKey(taskTitle);
    }

    /**
     * Add a task
     *
     * @param title       title of the task
     * @param startDate   starting date of the task
     * @param endDate     ending date of the task
     * @param description description of the task
     */
    @Override
    public void addTask(String title, LocalDate startDate, LocalDate endDate, String description, String collabo) throws UserExceptions {
        Task task = new Task(title, description, startDate, endDate);
        currentTask = task;
        if (main.getCurrentProject().getCollaborators().contains(collabo) && Database.userExists(collabo)) {
            task.getCollaborator().add(collabo);
        }
        main.getCurrentProject().addTask(task);
        main.updateDetailsUser();
        closeCurrentStage();
    }

    /**
     * Delete task in user tasklist and on the panel displayed
     */
    public void deleteTask() throws UserExceptions {
        main.getCurrentProject().removeTask(currentTask);
        main.updateDetailsUser();
        projectController.getProjectContentController().deleteTaskPanel(currentTask.getTitle());
    }

    /**
     * Check if the collaborator in parameter is already assigned with the current project
     *
     * @param collaborator : the collaborator to be check
     * @return : true if collaborator is assigned on current project, false otherwise
     */
    @Override
    public boolean isCollaboratorAssigned(String collaborator) {
        return main.getCurrentProject().getCollaborators().contains(collaborator);
    }

    /**
     * Refresh a task after it was modified
     * */
    @Override
    public void refreshTask(TaskPanel taskPanel, String accessibleText) {
        Task task = main.getConnectedUser().getTask(accessibleText);
        if(task != null){
            TaskView.refreshTask(taskPanel, task.getTitle(), task.getDescription(), task.getStartDate(), task.getEndDate(), task.getCollaborator());
        }
    }

    /** Close the current stage */
    @Override
    public void closeCurrentStage() {
        main.closeCurrentStage();
    }

    @Override
    public void putPanelToMap(String title, TaskPanel taskPanel) {
        mapTaskPanel.put(title, taskPanel);
    }

    /**
     * Verify if endDate end before or at the same time as main project.
     *
     * @param startDate : start date of the task
     * @param endDate : end date of the task
     * @return true if the ending date of the task is valid
     */
    @Override
    public boolean isTaskDateValid(LocalDate startDate, LocalDate endDate) {
        return main.getCurrentProject().isTaskDateValid(new Task("", "", startDate, endDate));
    }

    /**
     * Verify if the title of a task already exists or not
     *
     * @param taskTitle : the title of the task
     * @return true if title is unique, false otherwise
     */
    @Override
    public boolean taskTitleExists(String taskTitle) {
        return main.getConnectedUser().getTask(taskTitle) != null;
    }

    @Override
    public ProjectContentController getProjectContentController() {
        return projectController.getProjectContentController();
    }

    @Override
    public String getCurrentTaskName() {
        return currentTask.getTitle();
    }
}
