package kr.hs.dgsw.mdv.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import kr.hs.dgsw.mdv.R;
import kr.hs.dgsw.mdv.activity.setting.SettingFontActivity;
import kr.hs.dgsw.mdv.adapter.BookmarkAdapter;
import kr.hs.dgsw.mdv.adapter.MainAdapter;
import kr.hs.dgsw.mdv.database.DatabaseHelper;
import kr.hs.dgsw.mdv.item.BookmarkItem;
import kr.hs.dgsw.mdv.item.MainItem;

public class ReadTXTActivity extends AppCompatActivity{

    DatabaseHelper myDb;

    //Declare static for access from other activity.
    public static TextView readTextView;
    public static ScrollView readScroll;

    //TODO: 리팩터링 시 static 지우도록 노력하기
    public static AlertDialog bookmarkDialog;

    String text;
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
        path = getIntent().getStringExtra("PATH");

        //Get process of selected item.
        process = getIntent().getIntExtra("PROCESS", 0);

        final LinearLayout footer = findViewById(R.id.readFooter);
        ImageButton bookmarkButton = findViewById(R.id.bookmarkButton);
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


        bookmarkButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveBookmark();
                    }
                }
        );

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
            text = result.toString();
            readTextView.setText(text);
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


    public void saveBookmark(){
        Cursor bookmarkCursor = myDb.getBookmarkData(path);
        ArrayList<BookmarkItem> bookmarkItemList = new ArrayList<>();

        StringBuffer buffer = new StringBuffer();
        while(bookmarkCursor.moveToNext()){

            //TODO: 이거 왜 순서 꼬이지????
            String name = bookmarkCursor.getString(2);
            String path = bookmarkCursor.getString(1);
            int process = bookmarkCursor.getInt(3);

            Log.e("processDB", Integer.toString(process));
            String percent = bookmarkCursor.getString(4);

            bookmarkItemList.add(new BookmarkItem(name, path, process, percent));
        }

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ReadTXTActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_bookmark, null);
        mBuilder.setView(mView);

        ListView bookmarkListView = mView.findViewById(R.id.bookmarkListView);
        final BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(this, bookmarkItemList);
        bookmarkListView.setAdapter(bookmarkAdapter);

        Button addBookmarkButton = mView.findViewById(R.id.dialogAddBookmark);
        Button cancelButton = mView.findViewById(R.id.dialogCancel);

        bookmarkDialog = mBuilder.create();

        addBookmarkButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ReadTXTActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.dialog_bookmark_add, null);
                        mBuilder.setView(mView);

                        final EditText bookmarkET = mView.findViewById(R.id.bookmarkET);
                        Button addBookmarkButton = mView.findViewById(R.id.dialogOK);
                        Button cancelButton = mView.findViewById(R.id.dialogCancel);

                        final AlertDialog OptionDialog = mBuilder.create();

                        addBookmarkButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String name = bookmarkET.getText().toString();
                                        int process = readScroll.getScrollY();
                                        String percent;

                                        float scrollY = readScroll.getScrollY();
                                        float maxScroll = readScroll.getChildAt(0).getHeight();
                                        float formatTemp = (scrollY * 100) / maxScroll;
                                        percent = String.format("%.1f%%", formatTemp);

                                        bookmarkAdapter.addItem(name, path, process, percent);
                                        OptionDialog.dismiss();
                                    }
                                }
                        );

                        cancelButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        OptionDialog.dismiss();
                                    }
                                }
                        );

                        OptionDialog.show();
                    }
                }
        );

        cancelButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bookmarkDialog.dismiss();
                    }
                }
        );

        bookmarkDialog.show();
    }

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

        int statusBar = settingPref.getInt("statusBar", 0);
        getWindow().getDecorView().setSystemUiVisibility(statusBar);
    }

    public void replaceAll(){

    }

    public void saveProcess(){
        String percent;

        float scrollY = readScroll.getScrollY();
        float maxScroll = readScroll.getChildAt(0).getHeight();
        float formatTemp = (scrollY * 100) / maxScroll;

        percent = String.format("%.1f%%", formatTemp);

        myDb.saveProgress(path, readScroll.getScrollY(), percent);
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
}
