package com.example.dev.devapp_attendanceapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ActivityStudentInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_information);

        Intent intent=getIntent();
        TextView textView_student_name=(TextView)findViewById(R.id.tv_studentActivity_name);
        TextView textView_student_id=(TextView)findViewById(R.id.tv_studentActivity_id);
        TextView textView_student_course=(TextView)findViewById(R.id.tv_studentActivity_name);
        TextView textView_student_PoA=(TextView)findViewById(R.id.tv_studentActivity_name);

        textView_student_name.setText(intent.getStringExtra(SQLiteHelper_Students.COLUMN_STUDENT_NAME));
    }
}
