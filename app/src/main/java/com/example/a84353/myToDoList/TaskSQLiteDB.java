package com.example.a84353.myToDoList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TaskSQLiteDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "taskDatabase.db";

    private static final int DB_VERSION=1;

    public static final String TABLE_TASK="tasks";
    public static final String TABLE_NOTE="notes";
    private static final String TASK_TABLE_CREATE_SQL ="create table "+TABLE_TASK+"("+
            "id integer primary key autoincrement,"+
            "title varchar(20) not null,"+
            "list varchar(500),"+
            "beginTime text,"+
            "finishTime text,"+
            "photoUri text"+
            ")";
    private static final String NOTE_TABLE_CREATE_SQL ="create table "+TABLE_NOTE+"("+
            "id integer primary key autoincrement,"+
            "content varchar(500)"+")";

    public TaskSQLiteDB(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(TASK_TABLE_CREATE_SQL);
        db.execSQL(NOTE_TABLE_CREATE_SQL);
    }
    @Override
    public  void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        Log.i("debug","Why do you want to upgrade?");
    }


}
