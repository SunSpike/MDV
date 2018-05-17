package kr.hs.dgsw.mdv.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import kr.hs.dgsw.mdv.R;
import kr.hs.dgsw.mdv.activity.setting.SettingFontActivity;
import kr.hs.dgsw.mdv.activity.setting.SettingScreenActivity;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button goFontSetting = findViewById(R.id.fontSettingButton);
        Button goScreenSetting = findViewById(R.id.screenSettingButton);

        goFontSetting.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent goFont = new Intent(SettingActivity.this, SettingFontActivity.class);
                        startActivity(goFont);
                    }
                }
        );
        
        goScreenSetting.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent goFont = new Intent(SettingActivity.this, SettingScreenActivity.class);
                        startActivity(goFont);
                    }
                }
        );
    }
}