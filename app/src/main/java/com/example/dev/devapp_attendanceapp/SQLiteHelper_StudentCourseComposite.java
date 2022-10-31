package com.example.dev.devapp_attendanceapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by DEV on 30-05-2016.
 *
 * This class stores enrollment information of each student to each course.
 * That is, the selected students are only for this particular course only.
 *
 * Internally, we used a different idea.
 * This table stores the student_id and course_id as the composite primary key.
 * This is so because we are storing all student-course data in a single table
 * and we need to care these two cases:
 *
 *      >>If we make the student_id as a primary key then we cannot include the student
 *  in a second course which is not a big restriction, but still we want the user to have
 *  this flexibility
 *
 *      >>If we make the course_id as a primary key, then a particular course cannot have
 * more than one student which is a great restriction to go with.
 *
 * Therefore the idea is to make the table having a composite primary key which prevents the
 * table from inserting redundant data. The schema for the new table would be like this:
 *
 *  course-student((concatenated cid,sid) primary key,cid,sid);
 *
 *  Here we can easily get the student_id values for a particular course_id and vice-versa.
 *  All we are wasting is an extra column for each of the rows for removing redundancies,
 *  which is not a huge disadvantage on overall structure.
 */
public class SQLiteHelper_StudentCourseComposite extends SQLiteOpenHelper {
    Context context;
    private static SQLiteDatabase db_pointer=null;
    public static final String DATABASE_NAME = "attandance.db";
    public static final String TABLE_NAME = "student_course_composite";
    public static final String COLUMN_COMPOSITE_ID = "_id";
    public static final String COLUMN_STUDENT_ID = "student_id";
    public static final String COLUMN_COURSE_ID = "course_id";
    public static final String[] allColumns = {COLUMN_COMPOSITE_ID,COLUMN_STUDENT_ID,COLUMN_COURSE_ID };
    private static final String DATABASE_TABLE_CREATE_COMMAND = "create table "
                                                                + TABLE_NAME + "("
                                                                + COLUMN_COMPOSITE_ID + " varchar primary key, "
                                                                + COLUMN_STUDENT_ID + " integer not null, "
                                                                + COLUMN_COURSE_ID + " integer not null);";


    public Cursor getAllStudentsWithCourseID(String courseID) {
        //Log.i("dev","SQLiteHelper_StudentCourseComposite: Inside getAllStudentsWithCourseID...");
        open();
        Cursor c= db_pointer.query(TABLE_NAME,new String[]{COLUMN_STUDENT_ID},
                COLUMN_COURSE_ID+" = " +courseID, new String[] {}, null, null,null);
        c.moveToFirst();
        return c;
    }


    public long insertValues(String courseID,int studentID) {
        open();

        String composite_id = courseID + Integer.toString(studentID);

        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPOSITE_ID, composite_id);
        values.put(COLUMN_COURSE_ID, courseID);
        values.put(COLUMN_STUDENT_ID, studentID);

        long insertId = db_pointer.insert(TABLE_NAME, null,values);
        Log.i("dev","Value Successfully inserted with ID: "+insertId);

        return insertId;
    }

    public void deleteValueWithStudentID(int student_id)
    {
        System.out.println("Deregistering student with id: " + student_id);
        db_pointer.delete(TABLE_NAME,COLUMN_STUDENT_ID
                + " = " + student_id, null);
    }

    public void deleteValueWithStudentID(String studentID) {
        System.out.println("Deleting Student with id: " + studentID);
        db_pointer.delete(TABLE_NAME,COLUMN_STUDENT_ID
                + " = " + studentID, null);
    }

    public void deleteValueWithCourseID(String courseID) {
        System.out.println("Deleting Student with id: " + courseID);
        db_pointer.delete(TABLE_NAME,COLUMN_STUDENT_ID
                + " = " + courseID, null);
    }


    public SQLiteHelper_StudentCourseComposite(Context context) {
        super(context, DATABASE_NAME, null,1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        File db_file_reference = context.getDatabasePath(DATABASE_NAME);
        if(!db_file_reference.exists()){
           // Log.i("dev","SQLiteHelperOnCreate: DataBase " + DATABASE_NAME + " Already Exists!");
        }
        else{
            Log.i("dev","SQLiteHelperOnCreate: No SC-Linker DB entry found! Creating a new one...");
            Toast.makeText(context, "Database not found! Creating a new one...", Toast.LENGTH_SHORT).show();
            database.execSQL(DATABASE_TABLE_CREATE_COMMAND);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w("dev",
                "SQLiteHelperOnUpgrade: Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }


    public void open() throws SQLException {
        //checking wheather the database exists or not
        db_pointer = getReadableDatabase();
        if(db_pointer!=null) {
            //Log.i("dev", "Database Open: Database " + DATABASE_NAME + " Exists...");
        }
        else
            Log.i("dev","Database Open: Database " + DATABASE_NAME + " does not exist");

        //checking wheather the table student_course_composite exists or not
        try{
            //thie command below returns the table name from SQL_MASTER database
            Cursor row_name = db_pointer.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+TABLE_NAME+"';",null);
            //setting the cursor to the starting row of the result data.
            row_name.moveToFirst();
            //getString(0) returns the data at the 0th column of the result data
            String table_name_fromCursor = row_name.getString(0);
            //Log.i("dev","String returned from tableSearch: "+table_name_fromCursor);
            boolean table_exists = table_name_fromCursor.equals(TABLE_NAME);

            if(table_exists)
            {
                //Log.i("dev","Database Open: DoesTableExist: Table "+ TABLE_NAME + " exists");
            }
            else
            {
                Log.i("dev","Database Open: DoesTableExist: Table "+ TABLE_NAME + " does not exist");
                db_pointer.execSQL(DATABASE_TABLE_CREATE_COMMAND);
                Log.i("dev","Database Open: DoesTableExist: New Table created for "+TABLE_NAME);
            }
        }
        catch (Exception e)
        {
            Log.i("dev","Database Open: DoesTableExist: Error! Check exception");
            db_pointer.execSQL(DATABASE_TABLE_CREATE_COMMAND);
            Log.i("dev","Database Open: DoesTableExist: New Table created for "+TABLE_NAME);
        }
    }

    public void deleteTable()
    {
        open();
        db_pointer.delete(TABLE_NAME,null,null);
        Log.i("dev","Deleting table: " +TABLE_NAME);
        db_pointer.close();
    }

}
