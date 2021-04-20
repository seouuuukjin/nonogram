package edu.skku.map.nonogram;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;

public class ImageMaking {
    public ArrayList<Bitmap> slicedImg = new ArrayList<Bitmap>(40);
    public Bitmap coloredImg;

    public void imgSlicing(int originWidth, Bitmap targetImg){
        int standardWidth = originWidth / 20;
        int standardHeight = originWidth / 20;
        for(int i=0; i<20; i++){
            System.out.print("i" + i);
            for(int j=0; j<20; j++){
                slicedImg.add(Bitmap.createBitmap(targetImg, j * standardWidth, i* standardWidth, standardWidth, standardHeight));
                System.out.println("j" + j);
            }
        }
    }
    public void imgBlackWhiteScaling(final Bitmap targetImg, int originWidth, int originHeight){
        coloredImg = Bitmap.createBitmap(originWidth, originHeight, Bitmap.Config.ARGB_4444);
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
                coloredImg.setPixel(i, j, Color.argb(A, grayValue, grayValue, grayValue));
            }
        }
    }
}
