package com.example.dev.devapp_attendanceapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class ActivityAttandanceTaking extends AppCompatActivity {
    Context context;
    String courseID;
    HashMap hashMap;//hashMap is for tracking student attandance
    ListView listView;
    Calendar calendar;
    String []id_array;
    int year,month,day,hour,minute,second;
    Cursor cursor_StudentID_only;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attandance_taking);
        Log.i("dev","OnCreate: Activity Attandance...");

        context = getApplicationContext();
        hashMap = new HashMap();
        Intent intent = getIntent();
        courseID = intent.getStringExtra(SQLiteHelper_Courses.COLUMN_COURSE_ID);
        //Log.i("dev","Received Course ID: "+courseID);

        /**
         * We have used hashmap instead of a boolean array for tracking the attandance because
         * we need to track the attandance for a student by checking his studentID, there
         * is no scope for simple index checking. Therefore we need key value pairs to track it,
         * where key is the studentID, and the value is either 0 or 1.
         */

        listView = (ListView) findViewById(R.id.lv_TakeAttendance);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp_studentID = ((TextView) view.findViewById(R.id.attandance_layout_id)).getText().toString();

                if(hashMap.get(temp_studentID).equals("0"))
                {
                    hashMap.remove(temp_studentID);
                    hashMap.put(temp_studentID,"1");
                    ((LinearLayout) view.findViewById(R.id.ll_attandance_layout)).setBackgroundColor(Color.parseColor("#7CB342"));
                }
                else
                {
                    hashMap.remove(temp_studentID);
                    hashMap.put(temp_studentID,"0");
                    ((LinearLayout) view.findViewById(R.id.ll_attandance_layout)).setBackgroundColor(Color.parseColor("#F4511E"));
                }

                //Log.i("dev","listview Clicked at position: "+position);
                //Log.i("dev","attandance At position: "+position + " is: "+ hashMap.get(temp_studentID));
            }
        });

        Button submit_attandance = (Button) findViewById(R.id.but_takeAttandance_submit);
        submit_attandance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("dev","Submitting Attandance...");
                calendar = Calendar.getInstance();
                String dateString = DateFormatter(), timeString = TimeFormatter();
                String separator = "|";

                ContentValues contentValues = new ContentValues();
                String uniqueValue = courseID + separator + dateString + separator + timeString;
                Log.i("dev","Generated Composite Key: "+uniqueValue);

                contentValues.put(SQLiteHelper_AttandanceRegister.COLUMN_ATTANDANCE_PRIMARY_KEY,uniqueValue);
                contentValues.put(SQLiteHelper_AttandanceRegister.COLUMN_COURSE_ID,courseID);
                contentValues.put(SQLiteHelper_AttandanceRegister.COLUMN_DATE,dateString);
                contentValues.put(SQLiteHelper_AttandanceRegister.COLUMN_TIME,timeString);

                int counter=0;
                for(int i=0;i<cursor_StudentID_only.getCount();i++)
                {
                    String tempID = id_array[i];
                    String tempValue=  hashMap.get(tempID).toString();
                    cursor_StudentID_only.moveToNext();

                    if(tempValue.equals("1"))
                    {
                        counter++;
                        contentValues.put("'" + tempID + "'",tempValue);
                        /*Log.i("dev",i + " ID_Value pair: " + tempID + " ~~~ " + tempValue);
                        Log.i("dev",contentValues.getAsString("'" + tempID + "'"));*/
                    }
                }

                SQLiteHelper_AttandanceRegister register = new SQLiteHelper_AttandanceRegister(context);
                long value = register.insertValues(contentValues);
                if(value>=0)
                {
                    Toast.makeText(ActivityAttandanceTaking.this, "Marked "+ counter + " students as present...", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    Toast.makeText(ActivityAttandanceTaking.this, "Error! Return value is "+ value, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        final SQLiteHelper_StudentCourseComposite composite =new SQLiteHelper_StudentCourseComposite(this);
        cursor_StudentID_only = composite.getAllStudentsWithCourseID(courseID);
        cursor_StudentID_only.moveToFirst();

        id_array = new String[cursor_StudentID_only.getCount()];
        String id_string="";
        id_string+="( ";
        int i;
        for(i=0; i<cursor_StudentID_only.getCount()-1;i++) {
            /*Log.i("dev","Cursor course value["+i+"] is: "+
                    cursor_StudentID_only.getString(0));*/
            id_string += cursor_StudentID_only.getString(0);
            id_array[i] = cursor_StudentID_only.getString(0);
            cursor_StudentID_only.moveToNext();
            id_string +=" , ";
        }

        id_string += cursor_StudentID_only.getString(0);
        id_array[i] = cursor_StudentID_only.getString(0);

        id_string +=" );";

        Log.i("dev","ID array: "+id_string);

        /**
         * student IDs are saved in ID_array. It will be used to get the ful details about a student
         */

        final SQLiteHelper_Students students = new SQLiteHelper_Students(this);
        Cursor cursor_StudentFullDetails = students.getAllStudentsLimitedInfoWithStudentID(id_string);

        //Log.i("dev","cursor_StudentFullDetails Size: " + cursor_StudentFullDetails.getCount());

        /**
         * we get the details saved in cursor_StudentFullDetails. Using
         * SimpleCursorAdapter to set the adapter for viewing in listView
         */

        String [] fromFieldNames = new String[] {SQLiteHelper_Students.COLUMN_STUDENT_ID,
                SQLiteHelper_Students.COLUMN_STUDENT_NAME};
        int [] toViewIDs = new int[] {R.id.attandance_layout_id,R.id.attandance_layout_name};

        SimpleCursorAdapter simpleCursorAdapter  = new SimpleCursorAdapter(getApplicationContext(), R.layout.listview_attandance_layout, cursor_StudentFullDetails,
                fromFieldNames,toViewIDs,0);

        //Log.i("dev","CursorAdapter Size: " + simpleCursorAdapter.getCount());
        listView.setAdapter(simpleCursorAdapter);

        //populating hashMap
        cursor_StudentID_only.moveToFirst();
        for(i=0;i<cursor_StudentID_only.getCount();i++)
        {
            hashMap.put(cursor_StudentID_only.getString(0),"0");
            cursor_StudentID_only.moveToNext();
        }

    }

    private String DateFormatter() {

        //getting system values to initially start with
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        String separator = "/";
        String dateString = Integer.toString(day) + separator + Integer.toString(month) + separator + Integer.toString(year);

        //Log.i("dev","Date: "+dateString);
        return dateString;
    }

    private String TimeFormatter() {

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);

        String separator = ":";
        String timeString = Integer.toString(hour) + separator + Integer.toString(minute);

        //Log.i("dev","Time: "+timeString);
        return timeString;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //clearing hashmap so that no entry remains...
        hashMap.clear();
    }
}
