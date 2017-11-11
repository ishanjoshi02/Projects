package com.mitcoe.ishanjoshi.projects;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mitcoe.ishanjoshi.projects.Database_Handlers.TaskDatabase;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Task;

import java.util.List;

public class CurrentTask extends AppCompatActivity {

    List<Task> taskList;
    ArrayAdapter<Task> taskArrayAdapter;
    ListView listView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_task);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Current Tasks");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        TaskDatabase taskDatabase = new TaskDatabase(this);
        taskList = taskDatabase.getTaskList();

        button = (Button) findViewById(R.id.create_task_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CurrentTask.this, CreateTask.class));
            }
        });

        listView = (ListView) findViewById(R.id.display_current_tasks);
        taskArrayAdapter = new MyCustomTaskAdapter();
        listView.setAdapter(taskArrayAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(CurrentTask.this, MainActivity.class));
        return super.onOptionsItemSelected(item);
    }

    private class MyCustomTaskAdapter extends
            ArrayAdapter<Task> {
        MyCustomTaskAdapter() {
            super(CurrentTask.this,R.layout.display_project_cardview,taskList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null)
                itemView = LayoutInflater.from(getContext()).inflate(R.layout.display_project_cardview,parent,false);

            TextView textView = (TextView) itemView.findViewById(R.id.display_current_project_name);
            final Task currentTask = taskList.get(position);
            textView.setText(currentTask.getName());
            CardView cardView = (CardView) itemView.findViewById(R.id.display_project_card) ;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CurrentTask.this, ViewTask.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.parcedData),currentTask);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            return itemView;
        }
    }
}
