package com.mitcoe.ishanjoshi.projects;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.mitcoe.ishanjoshi.projects.Database_Handlers.ProjectDatabase;
import com.mitcoe.ishanjoshi.projects.Database_Handlers.TaskDatabase;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Project;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.ProjectTaskBundle;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CreateTask extends AppCompatActivity {
    TextInputEditText inputTaskName, inputTaskDescription;
    AutoCompleteTextView parentProjectAutoCompleteTextView, namesAutoCompleteTextView;
    List<String> parentProjectsList, namesList, namesAssignedList;
    ArrayAdapter<String> parentProjects, names, namesAssigned;
    DatePicker datePicker;
    ListView listView;
    FloatingActionButton floatingActionButton;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Create Task");

        final ProjectDatabase projectDatabase = new ProjectDatabase(this);

        List<Project> projectList;
        projectList = projectDatabase.getCurrentProjectsList();
        for (Project project : projectList) {
            parentProjectsList.add(project.getName());
        }
        String[] namesArray = getResources().getStringArray(R.array.NamesOfTeachers);

        inputTaskName = (TextInputEditText) findViewById(R.id.add_task_name_create_task);
        inputTaskDescription = (TextInputEditText) findViewById(R.id.add_task_description_create_task);
        parentProjectAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.AutoCompleteTextView_show_parent_projects);
        namesAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.list_all_names);
        namesList = Arrays.asList(namesArray);
        namesAssignedList = new ArrayList<>();
        parentProjects = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,parentProjectsList);
        names = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,namesList);
        namesAssigned = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,namesAssignedList);
        listView = (ListView) findViewById(R.id.selected_users);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.create_task_fab);
        datePicker = (DatePicker) findViewById(R.id.datePicker_create_task);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences(getString(R.string.userDetails),MODE_PRIVATE);
        initdatePicker();

        parentProjectAutoCompleteTextView.setAdapter(parentProjects);
        parentProjectAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = parentProjects.getItem(position);
                parentProjectAutoCompleteTextView.setText(""+selected);
            }
        });

        namesAutoCompleteTextView.setAdapter(names);
        namesAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentName = names.getItem(position);
                namesAssignedList.add(currentName);
                names.remove(currentName);
                namesAutoCompleteTextView.setText("");
                names.notifyDataSetChanged();
                namesAssigned.notifyDataSetChanged();
            }
        });

        listView.setAdapter(namesAssigned);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = inputTaskName.getText().toString();
                String taskDescription = inputTaskDescription.getText().toString();
                String parentProject = parentProjectAutoCompleteTextView.getText().toString();
                String completeBy = "" +  datePicker.getYear() +"/"+ (datePicker.getMonth() - 1) +"/" +  datePicker.getDayOfMonth();
                Gson gson = new Gson();
                if (taskName.equals(""))
                    Toast.makeText(CreateTask.this, "Please add Task Name", Toast.LENGTH_SHORT).show();
                else if (parentProject.equals(""))
                    Toast.makeText(CreateTask.this, "Please add Parent Project", Toast.LENGTH_SHORT).show();
                else {
                    Task task = new Task();
                    task.setName(taskName);
                    task.setDescription(taskDescription);
                    task.setAccepted(false);
                    task.setCompleteBy(completeBy);
                    task.setWorkers(namesAssignedList);
                    task.setParentProject(parentProject);
                    TaskDatabase taskDatabase = new TaskDatabase(CreateTask.this);
                    taskDatabase.addTask(task);
                    Project project = projectDatabase.getProject(parentProject);
                    ProjectTaskBundle projectTaskBundle = new ProjectTaskBundle();
                    projectTaskBundle.setTask(task);
                    projectTaskBundle.setProject(project);
                    projectTaskBundle.setBoss(sharedPreferences.getString(getString(R.string.userName),null));
                    projectTaskBundle.setBossEmail(sharedPreferences.getString(getString(R.string.userEmail),null));
                    for (String worker : namesAssignedList) {
                        projectTaskBundle.setReminder_days("1");
                        databaseReference.child(worker)
                                .child(getString(R.string.FirebaseStringData))
                                .push()
                                .setValue(gson.toJson(projectTaskBundle));
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CreateTask.this,CurrentTask.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    private void initdatePicker() {

        Calendar calendar = Calendar.getInstance();
        datePicker.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

    }
}
