package com.example.a84353.myToDoList;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class TaskArrayAdapter extends ArrayAdapter {
    private int rid;
    List<TaskInfo>tasks;

    TaskInfo task;
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

    @Nullable
    private static Activity findActivity(Context ct){
        if (ct instanceof Activity){
            return ((Activity)ct);
        }
        else if (ct instanceof ContextWrapper){
            ContextWrapper wrapper=(ContextWrapper)ct;
            return findActivity(wrapper.getBaseContext());
        }
        else return null;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        task=getItem(position);
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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ct=getContext();
                Intent it=new Intent(ct,TaskDetail.class);
                task=getItem(position);
                Log.i("debug","Item onclick"+position);
                it.putExtra("taskid",task.id);
                ct.startActivity(it);
            }
        });
        return view;
    }
}
