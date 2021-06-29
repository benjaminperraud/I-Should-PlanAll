package be.ac.ulb.infof307.g04.model;

import javafx.collections.ObservableList;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

/**
 * Implementation of class Project for "Histoire 2".
 * This class implements data structure for "Projets" and every operation on them
 */
public class Project implements Comparable<Project> {

    public static final int EXTRA_DAYS = 3;

    /** Attributes, constructors & comparators **/
    private String title;
    private String description;
    private LocalDate dueDate;
    private Duration report = Duration.ofDays(0);
    private final LocalDate startDate;

    private final List<String> labelsList = new LinkedList<>(); // w/o initializations, adding else will fail
    private Map<String,SubProject> subProjects = new HashMap<>();
    private Map<String,Task> tasksList = new HashMap<>();
    private Set<String> collaborators = new HashSet<>();

    /** constructors Project **/
    public Project(String title, String description, LocalDate dueDate, List<String> labels) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        startDate = LocalDate.now();
        for (String label: labels) {
            addLabel(label); // avoids adding several times same label
        }
    }
    public Project(String title, String description, LocalDate dueDate) {
        /* Overload if no labels are specified */
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        startDate = LocalDate.now();
    }
    public Project(String newTitle, String description, LocalDate dueDate, ObservableList<String> labels, Map<String,SubProject> subProjects, Map<String,Task> tasks, Set<String> collaborators) {
        title = newTitle;
        this.description = description;
        this.dueDate = dueDate;
        startDate = LocalDate.now();
        for (String label: labels) {
            addLabel(label); // avoids adding several times same label
        }
        this.subProjects = subProjects;
        tasksList = tasks;
        this.collaborators = collaborators;
    }

    @Override
    public int compareTo(Project project) {
        /* Suppose all projects have different titles */
        return title.compareToIgnoreCase(project.title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(getTitle(), project.getTitle()) && Objects.equals(getDescription(), project.getDescription()) && Objects.equals(getDueDate(), project.getDueDate())
                && Objects.equals(getReport(), project.getReport()) && Objects.equals(getStartDate(), project.getStartDate()) && Objects.equals(labelsList, project.labelsList)
                && Objects.equals(subProjects, project.subProjects) && Objects.equals(tasksList, project.tasksList) && Objects.equals(getCollaborators(), project.getCollaborators());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getDescription(), getDueDate(), getReport(), getStartDate(), labelsList, subProjects, tasksList, getCollaborators());
    }

    /** Getters **/

    public String getTitle() {
        return title;
    }

    public String getDescription() { return description; }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public List<String> getLabels() {
        return labelsList;
    }

    public Map<String, SubProject> getSubProjectList() {
        return subProjects;
    }

    public Map<String,Task> getTasksMap() {
        return tasksList;
    }

    public Task getTask(String title){
        return tasksList.get(title);
    }

    public LocalDate getStartDate() { return startDate; }

    public Set<String> getCollaborators() { return collaborators; }

    public Map<String,Task> getTasks() {
        return tasksList;
    }

    public Duration getReport(){return report;}


    /** Modifiers of project attributes **/

    public void setCollaborators(Set<String> collaborators) { this.collaborators = collaborators; }

    public boolean setTitle(String newTitle) {
        /* Returns false if newTitle is empty string */
        boolean emptyTitle = newTitle.isEmpty();
        if (!emptyTitle) {
            title = newTitle;
        }
        return emptyTitle;
    }

    public void setDescription(String newDescription) { description = newDescription; }

    public void setDueDate(LocalDate date) {
        dueDate = date;
    }


    /**
     * Report the reminder
     */
    public void report() {
        long daysLeft = Duration.between(LocalDate.now().atStartOfDay(), dueDate.atStartOfDay()).toDays();
        report = report.plusDays(EXTRA_DAYS - daysLeft);
    }

    /**
     * Add a label to the project
     * @param label : label to be added
     * @return true if label is added, false otherwise
     */
    public final boolean addLabel(String label) {
        /* Searches if similar label has already been added (case insensitively).
        Returns true if task has been added. */
        boolean toAdd = labelsList.stream().noneMatch(s -> s.equalsIgnoreCase(label)) & !label.isEmpty();
        if (toAdd) {
            labelsList.add(label);
        }
        return toAdd;
    }

    /**
     * Add a sub-project to the project
     * @param subProject : the sub-project to be added
     */
    public void addSubProject(SubProject subProject) {
        subProjects.put(subProject.getTitle(), subProject);
    }

    /**
     * Add a task to the project
     * @param task : the task to be added
     */
    public void addTask(Task task) {
        tasksList.put(task.getTitle(), task);
    }

    /**
     * Checks if endDate end before or at the same time as main project
     * @param task : task verified
     * @return : true if the ending date of the task is valid
     */
    public boolean isTaskDateValid(Task task) {
        return (LocalDate.parse(task.getEndDate().toString()).compareTo(dueDate) <= 0 );
    }

    /**
     * Add a collaborator to the project
     * @param username : the username of the collaborator
     */
    public void addCollaborator(String username) {
        collaborators.add(username);
    }

    /**
     * Remove a label from the project
     * @param label : the label to be removed
     */
    public void removeLabel(String label) {
        labelsList.remove(label);
    }

    /**
     * Remove a task from the project
     * @param task : the task to be removed
     */
    public void removeTask(Task task) {
        tasksList.remove(task.getTitle());
    }

    /**
     * Remove a sub-project from the project
     * @param subProject : the sub-project to be removed
     */
    public void removeSubproject(SubProject subProject) {
        subProjects.remove(subProject.getTitle()) ;
    }

    /**
     * Update the parent name of all sub-project of the Project
     * @param newTitle : the new title of the parent Project
     */
    public void updateSubParentName(String newTitle) {
        for (SubProject subProject : subProjects.values()) {
            subProject.setProjectParentName(newTitle);
            }
    }
}