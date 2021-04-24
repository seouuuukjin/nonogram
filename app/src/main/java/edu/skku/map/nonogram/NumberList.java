package edu.skku.map.nonogram;

import java.util.ArrayList;

public class NumberList {
    public ArrayList<ArrayList<Integer>> x = new ArrayList<ArrayList<Integer>>();
    public ArrayList<ArrayList<Integer>> y = new ArrayList<ArrayList<Integer>>();

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
}
