package com.example.a84353.test;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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
    private class noteEditPositionController{
        EditText et;

        double noteOriginX,noteOriginY;
        double noteWidth,noteheight;
        double r;
        double screenCenterX,screenCenterY;

        double dragStartX,dragStartY;
        double noteCenterX,noteCenterY;
        noteEditPositionController(EditText editText){
            et=editText;
            noteOriginX=et.getX();
            noteOriginY=et.getY();
            noteWidth=et.getWidth();
            noteheight=et.getHeight();
            noteCenterX=noteOriginX+noteWidth/2.0;
            screenCenterX=noteCenterX;
            noteCenterY=noteOriginY+noteheight/2.0;
            screenCenterY=noteCenterY;
            r=Math.sqrt(noteheight*noteheight+noteWidth*noteheight);
            /*Log.i("debug",noteOriginX+","+
                    noteOriginY+","+
                    noteWidth+","+
                    noteheight
            );*/
        }
        public void beginDrag(double x,double y){
            dragStartX=x;
            dragStartY=y;
            et.clearFocus();
            InputMethodManager iptMM = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            iptMM.hideSoftInputFromWindow(et.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        }
        public void endDrag(){
            double theta=Math.abs(Math.atan((noteCenterY-screenCenterY)/(noteCenterX-screenCenterX)));
            int fx=noteCenterX-screenCenterX>0?1:-1;
            int fy=noteCenterY-screenCenterY>0?1:-1;
            noteCenterX=screenCenterX;
            noteCenterY=screenCenterY;
            TranslateAnimation anim=new TranslateAnimation(0,(int)(fx*1000*Math.cos(theta)),0,(int)(fy*1000*Math.sin(theta)));
            anim.setDuration(1000);
            et.startAnimation(anim);
            et.setX((float)noteOriginX);
            et.setY((float)noteOriginY);
            //et.startAnimation(a);

            et.setRotation(0.0f);
            et.setFocusable(true);
            //InputMethodManager iptMM = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            //iptMM.showSoftInput(et,InputType.TYPE_MASK_FLAGS);
        }
        public void dragTo(double x,double y){
            double dx=x-dragStartX,dy=y-dragStartY;
            noteCenterX=screenCenterX+dx;
            noteCenterY=screenCenterY+dy;
            /*Log.i("debug",x+",ds"+
                    dragStartX+","+
                    y+",dy"+
                    dragStartY);*/
            et.setX((float)(noteCenterX-noteWidth/2));
            et.setY((float)(noteCenterY-noteheight/2));

            double theta=-Math.atan((noteCenterX-screenCenterX)/noteCenterY)*180/Math.PI;
           // Log.i("debug",dx+","+y+","+theta);
            et.setRotation((float) theta);
        }
    }

    noteEditPositionController controller;
    EditText et;
    public static NoteFragment newInstance() {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    private View.OnTouchListener myListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
           // Log.i("debug",event.getAction()+":"+event.getX()+","+event.getY());
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
        ConstraintLayout cs=view.findViewById(R.id.notePlane);
        cs.setOnTouchListener(myListener);
        //et=view.findViewById(R.id.noteCard);
        Timer tm=new Timer();
        TimerTask tk=new TimerTask() {
            @Override
            public void run() {
                controller=new noteEditPositionController((EditText) view.findViewById(R.id.noteCard));
            }
        };
        tm.schedule(tk,100);
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
