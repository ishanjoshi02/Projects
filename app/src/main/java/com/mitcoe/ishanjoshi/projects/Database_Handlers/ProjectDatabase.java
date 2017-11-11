package com.mitcoe.ishanjoshi.projects.Database_Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mitcoe.ishanjoshi.projects.Utility_Classes.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ishan Joshi on 14-Feb-17.
 */

public class ProjectDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "projects.db";
    private static final String TABLE_PROJECTS = "projects";
    private static final String ID_COLUMN = "_id";
    private static final String NAME_COLUMN = "_name";
    private static final String DESCRIPTION_COLUMN = "_description";
    private static final String COMPLETED_COLUMN = "_completed";
    public ProjectDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_PROJECTS + " ( " +
                        ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                        NAME_COLUMN + " TEXT , " +
                        DESCRIPTION_COLUMN + " TEXT , " +
                        COMPLETED_COLUMN + " INTEGER " +
                        " ) "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(
                "DROP TABLE IF  EXISTS " + TABLE_PROJECTS
        );
        this.onCreate(sqLiteDatabase);
    }

    public void addProject(Project project) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COLUMN,project.getName());
        contentValues.put(DESCRIPTION_COLUMN,project.getDescription());
        int flag = (project.getCompleted()) ? 1 : 0;
        contentValues.put(COMPLETED_COLUMN,flag);
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_PROJECTS, null, contentValues);
        sqLiteDatabase.close();

    }

    public Project getProject(String projectName) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_PROJECTS + " WHERE 1", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(cursor.getColumnIndex(NAME_COLUMN))!= null
                    && cursor.getInt(cursor.getColumnIndex(COMPLETED_COLUMN))!= 1 ) {
                 return new Project(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)),cursor.getString(cursor.getColumnIndex(DESCRIPTION_COLUMN)));

            }
            cursor.moveToNext();
        }


        sqLiteDatabase.close();
        return null;
    }

    public void deleteProjects(String projectName) {
        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(TABLE_PROJECTS,NAME_COLUMN + " = \"" + projectName +  "\"",null);

        database.close();

    }

    public List<Project> getCurrentProjectsList() {
        List<Project> projects = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_PROJECTS + " WHERE 1", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(cursor.getColumnIndex(NAME_COLUMN))!= null
                    && cursor.getInt(cursor.getColumnIndex(COMPLETED_COLUMN))!= 1 ) {
                projects.add(new Project(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)),cursor.getString(cursor.getColumnIndex(DESCRIPTION_COLUMN))));
            }
            cursor.moveToNext();
        }

        sqLiteDatabase.close();
        return projects;
    }

    public List<Project> getCompletedProjectList() {
        List<Project> projects = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_PROJECTS + " WHERE 1", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(cursor.getColumnIndex(NAME_COLUMN))!= null
                    && cursor.getInt(cursor.getColumnIndex(COMPLETED_COLUMN)) == 1 ) {
                projects.add(new Project(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)),cursor.getString(cursor.getColumnIndex(DESCRIPTION_COLUMN))));

            }
            cursor.moveToNext();
        }

        sqLiteDatabase.close();
        return projects;
    }



}
