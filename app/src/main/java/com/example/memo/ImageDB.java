package com.example.memo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

//이미지 디비(각 메모의 이미지들을 저장)
public class ImageDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MemoImage.db";
    public static final String MEMO_COLUMN_ID="id";
    public static final String MEMO_COLUMN_IMAGE="image";

    public ImageDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table all_Image "+ "(id integer, image blob)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS all_Image");
        onCreate(db);
    }


    //이미지들 넣기
    public boolean insertAllImage(Integer id, List<imageData> imageData)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //이미지 개수만큼 실행
        for(int i = 0;i<imageData.size();i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", id);
            contentValues.put("image", imageData.get(i).getMember_Image());
            db.insert("all_Image", null, contentValues);

        }
        return true;
    }

    //id와 관현된 이미지들 삭제
    public boolean deleteImage(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("all_Image","id = ?", new String[] { Integer.toString(id) });
        return true;
    }

    //id와 관련된 이미지들 가져오기
    public List<imageData> getAllImage(Integer id){
        List<imageData> array_List = new ArrayList<imageData>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select image from all_Image where id=" + id + "", null);
        res.moveToFirst();
        while (res.isAfterLast() == false){
            imageData data = new imageData(res.getBlob(res.getColumnIndex(MEMO_COLUMN_IMAGE)));
            array_List.add(data);
            res.moveToNext();
        }
        db.close();
        return array_List;
    }


}
