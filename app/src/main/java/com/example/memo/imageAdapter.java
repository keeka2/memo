package com.example.memo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public class imageAdapter extends BaseAdapter {
    private Context context;
    private List<imageData> dataList;

    public imageAdapter(Context context, List<imageData> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //이미지 리스트에 있는 이미지 넣기
        View v = View.inflate(context, R.layout.image, null);
        ImageView image = (ImageView) v.findViewById(R.id.Image);
        Bitmap bitmap = null;
        byte[] byteArray = dataList.get(position).getMember_Image();
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        image.setImageBitmap(bitmap);

        return v;
    }
}
