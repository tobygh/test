package com.example.a84353.myToDoList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;


public class WeekFragment extends Fragment {
    public static WeekFragment newInstance() {
        WeekFragment fragment = new WeekFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Calendar left=Calendar.getInstance(),right=Calendar.getInstance();
        int t=left.get(Calendar.DAY_OF_WEEK);
        int tid=getResources().getIdentifier("week"+t,"id",""+getActivity().getPackageName());
        TextView wt=view.findViewById(tid);
        if (wt!=null)wt.setBackgroundResource(R.drawable.weektoday);
        TaskSQLiteDB db=new TaskSQLiteDB(getActivity().getApplicationContext());
        SQLiteDatabase database=db.getWritableDatabase();
        ImageView blk;int bid;
        for (int i=1;i<=6;i++) {
            left.set(Calendar.HOUR_OF_DAY,6+i*2);
            left.set(Calendar.MINUTE,0);
            left.set(Calendar.SECOND,0);
            right.set(Calendar.HOUR_OF_DAY,7+i*2);
            right.set(Calendar.MINUTE,59);
            right.set(Calendar.SECOND,59);
            for (int j = 1; j <= 7; j++) {
                left.set(Calendar.DAY_OF_WEEK,j-1);
                right.set(Calendar.DAY_OF_WEEK,j-1);
                String sqlOp="select count(*) from "+db.TABLE_TASK+
                        " where (beginMTime<="+right.getTimeInMillis()+
                        " and finishMTime>="+left.getTimeInMillis()+")";
                Log.i("debug","week sql "+sqlOp);

                Cursor cs=database.rawQuery(sqlOp,
                        null);
                bid = getResources().getIdentifier("wb" + i + j, "id", "" + getActivity().getPackageName());
                blk = view.findViewById(bid);
                if (blk != null) {
                    if (cs.moveToFirst()){
                        int num=cs.getInt(0);
                        if(num>0)Log.i("debug","week num "+num);
                        switch (num){
                            case 0:blk.setImageResource(R.drawable.week_free);break;
                            case 1: blk.setImageResource(R.drawable.week_low);break;
                            case 2: blk.setImageResource(R.drawable.week_medium);break;
                            case 3: blk.setImageResource(R.drawable.week_high);break;
                            default:blk.setImageResource(R.drawable.week_insane);break;

                        }

                    }
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_week, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
