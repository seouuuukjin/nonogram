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
    public int[] zeroNumX = new int[20];
    public int[] zeroNumY = new int[20];
    int maxX;
    int maxY;

    //임시 이미지 변수
    //Bitmap deliveredImg;


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
        for(int i=0; i<20; i++){
            zeroNumX[i] = answerNumberList.zeroNumX_Backup[i];
            zeroNumY[i] = answerNumberList.zeroNumY_Backup[i];
        }
        return (20 + maxX) * (20 + maxY);
    }

    @Override
    public Object getItem(int position) {
        return slicedImg.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"StaticFieldLeak", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int X,Y;
        ImageView image = new ImageView(c);
        TextView textView = new TextView(c);
        textView.setTextSize(2, 6);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        X = position / (maxY + 20);
        Y = position % (maxY + 20);

        if((X < maxX) && (Y < maxY)){
            textView.setText(" ");
            return textView;
        }
        else if((X < maxX)  && (Y >= maxY)){
            //maxX보다 정답숫자배열의 길이가 짧아서 공백이 필요한 경우
            if(zeroNumX[Y - maxY] > 0){
                textView.setText(" ");
                zeroNumX[Y - maxY]--;
            }
            //그렇지 않은 경우
            else{
                textView.setText(Integer.toString(answerNumberList.x.get(Y-maxY).get(X-(answerNumberList.zeroNumX_Backup[Y-maxY])) ) );
            }
            return textView;
        }
        else if((X >= maxX) && (Y < maxY)) {
            //maxX보다 정답숫자배열의 길이가 짧아서 공백이 필요한 경우
            if(zeroNumY[X - maxX] > 0){
                textView.setText(" ");
                zeroNumY[X - maxX]--;
            }
            //그렇지 않은 경우
            else{
                textView.setText(Integer.toString(answerNumberList.y.get(X-maxX).get(Y-(answerNumberList.zeroNumY_Backup[X-maxX])) ) );
            }
            return textView;
        }
        else{
            image.setLayoutParams(new ViewGroup.LayoutParams(size / 25, size / 25));
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            image.setImageBitmap(slicedImg.get((X-maxX) * 20 + Y - maxY));
            //image.setImageBitmap(deliveredImg);
            //System.out.println(slicedImg.get((X-maxX) * 20 + Y - maxY));
            return image;
        }
    }
}
