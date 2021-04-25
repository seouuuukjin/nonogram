package edu.skku.map.nonogram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static edu.skku.map.nonogram.MainActivity.wantedSize;

public class NaverAPI {
    String clientID = "KRIEpEMi28TqAmxIyo5Y";
    String clientSecret = "5RBthImUlc";
    Bitmap returnImg;

    @SuppressLint("StaticFieldLeak")
    Context context;
    public NaverAPI(Context context){
        this.context = context;
    }
    public NaverAPI(){}

    public synchronized void NaverSearch(String keyword){
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder RequestUrl = HttpUrl.parse("https://openapi.naver.com/v1/search/image").newBuilder();
        RequestUrl.addQueryParameter("query", keyword);
        String url = RequestUrl.build().toString();

        Request req = new Request.Builder().addHeader("X-Naver-Client-id", clientID).addHeader("X-Naver-Client-Secret",clientSecret).url(url).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String resp = response.body().string();

                //3. imgWorker 인스턴스 생성
                ImageMaking imageWorker = new ImageMaking(context, wantedSize);
                //3-1. 추출한 이미지 링크를 담아서 task 실행과 함께 전달
                imageWorker.execute(parseResponse(resp));
            }
        });
    }
    public String parseResponse(String resp){
        String firstImgLink;
        Gson gson = new Gson();
        final DataModel totalData = gson.fromJson(resp, DataModel.class);
        firstImgLink = totalData.getItems().get(0).link;

        return firstImgLink;
    }
}
