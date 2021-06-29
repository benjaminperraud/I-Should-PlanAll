package be.ac.ulb.infof307.g04.controller.toolsManagement;

import be.ac.ulb.infof307.g04.controller.Main;
import be.ac.ulb.infof307.g04.controller.projectManagement.ProjectController;
import be.ac.ulb.infof307.g04.model.Project;
import be.ac.ulb.infof307.g04.model.stats.GlobalStats;
import be.ac.ulb.infof307.g04.model.stats.ProjectStats;
import be.ac.ulb.infof307.g04.view.MenuController;
import be.ac.ulb.infof307.g04.view.stats.EveryProjectStatsViewController;
import be.ac.ulb.infof307.g04.view.stats.ProjectStatsViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.util.List;

/** Class Stats Controller
 *
 * Listener : ProjectStatsViewController, MenuController
 *
 */
public class StatsController  implements ProjectStatsViewController.ProjectStatsListener, MenuController.StatsListener {

    /** Instance Variable Main */
    private final Main main;

    /** Constructor */
    public StatsController(Main main) {
        this.main = main;
    }

    /** Controller for display of project statistics
     * @see ProjectController */
    public void showProjectStats(String accessibleText){
        Project project = main.getConnectedUser().getProject(accessibleText);
        FXMLLoader loader = new FXMLLoader();
        Parent rootPane = Main.loadFXML(ProjectStatsViewController.class, loader);
        ProjectStatsViewController controller = loader.getController();
        controller.setListener(this);
        controller.showStats(project.getTitle());
        Stage stage = new Stage() ;
        stage.setTitle("Statistiques du projet");
        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/statistiques.png"));
        main.getUnclosedStage().add(stage);
        main.setNonReleasable(stage);
        assert rootPane != null;
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.showAndWait(); // Show stage
    }

    /** Show the statistics of every project */
    @Override
    public void showEveryProjectStats(){
        FXMLLoader loader = new FXMLLoader();
        Parent rootPane = Main.loadFXML(EveryProjectStatsViewController.class, loader);
        EveryProjectStatsViewController controller = loader.getController();
        GlobalStats globalStats = new GlobalStats(main.getConnectedUser());
        controller.showStats(globalStats.getProjectsCompletion());
        Stage stage = new Stage() ;
        stage.getIcons().add(new Image("file:src/be/ac/ulb/infof307/g04/resources/statistiques.png"));
        stage.setTitle("Statistiques du projet");
        main.getUnclosedStage().add(stage);
        main.setNonReleasable(stage);
        assert rootPane != null;
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.showAndWait(); // Show stage
    }

    /**
     * Gets statistics from a project
     * @param title : the title of the project
     * @return a LinkedList with project statistics
     */
    @Override
    public List<Pair<String, Long>> getStats(String title) {
        ProjectStats projectStats = new ProjectStats(main.getConnectedUser().getProject(title));
        return projectStats.getStats();
    }
}
