package be.ac.ulb.infof307.g04.view.reminder;

import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.view.popup.InfoPopup;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * Class Reminder View Controller
 * **/
public class ReminderViewController {

    /** Instance private ReminderListener **/
    private ReminderListener listener;

    /** Instance of JavaFX FXML **/
    @FXML
    private Accordion taskAccordion;
    @FXML
    private Accordion projAccordion;
    @FXML
    private Label reminderMessage;

    /**
     * Called by user click on 'Ok' button
     */
    @FXML
    void handleOk() {
        listener.closeCurrentStage();
    }

    /**
     * Called by user click on 'Reporté' button of a project
     * @param project: project's reminder to report
     * @param button: clicked button
     */
    void handleSelectedProject(String project, Button button){
        try {
            listener.reportProject(project);
            button.setText("Reporté !");
            button.setDisable(true);
        } catch (UserExceptions e) {
            Alert alert = InfoPopup.showPopup("Error report", "Erreur lors de la sauvegarde des données");
            alert.showAndWait();
        }
    }

    /**
     * Called by user click on 'Reporté' button of a task
     * @param project: task's reminder to report
     * @param button: clicked button
     */
    void handleSelectedTask(String project, String task, Button button){
        try {
            listener.reportTask(project, task);
            button.setText("Reporté !");
            button.setDisable(true);
        } catch (UserExceptions e) {
            Alert alert = InfoPopup.showPopup("Error report", "Erreur lors de la sauvegarde des données");
            alert.showAndWait();
        }
    }

    /**
     * Show informations about projects and tasks in the reminder popup
     * @param urgentProjects: number of urgent projects
     * @param urgentTasks: number of urgent tasks
     */
    public void showReminderMessage(int urgentProjects, int urgentTasks){
        reminderMessage.setText("Vous avez " + urgentProjects + " projet(s) et " + urgentTasks + " tâche(s) à terminer ces prochains jours :");
    }

    /**
     * Add the projects in the reminder popup
     * @param projectTitle: project's title
     * @param projectDescription: project's description
     * @param urgentProjects: number of urgent projects
     * @param remainingTasks: number of remaining tasks
     * @param projRemainingDays: remaining day of the project
     * @return: total number of urgent projects
     */
    public int showProject(String projectTitle, String projectDescription , int urgentProjects, int remainingTasks, long projRemainingDays) {
        int urgent = urgentProjects + 1;
        TitledPane projectPane = new TitledPane();
        projectPane.setText(projectTitle + " : " + projRemainingDays + " jour(s) restant(s)");
        VBox projectContent = new VBox();
        Button reportButton = new Button("Reporter (1 jour)");
        reportButton.setOnAction(event->handleSelectedProject(projectTitle, reportButton));
        projectContent.getChildren().add(new Label("Tâches restantes : " + remainingTasks));
        projectContent.getChildren().add(new Label("Description : " + projectDescription));
        projectContent.getChildren().add(reportButton);
        projectPane.setContent(projectContent);
        projAccordion.getPanes().add(projectPane);
        return urgent;
    }


    /**
     * Add the tasks in the reminder popup
     * @param projectTitle: project's title
     * @param taskTitle: task's title
     * @param taskDescription: task's description
     * @param remainingTasks: number of remaining tasks
     * @param taskRemainingDays: remaining day of the task
     * @return: total number of urgent tasks
     */
    public int showTask(String projectTitle, String taskTitle, String taskDescription, int remainingTasks, long taskRemainingDays) {
        int remaining = remainingTasks + 1;
        TitledPane taskPane = new TitledPane();
        taskPane.setText(taskTitle + " : " + taskRemainingDays + " jour(s) restant(s)");
        VBox taskContent = new VBox();
        Button reportButton = new Button("Reporter (1 jour)");
        reportButton.setOnAction(event->handleSelectedTask(projectTitle, taskTitle, reportButton));
        taskContent.getChildren().add(new Label("Projet : " + projectTitle));
        taskContent.getChildren().add(new Label("Description : " + taskDescription));
        taskContent.getChildren().add(reportButton);
        taskPane.setContent(taskContent);
        taskAccordion.getPanes().add(taskPane);
        return remaining;
    }

    /**
     * Sets the listener so the view controller can interact with it's controller
     * @param listener : listener for the Reminder Controller
     */
    public void setListener(ReminderListener listener) { this.listener = listener; }

    /**
     *  Interface Listener ReminderListener
     *
     * Function : - closeCurrentStage: Close the current stage
     *            - reportTask: Report the reminder of the task
     *            - reportProject: Report the reminder of the project
     *
     */
    public interface ReminderListener{
        void closeCurrentStage();
        void reportTask(String project, String task) throws UserExceptions;
        void reportProject(String project) throws UserExceptions;
    }
}