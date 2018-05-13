package kr.hs.dgsw.mdv.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import kr.hs.dgsw.mdv.R;
import kr.hs.dgsw.mdv.activity.setting.SettingActivity;
import kr.hs.dgsw.mdv.database.DatabaseHelper;

public class ReadTXTActivity extends AppCompatActivity{

    DatabaseHelper myDb;

    //Declare static for access from other activity.
    public static TextView readTextView;
    public static ScrollView readScroll;

    String path;
    int process;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_txt);
        myDb = new DatabaseHelper(this);

        readScroll = (ScrollView)findViewById(R.id.readScrollView);
        readTextView = (TextView)findViewById(R.id.readTextView);

        //Initialize Settings
        initSetting();

        //Get path of selected item.
        path = getIntent().getStringExtra("path");

        //Get process of selected item.
        String processString = getIntent().getStringExtra("process");
        process = Integer.parseInt(processString);

        Log.e("Process", Integer.toString(process));



        final LinearLayout footer = findViewById(R.id.readFooter);
        ImageButton goSettingButton = (ImageButton)findViewById(R.id.goSettingButton);

        //Read file at path and set text on readTextView.
        readFile(path);

        readScroll.post(new Runnable() {
            @Override
            public void run() {
                readScroll.setScrollY(process);
            }
        });

        //region ScrollView touchEvent, Show setting footer when touch under 100ms.
        readScroll.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Long pushTime = event.getEventTime() - event.getDownTime();
                if ( event.getAction() == event.ACTION_UP ) {
                    if ( pushTime < 100 ){
                        footer.setVisibility(View.VISIBLE);
                    }else{
                        footer.setVisibility(View.INVISIBLE);
                    }
                }
                return false;
            }
        });
        //endregion

        goSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goSetting = new Intent(ReadTXTActivity.this, SettingActivity.class);
                startActivity(goSetting);
            }
        });
    }

    //region File Read Method, append String to TextView
    private void readFile(String path) {
        BufferedReader br = null;
        String resultString = "";
        try {
            br = new BufferedReader(new FileReader(path));
            String line = br.readLine();
            StringBuilder result = new StringBuilder();
            while (line != null) {
                result.append(line);
                result.append("\n");
                line = br.readLine();
            }
            resultString = result.toString();
            readTextView.setText(resultString);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //endregion

    public void initSetting(){
        SharedPreferences settingPref = getSharedPreferences("Setting", Activity.MODE_PRIVATE);

        int textColor = settingPref.getInt("textColor", 0);
        readTextView.setTextColor(textColor);

        int textSize = settingPref.getInt("textSize", 10);
        readTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);

        int spaceSize = settingPref.getInt("spaceSize", 1);
        readTextView.setLineSpacing(spaceSize, 1);


        int top = settingPref.getInt("marginTop", 1);
        int bottom = settingPref.getInt("marginBottom", 1);
        int left = settingPref.getInt("marginLeft", 1);
        int right = settingPref.getInt("marginRight", 1);

        FrameLayout.LayoutParams textViewParams = (FrameLayout.LayoutParams) ReadTXTActivity.readTextView.getLayoutParams();
        RelativeLayout.LayoutParams scrollViewParams = (RelativeLayout.LayoutParams) ReadTXTActivity.readScroll.getLayoutParams();
        textViewParams.setMargins(left, 0, right, 0);
        scrollViewParams.setMargins(0, top, 0 ,bottom);
        ReadTXTActivity.readTextView.setLayoutParams(textViewParams);
        ReadTXTActivity.readScroll.setLayoutParams(scrollViewParams);


    }

    public void saveProcess(){
        String percent;

        float scrollY = readScroll.getScrollY();
        float maxScroll = readScroll.getChildAt(0).getHeight();
        Log.e("YAxis", Float.toString(scrollY));
        Log.e("YMAX", Float.toString(maxScroll));

        float formatTemp = (scrollY * 100) / maxScroll;
        percent = String.format("%.1f%%", formatTemp);
        Log.e("percent", percent);
        myDb.saveProgress(path, readScroll.getScrollY(), percent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("MAX", Integer.toString(readScroll.getChildAt(0).getHeight()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            Thread.sleep(500);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        saveProcess();
        super.onPause();
    }

    @Override
    protected void onStop() {
        saveProcess();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        saveProcess();
        super.onDestroy();
    }

    public void updateSetting(){

    }

    public static void ThreadResult(int value){
        readScroll.setScrollY(value);
    }
}
