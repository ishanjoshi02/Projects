package com.mitcoe.ishanjoshi.projects;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.mitcoe.ishanjoshi.projects.Database_Handlers.ProjectDatabase;
import com.mitcoe.ishanjoshi.projects.Database_Handlers.ProjectTaskBundleDatabase;
import com.mitcoe.ishanjoshi.projects.Database_Handlers.TaskDatabase;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Project;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.ProjectTaskBundle;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Task;

import java.util.ArrayList;
import java.util.List;

public class VerifyProject extends AppCompatActivity {

    private List<Task> tasks;
    private Project project;
    private Bundle onComplete;
    private TextView name, description;
    ListView listView;
    private DatabaseReference databaseReference;
    ArrayAdapter<Task> taskArrayAdapter;
    FloatingActionButton fab;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        handleIntent(getIntent());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_project);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        name = (TextView) findViewById(R.id.verify_project_name);
        description = (TextView) findViewById(R.id.verify_project_description);
        listView = (ListView) findViewById(R.id.list_all_task_verify_project);
        fab = (FloatingActionButton) findViewById(R.id.all_done_verify_project);
        final TextView textView = (TextView) findViewById(R.id.verify_project_tasks_textView);


        final SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.userDetails),MODE_PRIVATE);
        userName = sharedPreferences.getString(getString(R.string.userName),"");


        name.setText(project.getName());
        description.setText(project.getDescription());


        if (tasks == null)
            textView.setVisibility(View.INVISIBLE);
        else {
            taskArrayAdapter = new MyCustomAdapter();
            listView.setAdapter(taskArrayAdapter);
        }
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       ProjectDatabase projectDatabase = new ProjectDatabase(VerifyProject.this);
                                       projectDatabase.addProject(project);
                                       ProjectTaskBundleDatabase projectTaskBundleDatabase = new ProjectTaskBundleDatabase(VerifyProject.this);
                                       Gson gson = new Gson();
                                       for (Task task : tasks) {
                                           List<String> workers = task.getWorkers();
                                           for (String worker : workers) {
                                               ProjectTaskBundle projectTaskBundle = new ProjectTaskBundle();
                                               projectTaskBundle.setBoss(userName);
                                               projectTaskBundle.setProject(project);
                                               projectTaskBundle.setTask(task);
                                               projectTaskBundle.setBossEmail(sharedPreferences.getString(getString(R.string.userEmail),null));
                                               projectTaskBundle.setReminder_days("1");
                                               projectTaskBundleDatabase.add(projectTaskBundle);
                                               databaseReference.child(worker)
                                                       .child(getString(R.string.FirebaseStringData))
                                                       .push()
                                                       .setValue(gson.toJson(projectTaskBundle));

                                           }
                                       }
                                       startActivity(new Intent(VerifyProject.this, CurrentProjects.class));
                                   }
                               }
            );
        FloatingActionButton cancelButton = (FloatingActionButton) findViewById(R.id.cancel_project);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskDatabase taskDatabase = new TaskDatabase(VerifyProject.this);
                taskDatabase.deleteTasks(project.getName());
                startActivity(new Intent(VerifyProject.this,MainActivity.class));
            }
        });
    }

    private void handleIntent(Intent intent) {
        String callingIntent = intent.getStringExtra(getString(R.string.CallingActivity));
        onComplete = intent.getExtras();
        if (callingIntent.equals(AddTask.class.toString())) {
            project = (Project) onComplete.getSerializable(getString(R.string.ProjectParce));
            TaskDatabase taskDatabase = new TaskDatabase(this);
            tasks = taskDatabase.getTaskList(project.getName());
        }
        else if (callingIntent.equals(CreateProject.class.toString())) {
            project = (Project) onComplete.getSerializable(getString(R.string.ProjectParce));

        }
        else if (callingIntent.equals(EditTask.class.toString())) {
            Bundle bundle = intent.getExtras();
            Task task = (Task) bundle.getSerializable(getString(R.string.parcedData));
            tasks.add(intent.getIntExtra(getString(R.string.IntegerData),0),task);

        }

    }

    public class MyCustomAdapter extends ArrayAdapter<Task> {
        MyCustomAdapter() {
            super(VerifyProject.this,R.layout.display_project_cardview,tasks);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null)
                itemView = LayoutInflater.from(getContext()).inflate(R.layout.display_project_cardview,parent,false);
            TextView textView = (TextView) itemView.findViewById(R.id.display_current_project_name);
            final Task currentTask = tasks.get(position);
            textView.setText(currentTask.getName());
            textView.setTextSize(16);
            CardView cardView = (CardView) itemView.findViewById(R.id.display_project_card);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VerifyProject.this,EditTask.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.parcedData),currentTask);
                    intent.putExtra(getString(R.string.IntegerData),position);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            return itemView;
        }
    }
}
