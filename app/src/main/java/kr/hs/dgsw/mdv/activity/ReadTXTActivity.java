package kr.hs.dgsw.mdv.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.PersistableBundle;
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
import kr.hs.dgsw.mdv.activity.setting.ReadInterface;
import kr.hs.dgsw.mdv.activity.setting.SettingFontActivity;
import kr.hs.dgsw.mdv.adapter.BookmarkAdapter;
import kr.hs.dgsw.mdv.adapter.MainAdapter;
import kr.hs.dgsw.mdv.database.DatabaseHelper;
import kr.hs.dgsw.mdv.item.BookmarkItem;
import kr.hs.dgsw.mdv.item.MainItem;

public class ReadTXTActivity extends AppCompatActivity implements ReadInterface{

    DatabaseHelper database;

    //Declare static for access from other activity.
    public static TextView readTextView;
    public static ScrollView readScroll;
    public LinearLayout footer;

    //TODO: 리팩터링 시 static 지우도록 노력하기
    public static AlertDialog bookmarkDialog;

    String text;
    String path;
    int process;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    ListView bookmarkListView;
    ArrayList<BookmarkItem> bookmarkItemList = new ArrayList<>();

    int paddingTop;
    int paddingBottom;
    int howToPaging;

    float downX;
    float upX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_txt);

        //TODO: 어플리케이션 키면 마지막으로 읽었던거 켜기

        database = new DatabaseHelper(this);

        pref = getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        editor = pref.edit();

        readScroll = findViewById(R.id.readScrollView);
        readTextView = findViewById(R.id.readTextView);
        footer = findViewById(R.id.readFooter);

        //Initialize Settings
        initSetting();
        initBookmarkList();

        //Get path of selected item.
        path = getIntent().getStringExtra("PATH");

        //Get process of selected item.
        process = getIntent().getIntExtra("PROCESS", 0);


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


    public void initBookmarkList(){
        Cursor bookmarkCursor = database.getBookmarkData(path);

        while(bookmarkCursor.moveToNext()){
            String name = bookmarkCursor.getString(2);
            String path = bookmarkCursor.getString(1);
            int process = bookmarkCursor.getInt(3);

            Log.e("processDB", Integer.toString(process));
            String percent = bookmarkCursor.getString(4);

            bookmarkItemList.add(new BookmarkItem(name, path, process, percent));
        }
    }

    public void saveBookmark(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ReadTXTActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_bookmark, null);
        mBuilder.setView(mView);

        bookmarkListView = mView.findViewById(R.id.bookmarkListView);
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

    @Override
    public void initSetting(){
        SharedPreferences settingPref = getSharedPreferences("Setting", Activity.MODE_PRIVATE);

        int textColor = settingPref.getInt("textColor", 0);
        readTextView.setTextColor(textColor);

        int textSize = settingPref.getInt("textSize", 10);
        readTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);

        int spaceSize = settingPref.getInt("spaceSize", 1);
        readTextView.setLineSpacing(spaceSize, 1);

        paddingTop = settingPref.getInt("marginTop", 1);
        paddingBottom = settingPref.getInt("marginBottom", 1);
        int left = settingPref.getInt("marginLeft", 1);
        int right = settingPref.getInt("marginRight", 1);

        FrameLayout.LayoutParams textViewParams = (FrameLayout.LayoutParams) ReadTXTActivity.readTextView.getLayoutParams();
        RelativeLayout.LayoutParams scrollViewParams = (RelativeLayout.LayoutParams) ReadTXTActivity.readScroll.getLayoutParams();
        textViewParams.setMargins(left, 0, right, 0);
        scrollViewParams.setMargins(0, paddingTop, 0 , paddingBottom);
        ReadTXTActivity.readTextView.setLayoutParams(textViewParams);
        ReadTXTActivity.readScroll.setLayoutParams(scrollViewParams);

        int statusBar = settingPref.getInt("statusBar", 0);
        getWindow().getDecorView().setSystemUiVisibility(statusBar);

        howToPaging = settingPref.getInt("howToPaging", 0);
        initPaging(howToPaging);
    }

    public void initPaging(int howToPaging){
        if ( howToPaging == 0 ){
            View.OnTouchListener onTouchListener = new View.OnTouchListener(){
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
            };
            Log.e("0", "0");
            readScroll.setOnTouchListener(onTouchListener);
        } else if ( howToPaging == 1 ) {
            View.OnTouchListener onTouchListener = new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Long pushTime = event.getEventTime() - event.getDownTime();

                    if ( event.getAction() == event.ACTION_DOWN ){
                        downX = event.getX();
                    }
                    if ( event.getAction() == event.ACTION_MOVE ){
                        return true;
                    }
                    if ( event.getAction() == event.ACTION_UP ) {
                        upX = event.getX();

                        if ( downX - upX > 0 ){
                            readScroll.setScrollY(readScroll.getScrollY() + 2257 + paddingTop + paddingBottom);
                        } else if ( downX - upX < 0 ){
                            readScroll.setScrollY(readScroll.getScrollY() - 2237 + paddingTop + paddingBottom);
                        }

                        if ( pushTime < 100 ){
                            footer.setVisibility(View.VISIBLE);
                        }else{
                            footer.setVisibility(View.INVISIBLE);
                        }
                    }
                    return false;
                }
            };
            Log.e("1", "1");
            readScroll.setOnTouchListener(onTouchListener);
        }
    }

    public void replaceAll(){

    }

    @Override
    public void saveProcess(){
        String percent;

        float scrollY = readScroll.getScrollY();
        float maxScroll = readScroll.getChildAt(0).getHeight();
        float formatTemp = (scrollY * 100) / maxScroll;

        percent = String.format("%.1f%%", formatTemp);

        database.saveProgress(path, readScroll.getScrollY(), percent);
    }

    @Override
    protected void onResume() {
        Log.e("resume", "resume");
        initSetting();
        super.onResume();
    }

    @Override
    protected void onPause() {
        saveProcess();
        super.onPause();
    }
}
