package com.lovlalsha.studentsystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatActivity;

import com.lovlalsha.studentsystem.helpers.DatabaseConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                // do something...
                DatabaseConnection.getInstance(MainActivity.this);
            }
        }, 1);

        findViewById(R.id.show_all_btn).setOnClickListener(view -> {
            Intent in = new Intent(this, ShowAllTablesActivity.class);

            startActivity(in);
        });

        findViewById(R.id.list_class_students_btn).setOnClickListener(view -> {
            Intent in = new Intent(this, ListClassStudents.class);
            startActivity(in);
        });

        findViewById(R.id.show_preq_btn).setOnClickListener(view -> {
            Intent in = new Intent(this, ShowPrequisites.class);
            startActivity(in);

        });

        findViewById(R.id.enroll_btn).setOnClickListener(view -> {
            Intent in = new Intent(this, EnrollStudent.class);
            startActivity(in);
        });

        findViewById(R.id.drop_btn).setOnClickListener(view -> {
            Intent in = new Intent(this, DropStudent.class);
            startActivity(in);

        });

        findViewById(R.id.delete_btn).setOnClickListener(view -> {
            Intent in = new Intent(this, DeleteStudent.class);
            startActivity(in);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                // do something...
                DatabaseConnection.getInstance(MainActivity.this);
            }
        }, 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseConnection.disconnect(this);
    }
}