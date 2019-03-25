package com.example.a84353.myToDoList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapHandler {
    public static Bitmap decode(String base){
        Bitmap bm=null;
        if (base==null)return bm;
        byte bytes[]= Base64.decode(base,Base64.DEFAULT);
        if (bytes!=null)
            bm= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        return bm;
    }
    public static String encode(Bitmap bm){
        String str=null;
        if (bm==null)return str;
        ByteArrayOutputStream stream=null;
        try{
            stream=new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG,50,stream);
            stream.flush();stream.close();
            byte[] bytes=stream.toByteArray();
            str=Base64.encodeToString(bytes,0,bytes.length,Base64.DEFAULT);
        }catch (IOException ioe){
            Log.i("debug","encode"+ioe.getMessage());
        }

        return str;
    }
}
