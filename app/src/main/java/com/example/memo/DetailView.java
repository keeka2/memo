package com.example.memo;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.memo.MainActivity.imageList;

public class DetailView extends AppCompatActivity {
    private TextView title;
    private TextView body;
    private Button modify;
    private Button delete;
    MemoDb mydb;
    ImageDB myImageDb;
    String pre_title;
    String pre_body;
    static imageAdapter adapter;
    ListView listView;
    Bundle extras;
    int id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        title = (TextView) findViewById(R.id.title);
        body = (TextView) findViewById(R.id.body);
        modify = (Button) findViewById(R.id.modify);
        delete = (Button) findViewById(R.id.delete);
        listView = (ListView) findViewById(R.id.listview);
        adapter = new imageAdapter(getApplicationContext(), imageList);

        extras = getIntent().getExtras();


        //받아온 id로 제목, 내용, 사진등의 데이터를 받아와서 보여줌
        setData();


        //수정하기 버튼을 누르면 id, 제목, 내용을 보내며 수정 페이지로 넘어감
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle dataBundle = new Bundle();
                dataBundle.putString("id",extras.getString("id"));
                dataBundle.putString("title",pre_title);
                dataBundle.putString("body",pre_body);
                Intent intent = new Intent(DetailView.this, New_Modify_Memo.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });

        //삭제 버튼을 누르면 데이터 지우고 메인으로 돌아옴
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
    }

    //메모 삭제
    private void delete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("삭제하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //삭제를 완료하면 메인으로 돌아감
                        if(myImageDb.deleteImage(id) && mydb.deleteMemo(id)){
                            Toast.makeText(getApplicationContext(),"삭제 되었습니다.",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"취소되었습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

    //데이터 불러오기
    private void setData(){
        mydb = new MemoDb(this);
        myImageDb = new ImageDB(this);
        extras = getIntent().getExtras();

        //MemoDb에서 id에 해당하는 데이터 받아와서 반영하기
        id = Integer.parseInt(extras.getString("id"));
        Cursor rs = mydb.getData(id);
        rs.moveToFirst();
        pre_title = rs.getString(rs.getColumnIndex(MemoDb.MEMO_COLUMN_TITLE));
        pre_body = rs.getString(rs.getColumnIndex(MemoDb.MEMO_COLUMN_BODY));
        byte[] pre_byteArray = rs.getBlob(rs.getColumnIndex(MemoDb.MEMO_COLUMN_THUMBNAIL));

        if(!rs.isClosed()){
            rs.close();
        }
        title.setText(pre_title);
        body.setText(pre_body);

        //썸네일 사진이 존재 한다면 해당 id의 사진들을 imageList에 추가
        if(pre_byteArray!=null){
            imageList.clear();
            imageList = myImageDb.getAllImage(id);
            adapter = new imageAdapter(getApplicationContext(), imageList);
            listView.setAdapter(adapter);
            setListViewHeightBasedOnChildren(listView);
        } else{
            imageList.clear();
            adapter = new imageAdapter(getApplicationContext(), imageList);
            listView.setAdapter(adapter);
            setListViewHeightBasedOnChildren(listView);
        }
    }

    //이미지 리스트 크기 조절
    private void setListViewHeightBasedOnChildren(ListView listView) {
        if (adapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            //listItem.measure(0, 0);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight;
        listView.setLayoutParams(params);

        listView.requestLayout();
    }



    //돌아 올 때마다 데이터 초기화하여 다시 보여줌
    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }
}
