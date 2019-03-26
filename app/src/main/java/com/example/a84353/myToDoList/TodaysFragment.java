package com.example.a84353.myToDoList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TodaysFragment extends Fragment {
    private List<TaskInfo> data;

    public static TodaysFragment newInstance(/*String param1, String param2*/) {
        TodaysFragment fragment = new TodaysFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle bundle) {

        ListView ls = view.findViewById(R.id.ls_today);
        TextView iv = view.findViewById(R.id.addToday);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getContext(), TaskDetail.class);
                it.putExtra("id", "-1");
                startActivity(it);
            }
        });
        getData();
        TaskArrayAdapter adapter = new TaskArrayAdapter(getContext(), R.layout.task, data);
        ls.setAdapter(adapter);
        TextView tv=view.findViewById(R.id.tv_today);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft= getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fgContainer,AllNoteFragment.newInstance());
                ft.commit();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_todays, container, false);

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void getData() {
        data = new ArrayList<TaskInfo>();
        TaskSQLiteDB db = new TaskSQLiteDB(getActivity().getApplicationContext());
        SQLiteDatabase database = db.getWritableDatabase();
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);
        Cursor cs = database.query(db.TABLE_TASK, null, "beginMTime<="+cal.getTimeInMillis(), null, null, null, "beginMTime");
        TaskInfo ti;
        while (cs.moveToNext()) {
            ti=new TaskInfo(cs.getInt(0), cs.getString(1), cs.getLong(3), cs.getString(5));
            data.add(ti);
            //database.delete(db.TABLE_TASK,"id>=10",null);
        }

    }
}