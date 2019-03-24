package com.example.a84353.myToDoList;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.nio.charset.IllegalCharsetNameException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaskDetail extends AppCompatActivity {
    EditText taskTtl,taskCnt,sYear,sMonth,sDay,sHour,sMinute,eHour,eMinute;
    int taskId;
    TaskSQLiteDB db;
    SQLiteDatabase database;
    Calendar cal;
    SimpleDateFormat dateFormat;
    public static final String updateBroadcast="getListRefreshed";
    private class InfoBinder{
        InfoBinder(int tid){
            Log.i("debug","detail Created id"+tid);
            db=new TaskSQLiteDB(getApplicationContext());
            database=db.getWritableDatabase();
            Cursor cs=database.query(db.TABLE_TASK,null,"id="+tid,null,null,null,null);
            if (cs.moveToFirst())do{
                taskTtl.setText(""+cs.getString(1));
                taskCnt.setText(""+cs.getString(2));
                dateFormat =new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                cal=Calendar.getInstance();
                try{
                    cal.setTime(dateFormat.parse(cs.getString(3)));
                    sYear.setText(""+cal.get(Calendar.YEAR));
                    sMonth.setText(""+(cal.get(Calendar.MONTH)+1));
                    sDay.setText(""+cal.get(Calendar.DAY_OF_MONTH));
                    sHour.setText(""+cal.get(Calendar.HOUR));
                    sMinute.setText(""+cal.get(Calendar.MINUTE));
                }catch(ParseException pe){Log.i("debug","ParseError"+pe.getMessage());}

                try{
                    cal.setTime(dateFormat.parse(cs.getString(4)));
                    eHour.setText(""+cal.get(Calendar.HOUR));
                    eMinute.setText(""+cal.get(Calendar.MINUTE));
                }catch(ParseException pe){Log.i("debug","ParseError"+pe.getMessage());}


            }while (cs.moveToNext());
            else {
                taskTtl.setText("new Task");
                taskCnt.setText("more");
                Calendar cal=Calendar.getInstance();
                sYear.setText(""+cal.get(Calendar.YEAR));
                sMonth.setText(""+(cal.get(Calendar.MONTH)+1));
                sDay.setText(""+cal.get(Calendar.DAY_OF_MONTH));
                sHour.setText(""+cal.get(Calendar.HOUR));
                sMinute.setText(""+cal.get(Calendar.MINUTE));
                cal.setTimeInMillis(cal.getTimeInMillis()+3600000);
                eHour.setText(""+cal.get(Calendar.HOUR));
                eMinute.setText(""+cal.get(Calendar.MINUTE));
            }
        }
        void delete(){
            if (taskId>=0)
            database.delete(db.TABLE_TASK,"id="+taskId,null);
        }
        void update(){
            if (taskId>=0)
            database.delete(db.TABLE_TASK,"id="+taskId,null);
            ContentValues cv=new ContentValues();
            if (taskId>=0)cv.put("id",taskId);
            cv.put("title",taskTtl.getText().toString());
            cv.put("list",taskCnt.getText().toString());
            String ymd=sYear.getText()+"-"+
                    sMonth.getText()+"-"+
                    sDay.getText();
            String sdate=ymd+" "+
                    sHour.getText()+":"+
                    sMinute.getText()+":00";
            String edate=ymd+" "+
                    eHour.getText()+":"+
                    eMinute.getText()+":00";
            cv.put("beginTime",sdate);
            cv.put("finishTime",edate);
            database.insert(db.TABLE_TASK,null,cv);
            if (taskId==-1){
                Cursor cs=database.query(db.TABLE_TASK,null,"last_insert_rowid()",null,null,null,null,null);
                cs.moveToFirst();
                taskId=cs.getInt(0);
            }
        }
    }
    InfoBinder binder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Intent it=getIntent();
        int id=it.getIntExtra("taskid",-1);
        Log.i("debug","receive id"+id);
        taskId=id;
        taskTtl=findViewById(R.id.taskTitle);
        taskCnt=findViewById(R.id.noteBox);
        sYear=findViewById(R.id.editYear);
        sMonth=findViewById(R.id.editMonth);
        sDay=findViewById(R.id.editDay);
        sHour=findViewById(R.id.startHour);
        sMinute=findViewById(R.id.startMin);
        eHour=findViewById(R.id.finishHour);
        eMinute=findViewById(R.id.finishMin);
        binder=new InfoBinder(id);
        Button confrimBtn=findViewById(R.id.detailConfirm);
        Button deleteBtn=findViewById(R.id.deleteTask);
        confrimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binder.update();
                Intent it=new Intent(updateBroadcast);
                it.putExtra("Msg","Update");
                sendBroadcast(it);
                finish();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binder.delete();
                Intent it=new Intent(updateBroadcast);
                it.putExtra("Msg","Delete");
                sendBroadcast(it);
                finish();
            }
        });
    }
}
