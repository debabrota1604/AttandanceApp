package com.example.dev.devapp_attendanceapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by DEV on 27-05-2016.
 */
public class SQLiteHelper_Courses extends SQLiteOpenHelper{
    Context context;
    public static final String DATABASE_NAME = "attendance.db";
    public static final String TABLE_NAME = "course";
    public static final String COLUMN_COURSE_ID = "_id";
    public static final String COLUMN_COURSE_NAME = "course_name";
    public static final String COLUMN_COURSE_DATE_START = "course_start_date";
    public static final String COLUMN_COURSE_DATE_END = "course_end_date";
    public static final String COLUMN_DEPARTMENT_NAME = "dept_name";
    public static final String[] allImportantColumns = {COLUMN_COURSE_ID,
            COLUMN_COURSE_NAME,
            COLUMN_DEPARTMENT_NAME };
    public static final String[] allColumns = {COLUMN_COURSE_ID,
            COLUMN_COURSE_DATE_START,
            COLUMN_COURSE_DATE_END,
            COLUMN_COURSE_NAME,
            COLUMN_DEPARTMENT_NAME };


    private static SQLiteDatabase db_pointer=null;

    private static final String DATABASE_TABLE_CREATE_COMMAND = "create table " + TABLE_NAME + "("
                                                                + COLUMN_COURSE_ID + " integer primary key, "
                                                                + COLUMN_COURSE_DATE_START + " text, "
                                                                + COLUMN_COURSE_DATE_END + " text, "
                                                                + COLUMN_COURSE_NAME + " text not null, "
                                                                + COLUMN_DEPARTMENT_NAME + ");";


    public long insertValues(int courseID,String startDate, String EndDate, String courseName, String DepartmentName) {
        open();
        //putting the user data into contentValues for importing into table
        ContentValues values = new ContentValues();
        values.put(COLUMN_COURSE_ID, courseID);
        values.put(COLUMN_COURSE_DATE_START, startDate);
        values.put(COLUMN_COURSE_DATE_END, EndDate);
        values.put(COLUMN_COURSE_NAME, courseName);
        values.put(COLUMN_DEPARTMENT_NAME, DepartmentName);

        //inserting the user data into the table
        long insertId = db_pointer.insert(TABLE_NAME, null,values);
        Log.i("dev","Value Successfully inserted with ID: "+insertId);

        //db_pointer.close();
        return insertId;
    }

    public void deleteValueWithCourseID(String courseID) {
        System.out.println("Deleting Course with id: " + courseID);
        db_pointer.delete(TABLE_NAME,COLUMN_COURSE_ID
                + " = " + courseID, null);
    }

    public Cursor getAllCourses() {
        open();
        Cursor c= db_pointer.query(TABLE_NAME,allImportantColumns,
                null, new String[] {}, null, null,null);
        //db_Courses.close();
        return c;
    }

    public Cursor getAllDetailsWithCourseID(String courseID) {
        open();
        Cursor c= db_pointer.query(TABLE_NAME,allColumns,
                COLUMN_COURSE_ID+" = " +courseID, new String[] {}, null, null,null);
        //db_Courses.close();
        return c;
    }


    public SQLiteHelper_Courses(Context context) {
        super(context, DATABASE_NAME, null,1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        File db_file_reference = context.getDatabasePath(DATABASE_NAME);
        if(!db_file_reference.exists()){
            //Log.i("dev","SQLiteHelperOnCreate: DataBase " + DATABASE_NAME + " Already Exists! Skipping database creation...");
        }
        else{
            Log.i("dev","SQLiteHelperOnCreate: No DB entry found! Creating a new one...");
            Toast.makeText(context, "Database not found! Creating a new one...", Toast.LENGTH_SHORT).show();
            database.execSQL(DATABASE_TABLE_CREATE_COMMAND);;
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
        if(db_pointer!=null){
            //Log.i("dev","Database Open: Database " + DATABASE_NAME + " Exists...");
        }
        else
            Log.i("dev","Database Open: Database "+ DATABASE_NAME + " does not exist");

        //checking wheather the table student exists or not
        try{
            //thie command below returns the table name from SQL_MASTER database
            Cursor row_name = db_pointer.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+TABLE_NAME+"';",null);
            //setting the cursor to the starting row of the result data.
            row_name.moveToFirst();
            //getString(0) returns the data at the 0th column of the result data
            String table_name_fromCursor = row_name.getString(0);
            //Log.i("dev","String returned from tableSearch: "+table_name_fromCursor);
            boolean table_exists = table_name_fromCursor.equals(TABLE_NAME);

            //if the table exists we just exit, otherwise we create the table
            if(table_exists)
            {
                //Log.i("dev","Database Open: DoesTableExist: Table " + TABLE_NAME + " exists");
            }
            else
            {
                //Log.i("dev","Database Open: DoesTableExist: Table " + TABLE_NAME + " does not exist");
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



    public static String getDBName() {
        return DATABASE_NAME;
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public void deleteTable()
    {
        open();
        db_pointer.delete(TABLE_NAME,null,null);
        Log.i("dev","Deleting table: " +TABLE_NAME);
        db_pointer.close();
    }
}
