package com.example.a84353.test;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private FrameLayout fg_container;
    private NoteFragment nf;
    private TodaysFragment tf;
    private WeekFragment wf;
    private FragmentTransaction ft;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    ft=getSupportFragmentManager().beginTransaction();
                    /*ft.show(tf);
                    ft.hide(nf);
                    ft.hide(wf);*/

                    ft.replace(R.id.fgContainer,tf);
                    ft.commit();
                    return true;
                case R.id.navigation_dashboard:
                    ft=getSupportFragmentManager().beginTransaction();
                    /*ft.hide(tf);
                    ft.show(nf);
                    ft.hide(wf);*/
                    ft.replace(R.id.fgContainer,nf);
                    ft.commit();
                    return true;
                case R.id.navigation_notifications:
                    ft=getSupportFragmentManager().beginTransaction();
                    /*ft.hide(tf);
                    ft.hide(nf);
                    ft.show(wf);*/
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

        TaskSQLiteDB db=new TaskSQLiteDB(getApplicationContext());
        SQLiteDatabase database=db.getWritableDatabase();
        ContentValues values;
        values=new ContentValues();
        values.put("title","get ToDolist done");
        values.put("duetime","2019-3-20");
        database.insert(db.TABLE_TASK,null,values);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        tf=TodaysFragment.newInstance();
        nf=NoteFragment.newInstance();
        wf=WeekFragment.newInstance();
        ft=getSupportFragmentManager().beginTransaction();
        /*ft.add(R.id.fgContainer,tf);
        ft.add(R.id.fgContainer,nf);
        ft.add(R.id.fgContainer,wf);
        ft.hide(nf);
        ft.hide(wf);*/
        ft.commit();
    }

}
