package be.ac.ulb.infof307.g04.view.project;

import be.ac.ulb.infof307.g04.view.task.TaskPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


public class ProjectContentController {

    private TaskListener taskListener;
    private SubProjectListener subProjectListener;
    private ProjectListener projectListener;

    @FXML
    private Accordion taskAccordion;
    @FXML
    private Accordion subProjAccordion;


    /**
     * Called by user click on 'Ajouter une tâche' button
     * */
    @FXML
    public void handleAddTask(){
        taskListener.showAddTask();
    }

    /**
     * Called by user click on 'Ajouter un sous-projet' button
     * */
    @FXML
    public void handleAddSubProject(){
        subProjectListener.showAddSub();
    }

    public void addTaskPanelToAccordion(TitledPane t1){
        taskAccordion.getPanes().add(t1);
    }

    public void addSubPanelToAccordion(TitledPane t1) {
        subProjAccordion.getPanes().add(t1);
    }

    public void deleteTaskPanel(String nameTask){
        taskAccordion.getPanes().removeIf(pane -> pane.getText().equals(nameTask));
    }

    public void deleteSubprojectPanel(String nameSubproject){
        subProjAccordion.getPanes().removeIf(pane -> pane.getText().equals(nameSubproject));
    }

    /**
     * Create a SubProject Panel, add it to the view, and link the button to the appropriate show method
     */
    public void addPanelSub(String title, String description, LocalDate date, List<String> labels) {
        ProjectPanel projectPanel = new ProjectPanel(title, description, date, labels);
        projectListener.putPanelToMap(title, projectPanel);
        EventHandler<ActionEvent> modifyButtonHandler = event -> {
            Button btn = (Button) event.getSource();
            projectListener.showProjectInfoDisplay(btn.getAccessibleText());
            subProjectListener.refreshSubProject(projectPanel, btn.getAccessibleText());
        };
        EventHandler<ActionEvent> selectButtonHandler = event -> {
            Button btn = (Button) event.getSource();
            projectListener.showProjectContent(btn.getAccessibleText());
        };
        projectPanel.getModifyButton().setOnAction(modifyButtonHandler);
        projectPanel.getSelectButton().setOnAction(selectButtonHandler);
        projectListener.getProjectContentController().addSubPanelToAccordion(projectPanel.getPanel());
    }

    /**
     * Create a Task Panel, add it to the view, and link the button to the appropriate show method
     */
    public void addPanelTask(String title, String description, LocalDate startDate, LocalDate endDate, Set<String> collaborator) {
        TaskPanel taskPanel = new TaskPanel(title, description, startDate, endDate, collaborator) ;
        taskListener.putPanelToMap(title, taskPanel);
        // Handle edit button
        EventHandler<ActionEvent> modifyButtonHandler = event ->{
            Button btn = (Button) event.getSource();
            taskListener.showEditTask(btn.getAccessibleText());
            taskListener.refreshTask(taskPanel, btn.getAccessibleText());
        };
        taskPanel.getModifyButton().setOnAction(modifyButtonHandler);
        taskListener.getProjectContentController().addTaskPanelToAccordion(taskPanel.getPanel());
    }

    /** Sets the listener so the view controller can interact with it's controller
     * @param taskListener : listener for TaskController
     * @param subProjectListener : listener for SubProjectController
     * @param projectListener : listener for ProjectController
     */
    public void setListeners(TaskListener taskListener, SubProjectListener subProjectListener, ProjectListener projectListener) {
        this.taskListener = taskListener;
        this.subProjectListener = subProjectListener;
        this.projectListener = projectListener;
    }

    public interface TaskListener {
        void showAddTask();
        void showEditTask(String accessibleText);
        void putPanelToMap(String title, TaskPanel taskPanel);
        ProjectContentController getProjectContentController();
        void refreshTask(TaskPanel taskPanel, String accessibleText);
    }

    public interface SubProjectListener {
        void showAddSub();
        void refreshSubProject(ProjectPanel projectPanel, String accessibleText);
    }

    public interface ProjectListener {
        void showProjectInfoDisplay(String accessibleText);
        void showProjectContent(String accessibleText);
        ProjectContentController getProjectContentController();
        void putPanelToMap(String title, ProjectPanel projectPanel);
        void closeCurrentStage();
    }
}
