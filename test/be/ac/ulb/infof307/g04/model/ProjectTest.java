package be.ac.ulb.infof307.g04.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    LocalDate dummyDate = LocalDate.now();
    String dummyTitle = "Proj";
    String dummyDescription = "Projet 1";
    String[] labels = {"Project", "Boring", "SchoolStuffs", "project"};
    String[] subProjectsTitles = {"Sub1", "Sub2", "Bus3", "Sub2", "SubMarine"};
    Task[] tasks = {
            new Task("Tâche 1", "Grab some ammo", LocalDate.now(), LocalDate.now()),
            new Task("Tâche 2", "Reload your gun", LocalDate.now(), LocalDate.now()),
            new Task("Tâche 3", "Aim at the target", LocalDate.now(), LocalDate.now()),
            new Task("Tâche 4", "Shoot a nice shot", LocalDate.now(), LocalDate.now()),
            new Task("Tâche 2", "Re-reload Your Gun", LocalDate.now(), LocalDate.now())
    };
    Project proj = new Project(dummyTitle, dummyDescription, dummyDate);

    /* utils for test methods */

    void addLabels() {
        if (proj.getTasksMap().size() == 0) {
            for (String label : labels)
                proj.addLabel(label); // 4 labels but includes duplicates
        }
    }

    void addSubprojects() {
        if (proj.getSubProjectList().size() == 0) {
            for (String subProjTitle: subProjectsTitles)
                proj.addSubProject(new SubProject(subProjTitle, "Dare mighty things", dummyDate, proj));
        }
    }

    void addTasks() {
        if (proj.getTasksMap().size() == 0) {
            for (Task task: tasks)
                proj.addTask(task); // 4 labels but includes duplicate
        }
    }

    @Test
    void testNeqCompareTo() {
        Project proi = new Project("Proi", "Projet 1", dummyDate);
        assertNotEquals(0, proj.compareTo(proi));
    }

    @Test
    void testEqCompareTo() {
        Project proi = new Project("Proj", "Projet 12+1", dummyDate);
        assertEquals(0, proj.compareTo(proi));
    }

    @Test
    void testNeqGetTitle() {
        String isNotTitle = "Prod";
        assertNotEquals(0, proj.getTitle().compareTo(isNotTitle));
    }

    @Test
    void testEqGetTitle() {
        String isTitle = "Proj";
        assertEquals(0, proj.getTitle().compareTo(isTitle));
    }

    @Test
    void testNeqGetDescription() {
        assertNotEquals("Trojep 1", proj.getDescription());
    }

    @Test
    void testEqGetDescription() {
        assertEquals("Projet 1", proj.getDescription());
    }

    @Test
    void testEqGetDueDate() {
        assertEquals(proj.getDueDate(), dummyDate);
    }

    @Test
    void testChangeTitle() {
        String newTitle = "Super Project 1";
        proj.setTitle(newTitle);
        assertEquals(newTitle, proj.getTitle());
    }

    @Test
    void testChangeTitleEmpty() {
        proj.setTitle("");
        assertEquals(dummyTitle, proj.getTitle());
    }

    @Test
    void testChangeDescription() {
        String newDescription = "My super description";
        proj.setDescription(newDescription);
        assertEquals(newDescription, proj.getDescription());
    }

    @Test
    void testChangeDueDate() {
        LocalDate new_date = LocalDate.now().plusYears(1);
        proj.setDueDate(new_date);
        assertEquals(proj.getDueDate(), new_date);
    }

    @Test
    void testAddLabelDuplicate() {
        addLabels();
        assertEquals(3, proj.getLabels().size());
    }

    @Test
    void testAddLabelList() {
        addLabels();
        LinkedList<String> labs = new LinkedList<>(Arrays.asList(labels));
        labs.removeFirstOccurrence("project");

        labs.sort(Comparator.naturalOrder());
        proj.getLabels().sort(Comparator.naturalOrder());
        assertEquals(labs, proj.getLabels());
    }

    @Test
    void testAddSubProjectDuplicate() {
        addSubprojects();
        assertEquals(4, proj.getSubProjectList().size());
    }

    @Test
    void testAddSubProjectList() {
        LinkedList<Project> listProj = new LinkedList<>();
        for (String subProjTitle : subProjectsTitles)
            listProj.add(new Project(subProjTitle, "No des", dummyDate));
        addSubprojects();
        listProj.removeFirstOccurrence(new Project("sub2", "No des", dummyDate));
    }

    @Test
    void testAddTaskDuplicate() {
        addTasks();
        assertEquals(4, proj.getTasksMap().size());
    }

    @Test
    void testAddTaskList() {
        addTasks();
        LinkedList<Task> sampleTaskList = new LinkedList<>(Arrays.asList(tasks));
        sampleTaskList.removeFirstOccurrence(new Task("Tâche 2", "no des", dummyDate, dummyDate));
        LinkedList<Task> taskList = new LinkedList<>(proj.getTasksMap().values());
        Collections.sort(taskList);
        Collections.sort(sampleTaskList);
        assertEquals(sampleTaskList, taskList);
    }

    @Test
    void testRemoveLabel() {
        addLabels();
        proj.removeLabel("Project");
        assertEquals(2, proj.getLabels().size());
    }

    @Test
    void testRemoveSubproject() {
        addSubprojects();
        proj.removeSubproject(new SubProject("Sub1", "desc", LocalDate.now(), proj));
        assertEquals(3, proj.getSubProjectList().size());
    }

    @Test
    void testRemoveTask() {
        addTasks();
        proj.removeTask(tasks[2]);
        assertEquals(3, proj.getTasksMap().size());
    }

    @Test
    void testAddCollaborator() {
        User collaborator = new User("user", "collaborator", "lastname", "email@ulb.be", "1E2DEDZFNEO", new HashMap<>());
        proj.addCollaborator(collaborator.getUsername());
        assertEquals(1, proj.getCollaborators().size());
        assertTrue(proj.getCollaborators().contains("collaborator"));
    }


    @org.junit.jupiter.api.Test
    void reportProjectReminder(){
        assertEquals(Duration.ofDays(0), proj.getReport());
        proj.report();
        assertNotEquals(Duration.ofDays(0), proj.getReport());
    }
}
