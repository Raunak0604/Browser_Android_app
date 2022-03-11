package com.example.browser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DBhandler extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_Name = "HistoryDB";
    private static final String HISTORY_TABLE = "Historys";
    private static final String ID = "id";
    private static final String URL = "URL";
    private static final String TITLE = "title";


    public DBhandler(@Nullable  Context context){
        super(context,DB_Name,null,VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS " + HISTORY_TABLE + "("
                +ID +" integer PRIMARY KEY autoincrement , "
                +TITLE + " TEXT ,"
                +URL + " TEXT )";
        db.execSQL(CREATE_HISTORY_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String sql = "DROP TABLE " + HISTORY_TABLE;
        db.execSQL(sql);
        onCreate(db);
    }
    public void addHistory(History History) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, History.getHistory());
        values.put(URL, History.getHistoryURL());
        db.insert(HISTORY_TABLE, null, values);
    }
    public History getHistory(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                HISTORY_TABLE , new String[]{ID,TITLE,URL},
                ID+"=?",new String[] {String.valueOf(id)},
                null,null,null,null);
        History History;
        if(cursor!=null){
            cursor.moveToFirst();
            History = new History(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2)
            );
            return History;
        }else{
            return null;
        }
    }
    public ArrayList<History> getAllHistory(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList <History> HistoryList = new ArrayList<>();
        String query = "SELECT * FROM "+ HISTORY_TABLE;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                History History = new History();
                History.setHistoryId(Integer.parseInt(cursor.getString(0)));
                History.setHistory(cursor.getString(1));
                History.setHistoryURL(cursor.getString(2) );
                HistoryList.add(History);
            }while(cursor.moveToNext());
        }
        return HistoryList;
    }
    public void deleteAllHistory(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ HISTORY_TABLE);
    }
    public int updateHistory(History History){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE,History.getHistory());
        values.put(URL,History.getHistoryURL());
        return db.update(
                HISTORY_TABLE,
                values,
                ID + " = ?",
                new String[]{String.valueOf(History.getHistoryId())});
    }
    public void deleteHistory(History History){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                HISTORY_TABLE,
                ID + " = ?",
                new String[]{String.valueOf(History.getHistoryId())}
        );
        db.close();
    }
    public int getHistorysCount(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * From " + HISTORY_TABLE;
        Cursor cursor = db.rawQuery(query,null);
        return cursor.getCount();
    }
}
