package edu.skku.map.nonogram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.WorkSource;
import android.view.View;
import android.widget.AdapterView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static edu.skku.map.nonogram.MainActivity.gridAdapter;
import static edu.skku.map.nonogram.MainActivity.wantedSize;

public class ImageMaking extends AsyncTask<String, String, String> {
    public ArrayList<Bitmap> slicedImg = new ArrayList<Bitmap>(400);
    public ArrayList<Bitmap> slicedImg_Backup = new ArrayList<Bitmap>(400);

    public Bitmap BlackWhiteColoredImg;
    public Bitmap originImg;
    public int size = wantedSize;
    Context context;
    Bitmap blackImg = makingBlackImg(size / 20, size / 20);

    public ImageMaking(Context context, int size){
        this.context = context;
        this.size = size;
    }
    public ImageMaking(){
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        gridAdapter = new GridAdapter(context, slicedImg, wantedSize);
        MainActivity.gridView.setAdapter(gridAdapter);
        MainActivity.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("$$$$$$$$$$$$$");

            }
        });
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            //4. 전달받은 string으로 원하는 이미지 링크로 req 보내기
            NaverAPI naverResult = new NaverAPI();
            URL url = new URL(strings[0]);
            HttpURLConnection getImg = (HttpURLConnection)url.openConnection();
            getImg.setDoInput(true);
            getImg.connect();
            //4-1. 이미지를 받아오기
            InputStream is = getImg.getInputStream();
            naverResult.returnImg = BitmapFactory.decodeStream(is);
            //4-2. 흰색 바탕 이미지 만들기
            Bitmap whiteBoardImg = makingWhiteBoard(size, size);
            //5. 이미지 사이즈 정사각형으로 조정
            originImg = Bitmap.createScaledBitmap(naverResult.returnImg, size, size, true);
            //6. 이미지 흑백 변환
            BlackWhiteColoredImg = imgBlackWhiteScaling(originImg, size, size);
            //7. 이미지 2개 각각 20x20으로 나누기 -> 나눈 결과는 slicedImg , slicedImg_Backup로 들어가게 된다.
            imgSlicing(0, size, BlackWhiteColoredImg); //원본사진
            imgSlicing(1, size, whiteBoardImg); //그냥 흰색 사진

            //8. GridView 세팅하기
            // -> Main Thread 에서 해야하기 때문에, PostExcute에서 실행

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void imgSlicing(int mode, int originWidth, Bitmap targetImg){
        int standardWidth = originWidth / 20;
        int standardHeight = originWidth / 20;
        if(mode == 0){
            for(int i=0; i<20; i++){
                for(int j=0; j<20; j++){
                    slicedImg_Backup.add(Bitmap.createBitmap(targetImg, j * standardWidth, i* standardWidth, standardWidth, standardHeight));
                }
            }
        }
        else {
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    slicedImg.add(Bitmap.createBitmap(targetImg, j * standardWidth, i * standardWidth, standardWidth, standardHeight));
                }

            }
        }
    }
    public Bitmap imgBlackWhiteScaling(final Bitmap targetImg, int originWidth, int originHeight){
        Bitmap workingImg = Bitmap.createBitmap(originWidth, originHeight, Bitmap.Config.ARGB_4444);
        int R, G, B, A, px;
        for(int i=0; i<originWidth; i++){
            for(int j=0; j<originHeight; j++){
                px = targetImg.getPixel(i,j);
                A = Color.alpha(px);
                R = Color.red(px);
                G = Color.green(px);
                B = Color.blue(px);
                int grayValue = (int)(0.2989 * R + 0.5870 * G + 0.1140 * B);

                if(grayValue > 128)
                    grayValue = 255;
                else
                    grayValue = 0;
                workingImg.setPixel(i, j, Color.argb(A, grayValue, grayValue, grayValue));
            }
        }
        return workingImg;
    }
    public Bitmap makingWhiteBoard(int originWidth, int originHeight){
        Bitmap workingImg = Bitmap.createBitmap(originWidth, originHeight, Bitmap.Config.ARGB_4444);
        for(int i=0; i<originHeight; i++){
            for(int j=0; j<originWidth; j++){
                workingImg.setPixel(i, j, Color.WHITE);
            }
        }
        return workingImg;
    }
    public Bitmap makingBlackImg(int width, int height){
        Bitmap workingImg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                workingImg.setPixel(i, j, Color.BLACK);
            }
        }
        return workingImg;
    }
    public Bitmap convertToBlack(int width, int height, Bitmap workingImg){
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                workingImg.setPixel(i, j, Color.BLACK);
            }
        }
        return workingImg;
    }
}
