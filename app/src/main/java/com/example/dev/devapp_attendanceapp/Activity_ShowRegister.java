package com.example.dev.devapp_attendanceapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_ShowRegister extends AppCompatActivity {

    Intent intent;
    String courseID,courseName;
    TableLayout tableLayout;
    Cursor cursorRegister;
    boolean is_student_table_empty=false;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_register);

        intent=getIntent();
        context = getApplicationContext();
        //setting up some static informations in the Textview from the intent extras...
        courseID = intent.getStringExtra(SQLiteHelper_Courses.COLUMN_COURSE_ID);//default return value is 0
        Log.i("dev","RegisterActivity: Received Course ID: "+courseID);

        courseName = intent.getStringExtra(SQLiteHelper_Courses.COLUMN_COURSE_NAME);

        TextView tvCourseName = (TextView) findViewById(R.id.tv_register_courseName);
        tvCourseName.setText(courseName);

        TextView tvCourseID = (TextView) findViewById(R.id.tv_register_courseDetails);
        tvCourseID.setText(intent.getStringExtra(SQLiteHelper_Courses.COLUMN_COURSE_ID));

        TextView tvCourseStartDate = (TextView) findViewById(R.id.tv_register_startDate);
        tvCourseStartDate.setText(intent.getStringExtra(SQLiteHelper_Courses.COLUMN_COURSE_DATE_START));

        TextView tvCourseEndDate = (TextView) findViewById(R.id.tv_register_endDate);
        tvCourseEndDate.setText(intent.getStringExtra(SQLiteHelper_Courses.COLUMN_COURSE_DATE_END));

        TextView tvCourseDepartmentName = (TextView) findViewById(R.id.tv_register_courseDepartment);
        tvCourseDepartmentName.setText(intent.getStringExtra(SQLiteHelper_Courses.COLUMN_DEPARTMENT_NAME));

    }

    @Override
    protected void onStart() {
        super.onStart();

        //get the student IDs to format output

        //getting the student IDs for a particular course ID from Course_student table
        SQLiteHelper_StudentCourseComposite studentsInEachCourse =
                new SQLiteHelper_StudentCourseComposite(this);

        //Log.i("dev","Received CourseID: "+Integer.toString(courseID));

        //retrieve the students for the particular course_id
        Cursor cursor_StudentID_only = studentsInEachCourse.getAllStudentsWithCourseID(courseID);
        if(cursor_StudentID_only.getCount()==0)
        {
            is_student_table_empty=true;
            Log.i("dev","No student registered for this course.");
            return;
        }

        /**
         * we retrieve the cursor containing the student ids for a particular course.
         * Now parsing it to run another query on the Student database to get all infos
         * about a student with a particular student ID
         */

        String id_array= "";

        for(int i=0; i<cursor_StudentID_only.getCount();i++) {
            id_array += ", \"";
            id_array += cursor_StudentID_only.getString(0);
            id_array +="\" ";
            cursor_StudentID_only.moveToNext();
        }
        Log.i("dev","All Student ID array: " + id_array);

        /**
         * student IDs are saved in ID_array. It will be used to get the ful details about a student
         */

        SQLiteHelper_AttandanceRegister register = new SQLiteHelper_AttandanceRegister(this);
        cursorRegister = register.getDatabyCourseID(id_array,courseID);
        int column_count = cursorRegister.getColumnCount();
        int row_count = cursorRegister.getCount();

        tableLayout = (TableLayout) findViewById(R.id.tl_registerLayout);

        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        //this db is used for using the getStudentNameByID function below...
        SQLiteHelper_Students students = new SQLiteHelper_Students(this);

        cursor_StudentID_only.moveToFirst();
        for(int j=0;j<cursor_StudentID_only.getCount()+2;j++){
            if(j==0){
                TextView textView = new TextView(getBaseContext());
                textView.setTextColor(Color.parseColor("#E1BEE7"));
                textView.setPadding(5,5,5,5);
                textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                textView.setText("Date");

                tableRow.addView(textView);
            }
            else if(j==1){
                TextView textView = new TextView(getBaseContext());
                textView.setTextColor(Color.parseColor("#E1BEE7"));
                textView.setPadding(5,5,5,5);
                textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                textView.setText("Time");

                tableRow.addView(textView);
            }
            else{
                TextView textView = new TextView(getBaseContext());
                textView.setText(students.getStudentNameByID(cursor_StudentID_only.getString(0)));
                textView.setTextColor(Color.parseColor("#E1BEE7"));
                textView.setPadding(5,5,5,5);
                textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                cursor_StudentID_only.moveToNext();
                tableRow.addView(textView);
            }
        }
        tableLayout.addView(tableRow);

        //tableLayout.setBackgroundColor(Color.parseColor("#6D4C41"));

        /*Log.i("dev","Column_size: " + column_count);
        Log.i("dev","Row_size: " + row_count);*/
        cursorRegister.moveToFirst();

        for(int j=0;j<cursorRegister.getCount();j++)
        {
            TableRow newTableRow = new TableRow(this);
            newTableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            for(int i=0;i<column_count;i++)
            {
                //Log.i("dev","Outside Data[" + j + "]["+ i + "]: "+cursorRegister.getString(i));
                TextView textView = new TextView(this);
                    textView.setPadding(5,5,5,5);
                    textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.MATCH_PARENT));
                if(i>1)
                {
                    if (cursorRegister.getString(i) == null){
                        textView.setText("Absent");
                        textView.setTextColor(Color.parseColor("#FF0000"));
                    }
                    else if(cursorRegister.getString(i).equals("1"))
                    {
                        textView.setText("Present");
                        textView.setTextColor(Color.parseColor("#0000FF"));
                    }
                    else {
                        textView.setText("Absent");
                        textView.setTextColor(Color.parseColor("#FF0000"));
                    }
                }
                else {
                    textView.setText(cursorRegister.getString(i));
                }
                newTableRow.addView(textView);
            }
            if(j!=cursorRegister.getCount()-1)
            {
                cursorRegister.moveToNext();
            }
            tableLayout.addView(newTableRow);
        }
    }
}
