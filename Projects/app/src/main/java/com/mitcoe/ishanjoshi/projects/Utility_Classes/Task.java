package com.mitcoe.ishanjoshi.projects.Utility_Classes;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ishan Joshi on 14-Feb-17.
 */

public class Task implements Serializable {
    private String Name, Description, ParentProject, completeBy;
    private List<String> workers;
    private int days;
    private Boolean Accepted;
    private Boolean completed;
    public  Task() {
        Accepted = false;
        completed = false;
    }
    public Task(String name, String description, String parentProject,  List<String> workers, String completeBy) {
        Name = name;
        Description = description;
        ParentProject = parentProject;
        this.completeBy = completeBy;
        this.workers = workers;
        Accepted = false;
        completed = false;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCompleteBy() {
        return completeBy;
    }

    public void setCompleteBy(String completeBy) {
        this.completeBy = completeBy;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getParentProject() {
        return ParentProject;
    }

    public void setParentProject(String parentProject) {
        ParentProject = parentProject;
    }

    public List<String> getWorkers() {
        return workers;
    }

    public void setWorkers(List<String> workers) {
        this.workers = workers;
    }

    public Boolean getAccepted() {
        return Accepted;
    }

    public void setAccepted(Boolean accepted) {
        Accepted = accepted;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
