package com.example.databasetest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    private OnDatabaseChangeListener databaseChangeListener;

    public DatabaseManager(Context c) {
        context = c;
    }

    public DatabaseManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String username, String password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.USER_NAME, username);
        contentValues.put(DatabaseHelper.USER_PASSWORD, password);
        database.insert(DatabaseHelper.DATABASE_TABLE, null, contentValues);
        if (databaseChangeListener != null) {
            databaseChangeListener.onDatabaseChanged();
        }
    }

    public Cursor fetch() {
        String[] columns = new String[]{DatabaseHelper.USER_ID, DatabaseHelper.USER_NAME, DatabaseHelper.USER_PASSWORD};
        return database.query(DatabaseHelper.DATABASE_TABLE, columns, null, null, null, null, null);
    }

    public int update(long _id, String username, String password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.USER_NAME, username);
        contentValues.put(DatabaseHelper.USER_PASSWORD, password);
        int result = database.update(DatabaseHelper.DATABASE_TABLE, contentValues, DatabaseHelper.USER_ID + " = " + _id, null);
        if (databaseChangeListener != null) {
            databaseChangeListener.onDatabaseChanged();
        }
        return result;
    }

    public void delete(long _id) {
        database.delete(DatabaseHelper.DATABASE_TABLE, DatabaseHelper.USER_ID + " = " + _id, null);
        if (databaseChangeListener != null) {
            databaseChangeListener.onDatabaseChanged();
        }
    }

    public void setOnDatabaseChangeListener(OnDatabaseChangeListener listener) {
        this.databaseChangeListener = listener;
    }

    public interface OnDatabaseChangeListener {
        void onDatabaseChanged();
    }
}