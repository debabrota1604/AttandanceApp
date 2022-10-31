package com.example.dev.devapp_attendanceapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity_3_individual_courses extends AppCompatActivity {
    SimpleCursorAdapter simpleCursorAdapter;
    TextView listview_welcome_message;
    ListView listView;
    Boolean is_student_table_empty=false;
    Intent intent;
    String courseID;
    String courseName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_3individual_course);
        Log.i("dev","MainActivity3:OnCreate");

        intent=getIntent();
        //setting up some static informations in the Textview from the intent extras...
        courseID = intent.getStringExtra(SQLiteHelper_Courses.COLUMN_COURSE_ID);//default return value is 0
        Log.i("dev","MainActivity3: Received Course ID: "+courseID);

        courseName = intent.getStringExtra(SQLiteHelper_Courses.COLUMN_COURSE_NAME);

        TextView tvCourseName = (TextView) findViewById(R.id.tv_courseName);
        tvCourseName.setText(courseName);

        TextView tvCourseID = (TextView) findViewById(R.id.tv_individualCourses_courseDetails);
        tvCourseID.setText(intent.getStringExtra(SQLiteHelper_Courses.COLUMN_COURSE_ID));

        TextView tvCourseStartDate = (TextView) findViewById(R.id.tv_individualCourses_startDate);
        tvCourseStartDate.setText(intent.getStringExtra(SQLiteHelper_Courses.COLUMN_COURSE_DATE_START));

        TextView tvCourseEndDate = (TextView) findViewById(R.id.tv_individualCourses_endDate);
        tvCourseEndDate.setText(intent.getStringExtra(SQLiteHelper_Courses.COLUMN_COURSE_DATE_END));

        TextView tvCourseDepartmentName = (TextView) findViewById(R.id.tv_individualCourses_courseDepartment);
        tvCourseDepartmentName.setText(intent.getStringExtra(SQLiteHelper_Courses.COLUMN_DEPARTMENT_NAME));


        listview_welcome_message = (TextView) findViewById(R.id.activity3_listview_welcome_message);

        /**
         * Use ArrayAdapter when your data source is an array.
         * By default, ArrayAdapter creates a view for each array item by calling
         * toString() on each item and placing the contents in a TextView
         */
        listView = (ListView) findViewById(R.id.lvMain3Activity);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {

                //Log.i("dev","User clicked: " + ((TextView) view.findViewById(R.id.list_layout_name)).getText().toString());

                Intent intent = new Intent(MainActivity_3_individual_courses.this,ActivityStudentInformation.class);
                intent.putExtra(SQLiteHelper_Students.COLUMN_STUDENT_NAME,((TextView) view.findViewById(R.id.list_layout_name)).getText().toString());

                startActivity(intent);
            }
        });

        /**
         * This button add_student enrolls some student from the existing student database into
         * the course. That is, the selected students are only for this particular course only.
         *
         * go to SQLiteHelper_StudentCourseComposite class definition file for more
         * information about this table
         *
         * When the user wants to insert a new student, he need to click on the Add-new_student button
         * in the child Activity.
         */
        Button but_AddStudent = (Button) findViewById(R.id.but_addStudents);
        but_AddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("dev","MainActivity3: Starting Add Student Activity...");
                Intent intent = new Intent(MainActivity_3_individual_courses.this,Activity_ASTC_.class);
                intent.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_ID,courseID);
                intent.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_NAME,courseName);
                startActivity(intent);
            }
        });

        //This button removes a particular student from the course enrollment database
        Button but_RemoveStudent = (Button) findViewById(R.id.but_removeStudents);
        but_RemoveStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_student_table_empty)
                {
                    Toast.makeText(MainActivity_3_individual_courses.this, "No student is enrolled in this course.\n\n Add students first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("dev","MainActivity3: Starting Remove Student Activity...");
                Intent intent = new Intent(MainActivity_3_individual_courses.this,Activity_deregister_student.class);
                intent.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_ID,courseID);
                intent.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_NAME,courseName);
                startActivity(intent);
            }
        });

        Button but_takeAttandance = (Button) findViewById(R.id.but_takeAttendance);
        but_takeAttandance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_student_table_empty)
                {
                    Toast.makeText(MainActivity_3_individual_courses.this, "Enroll some students first...", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity_3_individual_courses.this, ActivityAttandanceTaking.class);
                intent.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_ID,courseID);
                intent.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_NAME,courseName);
                startActivity(intent);
            }
        });

        Button but_openRegister = (Button) findViewById(R.id.but_showRegister);
        but_openRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteHelper_AttandanceRegister register = new SQLiteHelper_AttandanceRegister(getBaseContext());
                if(register.is_table_empty())
                {
                    Toast.makeText(MainActivity_3_individual_courses.this, "No attandance has been registered yet...", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(MainActivity_3_individual_courses.this,Activity_ShowRegister.class);

                SQLiteHelper_Courses courses = new SQLiteHelper_Courses(getBaseContext());
                Cursor cursor = courses.getAllDetailsWithCourseID(courseID);
                cursor.moveToFirst();
                Log.i("dev","CourseID: " + courseID);

                //passing some extra data along with courseID through the intent for easier access to these infos.
                intent.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_ID,courseID);
                intent.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_DATE_START,cursor.getString(1));
                intent.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_DATE_END,cursor.getString(2));
                intent.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_NAME,cursor.getString(3));
                intent.putExtra(SQLiteHelper_Courses.COLUMN_DEPARTMENT_NAME,cursor.getString(4));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("dev","MainActivity3:OnActivityResult");

        /**
         * The idea is to retrieve the students names from the courses-student table and display in the listview.
         *
         * First we retrieve the student ids from the Courses-Student table and save to a cursor.
         * Then use another query to get all the student info and save that to another cursor.
         * Then use this new cursor to display the data.
         */

        //getting the student IDs for a particular course ID from Course_student table
        SQLiteHelper_StudentCourseComposite studentsInEachCourse =
                new SQLiteHelper_StudentCourseComposite(this);

        //Log.i("dev","Received CourseID: "+Integer.toString(courseID));

        //retrieve the students for the particular course_id
        Cursor cursor_StudentID_only = studentsInEachCourse.getAllStudentsWithCourseID(courseID);
        if(cursor_StudentID_only.getCount()==0)
        {
            is_student_table_empty=true;

            Log.i("dev","No student registered for this course. creating empty listview...");

            Toast.makeText(MainActivity_3_individual_courses.this, "Alert: No student enrolled for this course.", Toast.LENGTH_SHORT).show();
            listView.setAdapter(null);
            return;
        }

        /**
         * we retrieve the cursor containing the student ids for a particular course.
         * Now parsing it to run another query on the Student database to get all infos
         * about a student with a particular student ID
         */

        String id_array="( ";

        for(int i=0; i<cursor_StudentID_only.getCount()-1;i++) {
            //Log.i("dev","Cursor course value["+i+"] is: "+ cursor_StudentID_only.getString(0));

            id_array += cursor_StudentID_only.getString(0);
            cursor_StudentID_only.moveToNext();
            id_array +=" , ";
        }

        id_array += cursor_StudentID_only.getString(0);

        id_array +=" );";

        //Log.i("dev","ID array: "+id_array);

        /**
         * student IDs are saved in ID_array. It will be used to get the ful details about a student
         */

        SQLiteHelper_Students students = new SQLiteHelper_Students(this);
        Cursor cursor_StudentFullDetails = students.getAllStudentsWithStudentID(id_array);

        //Log.i("dev","cursor_StudentFullDetails Size: " + cursor_StudentFullDetails.getCount());

        /**
         * we get the details saved in cursor_StudentFullDetails. Using
         * SimpleCursorAdapter to set the adapter for viewing in listView
         */

        String [] fromFieldNames = new String[] {SQLiteHelper_Students.COLUMN_STUDENT_ID,
                SQLiteHelper_Students.COLUMN_STUDENT_NAME,
                SQLiteHelper_Students.COLUMN_STUDENT_AGE};
        int [] toViewIDs = new int[] {R.id.list_layout_id,R.id.list_layout_name,R.id.list_layout_deptName};

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.list_layout,
                cursor_StudentFullDetails,fromFieldNames,toViewIDs,0);

        //Log.i("dev","CursorAdapter Size: " + simpleCursorAdapter.getCount());

        //setting the listview adapter to show to the user
        listView.setAdapter(simpleCursorAdapter);

        if(is_student_table_empty)
        {
            listview_welcome_message.setText("No Student is enrolled for this Course...");
        }
        else {
            listview_welcome_message.setText("Students enrolled in this course:");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("dev","MainActivity3:OnStart");

        /**
         * The idea is to retrieve the students names from the courses-student table and display in the listview.
         *
         * First we retrieve the student ids from the Courses-Student table and save to a cursor.
         * Then use another query to get all the student info and save that to another cursor.
         * Then use this new cursor to display the data.
         */

        //getting the student IDs for a particular course ID from Course_student table
        SQLiteHelper_StudentCourseComposite studentsInEachCourse =
                new SQLiteHelper_StudentCourseComposite(this);

        //Log.i("dev","Received CourseID: "+Integer.toString(courseID));

        //retrieve the students for the particular course_id
        Cursor cursor_StudentID_only = studentsInEachCourse.getAllStudentsWithCourseID(courseID);
        if(cursor_StudentID_only.getCount()==0)
        {
            is_student_table_empty=true;

            Log.i("dev","No student registered for this course. creating empty listview...");

            Toast.makeText(MainActivity_3_individual_courses.this, "Alert: No student enrolled for this course.", Toast.LENGTH_SHORT).show();
            listView.setAdapter(null);
            return;
        }

        /**
         * we retrieve the cursor containing the student ids for a particular course.
         * Now parsing it to run another query on the Student database to get all infos
         * about a student with a particular student ID
         */

        String id_array="( ";

        for(int i=0; i<cursor_StudentID_only.getCount()-1;i++) {
            //Log.i("dev","Cursor course value["+i+"] is: "+ cursor_StudentID_only.getString(0));

            id_array += cursor_StudentID_only.getString(0);
            cursor_StudentID_only.moveToNext();
            id_array +=" , ";
        }

        id_array += cursor_StudentID_only.getString(0);

        id_array +=" );";

        //Log.i("dev","ID array: "+id_array);

        /**
         * student IDs are saved in ID_array. It will be used to get the ful details about a student
         */

        SQLiteHelper_Students students = new SQLiteHelper_Students(this);
        Cursor cursor_StudentFullDetails = students.getAllStudentsWithStudentID(id_array);

        //Log.i("dev","cursor_StudentFullDetails Size: " + cursor_StudentFullDetails.getCount());

        /**
         * we get the details saved in cursor_StudentFullDetails. Using
         * SimpleCursorAdapter to set the adapter for viewing in listView
         */

        String [] fromFieldNames = new String[] {SQLiteHelper_Students.COLUMN_STUDENT_ID,
                SQLiteHelper_Students.COLUMN_STUDENT_NAME,
                SQLiteHelper_Students.COLUMN_STUDENT_AGE};
        int [] toViewIDs = new int[] {R.id.list_layout_id,R.id.list_layout_name,R.id.list_layout_deptName};

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.list_layout,
                cursor_StudentFullDetails,fromFieldNames,toViewIDs,0);

        //Log.i("dev","CursorAdapter Size: " + simpleCursorAdapter.getCount());

        //setting the listview adapter to show to the user
        listView.setAdapter(simpleCursorAdapter);

        if(is_student_table_empty)
        {
            listview_welcome_message.setText("No Student is enrolled for this Course...");
        }
        else {
            listview_welcome_message.setText("Students enrolled in this course:");
        }

    }


    @Override
    protected void onStop() {
        super.onStop();

        Log.i("dev","MainActivity3:OnStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("dev","MainActivity3:OnDestroy");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.i("dev","MainActivity3:OnBackPressed");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i("dev","MainActivity3:OnPause");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("dev","MainActivity:OnResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.i("dev","MainActivity3:OnRestart");
    }

}
