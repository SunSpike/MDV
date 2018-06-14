package kr.hs.dgsw.mdv.activity.setting;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.hs.dgsw.mdv.R;
import kr.hs.dgsw.mdv.activity.ReadTXTActivity;
import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingFontActivity extends AppCompatActivity {

    int settingColor = 0;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_font);

        pref = getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        editor = pref.edit();

        Button setTextSizeButton = findViewById(R.id.textSizeSettingButton);
        Button setTextColorButton = findViewById(R.id.textColorSettingButton);
        Button selectFontButton = findViewById(R.id.textSettingButton);

        setTextSizeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setFontSize();
                    }
                }
        );

        setTextColorButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTextColor();
                    }
                }
        );

        selectFontButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );
    }

    //region setTextSize
    public void setFontSize(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingFontActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_setting_font_size, null);
        mBuilder.setView(mView);

        final EditText fontSizeET = mView.findViewById(R.id.fontSizeET);
        final TextView sampleText = mView.findViewById(R.id.settingSampleTextView);

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
                    sampleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Float.parseFloat(fontSizeET.getText().toString()));
                }catch(NumberFormatException e){
                    e.printStackTrace();
                }
            }
        };

        fontSizeET.addTextChangedListener(textWatcher);

        Button cancelButton = mView.findViewById(R.id.dialogCancel);
        Button okButton = mView.findViewById(R.id.dialogOK);

        final AlertDialog OptionDialog = mBuilder.create();


        okButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int textSize = Integer.parseInt(fontSizeET.getText().toString());
                        if ( textSize >= 1 && textSize <= 99 ){
                            ReadTXTActivity.readTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Float.parseFloat(fontSizeET.getText().toString()));
                            saveSettingData("textSize", textSize);
                            OptionDialog.dismiss();
                        }else{
                            Toast.makeText(SettingFontActivity.this, "1~99 사이의 숫자를 입력해주세요.", Toast.LENGTH_SHORT).show();
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

    public void setTextColor(){
        settingColor = ReadTXTActivity.readTextView.getCurrentTextColor();

        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, settingColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                ReadTXTActivity.readTextView.setTextColor(color);
                saveSettingData("textColor", color);
            }
        });
        colorPicker.show();
    }

    public void saveSettingData(String tag, int data){
        editor.putInt(tag, data);
        editor.commit();
    }
}
