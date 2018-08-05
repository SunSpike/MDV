package kr.hs.dgsw.mdv.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import kr.hs.dgsw.mdv.R;
import kr.hs.dgsw.mdv.adapter.MainAdapter;
import kr.hs.dgsw.mdv.database.DatabaseHelper;
import kr.hs.dgsw.mdv.item.MainItem;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    MainAdapter listViewAdapter;
    DatabaseHelper myDb;

    ArrayList<MainItem> listViewItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);

        //Initialize listViewItemList
        initListView();

        listView = (ListView)findViewById(R.id.mainListView);
        listViewAdapter = new MainAdapter(this, listViewItemList);
        listView.setAdapter(listViewAdapter);

        //Button goSetting = (Button)findViewById(R.id.settingButton);
        ImageButton floatButton = (ImageButton)findViewById(R.id.floatingButton);

        //region FloatButton onClick addFiles
        floatButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //권한을 갖고있는지 확인, 23이상이라면 Dialog 표시하여 권한 요청
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(MainActivity.this, "파일 탐색 권한을 요청합니다.", Toast.LENGTH_SHORT).show();
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
                        }

                        new MaterialFilePicker().
                                withActivity(MainActivity.this).
                                withRequestCode(1000).
                                withHiddenFiles(false).
                                start();
                    }
                }
        );
        //endregion
    }

    //At Select File
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == 1000 && resultCode == RESULT_OK ){
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            File file = new File(filePath);

            String fileName = file.getName();

            //insertData가 false라면 동일한 아이템이 존재한다는 뜻(경로가 같으므로).
            if ( myDb.insertBookData(fileName, filePath, 0, "0.0%") != false ){
                listViewAdapter.addItem(fileName, filePath, "0.0%");
            }else {
                Toast.makeText(MainActivity.this, "동일 이름의 파일이 이미 존재합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //At getPermission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1001:{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this, "권한 확인 완료!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "권한 확인 실패..", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //initListView on Create
    public void initListView(){
        listViewItemList = new ArrayList<MainItem>() ;

        Cursor res = myDb.getBookData();
        if(res.getCount() == 0){
            return;
        }
        while(res.moveToNext()){
            String name = res.getString(0);
            String path = res.getString(1);    //path
            String percent = res.getString(3); //percent

            if ( !name.equals("name") ){
                MainItem item = new MainItem();
                item.setFileName(name);
                item.setFilePath(path);
                item.setFilePercent(percent);

                listViewItemList.add(item);
            }
        }
    }
}
