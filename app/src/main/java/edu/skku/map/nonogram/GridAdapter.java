package edu.skku.map.nonogram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.solver.state.Dimension;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    private final Context c;
    private String imgString;
    public ArrayList<Bitmap> slicedImg;
    int size;
    public NumberList answerNumberList;

    int maxX;
    int maxY;

    //임시 이미지 변수
    Bitmap deliveredImg;

//    public GridAdapter(Context c){
//        this.c = c;
//    }
//    public GridAdapter(Context c, Bitmap bm, int size){
//        this.c = c;
//        this.deliveredImg = bm;
//        this.size = size;
//
//    }

    public GridAdapter(Context c, ArrayList<Bitmap> slicedImg, int size){
        this.c = c;
        this.slicedImg = (ArrayList<Bitmap>) slicedImg.clone();
        this.size = size;
        answerNumberList = new NumberList();
    }
    @Override
    public int getCount() {
        maxX = answerNumberList.maxSizeX();
        maxY = answerNumberList.maxSizeY();
        System.out.println("max => " + maxX + " => " + maxY);
        //System.out.println((20 + maxX) * (20 + maxY));
        return (20 + maxX) * (20 + maxY);

        //return slicedImg.size();
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

    @SuppressLint({"StaticFieldLeak", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int X,Y;
        ImageView image = new ImageView(c);
        TextView textView = new TextView(c);
        textView.setTextSize(2, 6);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        X = position / (maxY + 20);
        Y = position % (maxY + 20);

        System.out.println("(X, Y) = "+ X + ", " + Y);
//        System.out.println(position);
        if(X < maxX && Y < maxY){
            textView.setText(" ");
            return textView;
        }
        else if((X < maxX)  && (Y >= maxY)){
            //maxX보다 정답숫자배열의 길이가 짧아서 공백이 필요한 경우
            if(answerNumberList.zeroNumX[Y - maxY] > 0){
                textView.setText(" ");
                answerNumberList.zeroNumX[Y - maxY]--;
            }
            //그렇지 않은 경우
            else{
                textView.setText(Integer.toString(answerNumberList.x.get(Y-maxY).get(X-(answerNumberList.zeroNumX_Backup[Y-maxY])) ) );
            }
            return textView;
        }
        else if((X >= maxX) && (Y < maxY)) {
            //maxX보다 정답숫자배열의 길이가 짧아서 공백이 필요한 경우
            if(answerNumberList.zeroNumY[X - maxX] > 0){
                textView.setText(" ");
                answerNumberList.zeroNumY[X - maxX]--;
            }
            //그렇지 않은 경우
            else{
                textView.setText(Integer.toString(answerNumberList.y.get(X-maxX).get(Y-(answerNumberList.zeroNumY_Backup[X-maxX])) ) );
            }
            return textView;
        }
        else{
            image.setLayoutParams(new ViewGroup.LayoutParams(size / 20, size / 20));
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            image.setImageBitmap(slicedImg.get((X-maxX) * 20 + Y - maxY));
            //image.setImageBitmap(deliveredImg);
            //System.out.println(slicedImg.get(position));
            return image;
        }
//        image.setLayoutParams(new ViewGroup.LayoutParams(size / 20, size / 20));
//        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        //image.setImageBitmap(deliveredImg);
//        image.setImageBitmap(slicedImg.get(position));
//        //System.out.println(slicedImg.get(position));
//        return image;
//        //imageView.setImageBitmap(deliveredImg);
    }
}
