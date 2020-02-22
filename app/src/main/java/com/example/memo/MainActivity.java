package com.example.memo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    //사진들을 담는 리스트
    public static List<imageData> imageList;

    private ListView listView;
    private Button newMemo;
    MemoListAdapter adapter;
    List<Data> dataList;
    MemoDb mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydb = new MemoDb(this);

        //dataList에 모든 메모 데이터 추가
        dataList = mydb.getAllMemo();

        imageList = new ArrayList<imageData>();

        newMemo = (Button) findViewById(R.id.newMemo);

        listView = (ListView) findViewById(R.id.listview);
        adapter = new MemoListAdapter(getApplicationContext(), dataList);
        listView.setAdapter(adapter);

        //새 메모 작성하기
        newMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, New_Modify_Memo.class);
                MainActivity.this.startActivity(intent);
            }
        });

        //리스트 아이템 누르면 id를 보내며 메모 상세 보기로 이동
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parent.getItemAtPosition(position);
                Bundle dataBundle = new Bundle();
                dataBundle.putString("id",dataList.get(position).getMember_Id());
                Intent intent = new Intent(MainActivity.this, DetailView.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });

    }

    //메인으로 돌아 올 때마다 리스트 초기화 후 데이터를 다시 받아옴
    @Override
    protected void onResume() {
        super.onResume();
        dataList.clear();
        imageList.clear();

        dataList = mydb.getAllMemo();
        adapter = new MemoListAdapter(getApplicationContext(), dataList);
        listView.setAdapter(adapter);

    }
}
