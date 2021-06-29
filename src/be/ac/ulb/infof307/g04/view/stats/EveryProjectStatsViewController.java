package be.ac.ulb.infof307.g04.view.stats;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import java.util.LinkedList;
import java.util.Map;


/**
 * Class Every Project Stats View Controller
 * **/
public class EveryProjectStatsViewController {

    /** Instance of JavaFX FXML **/
    @FXML
    private BarChart<String, Number> collaborationBarChart;
    @FXML
    private BarChart<String, Number> timeBarChart;
    @FXML
    private BarChart<String, Number> tasksBarChart;


    /***
     * Calls every method necessary to show the stats of every project of the current user
     * @param stats is a Map containing the project title as a key and the stats related to this project as value
     */
    public void showStats(Map<String, LinkedList<Integer>> stats){
        showCollaborationStats(stats);
        showTimeStats(stats);
        showTasksStats(stats);
    }

    /**
     * Shows the statistics concerning the task of all the project
     * @param stats is a Map containing the project title as a key and the stats related to this project as value
     */
    private void showTasksStats(Map<String, LinkedList<Integer>> stats) {
        XYChart.Series<String, Number> unfinishedTasksData = new XYChart.Series<>();
        unfinishedTasksData.setName("Tâche(s) non finie(s)");
        for (String title : stats.keySet()){
            unfinishedTasksData.getData().add(new XYChart.Data<>(title, stats.get(title).get(2)));
        }
        XYChart.Series<String, Number> totalTasksData = new XYChart.Series<>();
        totalTasksData.setName("Tâche(s) totale(s)");
        for (String title: stats.keySet()){
            totalTasksData.getData().add(new XYChart.Data<>(title, stats.get(title).get(0)));
        }
        tasksBarChart.getData().add(unfinishedTasksData);
        tasksBarChart.getData().add(totalTasksData);
    }

    /** Shows the statistics concerning the time of all the project
     * @param stats is a Map containing the project title as a key and the stats related to this project as value
     */
    private void showTimeStats(Map<String, LinkedList<Integer>> stats) {
        XYChart.Series<String, Number> realTimeData = new XYChart.Series<>();
        realTimeData.setName("Temps réel");
        for (String title: stats.keySet()){
            realTimeData.getData().add(new XYChart.Data<>(title, stats.get(title).get(5)));
        }

        XYChart.Series<String, Number> expectedTimeData = new XYChart.Series<>();
        expectedTimeData.setName("Temps théorique");
        for (String title: stats.keySet()){
            expectedTimeData.getData().add(new XYChart.Data<>(title, stats.get(title).get(4)));
        }
        timeBarChart.getData().add(realTimeData);
        timeBarChart.getData().add(expectedTimeData);
    }
    /** Shows the statistics concerning the collaboration of all the project
     * @param stats is a Map containing the project title as a key and the stats related to this project as value
     */
    private void showCollaborationStats(Map<String, LinkedList<Integer>> stats) {
        XYChart.Series<String, Number> collaborationData = new XYChart.Series<>();
        for (String title: stats.keySet()){
            collaborationData.getData().add(new XYChart.Data<>(title, stats.get(title).get(3)));
        }
        collaborationBarChart.getData().add(collaborationData);
    }
}
