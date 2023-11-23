package com.example.databasetest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DatabaseManager(Context c) {
        context = c;
    }

    public DatabaseManager open() {
        dbHelper = new DatabaseHelper(context);
        try {
            database = dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            // Hier kannst du Logausgaben oder eine andere Fehlerbehandlung hinzufügen
            e.printStackTrace();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String username, String password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.USER_NAME, username);
        contentValues.put(DatabaseHelper.USER_PASSWORD, password);
        try {
            database.insert(DatabaseHelper.DATABASE_TABLE, null, contentValues);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Cursor fetch() {
        String[] columns = new String[]{DatabaseHelper.USER_ID, DatabaseHelper.USER_NAME, DatabaseHelper.USER_PASSWORD};
        Cursor cursor = null;
        try {
            cursor = database.query(DatabaseHelper.DATABASE_TABLE, columns, null, null, null, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cursor;
    }

    public int update(long _id, String username, String password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.USER_NAME, username);
        contentValues.put(DatabaseHelper.USER_PASSWORD, password);
        int i = 0;
        try {
            i = database.update(DatabaseHelper.DATABASE_TABLE, contentValues, DatabaseHelper.USER_ID + " = " + _id, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public void delete(long _id) {
        try {
            database.delete(DatabaseHelper.DATABASE_TABLE, DatabaseHelper.USER_ID + " = " + _id, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}