package com.mitcoe.ishanjoshi.projects;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import com.mitcoe.ishanjoshi.projects.Database_Handlers.TaskDatabase;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Project;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddTask extends AppCompatActivity {

    TextInputEditText inputName, inputDescription;
    AutoCompleteTextView autoCompleteTextView;
    TaskDatabase taskDatabase;
    List<String> TeachersList;
    ListView namesListView;
    ArrayAdapter<String> namesArrayAdapter;
    FloatingActionButton floatingActionButton;
    List<String> namesAssigned;
    DatePicker datePicker;
    String completeBy = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Adding Task to Project");
        inputName = (TextInputEditText) findViewById(R.id.get_Task_Name);
        inputDescription = (TextInputEditText) findViewById(R.id.get_Task_Description);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.continue_to_next);
        final String[] names = getResources().getStringArray(R.array.NamesOfTeachers);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        initdatePicker();
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.AutoCompleteTextView_assign_names) ;
        autoCompleteTextView.setHint("Add user");
        taskDatabase = new TaskDatabase(AddTask.this);
        TeachersList = new ArrayList<>(Arrays.asList(names));
        namesAssigned = new ArrayList<>();
        namesListView = (ListView) findViewById(R.id.assigned_Names);
        final List<String> namesAssigned = new ArrayList<>();
        namesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,namesAssigned);
        namesListView.setAdapter(namesArrayAdapter);

        final List<String> namesList = new ArrayList<>(Arrays.asList(names));
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,namesList);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String object = arrayAdapter.getItem(position);
                if (!namesList.contains(object))
                    Toast.makeText(AddTask.this, "Please Check Spelling of Name", Toast.LENGTH_SHORT).show();
                else {
                    arrayAdapter.remove(object);
                    autoCompleteTextView.setText("");
                    namesAssigned.add(object);
                    arrayAdapter.notifyDataSetChanged();
                    namesArrayAdapter.notifyDataSetChanged();
                }
            }
        });
        namesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String string = namesArrayAdapter.getItem(position);
                namesAssigned.remove(string);
                namesArrayAdapter.notifyDataSetChanged();
                TeachersList.add(string);
                arrayAdapter.notifyDataSetChanged();
            }
        });
        taskDatabase = new TaskDatabase(this);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = inputName.getText().toString();
                String description = inputDescription.getText().toString();
                if (taskName.equals(""))
                    Toast.makeText(AddTask.this, "Please add Task Name", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = getIntent();
                    Bundle bundle = intent.getExtras();
                    int count = intent.getIntExtra(getString(R.string.IntegerData),0);
                    Project project = (Project) bundle.getSerializable(getString(R.string.ProjectParce));
                    int year = datePicker.getYear();
                    int month = datePicker.getMonth() + 1;
                    int day = datePicker.getDayOfMonth();
                    completeBy = day + "/" + month + "/" + year;
                    count--;
                    Task ctask;
                     ctask = new Task(taskName,description,project.getName(),namesAssigned,completeBy);
                    ctask.setCompleted(false);
                    ctask.setAccepted(false);
                    taskDatabase.addTask(ctask);
                    if (count > 0) {
                        Intent next = new Intent(AddTask.this,AddTask.class);
                        Bundle bun = new Bundle();
                        bun.putSerializable(getString(R.string.ProjectParce),project);
                        next.putExtras(bun);
                        next.putExtra(getString(R.string.IntegerData),count);
                        startActivity(next);
                    }
                    else {
                        Bundle finalBundle = new Bundle();
                        Bundle before = getIntent().getExtras();
                        finalBundle.putSerializable(getString(R.string.ProjectParce),
                                before.getSerializable(getString(R.string.ProjectParce)));
                        Intent finalIntent = new Intent(AddTask.this,VerifyProject.class);
                        finalIntent.putExtras(finalBundle);
                        finalIntent.putExtra(getString(R.string.CallingActivity),AddTask.class.toString());
                        finalIntent.putExtra(getString(R.string.projectFirebase),project.getName());
                        startActivity(finalIntent);
                    }
                }
            }
        });
    }
    private void initdatePicker() {

        Calendar calendar = Calendar.getInstance();
        datePicker.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

    }
//todo add this feature in next update
    private class MyCustomUserAdapter extends
            ArrayAdapter<String> {
        MyCustomUserAdapter() {
            super(AddTask.this,R.layout.remove_user,namesAssigned);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null)
                itemView = LayoutInflater.from(getContext()).inflate(R.layout.remove_user,parent,false);
            TextView textView = (TextView) itemView.findViewById(R.id.display_name);
            final String current = namesAssigned.get(position);
            textView.setText(""+current);
            ImageButton imageButton = (ImageButton) itemView.findViewById(R.id.remove_name_image_button);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    namesArrayAdapter.remove(current);
                }
            });
            return itemView;
        }
    }
}
