package com.example.dev.devapp_attendanceapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by DEV on 28-05-2016.
 */
public class SQLiteHelper_Students extends SQLiteOpenHelper {
    Context context;
    public static final String DATABASE_NAME = "attandance.db";
    public static final String TABLE_NAME = "student";
    public static final String COLUMN_STUDENT_ID = "_id";
    public static final String COLUMN_STUDENT_NAME = "student_name";
    public static final String COLUMN_STUDENT_AGE = "student_age";
    public static final String[] allColumns = {COLUMN_STUDENT_ID,
            COLUMN_STUDENT_NAME,
            COLUMN_STUDENT_AGE };

    private static SQLiteDatabase db_pointer=null;

    private static final String DATABASE_TABLE_CREATE_COMMAND = "create table " + TABLE_NAME + "("
                                                            + COLUMN_STUDENT_ID + " integer primary key, "
                                                            + COLUMN_STUDENT_NAME + " text not null, "
                                                            + COLUMN_STUDENT_AGE + " integer);";

    public Cursor getAllStudentsWithStudentID(String studentIDarray) {
        open();
        Cursor c= db_pointer.rawQuery("select * from " + TABLE_NAME + " where "+ COLUMN_STUDENT_ID + " in "+ studentIDarray,null);
        return c;
    }

    public String getStudentNameByID(String studentID)
    {
        open();
        Cursor cursor = db_pointer.rawQuery("SELECT "+COLUMN_STUDENT_NAME + " FROM "
                + TABLE_NAME + " WHERE " + COLUMN_STUDENT_ID + " = " + studentID + ";",null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public Cursor getAllStudentsLimitedInfoWithStudentID(String studentIDarray) {
        open();
        Cursor c= db_pointer.rawQuery("select "+COLUMN_STUDENT_ID +" , "+ COLUMN_STUDENT_NAME + " from " + TABLE_NAME + " where "+ COLUMN_STUDENT_ID + " in "+ studentIDarray,null);
        return c;
    }

    public Cursor getAllStudentsWithoutStudentID(String studentIDarray) {
        open();
        //Log.i("dev","Without ID array...");
        Cursor c= db_pointer.rawQuery("select * from " + TABLE_NAME + " where "+ COLUMN_STUDENT_ID + " not in "+ studentIDarray,null);
        return c;
    }

    public Cursor getAllStudents() {
        open();
        Cursor c= db_pointer.query(TABLE_NAME, new String[] {COLUMN_STUDENT_ID,COLUMN_STUDENT_NAME},
                null, new String[] {}, null, null,null);
        return c;
    }
    public Cursor getAllStudentsAllAtributes() {
        open();
        Cursor c= db_pointer.query(TABLE_NAME, new String[] {COLUMN_STUDENT_ID,COLUMN_STUDENT_NAME,COLUMN_STUDENT_AGE},
                null, new String[] {}, null, null,null);
        return c;
    }

    public long insertValues(int studentID,String studentName, String studentAge) {
        open();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_ID, studentID);
        values.put(COLUMN_STUDENT_NAME, studentName);
        values.put(COLUMN_STUDENT_AGE,  studentAge);
        long insertId = db_pointer.insert(TABLE_NAME, null,values);
        Log.i("dev","Value Successfully inserted with ID: "+insertId);

        return insertId;
    }

    public void deleteEithStudentID(int student_id) {
        db_pointer.delete(TABLE_NAME,COLUMN_STUDENT_ID + " = " + student_id,null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        File db_file_reference = context.getDatabasePath(DATABASE_NAME);
        if(!db_file_reference.exists()){
            //Log.i("dev","SQLiteHelperOnCreate: DataBase  "+ DATABASE_NAME + " Already Exists!");
        }
        else{
            Log.i("dev","SQLiteHelperOnCreate: No DB entry found! Creating a new one...");
            Toast.makeText(context, "Database not found! Creating a new one...", Toast.LENGTH_SHORT).show();
            db.execSQL(DATABASE_TABLE_CREATE_COMMAND);;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public SQLiteHelper_Students(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }


    public void open() throws SQLException {
        //checking wheather the database exists or not
        db_pointer = getReadableDatabase();
        if(db_pointer!=null) {
            //Log.i("dev", "Database Open: Database " + DATABASE_NAME + " Exists...");
        }
        else
            Log.i("dev","Database Open: Database " + DATABASE_NAME + " does not exist");

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

            if(table_exists)
            {
                //Log.i("dev","Database Open: DoesTableExist: Table " + TABLE_NAME + " exists");
            }
            else
            {
                Log.i("dev","Database Open: DoesTableExist: Table " + TABLE_NAME + " does not exist");
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

