package com.example.dev.devapp_attendanceapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by DEV on 03-06-2016.
 */
public class SQLiteHelper_AttandanceRegister extends SQLiteOpenHelper {
    Context context;
    public static final String DATABASE_NAME = "attendance.db";
    public static final String TABLE_NAME = "attandance_register";
    public static final String COLUMN_ATTANDANCE_PRIMARY_KEY = "_id";
    public static final String COLUMN_COURSE_ID = "course_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";

    private static SQLiteDatabase db_pointer=null;

    /**
     * The idea is to divide the create table command into two or three strings
     * among which some will be dynamically calculated at runtime
     *
     * This table schema is:
     *  attandance_register(composite_id, courseID, date, time, {studentID} );
     *
     *  Thus for creation we need to take care of the student IDs which will be
     *  individual columns with boolean datatype.
     *
     *  Once created this table columns will only be modified when a particular
     *  student is deleted or inserted into the database.
     *
     *  SO, we need to provide such a function to do the table updation when a
     *  student is deleted from the database.
     */


    private static final String DATABASE_TABLE_CREATE_COMMAND_STARTING = "create table " + TABLE_NAME
                                                                + "(" + COLUMN_ATTANDANCE_PRIMARY_KEY + " VARCHAR primary key, "
                                                                + COLUMN_COURSE_ID + " text not null, "
                                                                + COLUMN_DATE + " text not null, "
                                                                + COLUMN_TIME + " text not null ";

    private static final  String DATABASE_TABLE_CREATE_COMMAND_ENDING = " );";

    private static  String DATABASE_TABLE_CREATE_COMMAND_MIDDLE = "";

    public void AlterTable_AddStudent(String studentID)
    {
        open();
        Log.i("dev","Altering table " + TABLE_NAME + " for new Student entry with ID: "+studentID);
        try{
            db_pointer.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN '" + studentID + "' text;");
        }
        catch (SQLiteException e)
        {
            Log.i("dev","SQLite exception occurred...");
        }
        db_pointer.close();
    }
    public void AlterTable_UpdateForStudentRemoval()
    {
        open();
        Log.i("dev","Altering table " + TABLE_NAME + " for Student deletion...");
        /**
         * This function operates upon a student deletion from student database
         * First the student database is updated and so when we cerate a new table after renaming the old,
         * the student IDs we supply is the current IDs remaining in the database.
         */
        db_pointer.execSQL("ALTER TABLE " + TABLE_NAME + " RENAME TO " + " OLD"+TABLE_NAME + " ;");
        Log.i("dev","Table renamed as old...");

        db_pointer.execSQL(DATABASE_TABLE_CREATE_COMMAND_STARTING + getMiddleCommand() + DATABASE_TABLE_CREATE_COMMAND_ENDING);

        Log.i("dev","Another new table created...");

        db_pointer.execSQL("INSERT into "+ TABLE_NAME + " values ( "+ getCurrentStudentIDs() + " ) " +
                " SELECT "+ getCurrentStudentIDs() + " FROM OLD"+TABLE_NAME + " );");

        Log.i("dev","Data Transfer completed from old table to new Table...");

        db_pointer.delete("OLD"+TABLE_NAME, null,null);

        Log.i("dev","Old table deleted...");
        db_pointer.close();
    }

    public long insertValues(ContentValues contentValues) {
        /*open();
        delete_table();*/
        open();

        try{
            long insertId = db_pointer.insert(TABLE_NAME, null,contentValues);
            Log.i("dev","Value Successfully inserted with ID: "+insertId);

            db_pointer.close();
            return insertId;
        }
        catch (SQLiteException e)
        {
            Log.i("dev","Exception occured!");
            db_pointer.close();
            return -1;
        }
    }

    public Cursor getDatabyCourseID(String columnNames, String courseID)
    {
        open();
        Log.i("dev","In GetDataByCourseID...");
        String separator = " , ";
        String initial_cols = COLUMN_DATE + separator + COLUMN_TIME;

        //Log.i("dev","Table Columns: " + SeeSchema());

        //the columns to display are provided by the calling function itself.
        //Therefore we don't use the seeSchema function.
        Log.i("dev","Query: " + "SELECT " + initial_cols + columnNames + " FROM " + TABLE_NAME
                + " WHERE " + COLUMN_COURSE_ID + " = "+ courseID +" ;");

        return  db_pointer.rawQuery("SELECT " + initial_cols + columnNames + " FROM " + TABLE_NAME
                                    + " WHERE " + COLUMN_COURSE_ID + " = "+ courseID +" ;",null);
    }

    public String SeeSchema()
    {
        open();
        Log.i("dev","see schema....");
        String result= "";
        Cursor row_name = db_pointer.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+TABLE_NAME+"';",null);
        //setting the cursor to the starting row of the result data.
        row_name.moveToFirst();
        //getString(0) returns the data at the 0th column of the result data
        String table_name_fromCursor = row_name.getString(0);

        Cursor table_details = db_pointer.rawQuery("Select * from " + table_name_fromCursor + " ;",null);

        String []s =table_details.getColumnNames();

        for(int i=0;i<table_details.getCount();i++)
        {
            //Log.i("dev","Table column["+i+"]: "+s[i]);
            result += s[i];
            result += " , ";
        }
        //Log.i("dev","Schema Value: "+result);

        return result;
    }

    public void open() throws SQLException {
        //checking wheather the database exists or not

        db_pointer = getReadableDatabase();
        if(db_pointer!=null){
            Log.i("dev","Database Open: Database " + DATABASE_NAME + " Exists...");
        }
        else
            Log.i("dev","Database Open: Database "+ DATABASE_NAME + " does not exist");

        //checking wheather the table student exists or not
        try{
            //this command below returns the table name from SQL_MASTER database
            Cursor row_name = db_pointer.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+TABLE_NAME+"';",null);
            //setting the cursor to the starting row of the result data.
            row_name.moveToFirst();
            //getString(0) returns the data at the 0th column of the result data
            String table_name_fromCursor = row_name.getString(0);
            Log.i("dev","String returned from tableSearch: "+table_name_fromCursor);
            boolean table_exists = table_name_fromCursor.equals(TABLE_NAME);

            //if the table exists we just exit, otherwise we create the table
            if(table_exists)
            {
                Log.i("dev","Database Open: DoesTableExist: Table " + TABLE_NAME + " exists");
            }
            else
            {
                Log.i("dev","Database Open: DoesTableExist: Table " + TABLE_NAME + " does not exist");
                db_pointer.execSQL(DATABASE_TABLE_CREATE_COMMAND_STARTING
                +getMiddleCommand()
                +DATABASE_TABLE_CREATE_COMMAND_ENDING);
                Log.i("dev","Database Open: DoesTableExist: New Table created for "+TABLE_NAME);
            }
        }
        catch (Exception e)
        {   Log.i("dev","DB Open: Create command: " +DATABASE_TABLE_CREATE_COMMAND_STARTING
                + getMiddleCommand()
                + DATABASE_TABLE_CREATE_COMMAND_ENDING);

            Log.i("dev","Database Open: DoesTableExist: Error! Check exception");
            db_pointer.execSQL(DATABASE_TABLE_CREATE_COMMAND_STARTING
                    +getMiddleCommand()
                    +DATABASE_TABLE_CREATE_COMMAND_ENDING);
            Log.i("dev","Database Open: DoesTableExist: New Table created for "+TABLE_NAME);
        }
    }

    private String getMiddleCommand()
    {
        SQLiteHelper_Students students = new SQLiteHelper_Students(context);
        Cursor cursor = students.getAllStudents();
        cursor.moveToFirst();

        String return_string="";
        for(int i=0;i<cursor.getCount();i++)
        {
            return_string += ", '";
            return_string += cursor.getString(0);
            cursor.moveToNext();

            //default value sets the value to 0 if no value is provided for that column
            return_string += "' varchar default \"0\" ";
        }

        //Log.i("dev","getMiddle: Middle String: "+return_string);

        return return_string;
    }

    private String getCurrentStudentIDs()
    {
        SQLiteHelper_Students students = new SQLiteHelper_Students(context);
        Cursor cursor = students.getAllStudents();
        cursor.moveToFirst();

        String return_string="";
        for(int i=0;i<cursor.getCount()-1;i++)
        {
            return_string+=" '";
            return_string += cursor.getString(0);
            cursor.moveToNext();
            return_string += "' , ";
        }

        return_string += cursor.getString(0);
        return_string += "' ";

        return return_string;
    }

    public SQLiteHelper_AttandanceRegister(Context context) {
        super(context, DATABASE_NAME, null,1);
        this.context = context;
    }

    public SQLiteHelper_AttandanceRegister(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteHelper_AttandanceRegister(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("dev",
                "SQLiteHelperOnUpgrade: Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        File db_file_reference = context.getDatabasePath(DATABASE_NAME);
        if(!db_file_reference.exists()){
            // Log.i("dev","SQLiteHelperOnCreate: DataBase " + DATABASE_NAME + " Already Exists!");
        }
        else{
            Log.i("dev","SQLiteHelperOnCreate: No SC-Linker DB entry found! Creating a new one...");

            //DATABASE_TABLE_CREATE_COMMAND_MIDDLE = getMiddleCommand();

            Toast.makeText(context, "Database not found! Creating a new one...", Toast.LENGTH_SHORT).show();
            db.execSQL(DATABASE_TABLE_CREATE_COMMAND_STARTING
                    + getMiddleCommand()
                    + DATABASE_TABLE_CREATE_COMMAND_ENDING);

            Log.i("dev","Create command: " +DATABASE_TABLE_CREATE_COMMAND_STARTING
                    + getMiddleCommand()
                    + DATABASE_TABLE_CREATE_COMMAND_ENDING);
        }
    }

    private void delete_table()
    {
        db_pointer.delete(TABLE_NAME,null,null);
        Log.i("dev","Table deleted!");
    }

    public void deleteTable()
    {
        open();
        db_pointer.delete(TABLE_NAME,null,null);
        Log.i("dev","Deleting table: " +TABLE_NAME);
        db_pointer.close();
    }

    public boolean is_table_empty()
    {
        open();
        Cursor cursor  = db_pointer.rawQuery("Select * from " + TABLE_NAME + "; " , null);

        if (cursor.getCount()==0) {
            db_pointer.close();
            return true;
        }
        else
        {
            db_pointer.close();
            return false;
        }
    }
}
