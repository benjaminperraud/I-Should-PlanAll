package be.ac.ulb.infof307.g04.model;

import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;

import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Implementation of class User.
 * This class implements data structure for a user of the application and every operation on him.
 */
public class User {

    /* Attributes */
    private String name;
    private String lastname;
    private String username;
    private String email;
    private String password;

    private Map<String, Project> listProject;
    private final Map<String, Set<String>> listWaitingCollab = new HashMap<>();

    /** void Constructors **/
    public User() {}

    /** Constructors new User **/
    public User(String name, String username, String lastname, String email, String password, Map<String, Project> listProject) {
        this.name = name;
        this.username = username;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.listProject = listProject;
    }
    /** Constructors Loading User **/
    public User(String name, String username, String lastname, String email, String password) {
        this.name = name;
        this.username = username;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        listProject = new HashMap<>();
    }

    /**
     * Set Object User
     **/
    public void set(String name, String username, String lastname, String email, String password, Map<String, Project> listProject) {
        this.name = name;
        this.username = username;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.listProject = listProject;
    }


    /**
     * Adds a project to the user
     * @param nameProject : the name of the project
     * @param project : the project to be added
     * @throws UserExceptions to report error occurred while adding the project to the user
     */
    public void addProject(String nameProject, Project project) throws UserExceptions {
        getListProject().put(nameProject, project);
        Database ddb = new Database(this);
        ddb.updateDetails();
    }

    /**
     * Checks if user has urgent task to complete
     * @return : true if user has urgent task, false otherwise
     */
    public boolean hasUrgentTasks() {
        LocalDate now = LocalDate.now();
        for (Project project : this.getListProject().values()) {
            if (project.getTasks() != null) {
                for (Map.Entry<String, Task> task : project.getTasks().entrySet()) {
                    long taskRemainingDays = ChronoUnit.DAYS.between(now, task.getValue().getEndDate());
                    if (taskRemainingDays <= 2 && taskRemainingDays >= 0) {
                        return true;
                    }
                }
                long projRemainingDays = ChronoUnit.DAYS.between(now, project.getDueDate());
                if (projRemainingDays <= 2 && projRemainingDays >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sends a project collaboration request to a user
     * @param invitedUserName : the username of the invited user
     * @param projectTitle    : the title of the project for which collaboration is desired
     * @throws UserExceptions to report error occurred while sending the collaboration
     */
    public void sendCollaboration(String invitedUserName, String projectTitle) throws UserExceptions {
        User user = Database.getUserFromDatabase(invitedUserName);
        user.addInvitationCollab(getUsername(), projectTitle);
        Database ddb = new Database(user);
        ddb.updateDetails();
    }

    /**
     * Adds a project collaboration request to the invited user's list of waiting collaboration requests
     * @param usernameHost : the username of the user who initiated the invitation to collaborate
     * @param projectTitle : the title of the project for which collaboration is requested
     */
    public void addInvitationCollab(String usernameHost, String projectTitle) {
        Set<String> projects = listWaitingCollab.get(usernameHost);
        if (projects == null) {
            projects = new HashSet<>();
        }
        projects.add(projectTitle);
        listWaitingCollab.put(usernameHost, projects);
    }

    /**
     * Adds a collaborator to the list of collaborators of a Project
     * @param usernameHost : the username of the user who initiated the invitation to collaborate
     * @param project      : the title of the project for which collaboration is requested
     * @return the updated project with the new collaborator
     * @throws UserExceptions to report error occurred while adding the collaborator
     */
    public Project addCollaborator(String usernameHost, String project) throws UserExceptions {
        User userHost = Database.getUserFromDatabase(usernameHost);
        Project actualProject = userHost.getProject(project);
        actualProject.addCollaborator(this.getUsername());
        actualProject.addCollaborator(usernameHost);
        Set<String> collaborators = userHost.getProject(project).getCollaborators();
        // adds the new collaborator for each existing collaborator
        for (String collaborator : collaborators) {
            if (collaborator.equals(this.getUsername())){
                break;
            }
            User userCollab = Database.getUserFromDatabase(collaborator);
            userCollab.getProject(project).addCollaborator(getUsername());
            Database ddbCollab = new Database(userCollab);
            ddbCollab.updateDetails();
        }
        Database ddbHost = new Database(userHost);
        ddbHost.updateDetails();
        // copy project in new collaborator
        getListProject().put(actualProject.getTitle(), actualProject);
        Database ddbCurrent = new Database(this);
        ddbCurrent.updateDetails();
        return actualProject;
    }

    /**
     * Removes a project collaboration request
     * @param hostUser    : the username of the user who initiated the invitation to collaborate
     * @param projectName : the title of the project for which collaboration is requested
     * @throws UserExceptions to report error occurred while removing the collaboration request
     */
    public void removeInvitation(String hostUser, String projectName) throws UserExceptions {
        getListWaitingCollab().get(hostUser).remove(projectName);
        Database ddb = new Database(this);
        ddb.updateDetails();
    }

    /**
     * Check if the project title is unique in all project of user
     *
     * @param title : the title of the project
     * @return true if the project is not a subproject
     */
    public boolean isProjectTitleUnique(String title) {
        for (Project project : listProject.values()) {
            if (project.getTitle().equals(title)) {
                return true;
            } else {
                boolean res = isSubProjectTitleUnique(project.getSubProjectList(), title);
                if (res) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Recursive method
     *
     * @param subProjectList : the subproject list
     * @param title : the title of the subproject
     * @return true if the subproject is not a project
     */
    public boolean isSubProjectTitleUnique(Map<String, SubProject> subProjectList, String title) {
        for (SubProject subProject : subProjectList.values()) {
            if (subProject.getTitle().equals(title)) {
                return true;
            } else {
                boolean res = isSubProjectTitleUnique(subProject.getSubProjectList(), title);
                if (res) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get a specified Task from all Projects and Subprojects of User
     *
     * @param taskName : name of searched task
     * @return : the task if found, null otherwise
     * */
    public Task getTask(String taskName) {
        Task task;
        for(Project project: listProject.values()){
            task = project.getTask(taskName);
            if(task != null) {
                return task;
            } else {
                task = getTaskGlobal(taskName, project.getSubProjectList());
                if(task != null){
                    return task;
                }
            }
        }
        return null;
    }

    /**
     *  Recursive method : get the specified task of User in the list of subprojects
     *
     * @param taskName : name of searched task
     * @param subProjectList : list of sub project
     * @return : the task if found, null otherwise
     */
    private Task getTaskGlobal(String taskName, Map<String, SubProject> subProjectList){
        if(subProjectList.size() == 0) {
            return null;
        }
        Task task;
        for (SubProject subProject : subProjectList.values()) {
            task = subProject.getTask(taskName);
            if (task != null) {
                return task;
            } else {
                return getTaskGlobal(taskName, subProject.getSubProjectList());
            }
        }
        return null;
    }

    /**
     * Gets a specified Project of User
     * @param projectName : name of searched project
     * @return : the Project if found, null otherwise
     */
    public Project getProject(String projectName) {
        Project proj = listProject.get(projectName);
        if (proj != null) {
            return proj;
        } else {
            for (Project project : this.listProject.values()) {
                SubProject res = getSubGlobal(project.getSubProjectList(), projectName);
                if (res != null) {
                    return res;
                }
            }
        }
        return null;
    }

    /**
     * Recursive method : get the specified subproject of User in the list of subprojects
     * @param subProjectList : list of subprojects of a project
     * @param projectName    : name of the subproject
     * @return subproject Object if found, null otherwise
     **/
    public SubProject getSubGlobal(Map<String, SubProject> subProjectList, String projectName) {
        SubProject sub = subProjectList.get(projectName);
        if (sub != null) {
            return sub;
        } else {
            for (SubProject subProject : subProjectList.values()) {
                if (subProject.getTitle().equals(projectName)) {
                    return subProject;
                } else {
                    if (subProject.getSubProjectList() != null && getSubGlobal(subProject.getSubProjectList(), projectName) != null) {
                        return getSubGlobal(subProject.getSubProjectList(), projectName);
                    }
                }
            }
        }
        return null;
    }

    /** Getters **/

    public String getName() {
        return name;
    }

    public String getUsername() { return username; }

    public String getLastname() { return lastname; }

    public String getEmail() { return email; }

    public String getPassword() { return password; }

    public Map<String, Project> getListProject() {
        return listProject;
    }

    public Map<String, Set<String>> getListWaitingCollab() {
        return listWaitingCollab;
    }
}
