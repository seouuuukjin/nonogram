package edu.skku.map.nonogram;

import edu.skku.map.nonogram.ImageMaking;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    Button toSearch, toGallery;
    EditText searchKeyword;
    GridAdapter gridAdapter;
    ImageView imageView;
    Context mainContext = this;
    //ArrayList<Bitmap> slicedImg;
    Bitmap origin;
    Display display;
    ImageMaking changedImg = new ImageMaking();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = findViewById(R.id.gridView);
        toGallery = findViewById(R.id.toGallery);
        toSearch = findViewById(R.id.toSearch);
        searchKeyword = findViewById(R.id.searchKeyword);
        //imageView = findViewById(R.id.imageView);

        toGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 101);
                //imageView.setImageBitmap(origin);
            }
        });

//        gridAdapter = new GridAdapter(mainContext, origin);
//        gridView.setAdapter(gridAdapter);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                //해당 아이템 클릭되었을 때 행동 정의
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            try {
                //갤러리에서 이미지를 가져온다. 비트맵 형식으로 가져옴
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                origin = BitmapFactory.decodeStream(inputStream);
                //imageView.setImageBitmap(origin);
                //이제 받아온 비트맵 이미지를 20x20으로 잘라서 리스트에 저장한다.
                System.out.println("시작1");
                //1. 화면 크기 구하기
                display = ((WindowManager)mainContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                Point size = new Point();
                display.getSize(size); // size.x => 가로 size.y => 세로
                int wantedSize = size.x;
                //2. 현재 이미지 크기를 resize
                origin = Bitmap.createScaledBitmap(origin, wantedSize, wantedSize, true);
                //3. 이미지 20등분
                changedImg.imgSlicing(size.x, origin);
                gridAdapter = new GridAdapter(mainContext, changedImg.slicedImg);
                gridView.setAdapter(gridAdapter);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        else if(requestCode == 101 && resultCode == RESULT_CANCELED){
//
//        }
    }
}