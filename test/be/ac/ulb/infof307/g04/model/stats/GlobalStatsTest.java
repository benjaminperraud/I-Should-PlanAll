package be.ac.ulb.infof307.g04.model.stats;

import be.ac.ulb.infof307.g04.model.Database;
import be.ac.ulb.infof307.g04.model.Project;
import be.ac.ulb.infof307.g04.model.Task;
import be.ac.ulb.infof307.g04.model.User;
import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import be.ac.ulb.infof307.g04.util.Archivers;
import be.ac.ulb.infof307.g04.util.exceptions.CompressException;
import be.ac.ulb.infof307.g04.util.exceptions.ExtractException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class GlobalStatsTest {

    User dummyUser;
    Project project1;
    Project project2;
    Project project3;

    @BeforeEach
    void initDummyUser() {
        try {
            dummyUser = new User("test", "test", "test", "test@mail.com", "test", new HashMap<>());
            initProjects();
            dummyUser.addProject("Projet1", project1);
            dummyUser.addProject("Projet2", project2);
            dummyUser.addProject("Projet3", project3);
        } catch (UserExceptions e) {
            System.out.println("Erreur utilisateur lors du test");
        }
    }

    @AfterAll
    static void cleanFiles() {
        new File(Database.USER_DIRECTORY + "test.json").delete();
    }

    void initProjects() {
        LocalDate dateNow = LocalDate.now();
        LocalDate datePast = dateNow.minusDays(1);
        LocalDate dateFuture = dateNow.plusDays(1);
        project1 = new Project("Projet1", "desc", dateFuture);
        project1.addTask(new Task("Task1", "desc", datePast, dateFuture));
        project1.addTask(new Task("Task2", "desc", datePast, datePast));
        project1.addTask(new Task("Task3", "desc", datePast, datePast));
        project2 = new Project("Projet2", "desc", dateNow);
        project2.addTask(new Task("Task4", "desc", datePast, datePast));
        project3 = new Project("Projet3", "desc", datePast);
        project3.addCollaborator("Collaborator");
    }

    @Test
    void testStats() {
        GlobalStats dummyStats = new GlobalStats(dummyUser);
        List<Pair<String, Long>> stats = dummyStats.getStats();
        Assertions.assertEquals(stats.get(0), new Pair<>("Nombre de tâches", 4L));
        Assertions.assertEquals(stats.get(1), new Pair<>("Tâches restantes", 1L));
        Assertions.assertEquals(stats.get(2), new Pair<>("Tâches accomplies", 3L));
    }

    @Test
    void testCompletionStats() {
        GlobalStats dummyStats = new GlobalStats(dummyUser);
        Map<String, LinkedList<Integer>> completionStats = dummyStats.getProjectsCompletion();
        Assertions.assertEquals(completionStats.get("Projet1"), new LinkedList<>(Arrays.asList(3, 2, 1, 0, 24, 0)));
        Assertions.assertEquals(completionStats.get("Projet2"), new LinkedList<>(Arrays.asList(1, 1, 0, 0, 0, 0)));
        Assertions.assertEquals(completionStats.get("Projet3"), new LinkedList<>(Arrays.asList(0, 0, 0, 1, -24, 0)));
    }

    @Test
    void testExport() {
        GlobalStats dummyStats = new GlobalStats(dummyUser);
        try {
            dummyStats.export("test", false);
            Archivers.extract(new File("test/testDir/testDest/test.tar.gz"), new File("test/testDir/testDest"));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileReader read = new FileReader("test/testDir/testDest/test.json");
            GlobalStats retrievedStats = gson.fromJson(read, GlobalStats.class);
            Assertions.assertEquals(retrievedStats.getStats(), dummyStats.getStats());
            read.close();
            new File("test/testDir/testDest/test.json").delete();
            new File("test/testDir/testDest/test.tar.gz").delete();
        } catch (IOException | CompressException | ExtractException e) {
            System.out.println("Erreur fichier lors du test");
        }
    }

}
