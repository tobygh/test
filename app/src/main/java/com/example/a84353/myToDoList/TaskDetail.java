package com.example.a84353.myToDoList;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TaskDetail extends AppCompatActivity {
    EditText taskTtl,taskCnt,sYear,sMonth,sDay,sHour,sMinute,
                            eHour,eMinute;
    ImageView iv_addPhoto,iv_taskPhoto;
    int taskId;
    TaskSQLiteDB db;
    SQLiteDatabase database;
    Calendar cal;
    SimpleDateFormat dateFormat;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private Uri photoUri;
    String photoBase;
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
                //dateFormat =new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                cal=Calendar.getInstance();
                //cal.setTimeZone(TimeZone.getTimeZone("GMT"));
                long mdate=cs.getLong(3);
                //cal.setTimeZone(TimeZone.getTimeZone("GMT"));
                cal.setTimeInMillis(mdate);
                sYear.setText(""+cal.get(Calendar.YEAR));
                sMonth.setText(""+(cal.get(Calendar.MONTH)+1));
                sDay.setText(""+cal.get(Calendar.DAY_OF_MONTH));
                sHour.setText(""+cal.get(Calendar.HOUR_OF_DAY));
                sMinute.setText(""+cal.get(Calendar.MINUTE));

                mdate=cs.getLong(4);
                cal.setTimeInMillis(mdate);
                eHour.setText(""+cal.get(Calendar.HOUR_OF_DAY));
                eMinute.setText(""+cal.get(Calendar.MINUTE));

                photoBase=cs.getString(5);
                if(photoBase!=null){
                    Bitmap photo=BitmapHandler.decode(photoBase);
                    if (photo!=null){
                        iv_taskPhoto.setImageBitmap(photo);
                    }
                }

            }while (cs.moveToNext());
            else {
                taskTtl.setText("new Task");
                taskCnt.setText("more");
                cal=Calendar.getInstance();
                //cal.setTimeZone(TimeZone.getTimeZone("GMT"));
                int yy=cal.get(Calendar.YEAR),
                mm=cal.get(Calendar.MONTH)+1,
                dd=cal.get(Calendar.DAY_OF_MONTH),
                hh=cal.get(Calendar.HOUR_OF_DAY),
                mn=cal.get(Calendar.MINUTE),
                ss=cal.get(Calendar.SECOND);
                sYear.setText(  ""+yy);
                sMonth.setText( ""+mm);
                sDay.setText(   ""+dd);
                sHour.setText(  ""+hh);
                sMinute.setText(""+mn);
                Calendar later=Calendar.getInstance();
                later.setTimeInMillis(cal.getTimeInMillis()+3600000);
                cal.set(yy,mm,dd,23,59,59);

                if (later.after(cal))
                    later=cal;

                eHour.setText(""+later.get(Calendar.HOUR_OF_DAY));

                eMinute.setText(""+later.get(Calendar.MINUTE));
            }
        }
        void delete(){
            if (taskId>=0)
            database.delete(db.TABLE_TASK,"id="+taskId,null);
        }
        private int String2Int(String str){
            Pattern p=Pattern.compile("[^0-9]");
            Matcher m=p.matcher(str);
            str= m.replaceAll("");
            Log.i("debug","s2i "+str);
            int res;
            if (str!=null)
                return res=Integer.parseInt(str);
            return 0;
        }
        void update(){
            boolean flag=true;
            int yy=String2Int(sYear.getText().toString()),
                    mm=String2Int(sMonth.getText().toString()),
                    dd=String2Int(sDay.getText().toString()),
                    hh=String2Int(sHour.getText().toString()),
                    mn=String2Int(sMinute.getText().toString()),
                    ehh=String2Int(eHour.getText().toString()),
                    emn=String2Int(eMinute.getText().toString());
            Log.i("debug","detail update "+yy+mm+dd+hh+mn+ehh+emn);
            if (hh>23){hh=23;flag=false;}
            if (mn>59){mn=59;flag=false;}
            if (ehh>23){ehh=23;flag=false;}
            if (emn>59){emn=59;flag=false;}
            if (mm>12){mm=12;flag=false;}
            else if (mm<1){mm=1;flag=false;}
            int days[]={-1,31,28,31,30,31,30,31,31,30,31,30,31};
            if ((yy%400==0)||((yy%4==0)&&(yy%100!=0)))days[2]=29;
            if (dd<1){dd=1;flag=false;}
            else if (dd>days[mm]){dd=days[mm];flag=false;}
            sYear.setText(""+yy);
            sMonth.setText(""+mm);
            sDay.setText(""+dd);
            sHour.setText(""+hh);
            sMinute.setText(""+mn);
            eHour.setText(""+ehh);
            eMinute.setText(""+emn);

            if (flag==false){
                Toast.makeText(TaskDetail.this, "Please Check the format", Toast.LENGTH_SHORT).show();
                return;
            }
            delete();
            ContentValues cv=new ContentValues();
            if (taskId>=0)cv.put("id",taskId);
            cv.put("title",taskTtl.getText().toString());
            cv.put("list",taskCnt.getText().toString());
            cal=Calendar.getInstance();
           // cal.setTimeZone(TimeZone.getTimeZone("GMT"));
            mm--;
            cal.set(yy,mm,dd,hh,mn,00);
            cv.put("beginMTime",cal.getTimeInMillis());
            cal.set(yy,mm,dd,ehh,emn,59);
            cv.put("finishMTime",cal.getTimeInMillis());
            if(photoBase!=null){
                Log.i("debug",photoBase);
                cv.put("photoUri",photoBase);
            }
            database.insert(db.TABLE_TASK,null,cv);
            if (taskId==-1){
                Cursor cs=database.query(db.TABLE_TASK,null,"last_insert_rowid()",null,null,null,null,null);
                cs.moveToFirst();
                taskId=cs.getInt(0);
            }
        }
    }
    static InfoBinder binder;
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
        iv_addPhoto=findViewById(R.id.addPhoto);
        iv_taskPhoto=findViewById(R.id.taskPhoto);
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
        iv_addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal=Calendar.getInstance();
                //cal.setTimeZone(TimeZone.getTimeZone("GMT"));
                File fDir=getExternalCacheDir();
                File pDir=new File(fDir,"images");
                if (!pDir.exists())pDir.mkdirs();
                String Name="photo"+cal.getTimeInMillis()+".jpg";
                File pFile=new File(pDir,Name);
                try {
                    if (pFile.exists())pFile.delete();
                    pFile.createNewFile();
                }catch (IOException ioe){Log.i("debug",ioe.getMessage());}


                if (Build.VERSION.SDK_INT>=24){
                    photoUri = FileProvider.getUriForFile(TaskDetail.this,"com.example.a84353.myToDoList.fileProvider",pFile);
                }
                else photoUri = Uri.fromFile(pFile);

                Intent itt=new Intent("android.media.action.IMAGE_CAPTURE");
                itt.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(itt,TAKE_PHOTO);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==TAKE_PHOTO){
            if (resultCode==RESULT_OK){
                try {
                    Log.i("debug","result taskid"+taskId);
                    Bitmap bm= BitmapFactory.decodeStream(getContentResolver().openInputStream(photoUri));
                    if (bm!=null) {
                        Matrix m0;
                        Log.i("debug", "bitmap generated!w:" + bm.getWidth() + ",h:" + bm.getHeight());
                        InputStream ipsm=getContentResolver().openInputStream(photoUri);
                        ExifInterface exif=new ExifInterface(ipsm);
                        int angle=90;
                        int orient=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
                        switch (orient){
                            case ExifInterface.ORIENTATION_ROTATE_90:angle=90;
                            case ExifInterface.ORIENTATION_ROTATE_180:angle=180;break;
                            case ExifInterface.ORIENTATION_ROTATE_270:angle=270;break;
                        }
                        Log.i("debug","after cam angle"+angle);
                        if (angle>0){
                            m0=new Matrix();
                            m0.postRotate(angle);
                            bm=Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),m0,false);
                        }

                        m0=new Matrix();
                        float sc=1.0f;
                        if (bm.getWidth()>800){
                            sc=800f/bm.getHeight();
                            m0.postScale(sc,sc);
                            bm=Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),m0,false);
                        }

                        photoBase=BitmapHandler.encode(bm);
                        if (iv_taskPhoto==null)iv_taskPhoto=findViewById(R.id.taskPhoto);
                        iv_taskPhoto.setImageBitmap(bm);
                        Log.i("debug","binderInfo");
                    }
                }catch(FileNotFoundException fnf){Log.i("debug","after cam "+fnf.getMessage());}
                catch (IOException ioe){Log.i("debug","after cam "+ioe.getMessage());}
            }
        }
    }
}
