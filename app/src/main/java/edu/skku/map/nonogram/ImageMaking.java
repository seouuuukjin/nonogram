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
    public int[][] answerSheet = new int[20][20];

    public Bitmap originImg;
    public Bitmap blackImg;

    public int size;
    Context context;


    public ImageMaking(Context context, int size){
        this.context = context;
        this.size = size;
        blackImg = makingBlackImg(size / 20, size / 20);
    }
    public ImageMaking(int size, Bitmap originImg){
        blackImg = makingBlackImg(size / 20, size / 20);
        this.originImg = originImg;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //8. GridAdapter 설정하기
        gridAdapter = new GridAdapter(context, slicedImg, wantedSize);
        //9.전체 사진 리스트 돌면서 측면에 써놓을 정답 숫자들 구하기 + 공백 갯수도 구해놓기
        fillListWithAnswerNumber(gridAdapter.answerNumberList);
        gridAdapter.answerNumberList.howmanyZero();
        //10. gridview의 column 갯수 알맞게 설정
        MainActivity.gridView.setNumColumns(gridAdapter.answerNumberList.maxSizeY() + 20);
        //11. 어댑터 붙이기
        MainActivity.gridView.setAdapter(gridAdapter);
        MainActivity.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("$$$$$$$$$$$$$");
                int X = position / (gridAdapter.maxY + 20);
                int Y = position % (gridAdapter.maxY + 20);
                if((X >= gridAdapter.maxX) && (Y >= gridAdapter.maxY)){
                    int targetPosition = (X-gridAdapter.maxX) * 20 + Y - gridAdapter.maxY;
                    int targetCheckPosition;
                    //원래 검정색 타일인 이미지를 눌렀을 때 -> 해당 타일 검정색으로 변환 및 정답과 같은지 체크
                    if(answerSheet[X - gridAdapter.maxX][Y - gridAdapter.maxY] == 1) {
                        //해당 타일 검정색으로 칠하기 - 팍셀 하나하나 칠함
                        convertToBlack(slicedImg.get(targetPosition).getWidth(), slicedImg.get(targetPosition).getHeight(), slicedImg.get(targetPosition));
                        MainActivity.gridView.setAdapter(gridAdapter);
                        //전체 타일 돌면서 백언해놓은 원본 흑백 이미지와 같은지 비교
                        int correct = 1;
                        for (int i = 0; i < 400; i++) {
                            //targetCheckPosition = (20 + gridAdapter.maxY) * (gridAdapter.maxX + i / 20) + gridAdapter.maxY + i % 20;
                            if (!(slicedImg.get(i).sameAs(slicedImg_Backup.get(i))))
                                correct = 0;
                        }
                        //만약 같다면, 토스트 메시지 출력
                        if (correct == 1) {
                            Toast myToast = Toast.makeText(context, "FINISH!", Toast.LENGTH_LONG);
                            myToast.show();
                        }
                    }
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
//                if(slicedImg_Backup.get(position).sameAs(blackImg)){
//                    //해당 타일 검정색으로 칠하기 - 팍셀 하나하나 칠함
//                    convertToBlack(slicedImg.get(position).getWidth(), slicedImg.get(position).getHeight(), slicedImg.get(position));
//                    MainActivity.gridView.setAdapter(gridAdapter);
//                    //전체 타일 돌면서 백언해놓은 원본 흑백 이미지와 같은지 비교
//                    int correct = 1;
//                    for(int i=0; i<400; i++){
//                        if( !(slicedImg.get(i).sameAs(slicedImg_Backup.get(i))) )
//                            correct = 0;
//                    }
//                    //만약 같다면, 토스트 메시지 출력
//                    if(correct == 1){
//                        Toast myToast = Toast.makeText(context,"FINISH!", Toast.LENGTH_LONG);
//                        myToast.show();
//                    }
//                }
//                //검정색이 아닌 타일을 눌렀을 때 -> 초기화
//                else{
//                    //오답 안내 메시지 출력
//                    Toast myToast = Toast.makeText(context,"Wrong Tile Clicked", Toast.LENGTH_SHORT);
//                    myToast.show();
//                    //전체 보드 하얀색으로 다시 초기화
//                    for(int i=0; i<400; i++){
//                        if(slicedImg.get(i).sameAs(blackImg))
//                            convertToWhite(slicedImg.get(i).getWidth(), slicedImg.get(i).getHeight(), slicedImg.get(i));
//                    }
//                    MainActivity.gridView.setAdapter(gridAdapter);
//                }
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
            //6. 이미지 2개 각각 20x20으로 나누기 -> 나눈 결과는 slicedImg , slicedImg_Backup로 들어가게 된다.
            imgSlicing(0, size, originImg); //원본사진
            imgSlicing(1, size, whiteBoardImg); //그냥 흰색 사진
            //7. 이미지 흑백 변환
            imgListBlackWhiteScaling();
            //8. GridView 세팅하기
            // -> Main Thread 에서 해야하기 때문에, OnPostExecute 에서 실행

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
    }
    public void imgListBlackWhiteScaling(/*int originWidth, int originHeight*/){

        for(int i=0; i<slicedImg_Backup.size(); i++){
            Bitmap targetImg = slicedImg_Backup.get(i);
            int R, G, B,px;
            int blackPxnum = 0, whitePxnum = 0;

            for(int x=0; x<targetImg.getWidth(); x++){
                for(int y=0; y<targetImg.getHeight(); y++){
                    px = targetImg.getPixel(x,y);
                    R = Color.red(px);
                    G = Color.green(px);
                    B = Color.blue(px);
                    int grayValue = (int)(0.2989 * R + 0.5870 * G + 0.1140 * B);
                    if(grayValue > 128){
                        whitePxnum++;
                    }
                    else{
                        blackPxnum++;
                    }
                }
            }
            if(blackPxnum >= whitePxnum){
                convertToBlack(slicedImg_Backup.get(i).getWidth(), slicedImg_Backup.get(i).getHeight(), slicedImg_Backup.get(i));
                answerSheet[i/20][i%20] = 1;
            }
            else{
                convertToWhite(slicedImg_Backup.get(i).getWidth(), slicedImg_Backup.get(i).getHeight(), slicedImg_Backup.get(i));
                answerSheet[i/20][i%20] = 0;
            }
        }
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
    public Boolean blackCheck(Bitmap targetImg){
        int R, G, B, px;
        px = targetImg.getPixel(3,5);
        R = Color.red(px);
        G = Color.green(px);
        B = Color.blue(px);
        return R == 0 && G == 0 && B == 0;
    }
    public void fillListWithAnswerNumber(NumberList listInstance){
        //처음에 가로 부분 시작
        for(int y =0; y<20; y++){
            int num = 0;
            ArrayList<Integer> data = new ArrayList<Integer>();
            for(int x=0; x<20; x++){
                if(blackCheck(slicedImg_Backup.get(20 * x + y))){
                    System.out.println("(y, x) = " + y + ", " + x);
                    num++;
                }
                else {
                    if(num != 0 ) {
                        data.add(num);
                        num = 0;
                        System.out.println(data.size());
                    }
                }
            }
            if(num != 0){
                System.out.println("filling(y, x) = " + y + ", 20");
                data.add(num);
                System.out.println(data.size());
            }
            listInstance.x.add(data);
        }
        //세로 부분 시작
        for(int x =0; x<20; x++){
            int num = 0;
            ArrayList<Integer> data = new ArrayList<Integer>();
            for(int y=0; y<20; y++){
                if(blackCheck(slicedImg_Backup.get(20 * x + y))){
                    num++;
                }
                else {
                    if(num != 0 ) {
                        System.out.println("(y, x) = " + y + ", " + x);
                        data.add(num);
                        num = 0;
                        System.out.println(data.size());
                    }
                }
            }
            if(num != 0){
                System.out.println("(y, x) = 20, " + x);
                data.add(num);
                System.out.println(data.size());
            }
            listInstance.y.add(data);
        }
    }
}
