package com.example.a84353.myToDoList;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/*for listView only*/
public class TaskInfo {
    int id;
    String title,date,base;
    TaskInfo(int tid,String tstr, long dstr,String bstr){
        id=tid;
        title=tstr;
        Calendar cal=Calendar.getInstance();
        Log.i("debug","taskInfo date" +dstr);
        cal.setTimeInMillis(dstr);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
        date=format.format(cal.getTime());
        base=bstr;
    }
}
