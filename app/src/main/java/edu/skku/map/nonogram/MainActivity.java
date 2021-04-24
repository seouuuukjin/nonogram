package edu.skku.map.nonogram;

import edu.skku.map.nonogram.ImageMaking;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static GridAdapter gridAdapter;
    @SuppressLint("StaticFieldLeak")
    public static GridView gridView;
    public static int wantedSize;

    Button toSearch, toGallery;
    EditText searchKeyword;
    ImageView imageView;
    Context mainContext = this;
    //ArrayList<Bitmap> slicedImg;
    Bitmap origin;
    Display display;
    ImageMaking changedImg;
    String keyword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridView);
        toGallery = findViewById(R.id.toGallery);
        toSearch = findViewById(R.id.toSearch);
        searchKeyword = findViewById(R.id.searchKeyword);
        //imageView = findViewById(R.id.imageView);
        toSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. EditText에서 검색 키워드 받기
                keyword = searchKeyword.getText().toString();
                //1-1. 화면 크기 구하기
                display = ((WindowManager)mainContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                Point size = new Point();
                display.getSize(size); // size.x => 가로 size.y => 세로
                //2. NaverAPI 인스턴스 만들어서 원하는 사진 사이즈, MainContext 전달
                wantedSize = size.x;
                NaverAPI client = new NaverAPI(mainContext);
                //2-1. 키워드로 검색
                client.NaverSearch(keyword);
                //3은 NaverAPI 클래스에서 실행
                //4,5,6,7,8은 ImageMaking에서 실행

            }
        });
        toGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 101);
            }
        });
    }
    //다음은 갤러리 버튼 눌렀을 때, 기본 갤러리앱으로 연결이 되며 일어나는 활동들
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            try {
                //갤러리에서 이미지를 가져온다. 비트맵 형식으로 가져옴
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                origin = BitmapFactory.decodeStream(inputStream);
                //1. 화면 크기 구하기
                display = ((WindowManager)mainContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                Point size = new Point();
                display.getSize(size); // size.x => 가로 size.y => 세로
                wantedSize = size.x;
                //2. 현재 이미지 크기를 resize
                origin = Bitmap.createScaledBitmap(origin, wantedSize, wantedSize, true);
                //2-1. 흰색 바탕 보드 사진 만들기
                changedImg = new ImageMaking(wantedSize);
                Bitmap whiteBoardImg = changedImg.makingNewWhiteBoard(wantedSize, wantedSize);
                //3. 현재 이미지를 흑백으로 변환
                changedImg.BlackWhiteColoredImg = changedImg.imgBlackWhiteScaling(origin, wantedSize, wantedSize);
                //3. 흑백으로 변환된 이미지 20x20 등분
                changedImg.imgSlicing(0, size.x, changedImg.BlackWhiteColoredImg);
                changedImg.imgSlicing(1, size.x, whiteBoardImg);


                System.out.println("img size : " + changedImg.slicedImg.size());
                System.out.println("backup img size : " + changedImg.slicedImg_Backup.size());

                //custom adapter 설정해서 gridview생성해주기
                gridAdapter = new GridAdapter(mainContext, changedImg.slicedImg, wantedSize);

                //4. 나눠진 이미지 보면서 이차원 배열에 알맞은 숫자 채우기
                //NumberList answerNumberList = new NumberList();
                changedImg.fillListWithAnswerNumber(gridAdapter.answerNumberList);

                gridView.setAdapter(gridAdapter);
                //gridview에서 해당 버튼 각각 이벤트 리스너 달아주기
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        System.out.println(changedImg.blackImg);
                            //원래 검정색 타일인 이미지를 눌렀을 때 -> 해당 타일 검정색으로 변환 및 정답과 같은지 체크
                            if(changedImg.slicedImg_Backup.get(position).sameAs(changedImg.blackImg)){
                                //해당 타일 검정색으로 칠하기 - 팍셀 하나하나 칠함
                                changedImg.convertToBlack(changedImg.slicedImg.get(position).getWidth(), changedImg.slicedImg.get(position).getHeight(), changedImg.slicedImg.get(position));
                                gridView.setAdapter(gridAdapter);
                                //전체 타일 돌면서 백언해놓은 원본 흑백 이미지와 같은지 비교
                                int correct = 1;
                                for(int i=0; i<400; i++){
                                    if( !(changedImg.slicedImg.get(i).sameAs(changedImg.slicedImg_Backup.get(i))) )
                                        correct = 0;
                                }
                                //만약 같다면, 토스트 메시지 출력
                                if(correct == 1){
                                    Toast myToast = Toast.makeText(mainContext,"FINISH!", Toast.LENGTH_LONG);
                                    myToast.show();
                                }
                            }
                            //검정색이 아닌 타일을 눌렀을 때 -> 초기화
                            else{
                                //오답 안내 메시지 출력
                                Toast myToast = Toast.makeText(mainContext,"Wrong Tile Clicked", Toast.LENGTH_SHORT);
                                myToast.show();
                                //전체 보드 하얀색으로 다시 초기화
                                for(int i=0; i<400; i++){
                                    if(changedImg.slicedImg.get(i).sameAs(changedImg.blackImg))
                                        changedImg.convertToWhite(changedImg.slicedImg.get(i).getWidth(), changedImg.slicedImg.get(i).getHeight(), changedImg.slicedImg.get(i));
                                }
                                gridView.setAdapter(gridAdapter);
                            }

                    }
                });
                inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}