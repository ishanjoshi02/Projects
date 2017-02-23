package com.mitcoe.ishanjoshi.projects;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mitcoe.ishanjoshi.projects.Database_Handlers.ProjectDatabase;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Project;

import java.util.List;

public class CurrentProjects extends AppCompatActivity {

    private ListView listView;
    List<Project> projects;
    ArrayAdapter<Project> projectArrayAdapter;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_projects);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Current Projects");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        button = (Button) findViewById(R.id.create_project_current);

        listView = (ListView) findViewById(R.id.list_current_projects);

        populateList();
        populateListView();

    }

    private void populateList() {
        ProjectDatabase projectDatabase =
                new ProjectDatabase(this);
        projects = projectDatabase.getCurrentProjectsList();
        if (projects.isEmpty()) {
            button.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CurrentProjects.this, CreateProject.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            });
        }
    }

    private void populateListView() {
        projectArrayAdapter =
                new ProjectCustomAdapter();
        listView.setAdapter(projectArrayAdapter);
        listView.setLongClickable(true);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CurrentProjects.this,MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private class ProjectCustomAdapter extends
            ArrayAdapter<Project> {
        public ProjectCustomAdapter() {
            super(CurrentProjects.this, R.layout.display_project_cardview, projects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null)
                itemView = LayoutInflater.from(getContext()).inflate(R.layout.display_project_cardview,parent,false);
            final Project project = projects.get(position);
            TextView projectName = (TextView) itemView.findViewById(R.id.display_current_project_name);
            projectName.setText("" + project.getName());

            CardView cardView = (CardView) itemView.findViewById(R.id.display_project_card);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CurrentProjects.this, ViewProject.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.bundleString),project);
                    intent.putExtra(getString(R.string.parcedData),bundle);
                    startActivity(intent);
                }
            });

            return itemView;
        }
    }

}
