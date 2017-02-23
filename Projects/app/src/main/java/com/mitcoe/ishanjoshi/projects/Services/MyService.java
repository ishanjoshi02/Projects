package com.mitcoe.ishanjoshi.projects.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mitcoe.ishanjoshi.projects.AcceptTaskActivity;
import com.mitcoe.ishanjoshi.projects.R;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Project;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.ProjectTaskBundle;
import com.mitcoe.ishanjoshi.projects.Utility_Classes.Task;

public class MyService extends Service {
    private Notification.Builder builder;
    private DatabaseReference messageReference,bundleReference = null;


    private Runnable r;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.userDetails),MODE_PRIVATE);
        final String Name = sharedPreferences.getString(getString(R.string.userName),"Ishan");
        r = new Runnable() {
            @Override
            public void run() {
                messageReference = FirebaseDatabase.getInstance().getReference().child(Name).child(getString(R.string.FirebaseStringMessage));
                bundleReference = FirebaseDatabase.getInstance().getReference().child(Name).child(getString(R.string.FirebaseStringData));
                messageReference.addValueEventListener(valueEventListenerMessage);
                bundleReference.addValueEventListener(valueEventListenerData);
            }
        };
        Thread thread = new Thread(r);
        thread.start();
        return Service.START_STICKY;
    }

    public MyService() {

    }

    @Override
    public void onDestroy() {
        messageReference.removeEventListener(valueEventListenerMessage);
        bundleReference.removeEventListener(valueEventListenerData);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ValueEventListener valueEventListenerData = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
            Task task;
            Project project;
            DatabaseReference databaseReference;
            Gson gson = new Gson();
            ProjectTaskBundle projectTaskBundle = new ProjectTaskBundle();
            for (DataSnapshot child : children) {
                String value = child.getValue(String.class);
                projectTaskBundle = gson.fromJson(value,ProjectTaskBundle.class);
                task = projectTaskBundle.getTask();
                project = projectTaskBundle.getProject();
                builder = new Notification.Builder(MyService.this);
                builder.setContentTitle(getString(R.string.app_name));
                builder.setAutoCancel(true);
                builder.setWhen(System.currentTimeMillis());
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setTicker(getString(R.string.app_name));
                builder.setContentText("You have received a task from " + projectTaskBundle.getBoss());
                builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                Intent intent = new Intent(MyService.this, AcceptTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.parcedData),projectTaskBundle);
                intent.putExtras(bundle);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(209,builder.build());
                databaseReference = child.getRef();
                databaseReference.removeValue();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private ValueEventListener valueEventListenerMessage = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
            String messaage, appName = getString(R.string.app_name);
            for (DataSnapshot child : children) {
                messaage = child.getValue(String.class);

                builder = new Notification.Builder(MyService.this);
                builder.setContentText(messaage);
                builder.setAutoCancel(true);
                builder.setContentTitle(appName);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setTicker(appName);
                builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(209,builder.build());

                DatabaseReference childDatabaseReference = child.getRef();
                childDatabaseReference.removeValue();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
