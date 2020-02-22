package com.example.memo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MemoDb extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "MyMemo.db";
    public static final String MEMO_TABLE_NAME="memo";
    public static final String MEMO_COLUMN_ID="id";
    public static final String MEMO_COLUMN_TITLE="title";
    public static final String MEMO_COLUMN_BODY="body";
    public static final String MEMO_COLUMN_THUMBNAIL="thumbnail";


    public MemoDb(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table memo "+ "(id integer , title text, body text, thumbnail blob)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS memo");
        onCreate(db);
    }

    //사진이 첨부되었을 때 메모db에 데이터 넣기
    public boolean insertMemo(Integer id, String title, String body, byte[] thumbnail){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id", id);
        contentValues.put("title", title);
        contentValues.put("body", body);
        contentValues.put("thumbnail", thumbnail);
        db.insert("memo", null, contentValues);
        return true;
    }

    //사진이 없을 때 메모db에 데이터 넣기
    public boolean insertMemo(Integer id, String title, String body) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id", id);
        contentValues.put("title", title);
        contentValues.put("body", body);
        db.insert("memo", null, contentValues);
        return true;
    }

    //관련 id의 데이터 가져오기
    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from memo where id=" + id +"", null);
        return res;
    }


    //썸네일 사진이 있을 때 메모 업데이트
    public boolean updateMemo(Integer id, String title, String body, byte[] thumbnail){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("body", body);
        contentValues.put("thumbnail", thumbnail);

        db.update("memo", contentValues,"id = ?", new String[] { Integer.toString(id) });
        return true;
    }

    //썸네일 사진이 없을 때 메모 업데이트(기존에 썸네일이 있었을 수도 있으므로 썸네일은 null처리)
    public boolean updateMemo(Integer id, String title, String body){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        byte[] thumbnail=null;
        contentValues.put("title", title);
        contentValues.put("body", body);
        contentValues.put("thumbnail", thumbnail);
        db.update("memo", contentValues,"id = ?", new String[] { Integer.toString(id) });
        return true;
    }



    //해당 id의 메모 삭제
    public boolean deleteMemo(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("memo","id = ?", new String[] { Integer.toString(id) });
        return true;
    }

    //모든 메모 가져오기
    public List<Data> getAllMemo(){
        List<Data> array_List = new ArrayList<Data>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from memo order by id desc", null);
        res.moveToFirst();
        while (res.isAfterLast() == false){
            Data data = new Data(res.getString(res.getColumnIndex(MEMO_COLUMN_ID)), res.getString(res.getColumnIndex(MEMO_COLUMN_TITLE)), res.getString(res.getColumnIndex(MEMO_COLUMN_BODY)), res.getBlob(res.getColumnIndex(MEMO_COLUMN_THUMBNAIL)));
            array_List.add(data);
            res.moveToNext();
        }
        return array_List;
    }

    //새 메모 작성 시 아이디 생성
    //메모가 있을 경우 디비에 있는 가장 큰 id에 1을 더해서 리턴
    //메모가 없으면 1 리턴
    public int getNextId(){
        int i=0;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select MAX(id) as id from memo", null);
        if(res!=null){
            res.moveToFirst();
            i=res.getInt(res.getColumnIndex(MEMO_COLUMN_ID));
        }
        return i+1;
    }

}
