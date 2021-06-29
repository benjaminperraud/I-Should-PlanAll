package be.ac.ulb.infof307.g04.model.stats;

import be.ac.ulb.infof307.g04.model.Project;
import be.ac.ulb.infof307.g04.model.Task;
import be.ac.ulb.infof307.g04.util.ToolDate;
import java.time.LocalDate;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

/** Class ProjectStats
 *
 * Gathers stats for a single project
 */
public class ProjectStats {

    /** Private static final constant **/
    private static final int SECOND_IN_ONE_HOUR = 3600;

    /** Instance Variable of Project **/
    private final Project project;

    /** Instance Variable of List<Pair<String, Long>> **/
    private final List<Pair<String, Long>> stats = new LinkedList<>();

    /** Constructor ProjectStats **/
    public ProjectStats(Project project) {
        this.project = project;
        if (project.getCollaborators() == null) {
            stats.add(new Pair<>("Nombre de collaborateurs", 0L));
        } else {
            stats.add(new Pair<>("Nombre de collaborateurs", (long) project.getCollaborators().size()));
        }
        stats.add(new Pair<>("Nombre total de tâches",(long) project.getTasksMap().size()));
        stats.add(new Pair<>("Nombre de tâches restantes", (long) countUnfinishedTasks()));
        stats.add(new Pair<>("Durée estimée du projet", Duration.between(this.project.getStartDate().atStartOfDay(), this.project.getDueDate().atStartOfDay()).getSeconds() / SECOND_IN_ONE_HOUR));
        stats.add(new Pair<>("Durée du projet", Duration.between(this.project.getStartDate().atStartOfDay(), LocalDate.now().atStartOfDay()).getSeconds() / SECOND_IN_ONE_HOUR));
    }

    /** Count all unfinished Tasks **/
    public final int countUnfinishedTasks() {
        LocalDate now = LocalDate.now();
        int result = 0;
        Map<String, Task> taskList = project.getTasksMap();
        for (Task task : taskList.values()) {
            result += ToolDate.isEarlierThanDate(now, task.getEndDate()) ? 1 : 0;
        }
        return result;
    }

    /** Getter stats **/
    public List<Pair<String, Long>> getStats() {
        return stats;
    }

}
