package edu.skku.map.nonogram;

import java.util.ArrayList;

public class DataModel {
    private String lastBuildDate;
    private String total;
    private String start;
    private String display;
    private ArrayList<ItemList> items = new ArrayList<ItemList>();

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public ArrayList<ItemList> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemList> items) {
        this.items = items;
    }
}
