package kr.hs.dgsw.mdv.activity;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.folioreader.FolioReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import kr.hs.dgsw.mdv.R;
import kr.hs.dgsw.mdv.activity.setting.ReadInterface;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

public class ReadEPUBActivity extends AppCompatActivity implements ReadInterface{

    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_epub);

        path = getIntent().getStringExtra("PATH");
        Log.e("path123", path);
        FolioReader folioReader = FolioReader.getInstance(getApplicationContext());
        folioReader.openBook(path);
    }

    @Override
    public void saveProcess() {

    }

    @Override
    public void initSetting() {

    }
}
