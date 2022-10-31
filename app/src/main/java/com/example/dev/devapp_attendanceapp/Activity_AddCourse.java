package com.example.dev.devapp_attendanceapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Activity_AddCourse extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    SQLiteHelper_Courses coursesDataBase;
    String SpinnerSelectedItem=null;
    Context context= null;

    private Calendar calendar;
    private TextView tv_startDate,tv_startTime,tv_endDate,tv_endTime;
    private int year, month, day, hour, minute, second;
    private String dateString = "",timeString = "";

    /*public static void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow( .getCurrentFocus().getWindowToken(), 0);
    }*/

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SpinnerSelectedItem = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        SpinnerSelectedItem= "Computer Science";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        setContentView(R.layout.activity_add_course);

        getIntent();
        context=getApplicationContext();

        Spinner spinner = (Spinner) findViewById(R.id.sp_AddCourse_DepartmentName);
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.departments_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //datepicker code
        calendar = Calendar.getInstance();

        //getting system values to initially start with
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        tv_startDate = (TextView) findViewById(R.id.tv_start_datepicker_add_course);
        tv_endDate = (TextView) findViewById(R.id.tv_end_datepicker_add_course);

        Button but_startDate = (Button) findViewById(R.id.but_start_datepicker_add_course);
        but_startDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        setStartDate(year,monthOfYear,dayOfMonth);
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(Activity_AddCourse.this,onDateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });

        Button but_endDate = (Button) findViewById(R.id.but_end_datepicker_add_course);
        but_endDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        setEndDate(year,monthOfYear,dayOfMonth);
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(Activity_AddCourse.this,onDateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);

        final EditText AddCourseName = (EditText) findViewById(R.id.et_AddCourse_CourseName);
        final EditText AddCourseID = (EditText) findViewById(R.id.et_AddCourse_CourseID);

        Button CreateCourse = (Button) findViewById(R.id.but_AddCourse_submit);
        CreateCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coursesDataBase=new SQLiteHelper_Courses(context);
                coursesDataBase.open();
                if(AddCourseID.getText().toString() == ""){
                    Toast.makeText(Activity_AddCourse.this, "Add Course ID please...", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(AddCourseName.getText().toString() == ""){
                    Toast.makeText(Activity_AddCourse.this, "Add Course Name please...", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(tv_startDate.getText().toString() == ""){
                    Toast.makeText(Activity_AddCourse.this, "Add Start Date please...", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(tv_endDate.getText().toString() == ""){
                    Toast.makeText(Activity_AddCourse.this, "Add End Date please...", Toast.LENGTH_SHORT).show();
                    return;
                }

                long retVal=coursesDataBase.insertValues(
                        Integer.parseInt(AddCourseID.getText().toString()),
                        tv_startDate.getText().toString(),
                        tv_endDate.getText().toString(),
                        AddCourseName.getText().toString(),
                        SpinnerSelectedItem);
                if(retVal>0)
                {
                    Toast.makeText(Activity_AddCourse.this, "Value Successfully inserted. ", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    Toast.makeText(Activity_AddCourse.this, "Error in insertion with return ID: " + retVal, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String DateFormatter(int year, int month, int day) {
        String separator = " / ";
        dateString = Integer.toString(day) + separator + Integer.toString(month+1) + separator + Integer.toString(year);

        //Log.i("dev","Date: "+dateString);
        return dateString;
    }

    private String TimeFormatter(int hour, int minute) {
        String separator = " : ";
        timeString = Integer.toString(hour) + separator + Integer.toString(minute);

        //Log.i("dev","Time: "+timeString);
        return timeString;
    }

    private void setStartDate( int year, int monthOfYear, int dayOfMonth)
    {
        tv_startDate.setText(DateFormatter(year,monthOfYear,dayOfMonth));
        //Log.i("dev","SetDate: "+dateString);
    }
    private void setEndDate( int year, int monthOfYear, int dayOfMonth)
    {
        tv_endDate.setText(DateFormatter(year,monthOfYear,dayOfMonth));
        //Log.i("dev","SetDate: "+dateString);
    }
    private void setStartTime(int hour, int minute)
    {
        tv_startTime.setText(TimeFormatter(hour,minute));
        //Log.i("dev","SetTime: "+timeString);
    }
    private void setEndTime(int hour, int minute)
    {
        tv_endTime.setText(TimeFormatter(hour,minute));
        //Log.i("dev","SetTime: "+timeString);
    }

}
