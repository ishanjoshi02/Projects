package com.mitcoe.ishanjoshi.projects;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mitcoe.ishanjoshi.projects.Database_Handlers.ProjectDatabase;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Project;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreateProject extends AppCompatActivity implements View.OnClickListener {

    // TODO: 15-Feb-17 add code here

    TextInputEditText inputName, inputDescription;
    ImageButton plusOne, minusOne;
    TextView numberOfTasks;
    FloatingActionButton floatingActionButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Project");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        minusOne = (ImageButton) findViewById(R.id.sub_task);
        minusOne.setOnClickListener(this);
        plusOne = (ImageButton) findViewById(R.id.add_task);
        plusOne.setOnClickListener(this);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.create_project_fab);
        floatingActionButton.setOnClickListener(this);

        numberOfTasks = (TextView) findViewById(R.id.number_of_tasks);
        numberOfTasks.setGravity(View.TEXT_ALIGNMENT_CENTER);



        inputName = (TextInputEditText) findViewById(R.id.get_Project_Name);
        inputDescription = (TextInputEditText) findViewById(R.id.get_Project_Description);


        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_task : {
                int no = Integer.valueOf(numberOfTasks.getText().toString());
                no++;
                numberOfTasks.setText(""+no);
                break;
            }
            case R.id.sub_task : {
                int no = Integer.valueOf(numberOfTasks.getText().toString());
                if (no > 0 ) {
                    no--;
                    numberOfTasks.setText("" + no);
                }
                break;
            }
            case R.id.create_project_fab : {
                String projectName = inputName.getText().toString();
                String description = inputDescription.getText().toString();

                if (projectName.equals("")) {
                    Toast.makeText(this, "Please add Project Name", Toast.LENGTH_SHORT).show();
                }
                else {
                    int no = Integer.valueOf(numberOfTasks.getText().toString());

                    if (no>0) {
                        List<Task> tasks = new ArrayList<>();
                        Intent intent = new Intent(CreateProject.this,AddTask.class);
                        intent.putExtra(getString(R.string.StringData),projectName);
                        intent.putExtra(getString(R.string.IntegerData),no);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(getString(R.string.parcedData), (Serializable) tasks);
                        bundle.putSerializable(getString(R.string.ProjectParce), new Project(projectName,description));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    else {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(getString(R.string.ProjectParce),new Project(projectName,description));
                        Intent intent = new Intent(CreateProject.this, VerifyProject.class);
                        intent.putExtras(bundle);
                        intent.putExtra(getString(R.string.CallingActivity),CreateProject.class.toString());
                        startActivity(intent);
                    }
                }
            }
            default:
                break;
        }
    }
}
