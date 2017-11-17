package com.example.pratikmishra.activityidentifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    float[] valuesx = {};
    float[] valuesy = {};
    float[] valuesz = {};

    Thread thread;
    Intent startSenseService = null;
    IntentFilter filter = null;
    SQLiteDatabase db = null;
    File dbFile = null;


    String tableName = null;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            float[] graphPoints = new float[3];
            graphPoints[0] = intent.getFloatExtra("xvalue", 0);
            graphPoints[1] = intent.getFloatExtra("yvalue", 0);
            graphPoints[2] = intent.getFloatExtra("zvalue", 0);
            uploadDatatoDb(graphPoints);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create a graph view using GraphView.java given and add it to a layout.


        filter = new IntentFilter("com.example.pratikmishra.activityidentifier");
        dbFile = new File(this.getExternalFilesDir(null) + "/assgn3");
        if (!dbFile.exists() && !dbFile.isDirectory()) {
            dbFile.mkdir();
        }
        db = SQLiteDatabase.openOrCreateDatabase(dbFile + "/activityDb", null);


    }

    public void onClickRunbutton(View V) throws InterruptedException {
        float[] timestamp = null;
        float[] valuesx = null;
        float[] valuesy = null;
        float[] valuesz = null;
    }


    public void uploadDatatoDb(float[] values) {

        // Create a Database if already not created and insert the accelerometer data.
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            if (tableName != null) {
                db.execSQL("insert into " + tableName + "(timestamp, activity, xvalue, yvalue, zvalue) values " +
                        "(" + timestamp + ", " + values[0] + ", " + values[1] + ", " + values[2] + " );");
            }
        } catch (Exception e) {
            System.out.println(e);
            Toast.makeText(this, "Database insert failed!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickCreateDbbutton(View V) throws InterruptedException {
        tableName = "Activity";
        try {
            db.beginTransaction();
            db.execSQL("create table if not exists " + tableName + " (timestamp timestamp, " +
                    "activity integer, " + "xvalue float, yvalue float, zvalue float);");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println(e);
            Toast.makeText(this, "Database created failed!!", Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
        }

        startSenseService = new Intent(MainActivity.this, acclerometerUpdate.class);
        startSenseService.putExtra("tableName", tableName);
        startService(startSenseService);
        Toast.makeText(this, "Table created!", Toast.LENGTH_SHORT).show();
    }

}