package be.ac.ulb.infof307.g04.view;

import be.ac.ulb.infof307.g04.model.Project;
import be.ac.ulb.infof307.g04.view.help.HelpViewController;
import be.ac.ulb.infof307.g04.view.project.ProjectPanel;
import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.time.LocalDate;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

/**
 * Class Menu Controller
 * **/
public class MenuController {

    /** Instance private MenuListener, UserListener, ProjectListener, CollabListener, StatsListener, HelpListener **/
    private MenuListener mainListener;
    private UserListener userListener;
    private ProjectListener projectListener;
    private CollabListener collabListener;
    private StatsListener statsListener;
    private HelpListener helpListener;

    /** Instance of JavaFX FXML **/
    @FXML
    private MenuButton userMenuButton;
    @FXML
    private Accordion accordionListProjects;
    @FXML
    private Text textError;
    @FXML
    private Label noProjLabel;
    @FXML
    private Pane myPane;
    @FXML
    private StackPane myStackPane;


    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    public void initialize() {
        myPane.setBackground(new Background(new BackgroundFill(Color.DARKGREY, CornerRadii.EMPTY, Insets.EMPTY)));
        myStackPane.setBackground(new Background(new BackgroundFill(Color.DARKGREY, CornerRadii.EMPTY, Insets.EMPTY)));
        textError.setText("");
        accordionListProjects.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                VBox vbox = (VBox) newValue.getContent();
                String projectDescription = mainListener.getProjectDescription(newValue.getText());
                List<String> projectLabels = mainListener.getProjectLabel(newValue.getText());
                LocalDate projectDate = mainListener.getProjectDueDate(newValue.getText());
                int i = 0;
                for(Node nodeIn: vbox.getChildren()){
                    if(nodeIn instanceof Label) {
                        if (i == 1) {
                            ((Label) nodeIn).setText("Description : " + projectDescription);
                        }
                        if (i == 0) {
                            ((Label) nodeIn).setText("Étiquette(s) : " + projectLabels);
                        }
                        i += 1;
                    } else if (nodeIn instanceof TextFlow) {
                        ((TextFlow) nodeIn).getChildren().set(1,new Text(projectDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))));
                    }
                }
            }
        });
    }

    /**
     * Display the username on the top right corner
     * @param name: name to display
     */
    public void displayUserName(String name){
        userMenuButton.setText(name);
    }

    /** JavaFX
     * Called by user click on 'Déconnexion' button
     * */
    @FXML
    public void handleDisconnectionAction(){
        mainListener.closeCurrentStage();
        userListener.showUserConnexion();
    }

    /** JavaFX
     * Called by user click on 'Modifier mon profil' button
     * */
    @FXML
    public void handleEditProfileAction(){
        textError.setText("");
        userListener.showEditProfile();
    }

    /** JavaFX
     * Called by user click on 'Statistiques des projets' button
     * */
    @FXML
    public void handleStatsAction() {
        textError.setText("");
        statsListener.showEveryProjectStats();
    }

    /** JavaFX
     * Called by user click on 'Collaborer' button
     * */
    @FXML
    public void handleCollaboration(){
        textError.setText("");
        collabListener.showCollaboration();
    }

    /** JavaFX
     * Called by user click on "Voir collaboration en attente"
     * */
    @FXML
    void handleWaitingCollab(){
        textError.setText("");
        collabListener.showWaitingCollaborations();
    }

    /** JavaFX
     * Called by user click on "Importer/Exporter un projet"
     * */
    @FXML
    void handleImportExport() {
        textError.setText("");
        mainListener.showImportExport();
    }

    /** JavaFX
     * Called by user click on 'Ajouter un projet' button
     * */
    @FXML
    void handleAddNewProject()  { //créer un nouveau projet
        projectListener.showAddProject();
    }

    /**
     * Add a new project to panel
     * @param title: title of the project
     * @param description: description of the project
     * @param endDate: end date of the project
     */
    public void addNewProject(String title, String description, LocalDate endDate) throws UserExceptions {
        addProjectToPanel(mainListener.createNewPaneProject(title, description, endDate));
        noProjLabel.setText(null);
        textError.setText("Un nouveau projet < "+ title +" > à bien été rajouté");
    }

    /** JavaFX
     * Called when user click on 'Aide', show Help Popup
     */
    @FXML
    void handleHelp() throws IOException {
        textError.setText("");
        helpListener.showHelp(HelpViewController.getOverview());
    }

    /**
     * Write a message in the project panel when there is no project
     */
    public void noProject(){
        noProjLabel.setText("Il n'y a pas de projet pour le moment");
    }

    /**
     * delete the message in the project panel
     */
    public void setNoProjectEmpty() {
        noProjLabel.setText(null);
    }

    /**
     * close the list panel
     */
    public void closePanel(){
        for (TitledPane pane : accordionListProjects.getPanes() ) {
            pane.setExpanded(false);
        }
    }

    /**
     * delete a project from the project panel
     * @param nameProject: name of the project to delete
     */
    public void deletePanel(String nameProject){
        accordionListProjects.getPanes().removeIf(pane -> pane.getText().equals(nameProject));
        if (accordionListProjects.getPanes().size() == 0){
            noProjLabel.setText("Il n'y a pas de projet pour le moment");
        }

        textError.setText("Un projet < " + nameProject + " > à bien été supprimé");
    }

    /**
     * Change the name of a project in the project panel
     * @param oldName: project's old name
     * @param newName: project's new name
     */
    public void refreshProjectName(String oldName, String newName) {
        textError.setText("");
        for (TitledPane pane : accordionListProjects.getPanes() ) {
            if (pane.getText().equals(oldName)){
                pane.setText(newName);
            }
        }
    }

    /**
     * Add a project in the project panel
     * @param t1: Titled Pane to add in the project panel
     */
    public void addProjectToPanel(TitledPane t1){
        accordionListProjects.getPanes().add(t1);
    }

    /**
     * Create a new project panel
     * @param project: project used to create the panel
     * @return the created panel
     */
    public TitledPane createNewProjectPanel(Project project) {
        ProjectPanel projectPanel = new ProjectPanel(project.getTitle(), project.getDescription(), project.getDueDate(), project.getLabels());
        projectListener.getMapProjectPanel().put(project.getTitle(),projectPanel);
        EventHandler<ActionEvent> modifyButtonHandler = event -> {
            textError.setText("");
            closePanel();
            Button btn = (Button) event.getSource();
            projectListener.showProjectInfoDisplay(btn.getAccessibleText());
        };
        EventHandler<ActionEvent> selectButtonHandler = event -> {
            textError.setText("");
            closePanel();
            Button btn = (Button) event.getSource();
            projectListener.showProjectContent(btn.getAccessibleText());
        };
        EventHandler<ActionEvent> statsButtonHandler = event -> {
            textError.setText("");
            closePanel();
            Button btn = (Button) event.getSource();
            statsListener.showProjectStats(btn.getAccessibleText());
        };
        projectPanel.getModifyButton().setOnAction(modifyButtonHandler);
        projectPanel.getSelectButton().setOnAction(selectButtonHandler);
        projectPanel.getStatsButton().setOnAction(statsButtonHandler);
        return projectPanel.getPanel();
    }

    /**
     * Sets the listener so the view controller can interact with it's controller
     * @param mainListener : the listener for Main
     * @param userListener : the listener for UserProfileController
     * @param projectlistener : the listener for ProjectController
     */
    public void setListeners(MenuListener mainListener, UserListener userListener, ProjectListener projectlistener, CollabListener collabListener, StatsListener statsListener, HelpListener helpListener) {
        this.mainListener = mainListener;
        this.userListener = userListener;
        this.projectListener = projectlistener;
        this.collabListener = collabListener;
        this.statsListener = statsListener;
        this.helpListener = helpListener;
    }

    /**
     *  Interface Listener MenuListener
     *
     * Function : - closeCurrentStage : Close the current window (stage)
     *            - createNewPaneProject: Return a new pane for a new project
     *            - getListProjectKeySet: Return the identifier of the projects of the connected user
     *            - getProjectDescription: Return the project's description by the identifier of the project
     *            - getProjectLabel: Return the project's label by the identifier of the project
     *            - getProjectDueDate: Return the project's due date by the identifier of the project
     *            - showImportExport: Show Import Export project scene
     *
     */
    public interface MenuListener{
        void closeCurrentStage();
        TitledPane createNewPaneProject(String title, String description, LocalDate endDate) throws UserExceptions;
        Set<String> getListProjectKeySet();
        String getProjectDescription(String text);
        List<String> getProjectLabel(String text);
        LocalDate getProjectDueDate(String text);
        void showImportExport();
    }

    /**
     *  Interface Listener UserListener
     *
     * Function : - showEditProfile: Show the EditProfile scene
     *            - showUserConnexion: Show the UserConnexion scene
     *
     */
    public interface UserListener{
        void showEditProfile();
        void showUserConnexion();
    }

    /**
     *  Interface Listener ProjectListener
     *
     * Function : - showProjectInfoDisplay: Show all information concerning a project (task, subproject)
     *            - showProjectContent: Show all information concerning a project (task, subproject)
     *            - showAddProject: Show the scene to add a new project to the main window
     *            - getMapProjectPanel: Return the project's panel
     *
     */
    public interface ProjectListener{
        void showProjectInfoDisplay(String accessibleText);
        void showProjectContent(String accessibleText);
        void showAddProject();
        Map<String,ProjectPanel> getMapProjectPanel();
    }

    /**
     *  Interface Listener CollabListener
     *
     * Function : - showCollaboration: Show Collaboration scene
     *            - showWaitingCollaborations: Show Waiting Collaboration scene
     *
     */
    public interface CollabListener{
        void showCollaboration();
        void showWaitingCollaborations();
    }

    /**
     *  Interface Listener StatsListener
     *
     * Function : - showEveryProjectStats: Show the statistics of every project
     *            - showProjectStats: Controller for display of project statistics
     *
     */
    public interface StatsListener{
        void showEveryProjectStats();
        void showProjectStats(String accessibleText);
    }

    /**
     *  Interface Listener HelpListener
     *
     * Function : - showHelp: Show Help section scene
     *
     */
    public interface HelpListener{
        void showHelp(int tab) throws IOException;
    }
}
