package edu.skku.map.nonogram;

import java.util.ArrayList;

public class NumberList {
    public ArrayList<ArrayList<Integer>> x = new ArrayList<ArrayList<Integer>>();
    public ArrayList<ArrayList<Integer>> y = new ArrayList<ArrayList<Integer>>();
    public int[] zeroNumX = new int[20];
    public int[] zeroNumY = new int[20];
    public int[] zeroNumX_Backup = new int[20];
    public int[] zeroNumY_Backup = new int[20];

    public int maxSizeX(){
        int max =0;
        for(int i=0;i<x.size();i++){
            if(max < x.get(i).size()){
                max = x.get(i).size();
            }
        }
        return max;
    }
    public int maxSizeY(){
        int max =0;
        for(int i=0;i<y.size();i++){
            if(max < y.get(i).size()){
                max = y.get(i).size();
            }
        }
        return max;
    }

    public void howmanyZero(){
        int maxX = maxSizeX();
        int maxY = maxSizeY();
        for(int i=0; i<20;i++){
            zeroNumX[i] = maxX - x.get(i).size();
            zeroNumY[i] = maxY - y.get(i).size();
            zeroNumX_Backup[i] = maxX - x.get(i).size();
            zeroNumY_Backup[i] = maxY - y.get(i).size();
//            zeroNumX[i] = maxY - y.get(i).size();
//            zeroNumY[i] = maxX - x.get(i).size();
//            zeroNumX_Backup[i] = maxY - y.get(i).size();
//            zeroNumY_Backup[i] = maxX - x.get(i).size();
        }
    }
}
