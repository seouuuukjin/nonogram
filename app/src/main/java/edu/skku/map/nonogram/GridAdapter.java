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

public class GridAdapter extends BaseAdapter {
    private Context c;
    private String imgString;
    Bitmap deliveredImg;
    public GridAdapter(Context c){
        this.c = c;
    }
    public GridAdapter(Context c, Bitmap bm){
        this.c = c;
        deliveredImg = bm;
    }
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        int imageID = Integer.parseInt(imgString.substring(imgString.lastIndexOf("/") + 1, imgString.length()));
//        Bitmap b =
        ImageView imageView;
        if(convertView == null){
            imageView = new ImageView(c);
        }
        else{
            imageView = (ImageView)convertView;
        }
        imageView.setImageBitmap(deliveredImg);
        return imageView;
    }
}
