package edu.skku.map.nonogram;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class ImageMaking {
    public ArrayList<Bitmap> slicedImg;

    public void imgSlicing(int originWidth, Bitmap targetImg){
        int standardWidth = originWidth / 20;
        int standardHeight = originWidth / 20;
        for(int i=0; i<20; i++){
            for(int j=0; j<20; j++){
                slicedImg.add(Bitmap.createBitmap(targetImg, i * standardWidth, j* standardWidth, standardWidth, standardHeight));
                System.out.println("i" + i);
                System.out.println("j" + j);
            }
        }

    }
}
