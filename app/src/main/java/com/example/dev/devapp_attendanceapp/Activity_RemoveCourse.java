package com.example.dev.devapp_attendanceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_RemoveCourse extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_course);
        context = getBaseContext();
        getIntent();

        final ListView listView = (ListView) findViewById(R.id.lv_removeCourse);

        final SQLiteHelper_Courses courses =new SQLiteHelper_Courses(getBaseContext());
        Cursor cursor = courses.getAllCourses();
        cursor.moveToFirst();

        for(int i=0; i<cursor.getCount();i++) {
            Log.i("dev","Cursor course value["+i+"] is: "+ cursor.getString(cursor.getColumnIndex(courses.COLUMN_COURSE_NAME)));
            cursor.moveToNext();
        }

        String [] fromFieldNames = new String[] {SQLiteHelper_Courses.COLUMN_COURSE_ID,
                SQLiteHelper_Courses.COLUMN_COURSE_NAME,
                SQLiteHelper_Courses.COLUMN_DEPARTMENT_NAME};
        int [] toViewIDs = new int[] {R.id.list_layout_id,R.id.list_layout_name,R.id.list_layout_deptName};

        SimpleCursorAdapter simpleCursorAdapter  = new SimpleCursorAdapter(getApplicationContext(), R.layout.list_layout, cursor,
                fromFieldNames,toViewIDs,0);

        Log.i("dev","CursorAdapter Size: " + simpleCursorAdapter.getCount());
        listView.setAdapter(simpleCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                String courseName = ((TextView) view.findViewById(R.id.list_layout_name)).getText().toString();
                final String courseID = ((TextView) view.findViewById(R.id.list_layout_id)).getText().toString();
                Log.i("dev", "User clicked: " + ((TextView) view.findViewById(R.id.list_layout_name)).getText().toString());

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_RemoveCourse.this);

                builder.setMessage("Sure to delete "+courseName+ " ?").setTitle("Warning !!!");

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Activity_RemoveCourse.this, "No change is done !", Toast.LENGTH_SHORT).show();
                        Log.i("dev", "id is: " + id + " position is: " + position);
                    }
                });

                builder.setNeutralButton("Ignore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Activity_RemoveCourse.this, "No change is done !", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("dev", "id is: " + id + " position is: " + position);

                        //removing course entry from composite database
                        SQLiteHelper_StudentCourseComposite composite = new SQLiteHelper_StudentCourseComposite(context);
                        composite.deleteValueWithCourseID(courseID);

                        //removing entry from courses database
                        courses.deleteValueWithCourseID(courseID);

                        String [] fromFieldNames = new String[] {SQLiteHelper_Courses.COLUMN_COURSE_ID,
                                SQLiteHelper_Courses.COLUMN_COURSE_NAME,
                                SQLiteHelper_Courses.COLUMN_DEPARTMENT_NAME};
                        int [] toViewIDs = new int[] {R.id.list_layout_id,R.id.list_layout_name,R.id.list_layout_deptName};

                        SimpleCursorAdapter simpleCursorAdapter  = new SimpleCursorAdapter(getApplicationContext(), R.layout.list_layout,
                                courses.getAllCourses(),fromFieldNames,toViewIDs,0);

                        Log.i("dev","CursorAdapter Size: " + simpleCursorAdapter.getCount());
                        listView.setAdapter(simpleCursorAdapter);
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
}
