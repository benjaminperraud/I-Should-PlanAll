package be.ac.ulb.infof307.g04.model;

import java.time.LocalDate;

/**
 * Implementation of class SubProject, same as Project with a project parent name.
 * A SubProject is a project nested in a parent project.
 */
public class SubProject extends Project {

    /* Attributes */

    private String projectParentName;

    /* Constructors */

    public SubProject(String title, String description, LocalDate value, Project parent) {
        super(title, description, value);
        projectParentName = parent.getTitle() ;
    }

    /* Getters/Setters */

    public String getProjectParentName(){
        return projectParentName;
    }

    public void setProjectParentName(String projectParentName){
        this.projectParentName = projectParentName;
    }
}