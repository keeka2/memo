package com.example.memo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.memo.MainActivity.imageList;

public class New_Modify_Memo extends AppCompatActivity {
    private TextView title;
    private TextView body;
    private Button add;
    private Button add_image;

    MemoDb mydb;
    ImageDB myImageDb;
    imageAdapter adapter;
    ListView listView;
    Bundle extras;
    Bitmap bitmap;
    int id=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__modify__memo);
        title = (EditText) findViewById(R.id.title);
        body = (EditText) findViewById(R.id.body);
        add = (Button) findViewById(R.id.add);
        add_image = (Button) findViewById(R.id.add_image);

        mydb = new MemoDb(this);
        myImageDb = new ImageDB(this);


        listView = (ListView) findViewById(R.id.listview);
        adapter = new imageAdapter(getApplicationContext(), imageList);
        listView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listView);



        extras = getIntent().getExtras();

        //extras가 null 이면 새 메모 작성을 누른 경우, null 이 아니라면 수정하기를 누른 경우
        if (extras != null){
            //id, 제목, 내용 받아옴
            id = Integer.parseInt(extras.getString("id"));
            title.setText(extras.getString("title"));
            body.setText(extras.getString("body"));
        }
        else {
            //저장 할 id를 만듬(임시 id)
            id=mydb.getNextId();
        }

        //저장하기 버튼: 제목, 내용, 사진이 모두 비어있다면 저장 안함
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageList.size()==0 && title.getText().toString().length()==0 && body.getText().toString().length()==0){
                    no_data_show();
                }
                else{
                    insert();
                }

            }
        });

        //사진 추가하기 버튼
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_show();
            }
        });

        //사진을 길게 누를시 사진 삭제 가능
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                delete_show(position);
                return true;
            }
        });


    }



    private void insert(){
        //수정 페이지인지 새 메모 작성 페이지 인지 확인
        if (extras != null){
            //이미지 첨부가 있으면
            if(imageList.size() != 0) {
                //기존에 있던 이미지들을 지움
                if (myImageDb.deleteImage(id)) {
                    //memoDb에 id,제목, 내용, 썸네일 넣고 imageDb에 첨부된 이미지들을 넣습니다
                    if (mydb.updateMemo(id, title.getText().toString(), body.getText().toString(), imageList.get(0).getMember_Image()) && myImageDb.insertAllImage(id, imageList)) {
                        Toast.makeText(getApplicationContext(), "수정됨", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "수정안됨", Toast.LENGTH_SHORT).show();
                    }
                }
            } else { //이미지 첨부가 없음
                //기존에 있떤 이미지들을 지움
                if (myImageDb.deleteImage(id)) {
                    //memoDb에 id,제목,내용을 넣습니다
                    if (mydb.updateMemo(id, title.getText().toString(), body.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "수정됨", Toast.LENGTH_SHORT).show();
                        finish();
                } else {
                    Toast.makeText(getApplicationContext(), "수정안됨", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
        //새 메모 작성이면 insert
        else {
            if (imageList.size() != 0) {
                if (mydb.insertMemo(id,title.getText().toString(), body.getText().toString(),imageList.get(0).getMember_Image()) && myImageDb.insertAllImage(id,imageList)) {
                    Toast.makeText(getApplicationContext(), "추가됨", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "추가안됨", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (mydb.insertMemo(id,title.getText().toString(), body.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "추가됨", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "추가안됨", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //이미지 추가 버튼 누를 시 이미지 선택 다이얼로그
    private void image_show(){
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("앨범");
        ListItems.add("촬영");
        ListItems.add("URL");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이미지 첨부");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                String selectedText = items[pos].toString();
                //앨범에서 선택
                if(selectedText=="앨범"){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 1);
                }
                //촬영 선택
                else if(selectedText=="촬영") {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, 2);
                    }
                }
                //URL 선택
                else {
                    //인터넷 연결 확인
                    if (get_Internet(getApplication()) == 1){
                        final AlertDialog.Builder builder2 = new AlertDialog.Builder(New_Modify_Memo.this);
                        final EditText input = new EditText(New_Modify_Memo.this);

                        //URL입력 다이얼로그 생성
                        //확인 버튼 누를 시 이미지 받아오기 시도
                        builder2.setTitle("URL입력");
                        builder2.setView(input);
                        builder2.setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Thread mThread = new Thread() {
                                        @Override
                                        public void run() {
                                            try {
                                                URL url = new URL(input.getText().toString());
                                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                                conn.setDoInput(true);
                                                conn.connect();
                                                InputStream in = conn.getInputStream();
                                                bitmap = BitmapFactory.decodeStream(in);
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };

                                    mThread.start();
                                    try {
                                        mThread.join();
                                        //이미지가 잘 받아와진다면 imageList에 추가 후 리스트뷰에 변경사항 반영
                                        if (bitmap != null) {
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            byte[] byteArray = stream.toByteArray();
                                            imageData imagedata = new imageData(byteArray);
                                            imageList.add(imagedata);
                                            adapter.notifyDataSetChanged();
                                            setListViewHeightBasedOnChildren(listView);
                                        } else {//이미지가 안받아와진다면 에러
                                            Toast.makeText(getApplicationContext(), "잘못된 URL 입니다", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                        });
                        builder2.setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });
                        AlertDialog alert = builder2.create();
                        alert.show();
                    } else{
                        //인터넷 연결이 안되있다면 오류 메세지
                        Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.show();
    }

    //작성된 내용이 없는데 저장을 눌렀을 때
    private void no_data_show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("작성된 내용이 없어 저장되지 않습니다.");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //수정중인 페이지 였다면 삭제 후 메인으로 돌아감
                        if (extras != null){
                            if(myImageDb.deleteImage(id) && mydb.deleteMemo(id)){
                                Intent intent = new Intent(New_Modify_Memo.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }else{
                            //새 메모 작성 페이지 였다면 그냥 종료시킴
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

    //사진 삭제
    private void delete_show(int position){
        final int pos=position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("삭제하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        imageList.remove(pos);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(),"삭제 되었습니다.",Toast.LENGTH_LONG).show();
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 어떤 리퀘스트에 응답하는지 확인
        //1 이면 앨범 선택, 2 이면 촬영 선택
        if (requestCode == 1) {
            // 리쿼스트가 성공적인지 확인
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.PNG,100,stream);
                    byte[] byteArray = stream.toByteArray();
                    // 이미지 표시
                    imageData imagedata = new imageData(byteArray);
                    imageList.add(imagedata);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if(requestCode == 2){
            if (resultCode == RESULT_OK) {
                try{
                    Bundle extras = data.getExtras();
                    Bitmap img = (Bitmap) extras.get("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.PNG,100,stream);
                    byte[] byteArray = stream.toByteArray();
                    imageData imagedata = new imageData(byteArray);
                    imageList.add(imagedata);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    //인터넷 연결 확인(연결 되면 1, 연결 안되면 0)
    private int get_Internet(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return 1;
        } else{
            return 0;
        }
    }

    //이미지 리스트 크기 조절
    private void setListViewHeightBasedOnChildren(ListView listView) {
        if (adapter == null) {
            return;
        }

        int totalHeight = 0;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight;
        listView.setLayoutParams(params);

        listView.requestLayout();
    }

    //사진 추가시 리스트뷰 업데이트
    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(listView);
    }

    @Override
    public void onBackPressed() {
        finish();

    }


}
