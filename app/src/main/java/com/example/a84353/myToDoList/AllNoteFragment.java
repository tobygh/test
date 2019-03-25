package com.example.a84353.myToDoList;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.a84353.myToDoList.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AllNoteFragment extends Fragment {
    private List<TaskInfo> data;
    private ListView list_allNote;
    public static AllNoteFragment newInstance() {

        Bundle args = new Bundle();

        AllNoteFragment fragment = new AllNoteFragment();
        fragment.setArguments(args);
        return fragment;
    }
    void getData(int k){
        data = new ArrayList<TaskInfo>();
        TaskSQLiteDB db = new TaskSQLiteDB(getActivity().getApplicationContext());
        String sortMethod="id";
        switch (k){
            case 1:sortMethod="beginMTime";break;
            case 2:sortMethod="finishMTime";break;
            case 3:sortMethod="title";break;
            default:break;
        }
        SQLiteDatabase database = db.getWritableDatabase();
        Cursor cs = database.query(db.TABLE_TASK, null, "1=1", null, null, null, sortMethod);
        TaskInfo ti;
        while (cs.moveToNext()) {
            Log.i("debug", "All fromDB: " +
                    "id:"+ cs.getInt(0) +
                    "title"+ cs.getString(1) +
                    "list"+ cs.getString(2)+
                    "base"+cs.getString(5));
            ti=new TaskInfo(cs.getInt(0), cs.getString(1), cs.getLong(3), cs.getString(5));
            data.add(ti);
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tv=view.findViewById(R.id.tv_allNote);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft= getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fgContainer,TodaysFragment.newInstance());
                ft.commit();
            }
        });
        getData(0);
        TaskArrayAdapter adapter = new TaskArrayAdapter(getContext(), R.layout.task, data);
        list_allNote=view.findViewById(R.id.ls_allNote);
        list_allNote.setAdapter(adapter);
        Spinner sp=view.findViewById(R.id.sortMethod);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("debug","itemSelect "+position);
                getData(position);
                TaskArrayAdapter adapter = new TaskArrayAdapter(getContext(), R.layout.task, data);
                list_allNote.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_note, container, false);
    }

}
