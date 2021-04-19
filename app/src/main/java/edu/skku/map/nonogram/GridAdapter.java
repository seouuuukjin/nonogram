package edu.skku.map.nonogram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    private final Context c;
    private String imgString;
    Bitmap deliveredImg;
    public ArrayList<Bitmap> slicedImg;

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
        this.slicedImg = slicedImg;
        System.out.println("왜 안되지?1");
    }
    @Override
    public int getCount() {
        return slicedImg.size();
    }

    @Override
    public Object getItem(int position) {
        return slicedImg.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        int imageID = Integer.parseInt(imgString.substring(imgString.lastIndexOf("/") + 1, imgString.length()));
//        Bitmap b =
        System.out.println("왜 안되지?2");
        ImageView imageView;
        if(convertView == null){
            imageView = new ImageView(c);
        }
        else{
            imageView = (ImageView)convertView;
        }
        imageView.setImageBitmap(deliveredImg);
        System.out.println("왜 안되지?3");
        return imageView;
    }
}
