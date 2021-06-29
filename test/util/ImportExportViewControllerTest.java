package util;

import be.ac.ulb.infof307.g04.model.Database;
import be.ac.ulb.infof307.g04.model.Project;
import be.ac.ulb.infof307.g04.model.User;
import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;

import static be.ac.ulb.infof307.g04.controller.projectManagement.ImportExportController.addProjectFromImport;
import static be.ac.ulb.infof307.g04.controller.projectManagement.ImportExportController.createJsonProject;
import static org.junit.jupiter.api.Assertions.*;

public class ImportExportViewControllerTest {

    User dummyUser = new User("Joseph", "Titou", "Tito", "titou@gmail.yg", "Testtest1");

    // No solution found to test the methods involving a FileChooser

    @org.junit.jupiter.api.BeforeEach
    void init() {
        new File("data/temp").mkdir();
        try {
            if (dummyUser.getListProject().size() == 0)
                dummyUser.addProject("dummyProject", new Project("dummyProject", "desc", LocalDate.now()));
        } catch (UserExceptions e) {
            System.out.println("Echec dans l'initialisation du test");
        }
    }

    @AfterAll
    static void cleanFiles() {
        new File(Database.USER_DIRECTORY + "Titou.json").delete();
    }

    @Test
    void testCreateJsonProject() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            createJsonProject("dummyProject", dummyUser);
            File archive = new File("data/temp/dummyProject.json");
            assertTrue(archive.exists());
            FileReader reader = new FileReader(archive);
            Project retrievedProject = gson.fromJson(reader, Project.class);
            assertEquals(retrievedProject.getDescription(), "desc");
            archive.delete();
        } catch (FileNotFoundException e) {
            System.out.println("Fichier introuvable");
        } catch (IOException e) {
            System.out.println("Erreur lors de la création du fichier");
        }
    }

    @Test
    void testAddProjectFromImport() {
        try {
            createJsonProject("dummyProject", dummyUser);
            Project retrievedProject = addProjectFromImport("data/temp/dummyProject.json");
            assertNotNull(retrievedProject);
            assertEquals(retrievedProject.getDescription(), "desc");
            File archive = new File("src/data/temp/dummyProject.json");
            archive.delete();
        } catch (IOException e) {
            System.out.println("Erreur lors de la création du fichier");
        }
    }

}
