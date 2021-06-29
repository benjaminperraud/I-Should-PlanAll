package be.ac.ulb.infof307.g04.controller.projectManagement;

import be.ac.ulb.infof307.g04.controller.Main;
import be.ac.ulb.infof307.g04.model.Project;
import be.ac.ulb.infof307.g04.model.SubProject;
import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.util.ToolDate;
import be.ac.ulb.infof307.g04.view.project.ProjectContentController;
import be.ac.ulb.infof307.g04.view.project.AddSubProjectDialogController;
import be.ac.ulb.infof307.g04.view.project.ProjectPanel;
import be.ac.ulb.infof307.g04.view.project.SubProjectView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller class for the ProjectContentController.SubProjectListener, AddSubProjectDialogController.SubProjectDialogListener view controllers,
 * allows to control stages related to sub-projects management
 */
public class SubProjectController implements ProjectContentController.SubProjectListener, AddSubProjectDialogController.SubProjectDialogListener {

    /** Instance Variable Main */
    private final Main main;

    /** Instance Variable AddSubProjectDialog Controller */
    private AddSubProjectDialogController addSubProjectDialogController;

    /** Instance Variable Project Controller */
    private final ProjectController projectController;

    /** Constructor */
    public SubProjectController(Main mainController, ProjectController projectController) {
        main = mainController;
        this.projectController = projectController;
    }

    /** Show the scene to add a new subproject to a project */
    @Override
    public void showAddSub() {
        FXMLLoader loader = new FXMLLoader();
        AnchorPane rootPane = (AnchorPane) Main.loadFXML(AddSubProjectDialogController.class, loader);
        AddSubProjectDialogController controller = loader.getController();
        addSubProjectDialogController = controller;
        controller.setListener(this);
        controller.setProjectListener(projectController);
        Stage stage = new Stage() ;
        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/analytics.png"));
        stage.setTitle("Ajouter un sous-projet");
        main.getUnclosedStage().add(stage);
        main.setNonReleasable(stage);
        assert rootPane != null;
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.showAndWait(); // Show the stage
    }

    /**
     * Refresh information of a sub-project
     *
     * @param projectPanel : the Panel in view representing a sub-project
     * @param accessibleText : the title of the sub-project
     */
    @Override
    public void refreshSubProject(ProjectPanel projectPanel, String accessibleText) {
        Project subProject = main.getConnectedUser().getProject(accessibleText);
        if (subProject != null){
            SubProjectView.refreshSubProject(projectPanel, subProject.getTitle(), subProject.getDescription(), subProject.getDueDate(), subProject.getLabels());
        }
    }

    /**
     * Checks if the fields of a sub-project are valid
     * @param endDate : the end date of the sub-project
     * @param titleField the title of the sub-project
     * @param descriptionField : the description of the sub-project
     * @return true if the fields are valid, otherwise it returns false
     */
    @Override
    public boolean checkValidity(DatePicker endDate, TextField titleField, TextArea descriptionField) {
        if (!ToolDate.isEarlierThanDate(endDate.getValue(), main.getCurrentProject().getDueDate())) {
            addSubProjectDialogController.wrongDate();
            return false;
        } else if (titleField.getText().isEmpty() | main.getConnectedUser().isProjectTitleUnique(titleField.getText())) {
            addSubProjectDialogController.wrongTitle();
            return false;
        } else {
            return true;
        }
    }

    /**
     * Add a sub-project to the current project
     * @param title : the title of the sub-project
     * @param description : the description of the sub-project
     * @param value : the end date of the sub-project
     * @throws UserExceptions to report error occurred while adding sub-project to user's project
     */
    @Override
    public void createNewSubProject(String title, String description, LocalDate value) throws UserExceptions {
        SubProject subProject = new SubProject(title, description, value, main.getCurrentProject());
        subProject.setCollaborators(main.getCurrentProject().getCollaborators());
        for (String label : main.getCurrentProject().getLabels()) {
            subProject.addLabel(label);
        }
        main.getCurrentProject().addSubProject(subProject);
        main.updateDetailsUser();
    }

    /**
     * Get labels from the parent project
     * @return a LinkedList with the labels of the parent project
     */
    @Override
    public List<String> getParentLabels() {
        return main.getCurrentProject().getLabels();
    }

    /** Close the current stage */
    @Override
    public void closeCurrentStage() {
        main.closeCurrentStage();
    }
}