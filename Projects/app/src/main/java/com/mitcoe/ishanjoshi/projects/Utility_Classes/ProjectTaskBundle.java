package com.mitcoe.ishanjoshi.projects.Utility_Classes;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Ishan Joshi on 16-Feb-17.
 */

public class ProjectTaskBundle implements Serializable {
    Project project;
    Task task;
    String reminder_days, boss, bossEmail;

    public String getBossEmail() {
        return bossEmail;
    }

    public void setBossEmail(String bossEmail) {
        this.bossEmail = bossEmail;
    }

    public Project getProject() {
        return project;
    }

    public String getBoss() {
        return boss;
    }

    public void setBoss(String boss) {
        this.boss = boss;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getReminder_days() {
        return reminder_days;
    }

    public String getJsonString() {
        Gson gson = new Gson();
        String returnString = "";
        returnString += gson.toJson(project);
        returnString += gson.toJson(task);

        return  returnString;
    }

    public void setReminder_days(String reminder_days) {
        this.reminder_days = reminder_days;
    }
}
