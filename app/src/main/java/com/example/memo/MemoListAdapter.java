package com.example.memo;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MemoListAdapter extends BaseAdapter {
    private Context context;
    private List<Data> dataList;


    public MemoListAdapter(Context context, List<Data> dataList){
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
        //layout폴더의 list.xml
        View v = View.inflate(context, R.layout.list, null);
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView body = (TextView) v.findViewById(R.id.body);


        ImageView thumbnail = (ImageView) v.findViewById(R.id.Thumbnail);

        String Body=dataList.get(position).getMember_Body();
        String Title = dataList.get(position).getMember_Title();
        //제목 길이가 20자 이상이면 뒤에 글자들을 ...으로 바꿈
        if(Title.length()>20){
            Title = Title.substring(0,20)+"...";
            title.setText(Title);
        }else{
            title.setText(Title);
        }

        //내용 길이가 35자 이상이면 뒤에 글자들을 ...으로 바꿈
        if(Body.length()>35){
            Body = Body.substring(0,35)+"...";
            body.setText(Body);
        }else{
            body.setText(Body);
        }

        Bitmap bitmap = null;

        byte[] byteArray = dataList.get(position).getMember_Thumbnail();
        //썸네일 사진이 있다면 사진을 넣고 없다면 이미지뷰의 가로 길이를 0으로 만들어 없앰
        if(byteArray!=null){
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            thumbnail.setImageBitmap(bitmap);
        } else{
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) thumbnail.getLayoutParams();
            params.width=0;
        }
        return v;
    }
}
