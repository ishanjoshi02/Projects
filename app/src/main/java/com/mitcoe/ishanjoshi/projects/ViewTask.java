package com.mitcoe.ishanjoshi.projects;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.mitcoe.ishanjoshi.projects.Database_Handlers.ProjectTaskBundleDatabase;
import com.mitcoe.ishanjoshi.projects.Database_Handlers.TaskDatabase;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.ProjectTaskBundle;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Task;

import java.util.List;

// TODO: 15-Feb-17 optimize all imports in all activities
public class ViewTask extends AppCompatActivity implements View.OnClickListener {

    Task task;
    TextView name, description, parentProject, completeBy;
    String nameString, descriptionString, parentProjectString, completeByString;
    ListView workers;
    ArrayAdapter<String> workerAdapter;
    List<String> workingPeople;
    Button removeTaskButton, setTaskCompletedButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        task = (Task) bundle.getSerializable(getString(R.string.parcedData));

        nameString = task.getName();
        descriptionString = task.getDescription();
        parentProjectString = task.getParentProject();
        completeByString = task.getCompleteBy();

        name = (TextView) findViewById(R.id.display_task_name);
        description = (TextView) findViewById(R.id.display_task_description);
        parentProject = (TextView) findViewById(R.id.display_task_parent_project);
        completeBy = (TextView) findViewById(R.id.display_task_complete_by);
        removeTaskButton = (Button) findViewById(R.id.remove_task);
        setTaskCompletedButton = (Button) findViewById(R.id.task_completed);

        actionBar.setTitle(nameString);

        name.setText("Task Name : "+nameString);
        description.setText("Task Description : " + descriptionString);
        parentProject.setText("Task Parent Project : "+parentProjectString);
        completeBy.setText("Task to be Completed by : " + completeByString);

        workingPeople = task.getWorkers();
        workerAdapter = new WorkerAdapter();
        workers = (ListView) findViewById(R.id.display_people_working);
        workers.setAdapter(workerAdapter);

        removeTaskButton.setOnClickListener(this);
        setTaskCompletedButton.setOnClickListener(this);
        Button postPoneButton = (Button) findViewById(R.id.postPone_task);
        postPoneButton.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        TaskDatabase taskDatabase = new TaskDatabase(ViewTask.this);
        switch (v.getId()) {
            case R.id.task_completed : {
                Task task1 = task;
                task1.setCompleted(true);
                taskDatabase.deleteTask(task);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                ProjectTaskBundleDatabase projectTaskBundleDatabase = new ProjectTaskBundleDatabase(this);
                ProjectTaskBundle projectTaskBundle = projectTaskBundleDatabase.get(task1.getName());
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.userDetails),MODE_PRIVATE);
                databaseReference.child(projectTaskBundle.getBoss()).child(getString(R.string.FirebaseStringMessage)).push()
                        .setValue(task1.getName()+" is completed by " + sharedPreferences.getString(getString(R.string.userName),null));
                startActivity(new Intent(ViewTask.this, MainActivity.class));
                break;
            }
            case R.id.remove_task : {
                taskDatabase.deleteTask(task);
                startActivity(new Intent(ViewTask.this, CurrentTask.class));
                break;
            }
            case R.id.postPone_task : {
                ProjectTaskBundleDatabase projectTaskBundleDatabase =
                        new ProjectTaskBundleDatabase(this);
                ProjectTaskBundle projectTaskBundle = projectTaskBundleDatabase.get(task.getName());
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL,projectTaskBundle.getBossEmail());
                i.putExtra(Intent.EXTRA_SUBJECT,"Reason to PostPone " + task.getName());
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ViewTask.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class WorkerAdapter extends
            ArrayAdapter<String> {
        WorkerAdapter() {
            super(ViewTask.this, R.layout.display_single_text, workingPeople);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            if (itemView == null)
                itemView = LayoutInflater.from(getContext()).inflate(R.layout.display_single_text,parent,false);

            TextView textView = (TextView) itemView.findViewById(R.id.display_string);
            textView.setText("" + workingPeople.get(position));

            return itemView;
        }
    }
}
