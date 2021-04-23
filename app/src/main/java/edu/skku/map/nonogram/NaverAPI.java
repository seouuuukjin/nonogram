package edu.skku.map.nonogram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static edu.skku.map.nonogram.MainActivity.gridAdapter;

public class NaverAPI {
    String clientID = "KRIEpEMi28TqAmxIyo5Y";
    String clientSecret = "5RBthImUlc";
    int size;
    Bitmap returnImg;

    @SuppressLint("StaticFieldLeak")
    Context context;
    public NaverAPI(int size, Context context){
        this.size = size;
        this.context = context;
    }
    public NaverAPI(){}

    public synchronized Bitmap NaverSearch(String keyword){
        //final Bitmap[] returnImg = new Bitmap[1];

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder RequestUrl = HttpUrl.parse("https://openapi.naver.com/v1/search/image").newBuilder();
        RequestUrl.addQueryParameter("query", keyword);
        String url = RequestUrl.build().toString();
        System.out.println(url);

        Request req = new Request.Builder().addHeader("X-Naver-Client-id", clientID).addHeader("X-Naver-Client-Secret",clientSecret).url(url).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String resp = response.body().string();
                System.out.println(resp);
                //3. imgWorker 인스턴스 생성
                ImageMaking imageWorker = new ImageMaking(context, size);
                //3-1. 추출한 이미지 링크를 담아서 task 실행과 함께 전달
                imageWorker.execute();
//                ((MainActivity)context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        System.out.println(resp);
//                    }
//                });
//                ((MainActivity)context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
            }
        });
        return returnImg;
    }
}
