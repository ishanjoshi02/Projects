package com.mitcoe.ishanjoshi.projects;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextView;
    List<String> TeachersList;
    private TextInputEditText textInputEditText, textInputEditText1;
    private String name, emailAddress, password = null;
    private FloatingActionButton fab;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null)
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sign Up/Sign In");
        fab = (FloatingActionButton) findViewById(R.id.complete_login);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.AutoCompleteTextView_display_all_names);
        Resources res = getResources();
        String TeacherNames[] = res.getStringArray(R.array.NamesOfTeachers);
        // TODO: 14-Feb-17 Add Names in the strings.xml file
        TeachersList = new ArrayList<>(Arrays.asList(TeacherNames));
        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,TeachersList);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                name = arrayAdapter.getItem(position);
                autoCompleteTextView.setText(name);
            }
        });
        textInputEditText = (TextInputEditText) findViewById(R.id.enter_user_email);
        textInputEditText1 = (TextInputEditText) findViewById(R.id.enter_user_password);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailAddress = textInputEditText.getText().toString();
                password = textInputEditText1.getText().toString();
                if (!emailAddress.equals("") && !password.equals("")) {
                    mAuth.createUserWithEmailAndPassword(emailAddress, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Boolean result = task.isSuccessful();
                                    if (result) {
                                        Toast.makeText(LoginActivity.this, "Created New Account", Toast.LENGTH_SHORT).show();
                                        saveUser();
                                    }
                                    else {
                                        mAuth.signInWithEmailAndPassword(emailAddress, password)
                                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        Boolean res = task.isSuccessful();
                                                        if (res) {
                                                            Toast.makeText(LoginActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
                                                            saveUser();
                                                        }
                                                        else
                                                            Toast.makeText(LoginActivity.this,
                                                                    "Can't Access Database\nPlease Try Again Later",
                                                                    Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            });
                }
            }
        });
    }
    private void saveUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.userDetails),MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.userName),name);
        editor.putString(getString(R.string.userEmail),emailAddress);
        editor.commit();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }
}
