package com.mitcoe.ishanjoshi.projects;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mitcoe.ishanjoshi.projects.Database_Handlers.ProjectDatabase;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Project;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditTask extends AppCompatActivity {
    Task task;
    FloatingActionButton floatingActionButton;
    TextInputEditText nameEditText, descriptionEditText;
    AutoCompleteTextView autoCompleteTextView, parentProjects;
    ListView listView;
    List<String> selectedList, notSelectedList, parentProjectNameList;
    List<Project> parentProjectList;
    ArrayAdapter<String> stringArrayAdapter,autoArrayAdapter,parentStringArrayAdapter;
    String[] names;
    String taskParentProject;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        task = (Task) getIntent().getExtras().getSerializable(getString(R.string.parcedData));

        floatingActionButton = (FloatingActionButton) findViewById(R.id.edited_fab);
        nameEditText = (TextInputEditText) findViewById(R.id.set_task_name_edit);
        descriptionEditText = (TextInputEditText) findViewById(R.id.set_task_description_edit);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.AutoCompleteTextView_edit_names);
        parentProjects = (AutoCompleteTextView) findViewById(R.id.list_parent_projects);
        listView = (ListView) findViewById(R.id.listView_edit_task);
        datePicker = (DatePicker) findViewById(R.id.editTask_datePicker);
        initdatePicker();
        selectedList = task.getWorkers();
        parentProjectNameList = new ArrayList<>();
        notSelectedList = selectedList;

        ProjectDatabase projectDatabase = new ProjectDatabase(this);
        parentProjectList = projectDatabase.getCurrentProjectsList();

        for (Project currentProject : parentProjectList) {
            String projectName = currentProject.getName();
            parentProjectNameList.add(projectName);
        }

        parentStringArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,parentProjectNameList);
        parentProjects.setAdapter(parentStringArrayAdapter);
        parentProjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                taskParentProject = parentStringArrayAdapter.getItem(position);
            }
        });

        names = getResources().getStringArray(R.array.NamesOfTeachers);

        for (String name : names) {
            notSelectedList.remove(name);
        }

        autoArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,notSelectedList);
        stringArrayAdapter = new MyCustomStringAdapter();

        nameEditText.setText("" + task.getName());
        final String descriptionOfTask = task.getDescription();

        if (!descriptionOfTask.equals("")) {
            descriptionEditText.setText(descriptionOfTask);
        }



        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo add gui code integration
               String NameOfTask;
                NameOfTask = nameEditText.getText().toString();
                String date = "" +  datePicker.getYear() + (datePicker.getMonth() - 1) + datePicker.getDayOfMonth();
                if (NameOfTask.equals(""))
                    Toast.makeText(EditTask.this, "Please add Task Name", Toast.LENGTH_SHORT).show();
                else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.parcedData),new Task(NameOfTask,descriptionOfTask,taskParentProject,selectedList,date));
                    Intent intent = new Intent(EditTask.this, VerifyProject.class);
                    intent.putExtra(getString(R.string.IntegerData),getIntent().getIntExtra(getString(R.string.IntegerData),0));
                    intent.putExtra(getString(R.string.CallingActivity),EditTask.class.toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

    }

    private void initdatePicker() {

        Calendar calendar = Calendar.getInstance();
        datePicker.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

    }

    private class MyCustomStringAdapter extends
            ArrayAdapter<String> {
        MyCustomStringAdapter() {
            super(EditTask.this,R.layout.remove_user,selectedList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null)
                LayoutInflater.from(getContext()).inflate(R.layout.remove_user,parent,false);
            TextView textView = (TextView) itemView.findViewById(R.id.display_string);
            final String currentName = selectedList.get(position);
            textView.setText(""+currentName);
            ImageButton imageButton = (ImageButton) itemView.findViewById(R.id.remove_name_image_button);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedList.remove(currentName);
                    notSelectedList.add(currentName);
                    stringArrayAdapter.notifyDataSetChanged();
                    autoArrayAdapter.notifyDataSetChanged();
                }
            });
            return itemView;
        }
    }
}
