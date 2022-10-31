package com.example.dev.devapp_attendanceapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_deregister_student extends AppCompatActivity {
    String courseID;
    String courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deregisterl_student);
        Log.i("dev","OnCreate: Activity Deregister Student...");

        Intent intent = getIntent();
        courseID = intent.getStringExtra(SQLiteHelper_Courses.COLUMN_COURSE_ID);
        Log.i("dev","Received Course ID: "+courseID);

        Button button_remove_student_from_database = (Button) findViewById(R.id.but_deleteStudentFromDB);
        button_remove_student_from_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Activity_deregister_student.this,Activity_RemoveStudent_DeleteEntryFromDatabase.class);
                startActivity(intent1);
            }
        });


        final ListView listView = (ListView) findViewById(R.id.lv_deregister_student);

        final SQLiteHelper_StudentCourseComposite composite =new SQLiteHelper_StudentCourseComposite(this);
        final Cursor cursor_StudentID_only = composite.getAllStudentsWithCourseID(courseID);
        cursor_StudentID_only.moveToFirst();

        String id_array="";
        id_array+="( ";

        for(int i=0; i<cursor_StudentID_only.getCount()-1;i++) {
            /*Log.i("dev","Cursor course value["+i+"] is: "+
                    cursor_StudentID_only.getString(0));*/
            id_array += cursor_StudentID_only.getString(0);
            cursor_StudentID_only.moveToNext();
            id_array +=" , ";
        }

        id_array += cursor_StudentID_only.getString(0);

        id_array +=" );";

        Log.i("dev","ID array: "+id_array);

        /**
         * student IDs are saved in ID_array. It will be used to get the ful details about a student
         */

        final SQLiteHelper_Students students = new SQLiteHelper_Students(this);
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

        SimpleCursorAdapter simpleCursorAdapter  = new SimpleCursorAdapter(getApplicationContext(), R.layout.list_layout, cursor_StudentFullDetails,
                fromFieldNames,toViewIDs,0);

        //Log.i("dev","CursorAdapter Size: " + simpleCursorAdapter.getCount());
        listView.setAdapter(simpleCursorAdapter);

        final String idArray = id_array;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                String studentName = ((TextView) view.findViewById(R.id.list_layout_name)).getText().toString();
                final String studentID = ((TextView) view.findViewById(R.id.list_layout_id)).getText().toString();

                Log.i("dev", "User clicked: " + ((TextView) view.findViewById(R.id.list_layout_name)).getText().toString());

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_deregister_student.this);
                builder.setMessage("Sure to delete "+studentName+ " ?").setTitle("Warning !!!");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Activity_deregister_student.this, "No change is done !", Toast.LENGTH_SHORT).show();
                        //Log.i("dev", "id is: " + id + " position is: " + position);
                    }
                });

                builder.setNeutralButton("Ignore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Activity_deregister_student.this, "No change is done !", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("dev", "id is: " + id + " position is: " + position);

                        composite.deleteValueWithStudentID(studentID);

                        Log.i("dev","Value deleted...");
                        Log.i("dev","RePopulating Listview...");

                        final Cursor cursor_StudentID_only = composite.getAllStudentsWithCourseID(courseID);
                        if(cursor_StudentID_only.getCount() == 0 )
                        {
                            Log.i("dev","Cursor is empty...");
                            Toast.makeText(Activity_deregister_student.this, "All students removed! ", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(Activity_deregister_student.this,MainActivity_3_individual_courses.class);

                            finishAndRemoveTask();
                            /*intent1.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_ID,courseID);
                            intent1.putExtra(SQLiteHelper_Courses.COLUMN_COURSE_NAME,courseName);

                            //starting individual course activity
                            startActivity(intent1);*/
                        }
                        else {
                            cursor_StudentID_only.moveToFirst();
                            Log.i("dev", "Cursor size: " + cursor_StudentID_only.getCount());

                            String id_array = "";
                            id_array += "( ";

                            for (int i = 0; i < cursor_StudentID_only.getCount() - 1; i++) {
                                Log.i("dev", "Cursor course value[" + i + "] is: " +
                                        cursor_StudentID_only.getString(0));

                                id_array += cursor_StudentID_only.getString(0);
                                cursor_StudentID_only.moveToNext();
                                id_array += " , ";
                            }

                            id_array += cursor_StudentID_only.getString(0);

                            id_array += " );";

                            Log.i("dev", "ID array: " + id_array);

                            /**
                             * student IDs are saved in ID_array. It will be used to get the ful details about a student
                             */

                            Cursor cursor_StudentFullDetails = students.getAllStudentsWithStudentID(id_array);

                            Log.i("dev", "cursor_StudentFullDetails Size: " + cursor_StudentFullDetails.getCount());

                            /**
                             * we get the details saved in cursor_StudentFullDetails. Using
                             * SimpleCursorAdapter to set the adapter for viewing in listView
                             */

                            String[] fromFieldNames = new String[]{SQLiteHelper_Students.COLUMN_STUDENT_ID,
                                    SQLiteHelper_Students.COLUMN_STUDENT_NAME,
                                    SQLiteHelper_Students.COLUMN_STUDENT_AGE};
                            int[] toViewIDs = new int[]{R.id.list_layout_id, R.id.list_layout_name, R.id.list_layout_deptName};

                            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.list_layout, cursor_StudentFullDetails,
                                    fromFieldNames, toViewIDs, 0);

                            Log.i("dev", "CursorAdapter Size: " + simpleCursorAdapter.getCount());
                            listView.setAdapter(simpleCursorAdapter);

                            Log.i("dev", "Listview Updated...");
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
}
