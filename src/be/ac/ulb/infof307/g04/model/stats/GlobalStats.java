package be.ac.ulb.infof307.g04.model.stats;

import be.ac.ulb.infof307.g04.controller.projectManagement.ImportExportController;
import be.ac.ulb.infof307.g04.model.Project;
import be.ac.ulb.infof307.g04.model.Task;
import be.ac.ulb.infof307.g04.model.User;
import be.ac.ulb.infof307.g04.util.exceptions.CompressException;
import javafx.util.Pair;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

/**
 * Class GlobalStats
 *
 * Computes and gathers statistics about all tasks/projects of a given user and their projects levels of completion
 */
public class GlobalStats {

    /** Private static final constant **/
    private static final int SECOND_IN_ONE_HOUR = 3600;

    /**  Instance transient User **/
    private final User user;

    /** Instance List<Pair<String, Long>> **/
    private final List<Pair<String, Long>> stats = new LinkedList<>();

    /** Instance Map<String, LinkedList<Integer>> **/
    private final Map<String, LinkedList<Integer>> projectsCompletion = new HashMap<>();

    /** Constructor GlobalStats **/
    public GlobalStats(User user) {
        this.user = user;
        countTasks();
        computeCompletion();
    }

    /** gathers stats about the user's tasks in all projects */
    public final void countTasks() {
        Long numberTasks = 0L;
        Long numberFinishedTasks = 0L;
        Long numberRemainingTasks = 0L;
        LocalDate now = LocalDate.now();
        for (Project project : user.getListProject().values()) {
            for (Task task : project.getTasksMap().values()) {
                numberTasks++;
                if (task.getEndDate().isBefore(now)) {
                    numberFinishedTasks++;
                }
                else {
                    numberRemainingTasks++;
                }
            }
        }
        stats.add(new Pair<>("Nombre de tâches", numberTasks));
        stats.add(new Pair<>("Tâches restantes", numberRemainingTasks));
        stats.add(new Pair<>("Tâches accomplies", numberFinishedTasks));
    }

    /** Gathers individual completion statistics for every project */
    public final void computeCompletion() {
        LocalDate now = LocalDate.now();
        for (Project project : user.getListProject().values()) {
            Integer numberTasks = 0;
            Integer numberFinishedTasks = 0;
            Integer numberRemainingTasks = 0;
            for (Task task : project.getTasksMap().values()) {
                numberTasks++;
                if (task.getEndDate().isBefore(now)) {
                    numberFinishedTasks++;
                }
                else {
                    numberRemainingTasks++;
                }
            }
            Integer estimatedTime = Math.toIntExact(Duration.between(project.getStartDate().atStartOfDay(), project.getDueDate().atStartOfDay()).getSeconds() / SECOND_IN_ONE_HOUR);
            Integer realTime = Math.toIntExact(Duration.between(project.getStartDate().atStartOfDay(), LocalDate.now().atStartOfDay()).getSeconds() / SECOND_IN_ONE_HOUR);
            projectsCompletion.put(
                    project.getTitle(),
                    new LinkedList<>(Arrays.asList(numberTasks, numberFinishedTasks, numberRemainingTasks, project.getCollaborators().size(), estimatedTime, realTime))
            );
        }
    }


    /** export the user's stats as a compressed .json */
    public void export(String fileName, boolean openExplorer) throws CompressException {
        ImportExportController.exportStats(fileName, this, openExplorer);
    }

    /** Getters and Setters **/
    public Map<String, LinkedList<Integer>> getProjectsCompletion() {
        return projectsCompletion;
    }

    /**
     * getStats()
     */
    public List<Pair<String, Long>> getStats() {
        return stats;
    }

}
