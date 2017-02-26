package com.mitcoe.ishanjoshi.projects;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mitcoe.ishanjoshi.projects.Database_Handlers.ProjectDatabase;
import com.mitcoe.ishanjoshi.projects.Database_Handlers.TaskDatabase;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Project;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Task;

import java.util.ArrayList;
import java.util.List;

public class ViewProject extends AppCompatActivity implements View.OnClickListener {

    private TextView projectDescription;
    private ListView taskListView;
    private Project project;
    String name, description;
    List<Task> tasks;
    private List<String> taskNames;
    ArrayAdapter<String> taskArrayAdapter;
    Button remove, completed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_project);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(getString(R.string.parcedData));
        project = (Project) bundle.getSerializable(getString(R.string.bundleString));
        projectDescription = (TextView) findViewById(R.id.display_project_description);
        String intentString = getIntent().getStringExtra(getString(R.string.IntentToString));
        name = project.getName();
        description = project.getDescription();
        projectDescription.setText("Project Description : \n"+description);
        actionBar.setTitle(name);
        remove = (Button) findViewById(R.id.remove_current_project);
        remove.setOnClickListener(this);
        completed = (Button) findViewById(R.id.set_current_project_completed);
        completed.setOnClickListener(this);
        if (intentString.equals(CompletedProject.class.toString()))
            completed.setVisibility(View.INVISIBLE);
        populateList();
        populateListView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void populateList() {
        TaskDatabase taskDatabase =
                new TaskDatabase(this);
        tasks = taskDatabase.getTaskList(name);
        taskNames = new ArrayList<>();
        for (Task task : tasks)
            taskNames.add(task.getName());
    }

    private void populateListView() {
        taskListView = (ListView) findViewById(R.id.list_project_tasks);
        taskArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,taskNames);
        taskListView.setAdapter(taskArrayAdapter);
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewProject.this,ViewTask.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.parcedData),taskArrayAdapter.getItem(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remove_current_project : {
                ProjectDatabase projectDatabase = new ProjectDatabase(this);
                projectDatabase.deleteProjects(project.getName());
                TaskDatabase taskDatabase = new TaskDatabase(this);
                taskDatabase.deleteTasks(project.getName());
                startActivity(new Intent(ViewProject.this,CurrentProjects.class));
                break;
            }
            case R.id.set_current_project_completed : {
                //todo add code to set all project's task to completed
                ProjectDatabase projectDatabase = new ProjectDatabase(this);
                projectDatabase.deleteProjects(project.getName());
                project.setCompleted(true);
                projectDatabase.addProject(project);
                startActivity(new Intent(ViewProject.this,CurrentProjects.class));
                break;

            }
            default: {
                super.onBackPressed();
            }
        }
    }
}
