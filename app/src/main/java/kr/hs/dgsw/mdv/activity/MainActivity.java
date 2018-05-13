package kr.hs.dgsw.mdv.activity;

import android.Manifest;
import android.app.Activity;
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

import kr.hs.dgsw.mdv.R;
import kr.hs.dgsw.mdv.adapter.MainAdapter;
import kr.hs.dgsw.mdv.database.DatabaseHelper;
import kr.hs.dgsw.mdv.item.MainItem;

public class MainActivity extends AppCompatActivity {

    private static MainActivity INSTANCE;

    TextView selectPath;
    ListView listView;
    MainAdapter listViewAdapter;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);

        listView = (ListView)findViewById(R.id.mainListView);
        listViewAdapter = new MainAdapter();
        listView.setAdapter(listViewAdapter);

        initListView();

        Button goSetting = (Button)findViewById(R.id.settingButton);
        ImageButton floatButton = (ImageButton)findViewById(R.id.floatingButton);



        //region floatButton onClickListener
        floatButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //권한을 갖고있는지 확인, 23이상이라면 Dialog 표시하여 권한 요청
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            Log.e("ERROR", "YOU DON'T HAVE PERMISSIONS");
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
                        }

                        new MaterialFilePicker().
                                withActivity(MainActivity.this).
                                withRequestCode(1000).
                                //withFilter(Pattern.compile(".*\\.txt$")).
                                withHiddenFiles(false).
                                start();
                    }
                }
        );
        //endregion

        //region listView onItemClickListener
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MainItem item = (MainItem)parent.getItemAtPosition(position);
                        Intent intent = new Intent(MainActivity.this, ReadTXTActivity.class);
                        intent.putExtra("path", item.getFilePath());
                        intent.putExtra("process", myDb.getProcess(item.getFilePath()));
                        startActivity(intent);
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
            if ( myDb.insertData(fileName, filePath, 0, "0.0%") != false ){
                listViewAdapter.addItem(fileName, filePath, "0.0%");
            }else {
                Toast.makeText(MainActivity.this, "동일 이름의 파일이 이미 존재합니다.", Toast.LENGTH_SHORT).show();
            }

            Log.e("file", readFile(filePath));

        }
    }

    //At getPermission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1001:{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Permission Denied?", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //initListView on Create
    public void initListView(){
        Cursor res = myDb.getAllData();
        if(res.getCount() == 0){
            return;
        }
        while(res.moveToNext()){
            String name = res.getString(0).toString();
            String path = res.getString(1).toString();    //path
            String process = res.getString(3).toString(); //percent

            if ( !name.equals("name") && name != null){
                listViewAdapter.addItem(name, path, process);
            }
        }
    }

    public void initSetting(){
        SharedPreferences setting = getSharedPreferences("Setting", Activity.MODE_PRIVATE);

    }

    //readFile Method
    private static String readFile(String path) {
        String returnString = "";
        BufferedReader br = null;

        try{
            br = new BufferedReader(new FileReader(path));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null){
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            returnString = sb.toString();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try{
                br.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        return returnString;
        /*FileInputStream stream = null;
        try {
            stream = new FileInputStream(new File(path));
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            Instead of using default, pass in a decoder.
            return Charset.defaultCharset().decode(bb).toString();
        }catch(FileNotFoundException e){
            e.printStackTrace();
            Log.e("FNFE", "FNFE");
        }catch(IOException e){
            e.printStackTrace();
            Log.e("IO", "IO");
        }
        finally{
            try{
                stream.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return "";*/
    }
}
