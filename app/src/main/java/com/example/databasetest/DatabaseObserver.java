package com.example.databasetest;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DatabaseObserver extends ContentObserver {

    private Context context;

    public DatabaseObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        // Database has changed, send updated file to PC
        sendDatabaseFileToPC();
    }


    public void sendDatabaseFileToPC() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get database file
                    File databaseFile = context.getDatabasePath(DatabaseHelper.DATABASE_NAME);
                    Log.i("DatabaseObserver", "Preparing to send database file: " + databaseFile.getAbsolutePath());

                    // Create connection
                    URL url = new URL("http://192.168.178.50:5000/upload");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    // Write file to output stream
                    OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                    out.write(("--" + boundary + "\r\n").getBytes());
                    out.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + databaseFile.getName() + "\"\r\n\r\n").getBytes());
                    FileInputStream in = new FileInputStream(databaseFile);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    out.write(("\r\n--" + boundary + "--\r\n").getBytes());
                    out.close();
                    in.close();

                    // Get response
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Log.i("DatabaseObserver", "File sent successfully");
                    } else {
                        Log.e("DatabaseObserver", "Failed to send file, HTTP response code: " + responseCode);
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    Log.e("DatabaseObserver", "Error sending file", e);
                }
            }
        }).start();
    }
}