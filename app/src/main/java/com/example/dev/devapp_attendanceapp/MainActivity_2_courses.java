package com.example.dev.devapp_attendanceapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity_2_courses extends AppCompatActivity {
    SQLiteHelper_StudentCourseComposite SQLiteHelper_studentCourseComposite;
    SimpleCursorAdapter simpleCursorAdapter;
    ListView listView;
    Boolean is_course_table_empty=false;
    Button but_CoursesRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("dev","MainActivity2:OnCreate");

        setContentView(R.layout.activity_main_2courses);

        /**
         * In this activity we get all the course name from the course database and show it to the instructor.
         * The instructor is expected to choose from the enlisted courses and proceed to the next activity.
         *
         * He is also able to add or remove courses if he gets admin privilege.
         */

        SQLiteHelper_studentCourseComposite = new SQLiteHelper_StudentCourseComposite(this);

        //This button adds a new course in the database
        Button but_CoursesAdd = (Button) findViewById(R.id.but_addCourses);
        but_CoursesAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("dev","Starting Add Courses Activity...");
                Intent intent = new Intent(MainActivity_2_courses.this,Activity_AddCourse.class);
                startActivity(intent);
            }
        });

        //This button removes an existing course from the database
        but_CoursesRemove = (Button) findViewById(R.id.but_removeCourses);
        but_CoursesRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the table is already empty we wont open the remove courses activity as an optimization.
                if(is_course_table_empty)
                {
                    Toast.makeText(MainActivity_2_courses.this, "Courses List is already empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i("dev","Starting Remove Courses Activity...");
                Intent intent = new Intent(MainActivity_2_courses.this,Activity_RemoveCourse.class);
                startActivity(intent);
            }
        });

        //This button removes the complete database itself. So, it is a restore default task.
        Button but_CoursesRemoveDB = (Button) findViewById(R.id.but_removeCoursesDB);
        but_CoursesRemoveDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking database existance from file manager which is a more accurate source.
                final File db_file_reference = getApplicationContext().getDatabasePath(SQLiteHelper_Courses.getDBName());
                if(db_file_reference.exists()){
                    Log.i("dev","button_RemoveCoursesDatabase: DataBase Already Exists! Proceeding to deletion dialog..");
                }else{
                    Log.i("dev","button_RemoveCoursesDatabase: Database already deleted! Returnig back...");
                    return;
                }

                //using alart dialog to promt user about the course deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity_2_courses.this);
                builder.setMessage("Are you sure to delete the whole Database?\n\n you will loose all your Courses, Students and other saved informations.").setTitle("Warning !!!");

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("dev","button_RemoveCoursesDatabase: AlertDialog: Nothing Deleted..");
                        Toast.makeText(MainActivity_2_courses.this, "No change is done !", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteHelper_Courses courses = new SQLiteHelper_Courses(getBaseContext());
                        SQLiteHelper_Students students = new SQLiteHelper_Students(getBaseContext());
                        SQLiteHelper_StudentCourseComposite composite = new SQLiteHelper_StudentCourseComposite(getBaseContext());
                        SQLiteHelper_AttandanceRegister register = new SQLiteHelper_AttandanceRegister(getBaseContext());

                        courses.deleteTable();
                        students.deleteTable();
                        composite.deleteTable();
                        register.deleteTable();

                        db_file_reference.delete();

                        Log.i("dev","button_RemoveCoursesDatabase: AlertDialog: Whole database deleted!..");
                        Toast.makeText(MainActivity_2_courses.this, "Database Deleted!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                //creating dialog and displaying it
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //populating the listview by reading the courses table in the database.
        listView = (ListView) findViewById(R.id.lvMain2Activity);
        final SQLiteHelper_Courses courses = new SQLiteHelper_Courses(getBaseContext());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * By clicking any of the listview elements, the user is redirected to the
             * selected course page under cursor. It is mandatory for taking attendance
             * for a particular course.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {

                Intent intent = new Intent(MainActivity_2_courses.this,MainActivity_3_individual_courses.class);
                String courseID = ((TextView) view.findViewById(R.id.list_layout_id)).getText().toString();

                Cursor cursor = courses.getAllDetailsWithCourseID(courseID);
                cursor.moveToFirst();
                Log.i("dev","CourseID: " + courseID);

                //passing some extra data along with courseID through the intent for easier access to these infos.
                intent.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_ID,courseID);
                intent.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_DATE_START,cursor.getString(1));
                intent.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_DATE_END,cursor.getString(2));
                intent.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_NAME,cursor.getString(3));
                intent.putExtra(SQLiteHelper_Courses.COLUMN_DEPARTMENT_NAME,cursor.getString(4));

                //starting individual course activity
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("dev","MainActivity2:OnStart");

        /**
         * The main idea to use onStart after using onCreate is that
         * whenever we call and get returned from a next activity to this activity, the
         * function callings for this Activity are in this order:
         *  onPause() > onStop() > onRestart() > onStart() > onResume()
         *  therefore we used another function which is onStart in this case.
         *
         *  When this activity is launched, the functions are called in this order:
         *  onCreate() > onStart() > onResume()
         *  So, we moved all the UI uodate jobs to the onStart() function.
         *  It serves both ways, when creating this Activity for the first time and
         *  again while returning from a child activity
         */

        //creating an instance of the courses table class and getting all the values in it to display into our listview

        SQLiteHelper_Courses courses =new SQLiteHelper_Courses(getBaseContext());

        //cursor is a data structure which contains the result of a SQLite query and
        //mainly points to one row of the 2D result array.
        Cursor cursor = courses.getAllCourses();

        //setting the internal pointer to the starting row for extracting data in a for-loop
        cursor.moveToFirst();

        //this is for debuggig purpose...
        /*for(int i=0; i<cursor.getCount();i++) {
            Log.i("dev","Cursor course value["+i+"] is: "+ cursor.getString(cursor.getColumnIndex(courses.COLUMN_COURSE_NAME)));
            cursor.moveToNext();
        }*/

        //creating two strings for feeding the SimpleCursorAdapter constructor
        String [] fromFieldNames = new String[] {SQLiteHelper_Courses.COLUMN_COURSE_ID,
                SQLiteHelper_Courses.COLUMN_COURSE_NAME,
                SQLiteHelper_Courses.COLUMN_DEPARTMENT_NAME};
        int [] toViewIDs = new int[] {R.id.list_layout_id,R.id.list_layout_name,R.id.list_layout_deptName};

        //Simple cursor adapter creates an adapter containing the views which we feed to the listview as an adapter
        simpleCursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.list_layout, cursor,
                fromFieldNames,toViewIDs,0);

        //Log.i("dev","CursorAdapter Size: " + simpleCursorAdapter.getCount());

        listView.setAdapter(simpleCursorAdapter);

        //analyzing the Adapter and deciding wheather the table has any rows or not.
        // If there is no rows we wont let the remove_courses button to open up
        //another activity
        if(simpleCursorAdapter.getCount()<1)
        {//the course table has no entries
            is_course_table_empty=true;
        }
        //cursor.close();

        //these layouts are for UI updates. EmptyTable shows some statements to the user that the table is empty,
        //and the non-empty table shows the column names to the user as a free service...
        //We used boolean logic to show wheather the EmptyTable LinearLayout or the NonEmptyTableLinearLayout...
        LinearLayout EmptyTable = (LinearLayout) findViewById(R.id.ll_main2activity_courseListHeaders_EmptyTable);
        LinearLayout NonEmptyTable = (LinearLayout) findViewById(R.id.ll_main2activity_courseListHeaders);
        if(is_course_table_empty)
        {
            EmptyTable.setVisibility(View.VISIBLE);
            NonEmptyTable.setVisibility(View.INVISIBLE);
        }
        else {
            EmptyTable.setVisibility(View.GONE);
        }
    }

//Nothing important below...























    @Override
    protected void onStop() {
        super.onStop();

        Log.i("dev","MainActivity2:OnStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("dev","MainActivity2:OnDestroy");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.i("dev","MainActivity2:OnBackPressed");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i("dev","MainActivity2:OnPause");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("dev","MainActivity2:OnResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.i("dev","MainActivity2:OnRestart");
    }
}
