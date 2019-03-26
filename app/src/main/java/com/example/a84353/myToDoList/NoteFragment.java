package com.example.a84353.myToDoList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class NoteFragment extends Fragment {
    static Timer theTimer;
    private static Handler timerHandler;
    ConstraintLayout cs;
    EditText et,low;
    ImageView iv_left,iv_right,iv_add;
    List<Integer>key;
    List<String> notes;
    int animDelay=250;
    private class readerController{
        TaskSQLiteDB db;
        SQLiteDatabase database;
        private int idx=0;
        readerController(){
            idx=1;
            db=new TaskSQLiteDB(getActivity().getApplicationContext());
            database=db.getWritableDatabase();
            Cursor cs=database.query(db.TABLE_NOTE,null,"1=1",null,null,null,null);
            String tnote;int k;
            if (cs.moveToFirst())do{
                k=cs.getInt(0);
                tnote=cs.getString(1);
                Log.i("debug","noteId: "+k+"content: "+tnote);
                key.add(k);
                notes.add(tnote);
            }while(cs.moveToNext());
            if(idx>=notes.size())
                et.setText("");
            else et.setText(notes.get(idx));

            et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    ContentValues cv=new ContentValues();
                    String ctn=((EditText)v).getText().toString();
                    if (idx<notes.size()){//edit op
                        database.delete(db.TABLE_NOTE,"id="+key.get(idx),null);
                        cv.put("id",key.get(idx));
                        cv.put("content",ctn);
                        database.insert(db.TABLE_NOTE,null,cv);
                        notes.set(idx,ctn);
                    }
                    else {//add op
                        cv.put("content",ctn);
                        database.insert(db.TABLE_NOTE,null,cv);
                        Cursor cs=database.query(db.TABLE_NOTE,null,"last_insert_rowid()",null,null,null,null);
                        cs.moveToFirst();
                        int lastID=cs.getInt(0);
                        Log.i("debug","lastId"+lastID);
                        key.add(lastID);
                        notes.add(ctn);
                    }
                    /*if(idx<notes.size()){//edit operation
                        cv.put("id",key.get(idx));
                        database.delete(db.TABLE_NOTE,"id="+key.get(idx),null);
                        notes.set(idx,((EditText)v).getText().toString());
                    }
                    database.insert(db.TABLE_NOTE,null,cv);
                    if(idx>=notes.size()){//add operation
                        notes.add()
                    }*/
                }
            });
        }
        public void update(){
            Cursor cs=database.query(db.TABLE_NOTE,null,"1=1",null,null,null,null);
            String tnote;int k;
            notes.clear();key.clear();
            if (cs.moveToFirst())do{
                k=cs.getInt(0);
                tnote=cs.getString(1);
                notes.add(tnote);
                key.add(k);
            }while(cs.moveToNext());
            if(idx>=notes.size())
                et.setText("");
            else et.setText(notes.get(idx));
        }
        public void displaylast(){
            if (notes.isEmpty())return;
            if(idx>0)idx--;
            else idx=notes.size()-1;
            et.setText(notes.get(idx));
            Log.i("debug","current idx"+idx);
        }
        public void displayNext(){
            if (notes.isEmpty())return;
            if(idx<notes.size()-1)idx++;
            else idx=0;
            et.setText(notes.get(idx));
            Log.i("debug","current idx"+idx);
        }
        public void newNote(){
            idx=notes.size();
            et.setText("");
        }
        public String getNextNote(){
            if (notes.isEmpty())return null;
            int nid=idx+1;if (nid>=notes.size())nid=0;
            return notes.get(nid);
        }
        public String getLastNote(){
            if (notes.isEmpty())return null;
            int nid=idx-1;if (nid<0)nid=notes.size()-1;
            return notes.get(nid);
        }
    }
    private readerController reader;
    private class noteEditPositionController{
        EditText et;
        TimerTask getBack;
        double noteOriginX,noteOriginY;
        double noteWidth,noteHeight;
        double r;
        double screenCenterX,screenCenterY;
        int switchNote;
        double dragStartX,dragStartY;
        double noteCenterX,noteCenterY;
        noteEditPositionController(EditText editText){
            et=editText;
            noteOriginX=et.getX(); noteOriginY=et.getY();
            noteWidth=et.getWidth(); noteHeight=et.getHeight();
            noteCenterX=noteOriginX+noteWidth/2.0;
            screenCenterX=noteCenterX;
            noteCenterY=noteOriginY+noteHeight/2.0;
            screenCenterY=noteCenterY;
        }
        public void beginDrag(double x,double y){
            dragStartX=x;
            dragStartY=y;
            noteCenterY=screenCenterY;
            noteCenterX=screenCenterX;
            et.clearFocus();
            InputMethodManager iptMM = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            iptMM.hideSoftInputFromWindow(et.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        public void endDrag(){
            switchNote=0;
            boolean out=false;
            double dx=noteCenterX-screenCenterX,dy=noteCenterY-screenCenterY;
            Log.i("debug","enddrag");
            double theta=Math.atan(Math.abs(dy/dx));
            if(Math.abs(dx)+Math.abs(dy)>300)out=true;
            AnimationSet anims=new AnimationSet(false);
            if (out){
                smoothToOut(1);
            }
            /*if(out){
                //switch only get next note
                switchNote=1;
                int ox=(int)(1000*Math.cos(theta));if(dx<0){ox=-ox;}
                int oy=(int)(1000*Math.sin(theta));if (dy<0)oy=-oy;
                TranslateAnimation tanim=new TranslateAnimation(0,ox,0,oy);
                anims.addAnimation(tanim);
                anims.setDuration(animDelay);
                et.startAnimation(anims);
                cs.setOnTouchListener(null);
                getBack=new TimerTask() {
                    @Override
                    public void run() {
                        timerHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                et.setVisibility(View.INVISIBLE);
                                //try to avoid the shake
                                et.clearAnimation();
                                et.setX((float)noteOriginX);
                                et.setY((float)noteOriginY);
                                et.setFocusable(true);
                                et.setRotation(0.0f);
                                et.setVisibility(View.VISIBLE);
                                noteCenterX=screenCenterX;
                                noteCenterY=screenCenterY;
                                cs.setOnTouchListener(myListener);
                                if(switchNote!=0){
                                    if(switchNote==1)reader.swipeLeft();
                                    if(switchNote==-1) reader.swipeRight();
                                }
                            }
                        });

                    }
                };
                theTimer.schedule(getBack,animDelay-10);
            }*/
            else {
                smoothToCenter(0);
            }/*
            else {
                TranslateAnimation tanim = new TranslateAnimation(0, (int)(-dx), 0, (int)(-dy));
                anims.addAnimation(tanim);
                anims.setDuration(animDelay);
                anims.setFillAfter(true);
                anims.setFillBefore(false);
                et.startAnimation(anims);
            }

            cs.setOnTouchListener(null);
            getBack=new TimerTask() {
                @Override
                public void run() {
                    timerHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            et.setVisibility(View.INVISIBLE);
                            //try to avoid the shake
                            et.clearAnimation();
                            et.setX((float)noteOriginX);
                            et.setY((float)noteOriginY);
                            et.setFocusable(true);
                            et.setRotation(0.0f);
                            et.setVisibility(View.VISIBLE);
                            noteCenterX=screenCenterX;
                            noteCenterY=screenCenterY;
                            cs.setOnTouchListener(myListener);
                            if(switchNote!=0){
                                if(switchNote==1)reader.swipeLeft();
                                if(switchNote==-1) reader.swipeRight();
                            }
                        }
                    });

                }
            };
            theTimer.schedule(getBack,animDelay-10);*/
            //open keyboard, but why?
            //InputMethodManager iptMM = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            //iptMM.showSoftInput(et,InputType.TYPE_MASK_FLAGS);
        }
        public void dragTo(double x,double y){
            double dx=x-dragStartX,dy=y-dragStartY;
            low.setText(reader.getNextNote());
            noteCenterX=screenCenterX+dx;
            noteCenterY=screenCenterY+dy;
            et.setX((float)(noteCenterX-noteWidth/2));
            et.setY((float)(noteCenterY-noteHeight/2));
            //rotating works not well
            //et.setRotation((float)(-Math.atan((noteCenterX-screenCenterX)/(noteCenterY+1000))*180/Math.PI));
        }
        public void avoidAnotherAnimation(){
            iv_left.setClickable(false);
            iv_right.setClickable(false);
            cs.setOnTouchListener(null);
        }
        public void readyToAcceptAnimation(){
            iv_left.setClickable(true);
            iv_right.setClickable(true);
            cs.setOnTouchListener(myListener);
        }

        public void smoothToCenter(final int toLast){
            double dx=noteCenterX-screenCenterX,dy=noteCenterY-screenCenterY;
            double theta=Math.atan(Math.abs(dy/dx));
            AnimationSet anims=new AnimationSet(false);
            TranslateAnimation tanim = new TranslateAnimation(0, (int)(-dx), 0, (int)(-dy));
            anims.addAnimation(tanim);
            anims.setDuration(animDelay);
            anims.setFillAfter(true);
            anims.setFillBefore(false);
            avoidAnotherAnimation();
            et.startAnimation(anims);
            //cs.setOnTouchListener(null);
            getBack=new TimerTask() {
                @Override
                public void run() {
                    timerHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            et.setVisibility(View.INVISIBLE);
                            et.clearAnimation();
                            et.setX((float)noteOriginX);
                            et.setY((float)noteOriginY);
                            et.setFocusable(true);
                            et.setRotation(0.0f);
                            et.setVisibility(View.VISIBLE);
                            noteCenterX=screenCenterX;
                            noteCenterY=screenCenterY;
                            readyToAcceptAnimation();
                            //cs.setOnTouchListener(myListener);
                            if (toLast==1)reader.displaylast();
                        }
                    });

                }
            };
            theTimer.schedule(getBack,animDelay-10);
        }
        public void smoothToOut(final int toNext) {
            double dx = noteCenterX - screenCenterX, dy = noteCenterY - screenCenterY;
            double theta = Math.atan(Math.abs(dy / dx));
            AnimationSet anims = new AnimationSet(false);
            //switch out get next note
            int ox = (int) (1000 * Math.cos(theta));
            if (dx < 0) {ox = -ox;}
            int oy = (int) (1000 * Math.sin(theta));
            if (dy < 0) {oy = -oy;}
            TranslateAnimation tanim = new TranslateAnimation(0, ox, 0, oy);
            anims.addAnimation(tanim);
            anims.setDuration(animDelay);
            et.startAnimation(anims);
            avoidAnotherAnimation();
            //cs.setOnTouchListener(null);
            getBack = new TimerTask() {
                @Override
                public void run() {
                    timerHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            et.setVisibility(View.INVISIBLE);
                            //try to avoid the shake
                            et.clearAnimation();
                            et.setX((float) noteOriginX);
                            et.setY((float) noteOriginY);
                            et.setFocusable(true);
                            et.setRotation(0.0f);
                            et.setVisibility(View.VISIBLE);
                            noteCenterX = screenCenterX;
                            noteCenterY = screenCenterY;
                            readyToAcceptAnimation();
                            //cs.setOnTouchListener(myListener);
                            if (toNext==1)reader.displayNext();
                        }
                    });

                }
            };
            theTimer.schedule(getBack, animDelay - 10);
        }
        public void autoDrag(double x,double y){

                controller.noteCenterX += x;
                controller.noteCenterY += y;
            if (x>0) {
                low.setText(reader.getNextNote());
                smoothToOut(1);
            }
            else {
                low.setText(notes.get(reader.idx));

                et.setX((float)(noteCenterX-noteWidth/2));
                et.setY((float)(noteCenterY-noteHeight/2));
                et.setText(reader.getLastNote());
                smoothToCenter(1);
            }
        }
    }
    noteEditPositionController controller;
    public static NoteFragment newInstance() {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    private View.OnTouchListener myListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction()==1){//up
                controller.endDrag();
                return false;
            }
            else if (event.getAction()==0){//down
                controller.beginDrag(event.getX(),event.getY());
                return true;
            }
            else if (event.getAction()==2){//hold
                controller.dragTo(event.getX(),event.getY());
                return true;
            }
            else return true;
        }
    };
    @Override
    public void onViewCreated(final View view, @Nullable Bundle bundle){
        Log.i("debug","created!");
        cs=view.findViewById(R.id.notePlane);
        cs.setOnTouchListener(myListener);
        et=view.findViewById(R.id.noteCard);
        low=view.findViewById(R.id.noteCardAvoid);
        iv_left=view.findViewById(R.id.leftArrow);
        iv_right=view.findViewById(R.id.rightArrow);
        iv_add=view.findViewById(R.id.addNote);
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reader.newNote();
            }
        });
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.autoDrag(-500,Math.random()*500-250);
            }
        });
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.autoDrag(500,Math.random()*500-250);
            }
        });
        theTimer=new Timer();
        timerHandler=new Handler();
        notes=new ArrayList<String>();
        key=new ArrayList<Integer>();
        TimerTask tk=new TimerTask() {
            @Override
            public void run() {
                timerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        controller=new noteEditPositionController((EditText) view.findViewById(R.id.noteCard));
                        reader=new readerController();
                    }
                });
            }
        };
        theTimer.schedule(tk,100);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


}
