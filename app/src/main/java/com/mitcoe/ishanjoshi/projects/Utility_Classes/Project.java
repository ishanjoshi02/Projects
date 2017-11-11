package com.mitcoe.ishanjoshi.projects.Utility_Classes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ishan Joshi on 14-Feb-17.
 */

public class Project implements Serializable {
    private String Name, Description;

    Boolean completed;
    public Project (String Name, String Description) {
        this.Name = Name;
        this.Description = Description;
        completed =false;

    }
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

}
