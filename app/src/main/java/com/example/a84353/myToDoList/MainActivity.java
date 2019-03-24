package com.example.a84353.myToDoList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.nsd.NsdManager;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {
    Timer theTimer;
    static Handler theHandler;
    private FrameLayout fg_container;
    private NoteFragment nf;
    private TodaysFragment tf;
    private WeekFragment wf;
    private FragmentTransaction ft;
    private UpdateBroadcastReceiver receiver;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    ft=getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fgContainer,tf);
                    ft.commit();
                    return true;
                case R.id.navigation_dashboard:
                    ft=getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fgContainer,nf);
                    ft.commit();
                    return true;
                case R.id.navigation_notifications:
                    ft=getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fgContainer,wf);
                    ft.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        tf=TodaysFragment.newInstance();
        nf=NoteFragment.newInstance();
        wf=WeekFragment.newInstance();
        theTimer=new Timer();
        theHandler=new Handler();
        ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fgContainer,tf);
        ft.commit();
        receiver=new UpdateBroadcastReceiver();
        IntentFilter inf=new IntentFilter(TaskDetail.updateBroadcast);
        registerReceiver(receiver,inf);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private class UpdateBroadcastReceiver extends BroadcastReceiver{
        @Override public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("Msg");
            Log.i("debug","Receive Broadcase "+msg);
            if (msg.equals("Update")){
                TimerTask task=new TimerTask() {
                    @Override
                    public void run() {
                        theHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ft=getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fgContainer,TodaysFragment.newInstance());
                                ft.commit();
                            }
                        });

                    }
                };
                theTimer.schedule(task,100);
            }
            else if (msg.equals("Delete")){
                TimerTask task=new TimerTask() {
                    @Override
                    public void run() {
                        theHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ft=getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fgContainer,TodaysFragment.newInstance());
                                ft.commit();
                            }
                        });

                    }
                };
                theTimer.schedule(task,100);
            }
        }

    }
}
