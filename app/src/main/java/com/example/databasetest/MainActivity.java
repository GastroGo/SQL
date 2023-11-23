package com.example.databasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText editUserID;
    EditText editUserName;
    EditText editUserPassword;

    DatabaseManager dbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editUserID = findViewById(R.id.editTextID);
        editUserName = findViewById(R.id.editTextUserName);
        editUserPassword = findViewById(R.id.editTextPassword);

        dbManager = new DatabaseManager(this);
        try {
            dbManager.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        dbManager.setOnDatabaseChangeListener(new DatabaseManager.OnDatabaseChangeListener() {
            @Override
            public void onDatabaseChanged() {
                // Database has changed, send updated file to PC
                DatabaseObserver observer = new DatabaseObserver(new Handler(), MainActivity.this);
                observer.sendDatabaseFileToPC();
            }
        });
    }

    public void btnInsertPressed(View view) {
        dbManager.insert(editUserName.getText().toString(), editUserPassword.getText().toString());
    }
    public void btnFetchPressed(View view) {

        Cursor cursor = dbManager.fetch();

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String ID = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_ID));
                @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_NAME));
                @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_PASSWORD));
                Log.i("DATABASE_TAG", "I have read ID : " + ID + " username : " + username + " password : " + password);
            } while (cursor.moveToNext());
        }

    }
    public void btnUpdatePressed(View view) {

        dbManager.update(Long.parseLong(editUserID.getText().toString()), editUserName.getText().toString(), editUserPassword.getText().toString());
    }
    public void btnDeletePressed(View view) {
        dbManager.delete(Long.parseLong(editUserID.getText().toString()));
    }
}