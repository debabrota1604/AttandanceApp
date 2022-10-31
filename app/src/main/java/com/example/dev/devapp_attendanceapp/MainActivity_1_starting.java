package com.example.dev.devapp_attendanceapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity_1_starting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("dev","MainActivity1:OnCreate");

        setContentView(R.layout.activity_main_1starting);

        /**
         * This app is mainly for a single person use, usually the instructor.
         * Therefore this app activities are filtered for a single person use.
         *
         * First the user or the instructor opens the app. He is greeted with a
         * simple log in window, which he can skip and move forward.
         *      If the instructor logs in, then he gets admin priviledge by which
         * he can create, delete or modify data in the database. But without
         * the admin access he is able to view the data and take attandance only.
         */

        Button butProceed= (Button) findViewById(R.id.proceed);
        butProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_1_starting.this, MainActivity_2_courses.class);
                startActivity(intent);
            }
        });
    }


//Nothing important below...
















    @Override
    protected void onStart() {
        super.onStart();

        Log.i("dev","MainActivity1:OnStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.i("dev","MainActivity1:OnRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i("dev","MainActivity1:OnPause");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("dev","MainActivity1:OnResume");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i("dev","MainActivity1:OnStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("dev","MainActivity1:OnDestroy");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.i("dev","MainActivity1:OnBackPressed");
    }


}
