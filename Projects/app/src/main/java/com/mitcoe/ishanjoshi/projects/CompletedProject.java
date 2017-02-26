package com.mitcoe.ishanjoshi.projects;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mitcoe.ishanjoshi.projects.Database_Handlers.ProjectDatabase;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Project;

import java.util.ArrayList;
import java.util.List;

public class CompletedProject extends AppCompatActivity {

    List<String> completedProjectsList;
    ArrayAdapter<String> completedProjects;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_project);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Completed Projects");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        ProjectDatabase projectDatabase = new ProjectDatabase(this);
        final List<Project> projects
                = projectDatabase.getCompletedProjectList();
        completedProjectsList = new ArrayList<>();
        for (Project project : projects)
            completedProjectsList.add(project.getName());
        completedProjects = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,completedProjectsList);
        listView = (ListView) findViewById(R.id.show_completed_projects);
        listView.setAdapter(completedProjects);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Project currentProject = projects.get(position);
                Intent intent = new Intent(CompletedProject.this,ViewProject.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.bundleString),currentProject);
                intent.putExtra(getString(R.string.parcedData),bundle);
                String activityToString = CompletedProject.class.toString();
                intent.putExtra(getString(R.string.IntentToString),activityToString);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CompletedProject.this,MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
