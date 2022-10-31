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

public class Activity_RemoveStudent_DeleteEntryFromDatabase extends AppCompatActivity {
    ListView listView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_student_delete_entry_from_database);
        context = getBaseContext();
        getIntent();

        listView= (ListView) findViewById(R.id.lv_removeStudent);

        final SQLiteHelper_Students students =new SQLiteHelper_Students(context);
        final Cursor cursor = students.getAllStudentsAllAtributes();
        cursor.moveToFirst();

        String [] fromFieldNames = new String[] {SQLiteHelper_Students.COLUMN_STUDENT_ID,
                SQLiteHelper_Students.COLUMN_STUDENT_NAME,SQLiteHelper_Students.COLUMN_STUDENT_AGE};
        int [] toViewIDs = new int[] {R.id.list_layout_id,R.id.list_layout_name,R.id.list_layout_deptName};

        SimpleCursorAdapter simpleCursorAdapter  = new SimpleCursorAdapter(getApplicationContext(), R.layout.list_layout, cursor,
                fromFieldNames,toViewIDs,0);

        Log.i("dev","CursorAdapter Size: " + simpleCursorAdapter.getCount());
        listView.setAdapter(simpleCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                String studentName = ((TextView) view.findViewById(R.id.list_layout_name)).getText().toString();
                final String studentID = ((TextView) view.findViewById(R.id.list_layout_id)).getText().toString();
                Log.i("dev", "User clicked: " + ((TextView) view.findViewById(R.id.list_layout_name)).getText().toString());

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_RemoveStudent_DeleteEntryFromDatabase.this);

                builder.setMessage("Sure to delete "+studentName+ " ?").setTitle("Warning !!!");

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Activity_RemoveStudent_DeleteEntryFromDatabase.this, "No change is done !", Toast.LENGTH_SHORT).show();
                        Log.i("dev", "id is: " + id + " position is: " + position);
                    }
                });

                builder.setNeutralButton("Ignore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Activity_RemoveStudent_DeleteEntryFromDatabase.this, "No change is done !", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("dev", "id is: " + id + " position is: " + position);

                        //removing student entry from student database
                        students.deleteEithStudentID(Integer.parseInt(studentID));

                        //removing student entry from composite database
                        SQLiteHelper_StudentCourseComposite composite = new SQLiteHelper_StudentCourseComposite(context);
                        composite.deleteValueWithStudentID(studentID);

                        //removing student entry from register database
                        SQLiteHelper_AttandanceRegister register= new SQLiteHelper_AttandanceRegister(context);
                        register.AlterTable_UpdateForStudentRemoval();

                        Cursor cursor1 = students.getAllStudentsAllAtributes();
                        cursor1.moveToFirst();

                        String [] fromFieldNames = new String[] {SQLiteHelper_Students.COLUMN_STUDENT_ID,
                                SQLiteHelper_Students.COLUMN_STUDENT_NAME,SQLiteHelper_Students.COLUMN_STUDENT_AGE};
                        int [] toViewIDs = new int[] {R.id.list_layout_id,R.id.list_layout_name,R.id.list_layout_deptName};

                        SimpleCursorAdapter simpleCursorAdapter  = new SimpleCursorAdapter(getApplicationContext(), R.layout.list_layout, cursor1,
                                fromFieldNames,toViewIDs,0);

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