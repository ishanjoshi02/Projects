package com.mitcoe.ishanjoshi.projects.Database_Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mitcoe.ishanjoshi.projects.Utility_Classes.Project;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.ProjectTaskBundle;

/**
 * Created by Ishan Joshi on 18-Feb-17.
 */

public class ProjectTaskBundleDatabase extends SQLiteOpenHelper {
    private Context context;
    private static String DATABASE_NAME = "projectTaskBundle.db";
    private static int DATABASE_VERSION = 1;
    private static String TABLE = "projectTaskTable";
    private static String ID_COLUMN = "_id";
    private static String TASKNAME_COLUMN = "_taskName";
    private static String PROJECTNAME_COLUMN = "_projectName";
    private static String BOSSNAME_COLUMN = "_bossName";
    private static String BOSSEMAIL_COLUMN = "_bossEmail";
    private static String REMINDERDAYS = "_days";
    public ProjectTaskBundleDatabase(Context con) {
        super(con,DATABASE_NAME,null,DATABASE_VERSION);
        this.context = con;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE + " (  " +
                        ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                        TASKNAME_COLUMN + " TEXT , " +
                        PROJECTNAME_COLUMN + " TEXT , " +
                        BOSSNAME_COLUMN + " TEXT , " +
                        BOSSEMAIL_COLUMN + " TEXT, " +
                        REMINDERDAYS + " INTEGER " + " )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " +TABLE + " IF EXISTS");
        this.onCreate(db);
    }

    public void add(ProjectTaskBundle projectTaskBundle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASKNAME_COLUMN,projectTaskBundle.getTask().getName());
        contentValues.put(PROJECTNAME_COLUMN,projectTaskBundle.getProject().getName());
        contentValues.put(BOSSNAME_COLUMN,projectTaskBundle.getBoss());
        contentValues.put(BOSSEMAIL_COLUMN,projectTaskBundle.getBossEmail());
        contentValues.put(REMINDERDAYS,Integer.valueOf(projectTaskBundle.getReminder_days()));
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE,null,contentValues);
    }

    public ProjectTaskBundle get(String TaskName) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE + " Where 1",null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String taskName = cursor.getString(cursor.getColumnIndex(TASKNAME_COLUMN));
            if (taskName == TaskName) {
                String projectName = cursor.getString(cursor.getColumnIndex(PROJECTNAME_COLUMN));
                String BossName = cursor.getString(cursor.getColumnIndex(BOSSNAME_COLUMN));
                String BossEmail = cursor.getString(cursor.getColumnIndex(BOSSEMAIL_COLUMN));
                int days = cursor.getInt(cursor.getColumnIndex(REMINDERDAYS));
                ProjectTaskBundle projectTaskBundle = new ProjectTaskBundle();
                TaskDatabase taskDatabase = new TaskDatabase(context);
                ProjectDatabase projectDatabase = new ProjectDatabase(context);
                projectTaskBundle.setTask(taskDatabase.getTask(taskName));
                projectTaskBundle.setBossEmail(BossEmail);
                projectTaskBundle.setBoss(BossName);
                projectTaskBundle.setProject(projectDatabase.getProject(projectName));
                projectTaskBundle.setReminder_days(""+days);
                return projectTaskBundle;
            }
        }
        return null;
    }
}
