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
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    private final Context c;
    private String imgString;
    public ArrayList<Bitmap> slicedImg;
    int size;
    //임시 이미지 변수
    Bitmap deliveredImg;

    public GridAdapter(Context c){
        this.c = c;
    }
    public GridAdapter(Context c, Bitmap bm, int size){
        this.c = c;
        this.deliveredImg = bm;
        this.size = size;
    }
    public GridAdapter(Context c, ArrayList<Bitmap> slicedImg, int size){
        this.c = c;
        this.slicedImg = (ArrayList<Bitmap>) slicedImg.clone();
        this.size = size;
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

        System.out.println("positions : "+  position);
        ImageView image;
        if(convertView == null){
            image = new ImageView(c);
        }
        else{
            image = (ImageView) convertView;
        }
        image.setLayoutParams(new ViewGroup.LayoutParams(size / 20, size / 20));
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setImageBitmap(slicedImg.get(position));
        //imageView.setImageBitmap(deliveredImg);
        return image;
    }
}
