package com.example.dev.devapp_attendanceapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Activity_AddStudent_NewEntryToDatabase extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    SQLiteHelper_Students studentsDatabase;
    String SpinnerSelectedItem=null;
    Context context= null;
    Spinner spinner;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("dev","Activity_AddStudent_NewEntryToDatabase:OnCreate");

        //setting up some initial parameters
        setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        setContentView(R.layout.activity_addstudent_new_entry_to_database);
        getIntent();
        context=getApplicationContext();

        //spinner displays the registered courses from the database
        spinner = (Spinner) findViewById(R.id.sp_addStudentCourse);
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        //Here we extract the data user have entered to the fill-up from and put them into the database
        final EditText AddStudentName = (EditText) findViewById(R.id.et_addStudent_studentName);
        final EditText AddStudentID = (EditText) findViewById(R.id.et_addStudent_studentID);
        final EditText AddStudentAge = (EditText) findViewById(R.id.et_addStudent_studentAge);

        Button CreateStudent = (Button) findViewById(R.id.but_StudentAdd_submit);
        CreateStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * It seems that we are creating a new database here. But that's not the case.
                 * We pass the application context from the constructor. This sets the context
                 * in the object. After that we access the old tables which are static entities
                 * stored in the user's internal app memory. Therefor no database is created here.
                 */
                studentsDatabase=new SQLiteHelper_Students(context);
                studentsDatabase.open();
                long retVal=studentsDatabase.insertValues(
                        Integer.parseInt(AddStudentID.getText().toString()),
                        AddStudentName.getText().toString(),
                        AddStudentAge.getText().toString());

                SQLiteHelper_AttandanceRegister register = new SQLiteHelper_AttandanceRegister(context);
                register.AlterTable_AddStudent(AddStudentID.getText().toString());

                Log.i("dev","Register table altered...");

                if(retVal>0)
                {
                    Toast.makeText(Activity_AddStudent_NewEntryToDatabase.this, "Value Successfully inserted at position "+retVal, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("dev","Activity_AddStudent_NewEntryToDatabase:OnStart");

        SQLiteHelper_Courses courses =new SQLiteHelper_Courses(getBaseContext());
        Cursor cursor = courses.getAllCourses();
        cursor.moveToFirst();

        String [] courses_array=new String[cursor.getCount()] ;

        for(int i=0; i<cursor.getCount();i++) {
            //Log.i("dev","Cursor course value["+i+"] is: "+ cursor.getString(cursor.getColumnIndex(courses.COLUMN_COURSE_NAME)));
            courses_array[i]= cursor.getString(cursor.getColumnIndex(courses.COLUMN_COURSE_NAME));
            cursor.moveToNext();
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,R.layout.listview_simple_row_layout,courses_array);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter1);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SpinnerSelectedItem = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        SpinnerSelectedItem= "Computer Science";
    }




//Nothing important below...



























    @Override
    protected void onStop() {
        super.onStop();

        Log.i("dev","Activity_AddStudent_NewEntryToDatabase:OnStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("dev","Activity_AddStudent_NewEntryToDatabase:OnDestroy");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.i("dev","Activity_AddStudent_NewEntryToDatabase:OnBackPressed");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i("dev","Activity_AddStudent_NewEntryToDatabase:OnPause");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("dev","Activity_AddStudent_NewEntryToDatabase:OnResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.i("dev","Activity_AddStudent_NewEntryToDatabase:OnRestart");
    }
}
