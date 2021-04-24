package edu.skku.map.nonogram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.WorkSource;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

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
    //public ArrayList<Bitmap> slicedImg_White = new ArrayList<Bitmap>(400);

    public Bitmap BlackWhiteColoredImg;
    public Bitmap originImg;
    //public Bitmap whiteImg;
    public Bitmap blackImg;

    public int size;
    Context context;


    public ImageMaking(Context context, int size){
        this.context = context;
        this.size = size;
        blackImg = makingBlackImg(size / 20, size / 20);
//        whiteImg = makingNewWhiteBoard(size, size);
//        imgSlicing(2, size, whiteImg);
    }
    public ImageMaking(int size){
        blackImg = makingBlackImg(size / 20, size / 20);
//        whiteImg = makingNewWhiteBoard(size, size);
//        imgSlicing(2, size, whiteImg);
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
                if(slicedImg_Backup.get(position).sameAs(blackImg)){
                    //해당 타일 검정색으로 칠하기 - 팍셀 하나하나 칠함
                    convertToBlack(slicedImg.get(position).getWidth(), slicedImg.get(position).getHeight(), slicedImg.get(position));
                    MainActivity.gridView.setAdapter(gridAdapter);
                    //전체 타일 돌면서 백언해놓은 원본 흑백 이미지와 같은지 비교
                    int correct = 1;
                    for(int i=0; i<400; i++){
                        if( !(slicedImg.get(i).sameAs(slicedImg_Backup.get(i))) )
                            correct = 0;
                    }
                    //만약 같다면, 토스트 메시지 출력
                    if(correct == 1){
                        Toast myToast = Toast.makeText(context,"FINISH!", Toast.LENGTH_LONG);
                        myToast.show();
                    }
                }
                //검정색이 아닌 타일을 눌렀을 때 -> 초기화
                else{
                    //오답 안내 메시지 출력
                    Toast myToast = Toast.makeText(context,"Wrong Tile Clicked", Toast.LENGTH_SHORT);
                    myToast.show();
                    //전체 보드 하얀색으로 다시 초기화
                    for(int i=0; i<400; i++){
                        if(slicedImg.get(i).sameAs(blackImg))
                            convertToWhite(slicedImg.get(i).getWidth(), slicedImg.get(i).getHeight(), slicedImg.get(i));
                    }
                    MainActivity.gridView.setAdapter(gridAdapter);
                }
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
            Bitmap whiteBoardImg = makingNewWhiteBoard(size, size);
            //5. 이미지 사이즈 정사각형으로 조정
            originImg = Bitmap.createScaledBitmap(naverResult.returnImg, size, size, true);
            //6. 이미지 흑백 변환
            BlackWhiteColoredImg = imgBlackWhiteScaling(originImg, size, size);
            //7. 이미지 2개 각각 20x20으로 나누기 -> 나눈 결과는 slicedImg , slicedImg_Backup로 들어가게 된다.
            imgSlicing(0, size, BlackWhiteColoredImg); //원본사진
            imgSlicing(1, size, whiteBoardImg); //그냥 흰색 사진
            //7-1. 이미지 나눠진것 보면서 이차원 배열에 각 숫자들 채워넣기
            NumberList answerNumberList = new NumberList();

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
        else if(mode == 1){
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    slicedImg.add(Bitmap.createBitmap(targetImg, j * standardWidth, i * standardWidth, standardWidth, standardHeight));
                }

            }
        }
//        else if(mode == 2){
//            for(int i=0; i<20; i++){
//                for(int j=0; j<20; j++){
//                    slicedImg_White.add(Bitmap.createBitmap(targetImg, j * standardWidth, i* standardWidth, standardWidth, standardHeight));
//                }
//            }
//        }
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
    public Bitmap makingNewWhiteBoard(int originWidth, int originHeight){
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
    public Bitmap convertToWhite(int width, int height, Bitmap workingImg){
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                workingImg.setPixel(i, j, Color.WHITE);
            }
        }
        return workingImg;
    }
    public void fillListWithAnswerNumber(NumberList listInstance){
        //처음에 가로 부분 시작
        for(int x =0; x<20; x++){
            int num = 0;
            ArrayList<Integer> data = new ArrayList<Integer>();
            for(int y=0; y<20; y++){
                if(slicedImg_Backup.get(20 * y + x).sameAs(blackImg)){
                    num++;
                }
                else {
                    if(num != 0 ) {
                        data.add(num);
                        num = 0;
                    }
                }
            }
            if(num != 0){
                data.add(num);
            }
            listInstance.x.add(data);
            System.out.println("------------\nInstance size-x : " + listInstance.x.size());
            System.out.println("Instance size-y : " + listInstance.y.size());
            System.out.println("tmp data list size : " + data.size());
        }
        //세로 부분 시작
        for(int y =0; y<20; y++){
            int num = 0;
            ArrayList<Integer> data = new ArrayList<Integer>();
            for(int x=0; x<20; x++){
                if(slicedImg_Backup.get(20 * y + x).sameAs(blackImg)){
                    num++;
                }
                else {
                    if(num != 0 ) {
                        data.add(num);
                        num = 0;
                    }
                }
            }
            if(num != 0){
                data.add(num);
            }
            listInstance.y.add(data);
            System.out.println("------------\nInstance size-x : " + listInstance.x.size());
            System.out.println("Instance size-y : " + listInstance.y.size());
            System.out.println("tmp data list size : " + data.size());
        }
    }
}
