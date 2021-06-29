package be.ac.ulb.infof307.g04.model;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of class Task.
 * This class implements data structure for a specific task of a project.
 */
public class Task implements Comparable<Task> {

    /* Attributes, constructors & comparators */

    private static final int HASH = 31 ;

    private LocalDate startDate;
    private LocalDate endDate;
    private Duration report = Duration.ofDays(0);
    private final String description;
    private final String title;
    private final Set<String> collaborator;

    /** constructors Task **/
    public Task(String title, String description, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate.isBefore(startDate) ? startDate : endDate;
        this.collaborator = new HashSet<>();
    }

    public Task(String title, String description, Set<String> collaborator) {
        this.title = title;
        this.description = description;
        this.collaborator = collaborator;
    }

    /**
     * Compare End date with a other send in parameter
     * @param other : the task who is compared
     */
    @Override
    public int compareTo(Task other) {
        /* Compares dates in ascending order */
        LocalDate thisEndDate = this.endDate;
        LocalDate otherEndDate = other.endDate;
        int res = thisEndDate.compareTo(otherEndDate);
        if (res == 0) { // dates are equal, compare on the title
            return title.compareToIgnoreCase(other.title);
        }
        return res;
    }

    /**
     * check if a unknown object is a task or null object
     * @param unknown : the object who is compared
     */
    @Override
    public boolean equals(Object unknown) {
        if (this == unknown){
            return true;
        }
        if (unknown == null || getClass() != unknown.getClass()){
            return false;
        }
        Task task = (Task) unknown;
        if (!Objects.equals(endDate, task.endDate)){
            return false;
        }
        return Objects.equals(title, task.title);
    }

    /**
     * Convert Local date object to integer
     */
    @Override
    public int hashCode() {
        int result = endDate != null ? endDate.hashCode() : 0;
        result = HASH * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    /**
     * Report the reminder
     */
    public void report(){
        long daysLeft = Duration.between(LocalDate.now().atStartOfDay(), endDate.atStartOfDay()).toDays();
        report = report.plusDays(Project.EXTRA_DAYS - daysLeft);
    }

    /** Getters & Setters **/

    public LocalDate getStartDate() { return startDate; }

    public LocalDate getEndDate() { return endDate; }

    public String getDescription() { return description; }

    public String getTitle() { return title; }

    public Set<String> getCollaborator() { return collaborator; }

    public Duration getReport(){return report;}

    public void setStartDate(LocalDate value) { startDate = value; }

    public void setEndDate(LocalDate value) { endDate = value; }
}
