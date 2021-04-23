package edu.skku.map.nonogram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    private final Context c;
    private String imgString;
    public ArrayList<Bitmap> slicedImg;
    //임시 이미지 변수
    Bitmap deliveredImg;


    public GridAdapter(Context c){
        this.c = c;
    }
    public GridAdapter(Context c, Bitmap bm){
        this.c = c;
        this.deliveredImg = bm;
        System.out.println("왜 안되지?1");
    }
    public GridAdapter(Context c, ArrayList<Bitmap> slicedImg){
        this.c = c;
        this.slicedImg = (ArrayList<Bitmap>) slicedImg.clone();
        System.out.println("왜 안되지?1");
    }
    @Override
    public int getCount() {
        return slicedImg.size();
        //return 1;
    }

    @Override
    public Object getItem(int position) {
        return slicedImg.get(position);
        //return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        int imageID = Integer.parseInt(imgString.substring(imgString.lastIndexOf("/") + 1, imgString.length()));

        System.out.println("positions : "+  position);
        System.out.println("왜 안되지?2");
        ImageView imageView;
        if(convertView == null){
            imageView = new ImageView(c);
        }
        else{
            imageView = (ImageView)convertView;
        }
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageBitmap(slicedImg.get(position));
        //imageView.setImageBitmap(deliveredImg);
        System.out.println("왜 안되지?3");
        return imageView;
    }
}
