package com.example.a84353.test;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TaskArrayAdapter extends ArrayAdapter {
    private int rid;
    List<TaskInfo>tasks;
    public TaskArrayAdapter(Context context, int resid, List<TaskInfo>list){
        super(context,resid,list);
        rid=resid;
        tasks=list;
    }
/*
    @Override
    public View getView(int position, View converView, ViewGroup parent){
        TaskInfo task=(TaskInfo) (getItem(position));
        ConstraintLayout taskItem=new ConstraintLayout(getContext());
        String inflater=Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater lf=(LayoutInflater)getContext().getSystemService(inflater);
        lf.inflate(rid,taskItem,true);
        TextView tv_title=(TextView)taskItem.findViewById(R.id.taskTitle);
        TextView tv_date=(TextView)taskItem.findViewById(R.id.taskDate);

        tv_title.setText(task.title);
        tv_date.setText(task.date);
        return taskItem;
    }
    */
    @Override
    public TaskInfo getItem(int pos){
        return tasks.get(pos);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskInfo task=getItem(position);
        Log.i("debug","fromList:"+position+"t"+task.title+"d"+task.date);
        View view;
        TaskItemHolder holder;
        if(convertView==null){
            view=LayoutInflater.from(getContext()).inflate(rid,parent,false);
            holder=new TaskItemHolder();
            holder.taskDate=view.findViewById(R.id.taskDate);
            holder.taskTitle=view.findViewById(R.id.taskTitle);
            view.setTag(holder);
        }
        else {
            view=convertView;
            holder=(TaskItemHolder) convertView.getTag();
        }
        holder.taskTitle.setText(task.title);
        holder.taskDate.setText(task.date);
        return view;
    }
}
