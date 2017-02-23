package com.mitcoe.ishanjoshi.projects;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.mitcoe.ishanjoshi.projects.Database_Handlers.ProjectDatabase;
import com.mitcoe.ishanjoshi.projects.Database_Handlers.ProjectTaskBundleDatabase;
import com.mitcoe.ishanjoshi.projects.Database_Handlers.TaskDatabase;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Project;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.ProjectTaskBundle;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Task;

import java.util.List;

public class AcceptTaskActivity extends AppCompatActivity implements View.OnClickListener {
    Task task;
    Project project;
    ProjectTaskBundle projectTaskBundle;
    TextView name, description, parentProject, completeBy;
    String nameString, descriptionString, parentProjectString, completeByString;
    String Boss,user;
    ListView workers;
    ArrayAdapter<String> workerAdapter;
    List<String> workingPeople;
    Button acceptButton, declineButton;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_task);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.userDetails),MODE_PRIVATE);
        user = sharedPreferences.getString(getString(R.string.userName),"");

        Bundle bundle = getIntent().getExtras();
        projectTaskBundle = (ProjectTaskBundle) bundle.getSerializable(getString(R.string.parcedData));
        project = projectTaskBundle.getProject();
        task = projectTaskBundle.getTask();
        Boss = projectTaskBundle.getBoss();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Task shared By " + Boss);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference().child(Boss).child(getString(R.string.FirebaseStringMessage));

        nameString = task.getName();
        descriptionString = task.getDescription();
        parentProjectString = task.getParentProject();
        completeByString = task.getCompleteBy();

        name = (TextView) findViewById(R.id.display_task_name1);
        description = (TextView) findViewById(R.id.display_task_description1);
        parentProject = (TextView) findViewById(R.id.display_task_parent_project1);
        completeBy = (TextView) findViewById(R.id.display_task_complete_by1);

        actionBar.setTitle(nameString);

        name.setText("Task Name : " + nameString);
        description.setText("Task Description : " + descriptionString);
        parentProject.setText("Task Parent Project : " + parentProjectString);
        completeBy.setText("Task to be Completed by : " + completeByString);

        workingPeople = task.getWorkers();
        workerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,workingPeople);
        workers = (ListView) findViewById(R.id.display_people_working1);
        workers.setAdapter(workerAdapter);

        acceptButton = (Button) findViewById(R.id.accept_task);
        acceptButton.setOnClickListener(this);
        declineButton = (Button) findViewById(R.id.decline_task);
        declineButton.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.accept_task : {
                task.setAccepted(true);
                TaskDatabase taskDatabase = new TaskDatabase(this);
                ProjectDatabase projectDatabase = new ProjectDatabase(this);
                ProjectTaskBundleDatabase projectTaskBundleDatabase = new ProjectTaskBundleDatabase(this);
                projectTaskBundleDatabase.add(projectTaskBundle);
                projectDatabase.addProject(project);
                task.setAccepted(true);
                task.setCompleted(false);
                taskDatabase.addTask(task);
                //todo notify about task code
                databaseReference.push().setValue(user + " has accepted " + task.getName());
                startActivity(new Intent(AcceptTaskActivity.this,MainActivity.class));
                break;
            }

            case R.id.decline_task : {
                final boolean[] flag = {true};
                databaseReference.push().setValue(user + " has declined " + task.getName());
                //todo check below code
                final CoordinatorLayout coordinatorLayout = new CoordinatorLayout(this);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Message is deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                flag[0] = false;
                            }
                        });
                snackbar.show();
                if (flag[0]) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL,projectTaskBundle.getBossEmail());
                    intent.putExtra(Intent.EXTRA_SUBJECT,"Reason to decline " + task.getName());
                    try {
                        startActivity(Intent.createChooser(intent, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(AcceptTaskActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }

            default:
                break;
        }
    }

    /*private class MyWorkerAdapter extends
            ArrayAdapter<String> {
        MyWorkerAdapter() {
            super(AcceptTaskActivity.this, R.layout.display_single_text, workingPeople);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            if (itemView == null)
                LayoutInflater.from(getContext()).inflate(R.layout.display_single_text,parent,false);

            TextView textView = (TextView) itemView.findViewById(R.id.display_string);
            textView.setText("" + workingPeople.get(position));

            return itemView;
        }
    }*/
}
