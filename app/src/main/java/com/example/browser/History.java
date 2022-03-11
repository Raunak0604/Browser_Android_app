package com.example.browser;

public class History {
    public int HistoryId;
    public String History;
    public String HistoryURL;
    public History(){
        this.History="";
    }
    public History(int vHistoryId,String vHistory,String vHistoryURL){
        this.History = vHistory;
        this.HistoryId = vHistoryId;
        this.HistoryURL = vHistoryURL;
    }
    public History(String vHistory,String vHistoryURL){
        this.History = vHistory;
        this.HistoryURL = vHistoryURL;
    }
    public int getHistoryId(){
        return HistoryId;
    }
    public void setHistoryId(int vHistoryId){
        this.HistoryId = vHistoryId;
    }
    public String getHistoryURL(){
        return HistoryURL;
    }
    public void setHistoryURL(String vURL){
        HistoryURL = vURL;
    }
    public String getHistory(){
        return History;
    }
    public void setHistory(String vHistory){
        History = vHistory;
    }
}

