package kr.hs.dgsw.mdv.activity;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import kr.hs.dgsw.mdv.R;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

public class ReadEPUBActivity extends AppCompatActivity {

    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_epub);
        AssetManager assetManager = getAssets();

        EpubReader reader = new EpubReader();
        TextView testTextView = findViewById(R.id.testTextView);

        path = getIntent().getStringExtra("PATH");
        Log.e("E_PATH", path);
        try{
            FileInputStream epubInputStream = new FileInputStream(path);
            Book book = reader.readEpub(epubInputStream);
            testTextView.setText(book.toString());
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
