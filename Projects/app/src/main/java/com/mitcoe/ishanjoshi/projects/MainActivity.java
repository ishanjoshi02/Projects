package com.mitcoe.ishanjoshi.projects;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mitcoe.ishanjoshi.projects.Services.MyService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button currentProject, createProject, completedProjects, currentTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        startService(new Intent(MainActivity.this, MyService.class));

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Toast
                            .makeText(MainActivity.this, "Please Sign Up/Sign In", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        };

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Welcome");

        currentProject = (Button) findViewById(R.id.current_projects);
        createProject = (Button) findViewById(R.id.create_project);
        completedProjects = (Button) findViewById(R.id.completed_projects);
        currentTasks = (Button) findViewById(R.id.current_tasks);

        currentProject.setOnClickListener(this);
        createProject.setOnClickListener(this);
        completedProjects.setOnClickListener(this);
        currentTasks.setOnClickListener(this);

        startservice();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // TODO: 15-Feb-17 add Intents Below
            case R.id.current_projects : {
                startActivity(new Intent(MainActivity.this, CurrentProjects.class));
                break;
            }
            case R.id.completed_projects : {
                startActivity(new Intent(MainActivity.this, CompletedProject.class));
                break;
            }

            case R.id.create_project : {
                startActivity(new Intent(MainActivity.this, CreateProject.class));
              break;
            }
            case R.id.current_tasks : {
                startActivity(new Intent(MainActivity.this,CurrentTask.class));
                break;
            }
            default:
                break;
        }
    }

    public void startservice() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.userDetails),MODE_PRIVATE);
        Intent intent = new Intent(this,MyService.class);
        startService(intent);
    }

}
