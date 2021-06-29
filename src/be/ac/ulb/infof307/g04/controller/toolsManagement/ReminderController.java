package be.ac.ulb.infof307.g04.controller.toolsManagement;

import be.ac.ulb.infof307.g04.controller.Main;
import be.ac.ulb.infof307.g04.model.Project;
import be.ac.ulb.infof307.g04.model.Task;
import be.ac.ulb.infof307.g04.model.User;
import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.view.reminder.ReminderViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * Class Reminder Controller
 *
 * Listener : ReminderViewController
 */
public class ReminderController implements ReminderViewController.ReminderListener {

    /** Instance Variable Main */
    private final Main main;

    /** Instance Variable ReminderView Controller */
    private ReminderViewController reminderController;

    /** Constructor */
    public ReminderController(Main mainController) {
        main = mainController;
        reminderController = new ReminderViewController();
    }

    /**
     * Show the reminder scene
     */
    public void showSceneReminder() {
        FXMLLoader loader = new FXMLLoader();
        Parent rootPane = Main.loadFXML(ReminderViewController.class, loader);
        reminderController = loader.getController();
        reminderController.setListener(this);
        controlReminder(main.getConnectedUser());
        Stage stage = new Stage();
        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/reminder.png"));
        stage.setTitle("Rappels");
        main.getUnclosedStage().add(stage);
        main.setNonReleasable(stage);
        assert rootPane != null;
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.show(); // Shows the stage
    }

    /**
     * Control if there is reminder to show for a user
     * @param user: user controlled
     */
    public void controlReminder(User user) {
        LocalDate now = LocalDate.now();
        int urgentTasks = 0;
        int urgentProjects = 0;
        for (Project project : user.getListProject().values()) {
            int remainingTasks = 0;
            for (Map.Entry<String, Task> task : project.getTasks().entrySet()) {
                long taskRemainingDays = ChronoUnit.DAYS.between(now.atStartOfDay(), task.getValue().getReport().addTo(task.getValue().getEndDate().atStartOfDay()));
                if (taskRemainingDays <= 2 && taskRemainingDays >= 0) {
                    remainingTasks = reminderController.showTask(project.getTitle(), task.getValue().getTitle(), task.getValue().getDescription(), remainingTasks, taskRemainingDays);
                }
            }
            urgentTasks += remainingTasks;
            long projRemainingDays = ChronoUnit.DAYS.between(now.atStartOfDay(), project.getReport().addTo(project.getDueDate().atStartOfDay()));
            if (projRemainingDays <= 2 && projRemainingDays >= 0) {
                urgentProjects = reminderController.showProject(project.getTitle(), project.getDescription(), urgentProjects, remainingTasks, projRemainingDays);
            }
        }
        reminderController.showReminderMessage(urgentProjects, urgentTasks);
    }

    /** Close the current stage */
    @Override
    public void closeCurrentStage() {
        main.closeCurrentStage();
    }

    /**
     * Report the reminder of the task
     * @param project: name of the task's project
     * @param task: name of the task
     */
    @Override
    public void reportTask(String project, String task) throws UserExceptions {
        main.reportTask(project, task);
    }

    /**
     * Report the reminder of the project
     * @param project: name of the project
     */
    @Override
    public void reportProject(String project) throws UserExceptions {
        main.reportProject(project);
    }
}
