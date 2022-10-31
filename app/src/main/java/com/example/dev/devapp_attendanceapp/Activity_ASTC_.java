package com.example.dev.devapp_attendanceapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class Activity_ASTC_ extends AppCompatActivity {
    ListView listView;
    String courseID;
    Cursor cursor;
    boolean [] attandance_array;
    SQLiteHelper_StudentCourseComposite studentCourseComposite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astc_);

        Intent intent = getIntent();
        courseID = intent.getStringExtra(SQLiteHelper_Courses.COLUMN_COURSE_ID);
        Log.i("dev","Received Course ID: "+courseID);
        studentCourseComposite = new SQLiteHelper_StudentCourseComposite(this);

        Button but_addStudent = (Button) findViewById(R.id.but_astc_add_new_student);
        but_addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_ASTC_.this,Activity_AddStudent_NewEntryToDatabase.class);
                startActivity(intent);
            }
        });

        Button but_enrol_to_course = (Button) findViewById(R.id.but_astc_enroll);
        but_enrol_to_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("dev","Enroll button pressed!");

                int count=0;

                for(int i=0;i<attandance_array.length;i++)
                {
                    if(attandance_array[i])
                    {
                        cursor.moveToPosition(i);
                        int student_id = cursor.getInt(cursor.getColumnIndex(SQLiteHelper_Students.COLUMN_STUDENT_ID));
                        Log.i("dev","selected student id: " + student_id);

                        studentCourseComposite.insertValues(courseID,student_id);
                        count++;
                    }
                }
                if(count>0)
                {
                    Toast.makeText(Activity_ASTC_.this, count + " Students enrolled successfully !", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    Toast.makeText(Activity_ASTC_.this, "Error! Select some student first...", Toast.LENGTH_SHORT).show();
                }

            }
        });

        listView = (ListView)findViewById(R.id.lv_astc);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(attandance_array[position] == false)
                {
                    attandance_array[position] = true;
                    ((LinearLayout) view.findViewById(R.id.ll_attandance_layout)).setBackgroundColor(Color.parseColor("#7CB342"));
                }
                else
                {
                    attandance_array[position] = false;
                    ((LinearLayout) view.findViewById(R.id.ll_attandance_layout)).setBackgroundColor(Color.parseColor("#F4511E"));
                }

                Log.i("dev","listview Clicked at position: "+position);
                Log.i("dev","attandance At position: "+position + " is: "+ attandance_array[position]);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("dev","Activity_AddStudent_ToCourse:OnStart");
        boolean is_enrolmentTable_empty=false;
        String id_array="";

        //receiving the exsisting students enrolled in this course
        SQLiteHelper_StudentCourseComposite studentsInEachCourse =
                new SQLiteHelper_StudentCourseComposite(this);

        //retrieve the students for the particular course_id
        Cursor cursor_StudentID_only = studentsInEachCourse.getAllStudentsWithCourseID(courseID);
        if(cursor_StudentID_only.getCount()==0) {
            is_enrolmentTable_empty = true;
        }
        else {
            id_array="( ";

            for(int i=0; i<cursor_StudentID_only.getCount()-1;i++) {
                Log.i("dev","Student id value["+i+"] is: "+
                        cursor_StudentID_only.getString(0));

                id_array += cursor_StudentID_only.getString(0);
                cursor_StudentID_only.moveToNext();
                id_array +=" , ";
            }

            id_array += cursor_StudentID_only.getString(0);

            id_array +=" );";

            Log.i("dev","ID array: "+id_array);
        }

        if(is_enrolmentTable_empty == true)
        {//showing all the students from the students database
            //creating an instance of the students table class and getting all the values in it to display into our listview
            SQLiteHelper_Students students = new SQLiteHelper_Students(this);

            //cursor is a data structure which contains the result of a SQLite query and
            //mainly points to one row of the 2D result array.
            cursor = students.getAllStudents();

            //setting the internal pointer to the starting row for extracting data in a for-loop
            cursor.moveToFirst();

            //creating two strings for feeding the SimpleCursorAdapter constructor
            String [] fromFieldNames = new String[] {SQLiteHelper_Students.COLUMN_STUDENT_ID,
                    SQLiteHelper_Students.COLUMN_STUDENT_NAME};
            int [] toViewIDs = new int[] {R.id.attandance_layout_id,R.id.attandance_layout_name};

            //Simple cursor adapter creates an adapter containing the views which we feed to the listview as an adapter
            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.listview_attandance_layout, cursor,
                    fromFieldNames,toViewIDs,0);

            listView.setAdapter(simpleCursorAdapter);

            Log.i("dev","adapter size: "+simpleCursorAdapter.getCount());

            attandance_array = new boolean[simpleCursorAdapter.getCount()];
        }
        else
        {//filtering out the existing ones...
            //creating an instance of the students table class and getting all the values in it to display into our listview
            SQLiteHelper_Students students = new SQLiteHelper_Students(this);

            //cursor is a data structure which contains the result of a SQLite query and
            //mainly points to one row of the 2D result array.
            cursor = students.getAllStudentsWithoutStudentID(id_array);

            //setting the internal pointer to the starting row for extracting data in a for-loop
            cursor.moveToFirst();

            //creating two strings for feeding the SimpleCursorAdapter constructor
            String [] fromFieldNames = new String[] {SQLiteHelper_Students.COLUMN_STUDENT_ID,
                    SQLiteHelper_Students.COLUMN_STUDENT_NAME};
            int [] toViewIDs = new int[] {R.id.attandance_layout_id,R.id.attandance_layout_name};

            //Simple cursor adapter creates an adapter containing the views which we feed to the listview as an adapter
            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.listview_attandance_layout, cursor,
                    fromFieldNames,toViewIDs,0);

            listView.setAdapter(simpleCursorAdapter);

            Log.i("dev","adapter size: "+simpleCursorAdapter.getCount());

            attandance_array = new boolean[simpleCursorAdapter.getCount()];
        }
    }
}
