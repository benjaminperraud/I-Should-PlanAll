package be.ac.ulb.infof307.g04.view.stats;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.util.Pair;
import java.util.List;

/**
 * Class Project Stats View Controller
 * **/
public class ProjectStatsViewController {

    /** Instance private ProjectStatsListener **/
    private ProjectStatsListener listener;

    /** Instance of JavaFX FXML **/
    @FXML
    private Label projectTitle;
    @FXML
    private Label personStats;
    @FXML
    private PieChart taskPieChart;
    @FXML
    private BarChart<String, Number> timeBarChart;

    /***
     * Shows the stats of a project
     * @param title the title of the project to show stats
     */
    public void showStats(String title){
        projectTitle.setText("Statistiques de : " + title);
        List<Pair<String, Long>> projectStats = listener.getStats(title);
        personStats.setText(projectStats.get(0).getValue().toString());
        showTime(title, projectStats);
        showTasks(projectStats);
    }

    /**
     * Fills the bar chart with the estimated time and the real time for the selected project
     * @param title title of the selected project
     * @param projectStats stats of the selected project
     */
    public void showTime(String title, List<Pair<String, Long>> projectStats){
        XYChart.Series<String, Number> realTimeData = new XYChart.Series<>();
        realTimeData.setName("Temps réel");
        realTimeData.getData().add(new XYChart.Data<>(title, projectStats.get(4).getValue()));

        XYChart.Series<String, Number> expectedTimeData = new XYChart.Series<>();
        expectedTimeData.setName("Temps théorique");
        expectedTimeData.getData().add(new XYChart.Data<>(title, projectStats.get(3).getValue()));
        timeBarChart.getData().add(realTimeData);
        timeBarChart.getData().add(expectedTimeData);
    }

    /**
     * Fills the pie chart with the finished and unfinished tasks of the selected project
     * @param projectStats stats of the selected project
     */
    public void showTasks(List<Pair<String, Long>> projectStats){
        int finishedTasks = (int) (projectStats.get(1).getValue() - projectStats.get(2).getValue());
        PieChart.Data finishedTasksSlice = new PieChart.Data("Tâche(s) finie(s)", finishedTasks);
        finishedTasksSlice.setName("Tâche(s) finie(s)");
        PieChart.Data unfinishedTasksSlice = new PieChart.Data("Tâche(s) finie(s)", projectStats.get(2).getValue());
        unfinishedTasksSlice.setName("Tâche(s) non finie(s)");
        taskPieChart.getData().add(finishedTasksSlice);
        taskPieChart.getData().add(unfinishedTasksSlice);
        taskPieChart.setLabelLineLength(50);
        taskPieChart.setLabelsVisible(true);
        taskPieChart.setLegendSide(Side.TOP);
        taskPieChart.setStartAngle(180);
    }

    /**
     * Sets the listener so the view controller can interact with it's controller
     * @param listener : listener for the StatsController
     */
    public void setListener(ProjectStatsListener listener) { this.listener = listener; }

    /**
     *  Interface Listener ProjectStatsListener
     *
     * Function : - showEveryProjectStats : Shows the stats of every projects
     *            - getStats : Return the stats of a project by the title of the project
     *
     */
    public interface ProjectStatsListener {
        List<Pair<String, Long>> getStats(String title);
    }

}
