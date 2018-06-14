package kr.hs.dgsw.mdv.activity.setting;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import kr.hs.dgsw.mdv.R;
import kr.hs.dgsw.mdv.activity.ReadTXTActivity;
import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingScreenActivity extends AppCompatActivity {

    int settingColor = 0;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_screen);

        pref = getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        editor = pref.edit();

        Button setLineSpaceButton = findViewById(R.id.lineSpaceSettingButton);
        Button setPaddingButton = findViewById(R.id.paddingSettingButton);
        Button setStatusBarButton = findViewById(R.id.statusBarSettingButton);
        Button setPagingButton = findViewById(R.id.pagingSettingButton);
        Button setScreenColorButton = findViewById(R.id.screenColorSettingButton);
        setLineSpaceButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setLineSpace();
                    }
                }
        );

        setPaddingButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setPadding();
                    }
                }
        );

        setStatusBarButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setStatusBar();
                    }
                }
        );

        setPagingButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );

        setScreenColorButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setScreenColor();
                    }
                }
        );
    }

    //region setLineSpace
    public void setLineSpace(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingScreenActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_setting_screen_space, null);
        mBuilder.setView(mView);

        final TextView sampleText = mView.findViewById(R.id.settingSampleTextView);
        final EditText lineSpaceET = mView.findViewById(R.id.lineSpaceET);
        Button cancelButton = mView.findViewById(R.id.dialogCancel);
        Button okButton = mView.findViewById(R.id.dialogOK);

        final AlertDialog OptionDialog = mBuilder.create();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    int spaceSize = Integer.parseInt(lineSpaceET.getText().toString());
                    sampleText.setLineSpacing(spaceSize, 1);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };

        //TODO: 줄간격 textWatcher 만들기
        lineSpaceET.addTextChangedListener(textWatcher);

        okButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int spaceSize = Integer.parseInt(lineSpaceET.getText().toString());
                        if ( spaceSize > 1 && spaceSize < 100 ){
                            ReadTXTActivity.readTextView.setLineSpacing(spaceSize, 1);
                            saveSettingData("spaceSize", spaceSize);
                            OptionDialog.dismiss();
                        }else{
                            Toast.makeText(SettingScreenActivity.this, "1~100 사이의 숫자를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
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
    //endregion

    //region setPadding
    public void setPadding(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingScreenActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_setting_screen_padding, null);
        mBuilder.setView(mView);

        final FrameLayout.LayoutParams textViewParams = (FrameLayout.LayoutParams) ReadTXTActivity.readTextView.getLayoutParams();
        final RelativeLayout.LayoutParams scrollViewParams = (RelativeLayout.LayoutParams) ReadTXTActivity.readScroll.getLayoutParams();

        final EditText paddingTopET = mView.findViewById(R.id.paddingTopET);
        final EditText paddingBottomET = mView.findViewById(R.id.paddingBottomET);
        final EditText paddingLeftET = mView.findViewById(R.id.paddingLeftET);
        final EditText paddingRightET = mView.findViewById(R.id.paddingRightET);

        paddingTopET.setText(Integer.toString(scrollViewParams.topMargin));
        paddingBottomET.setText(Integer.toString(scrollViewParams.bottomMargin));
        paddingLeftET.setText(Integer.toString(textViewParams.leftMargin));
        paddingRightET.setText(Integer.toString(textViewParams.rightMargin));


        Button cancelButton = mView.findViewById(R.id.dialogCancel);
        Button okButton = mView.findViewById(R.id.dialogOK);

        final AlertDialog OptionDialog = mBuilder.create();


        okButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int top = Integer.parseInt(paddingTopET.getText().toString());
                        int bottom = Integer.parseInt(paddingBottomET.getText().toString());
                        int left = Integer.parseInt(paddingLeftET.getText().toString());
                        int right = Integer.parseInt(paddingRightET.getText().toString());

                        if ( top >= 0    && top < 100    &&
                             bottom >= 0 && bottom < 100 &&
                             left >= 0   && left < 100   &&
                             right >= 0  && right < 100 )
                        {
                            textViewParams.setMargins(left, 0, right, 0);
                            scrollViewParams.setMargins(0, top, 0 ,bottom);
                            ReadTXTActivity.readTextView.setLayoutParams(textViewParams);
                            ReadTXTActivity.readScroll.setLayoutParams(scrollViewParams);

                            saveSettingData("marginLeft", left);
                            saveSettingData("marginTop", top);
                            saveSettingData("marginRight", right);
                            saveSettingData("marginBottom", bottom);
                            OptionDialog.dismiss();
                        }else{
                            Toast.makeText(SettingScreenActivity.this, "0~100 사이의 숫자를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
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
    //endregion

    //region setStatusBar
    public void setStatusBar(){

        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;

        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("off", "Turning immersive mode mode off. ");
        } else {
            Log.i("on", "Turning immersive mode mode on.");
        }

        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);

        if ( newUiOptions == 0 ){
            Toast.makeText(SettingScreenActivity.this, "이제 상태줄이 표시됩니다!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SettingScreenActivity.this, "이제 상태줄이 표시되지 않습니다!", Toast.LENGTH_SHORT).show();
        }

        saveSettingData("statusBar", newUiOptions);
    }
    //endregion

    public void setPaging(){}

    //region setScreenColor
    public void setScreenColor(){
        Drawable background = ReadTXTActivity.readTextView.getBackground();
        if (background instanceof ColorDrawable)
            settingColor = ((ColorDrawable) background).getColor();

        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, settingColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                settingColor = color;
                ReadTXTActivity.readTextView.setBackgroundColor(settingColor);
            }
        });
        colorPicker.show();
    }
    //endregion

    public void saveSettingData(String tag, int data){
        editor.putInt(tag, data);
        editor.commit();
    }
}
