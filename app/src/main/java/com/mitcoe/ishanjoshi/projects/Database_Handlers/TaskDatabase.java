package com.mitcoe.ishanjoshi.projects.Database_Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.mitcoe.ishanjoshi.projects.Utility_Classes.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ishan Joshi on 14-Feb-17.
 */

public class TaskDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tasks.db";
    private static final String TABLE_TASKS = "tasks";
    private static String ID_COLUMN ="_id";
    private Context con;
    private static String NAME_COLUMN = "_name";
    private static String DESCRIPTION_COLUMN = "_description";
    private static String PARENT_PROJECT_COLUMN = "_parent_project";
    private static String WORKERS_COLUMN = "_workers";
    private static String COMPLETE_BY_COLUMN = "_completeBy";
    private static String COMPLETED_COLUMN = "_completed";

    public TaskDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        con = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_TASKS + " ( " +
                        ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                        NAME_COLUMN + " TEXT , " +
                        DESCRIPTION_COLUMN + " TEXT , " +
                        PARENT_PROJECT_COLUMN + " TEXT , " +
                        WORKERS_COLUMN + " TEXT , " +
                        COMPLETE_BY_COLUMN + " TEXT , " +
                        COMPLETED_COLUMN + " INTEGER " +
                        " ) "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(
                "DROP TABLE IF  EXISTS " + TABLE_TASKS
        );
        this.onCreate(sqLiteDatabase);
    }

    public void deleteTask(Task task) {
        String Name = task.getName();

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_TASKS, NAME_COLUMN + " =  "+"\""+Name+"\"" ,null);
        sqLiteDatabase.close();
    }
    public void deleteTasks(String projectName) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_TASKS, PARENT_PROJECT_COLUMN + " = " +"\""+ projectName+"\"",null);
        sqLiteDatabase.close();
    }

    public List<Task> getTaskList() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM  " + TABLE_TASKS + " WHERE 1", null);
        cursor.moveToFirst();
        String name, description , workers, parentProject, completeBy = null;
        int completed;
        while (!cursor.isAfterLast()) {
            name = cursor.getString(cursor.getColumnIndex(NAME_COLUMN));
            completed = cursor.getInt(cursor.getColumnIndex(COMPLETED_COLUMN));
            if (name != null && completed!=1) {
                parentProject = cursor.getString(cursor.getColumnIndex(PARENT_PROJECT_COLUMN));
                description = cursor.getString(cursor.getColumnIndex(DESCRIPTION_COLUMN));
                workers = cursor.getString(cursor.getColumnIndex(WORKERS_COLUMN));
                completeBy = cursor.getString(cursor.getColumnIndex(COMPLETE_BY_COLUMN));
                tasks.add(new Task(name, description, parentProject, Arrays.asList(workers.split(",")),completeBy));
            }
            cursor.moveToNext();
        }
        sqLiteDatabase.close();
        return tasks;
    }

    public List<Task> getTaskList(String ProjectName) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM  " + TABLE_TASKS + " WHERE 1" , null);
        cursor.moveToFirst();
        String name, description , workers, parentProject, completeBy;
        while (!cursor.isAfterLast()) {
            parentProject = cursor.getString(cursor.getColumnIndex(PARENT_PROJECT_COLUMN));
            if (parentProject.equals(ProjectName)) {
                name = cursor.getString(cursor.getColumnIndex(NAME_COLUMN));
                description = cursor.getString(cursor.getColumnIndex(DESCRIPTION_COLUMN));
                workers = cursor.getString(cursor.getColumnIndex(WORKERS_COLUMN));
                completeBy = cursor.getString(cursor.getColumnIndex(COMPLETE_BY_COLUMN));
                tasks.add(new Task(name, description, parentProject, Arrays.asList(workers.split(",")),completeBy));
            }
            cursor.moveToNext();
        }
        sqLiteDatabase.close();
        return tasks;
    }

    public Task getTask(String taskName) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE 1",null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(cursor.getColumnIndex(NAME_COLUMN));
            if (name == taskName) {
                String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION_COLUMN));
                String completeBy = cursor.getString(cursor.getColumnIndex(COMPLETE_BY_COLUMN));
                String parentProject = cursor.getString(cursor.getColumnIndex(PARENT_PROJECT_COLUMN));
                List<String> workers = Arrays.asList((cursor.getString(cursor.getColumnIndex(WORKERS_COLUMN))).split(","));
                return new Task(name,description,parentProject,workers,completeBy);
            }
        }
        return null;
    }

    public void addTask(Task task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COLUMN, task.getName());
        contentValues.put(DESCRIPTION_COLUMN,task.getDescription());
        contentValues.put(PARENT_PROJECT_COLUMN,task.getParentProject());
        List<String> workers = task.getWorkers();
        String parseable = "";
        for (String worker : workers) {
            parseable += worker + ",";
        }
        contentValues.put(WORKERS_COLUMN,parseable);
        contentValues.put(COMPLETE_BY_COLUMN,task.getCompleteBy());
        int flag = (task.getCompleted()) ? 1 : 0 ;
        contentValues.put(COMPLETED_COLUMN, flag);
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_TASKS, null, contentValues);
        Toast.makeText(con, "Added Task", Toast.LENGTH_SHORT).show();
        sqLiteDatabase.close();
    }
}
