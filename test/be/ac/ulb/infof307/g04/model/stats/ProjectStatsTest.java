package be.ac.ulb.infof307.g04.model.stats;

import be.ac.ulb.infof307.g04.model.Project;
import be.ac.ulb.infof307.g04.model.Task;
import javafx.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

public class ProjectStatsTest {

    Project dummyProject;

    @BeforeEach
    void initDummyProject() {
        LocalDate dateNow = LocalDate.now();
        LocalDate datePast = dateNow.minusDays(1);
        LocalDate dateFuture = dateNow.plusDays(1);
        dummyProject = new Project("dummyProject", "desc", dateFuture);
        dummyProject.addTask(new Task("Task1", "desc", datePast, dateFuture));
        dummyProject.addTask(new Task("Task2", "desc", datePast, datePast));
        dummyProject.addTask(new Task("Task3", "desc", datePast, datePast));
        dummyProject.addCollaborator("Collaborator");
    }

    @Test
    void testStats() {
        ProjectStats dummyStats = new ProjectStats(dummyProject);
        List<Pair<String, Long>> stats = dummyStats.getStats();
        Assertions.assertEquals(stats.get(0), new Pair<>("Nombre de collaborateurs", new Long(1)));
        Assertions.assertEquals(stats.get(1), new Pair<>("Nombre total de tâches", new Long(3)));
        Assertions.assertEquals(stats.get(2), new Pair<>("Nombre de tâches restantes", new Long(1)));
        Assertions.assertEquals(stats.get(3), new Pair<>("Durée estimée du projet", new Long(24)));
        Assertions.assertEquals(stats.get(4), new Pair<>("Durée du projet", new Long(0)));
    }

}
