package com.example.a84353.test;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
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
    public void onViewCreated(final View view, @Nullable Bundle bundle){
        ListView ls=view.findViewById(R.id.ls_today);
        getData();
        TaskArrayAdapter adapter = new TaskArrayAdapter(getContext(),R.layout.task,data);
        ls.setAdapter(adapter);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_todays, container, false);

        return root;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void getData(){
        data=new ArrayList<TaskInfo>();
       // TaskInfo ti=new TaskInfo("10","20");
       // Log.i("debug","d"+ti.date+'t'+ti.title );
        //Cursor cs=
        // data.add(ti);data.add(ti);data.add(ti);data.add(ti);data.add(ti);data.add(ti);data.add(ti);data.add(ti);data.add(ti);data.add(ti);
        TaskSQLiteDB db=new TaskSQLiteDB(getActivity().getApplicationContext());
        SQLiteDatabase database=db.getWritableDatabase();
        Cursor cs=database.query(db.TABLE_TASK,null,"id >= 10",null,null,null,null);
        TaskInfo ti;
        while(cs.moveToNext()){
            ti=new TaskInfo(cs.getString(1),cs.getString(2));
            data.add(ti);
            Log.i("debug","fromDB:"+cs.getInt(0)+cs.getString(1)+cs.getString(2));
        }
        //database.delete(db.TABLE_TASK,"id>=10",null);
    }
    private void addItem(View view){

    }
}
