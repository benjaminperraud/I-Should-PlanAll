package be.ac.ulb.infof307.g04.model;

import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.io.*;
import java.util.Map;
import java.util.Set;

public class UserTest {

    @Test
    void set() {
        User newUser = new User("Fay","BH" ,"Test", "fay@gmail.com", "1E2DEDZFNEO", new HashMap<>());
        newUser.set("1","2" ,"3", "3@gmail.com", "4", new HashMap<>());
        Assertions.assertEquals("1",newUser.getName());
        Assertions.assertEquals("3",newUser.getLastname());
        Assertions.assertEquals("2",newUser.getUsername());
        Assertions.assertEquals("3@gmail.com",newUser.getEmail());
        Assertions.assertEquals("4",newUser.getPassword());
        Assertions.assertEquals("{}", newUser.getListProject().toString());
        Assertions.assertEquals(0,newUser.getListProject().size());
    }

    @Test
    void register() throws UserExceptions {
        User newUser = new User("Fay","TestRegister" ,"BH", "fay@gmail.com", "1E2DEDZFNEO", new HashMap<>());
        Database ddb = new Database(newUser);
        Assertions.assertEquals("Fay",newUser.getName());
        Assertions.assertEquals("TestRegister",newUser.getUsername());
        Assertions.assertEquals("BH",newUser.getLastname());
        Assertions.assertEquals("fay@gmail.com",newUser.getEmail());
        Assertions.assertEquals("1E2DEDZFNEO",newUser.getPassword());
        Assertions.assertEquals("{}", newUser.getListProject().toString());
        Assertions.assertEquals(0,newUser.getListProject().size());
        Database.register("Fay","TestRegister" ,"BH", "fay@gmail.com", "1E2DEDZFNEO");
        Assertions.assertTrue(UserExceptions.getSuccess());
        File userFile = new File(Database.USER_DIRECTORY + newUser.getUsername() + ".json");
        Assertions.assertTrue(userFile.exists());
        boolean returndelete = userFile.delete();
        Assertions.assertTrue(returndelete);
    }

    @Test
    void login() throws UserExceptions {
        User testUser = new User();
        Database ddb = new Database(testUser);
        Database.register("Fay","TestLogin" ,"BH", "fay@gmail.com", "1E2DEDZFNEO");
        Assertions.assertTrue(UserExceptions.getSuccess());
        File userFile = new File(Database.USER_DIRECTORY + "TestLogin.json");
        Assertions.assertTrue(userFile.exists());
        ddb.login("TestLogin", "1E2DEDZFNEO");
        Assertions.assertTrue(UserExceptions.getSuccess());
        boolean returndelete = userFile.delete();
        Assertions.assertTrue(returndelete);
        userFile.delete();
    }

    @Test
    void testEditUserInformation() {
        try {
            User dummyUser = new User("Dummy", "Dummy", "Dummy", "dummy@mail.com", "Whatever17");
            Database ddb = new Database(dummyUser);
            Database.register("Dummy", "Dummy", "Dummy", "dummy@mail.com", "Whatever17");
            ddb.login("Dummy", "Whatever17");
            ddb.editUserInformations("Dummy2Name", "Dummy2Uname", "Dummy2Lname", "dummy2@mail.com", "Whatever18", new HashMap<>());
            User tester = new User("Dummy2Name","Dummy2Uname","Dummy2Lname", "dummy2@mail.com","Whatever18");
            ddb.login("Dummy2Uname", "Whatever18");
            Assertions.assertEquals(tester.getName(), "Dummy2Name");
            Assertions.assertEquals(tester.getUsername(), "Dummy2Uname");
            Assertions.assertEquals(tester.getLastname(), "Dummy2Lname");
            Assertions.assertEquals(tester.getEmail(), "dummy2@mail.com");
            Assertions.assertEquals(tester.getPassword(), "Whatever18");
            Assertions.assertFalse(new File("data/users/Dummy.json").exists());
            new File("data/users/Dummy2Uname.json").delete();
        } catch (UserExceptions e) {
            System.out.println("Erreur utilisateur lors du test");
        }
    }

    @Test
    void testUserExists() {
        try {
            User dummyUser = new User();
            Database ddb = new Database(dummyUser);
            Database.register("Dummy", "Dummy", "Dummy", "dummy@mail.com", "Whatever17");
            ddb.login("Dummy", "Whatever17");
            Assertions.assertTrue(Database.userExists("Dummy"));
            new File("data/users/Dummy.json").delete();
            Assertions.assertFalse(Database.userExists("Dummy"));
        } catch (UserExceptions e) {
            System.out.println("Erreur utilisateur lors du test");
        }
    }

    @Test
    void testAddProject() throws UserExceptions {
        User user = new User("user", "host", "lastname", "email@ulb.be", "1E2DEDZFNEO", new HashMap<>());
        user.addProject("Project name", new Project("Project title", "Project description", LocalDate.now()));
        Assertions.assertEquals(1, user.getListProject().size());
        Assertions.assertTrue(user.getListProject().containsKey("Project name"));
        Assertions.assertEquals("Project title", user.getProject("Project name").getTitle());
        Assertions.assertEquals("Project description", user.getProject("Project name").getDescription());
        Assertions.assertEquals(LocalDate.now(), user.getProject("Project name").getDueDate());
        new File(Database.USER_DIRECTORY + "host.json").delete();
    }

    @Test
    void testHasUrgentTasks() throws UserExceptions {
        User user = new User("user", "host", "lastname", "email@ulb.be", "1E2DEDZFNEO", new HashMap<>());
        user.addProject("Projet collaboratif", new Project("Projet collaboratif", "Description",LocalDate.now()));
        Assertions.assertTrue(user.hasUrgentTasks());
        User user2 = new User("user", "host", "lastname", "email@ulb.be", "1E2DEDZFNEO", new HashMap<>());
        Assertions.assertFalse(user2.hasUrgentTasks());
        new File(Database.USER_DIRECTORY + "host.json").delete();
    }
    
    @Test
    void testAddInvitationCollab() throws UserExceptions {
        User user = new User("user", "host", "lastname", "email@ulb.be", "1E2DEDZFNEO", new HashMap<>());
        User invited = new User("user", "invited", "lastname", "email@ulb.be", "1E2DEDZFNEO", new HashMap<>());
        user.addProject("Projet collaboratif", new Project("Projet collaboratif", "Description", LocalDate.now()));
        invited.addInvitationCollab(user.getUsername(), "Projet collaboratif");
        Map<String, Set<String>> listWaitingCollabs = invited.getListWaitingCollab();
        Assertions.assertEquals(1, listWaitingCollabs.size());
        Assertions.assertTrue(listWaitingCollabs.containsKey(user.getUsername()));
        Assertions.assertTrue(listWaitingCollabs.get(user.getUsername()).contains("Projet collaboratif"));
        new File(Database.USER_DIRECTORY + "invited.json").delete();
        new File(Database.USER_DIRECTORY + "host.json").delete();
    }

    @Test
    void testAddCollaborator() throws UserExceptions {
        User user = new User("user", "host", "lastname", "email@ulb.be", "1E2DEDZFNEO", new HashMap<>());
        User invited = new User("user", "invited", "lastname", "email@ulb.be", "1E2DEDZFNEO", new HashMap<>());
        user.addProject("Projet collaboratif", new Project("Projet collaboratif", "Description", LocalDate.now()));
        invited.addCollaborator(user.getUsername(), "Projet collaboratif");
        Assertions.assertEquals(1,invited.getListProject().size() );
        Assertions.assertTrue(invited.getListProject().containsKey("Projet collaboratif"));
        new File(Database.USER_DIRECTORY + "invited.json").delete();
        new File(Database.USER_DIRECTORY + "host.json").delete();
    }

    @Test
    void testRemoveInvitation() throws UserExceptions {
        User user = new User("user", "host", "lastname", "email@ulb.be", "1E2DEDZFNEO", new HashMap<>());
        User invited = new User("user", "invited", "lastname", "email@ulb.be", "1E2DEDZFNEO", new HashMap<>());
        invited.addInvitationCollab(user.getUsername(), "Projet collaboratif");
        Assertions.assertTrue(invited.getListWaitingCollab().get(user.getUsername()).contains("Projet collaboratif"));
        invited.removeInvitation(user.getUsername(),"Projet collaboratif");
        Assertions.assertFalse(invited.getListWaitingCollab().get(user.getUsername()).contains("Projet collaboratif"));
        new File(Database.USER_DIRECTORY + "invited.json").delete();
        new File(Database.USER_DIRECTORY + "host.json").delete();
    }
}
