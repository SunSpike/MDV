package kr.hs.dgsw.mdv.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.ScrollBar;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.File;

import kr.hs.dgsw.mdv.R;
import kr.hs.dgsw.mdv.activity.setting.ReadInterface;
import kr.hs.dgsw.mdv.database.DatabaseHelper;

public class ReadPDFActivity extends AppCompatActivity implements ReadInterface {

    DatabaseHelper database;
    PDFView pdfView;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_pdf);

        pdfView = findViewById(R.id.pdfView);
        ScrollBar scrollBar = findViewById(R.id.pdfScrollBar);

        pdfView.setScrollBar(scrollBar);

        scrollBar.setHorizontal(false);

        Intent i = this.getIntent();
        path = i.getExtras().getString("PATH");
        int process = i.getExtras().getInt("PROCESS", 1);

        File file = new File(path);

        if(file.canRead()){
            pdfView.fromFile(file).defaultPage(process).onLoad(new OnLoadCompleteListener() {
                @Override
                public void loadComplete(int nbPages) {
                    Toast.makeText(ReadPDFActivity.this, String.valueOf(nbPages), Toast.LENGTH_SHORT).show();
                }
            }).load();
        }
    }

    //TODO: pdf, epub 진행상황 저장하기

    @Override
    protected void onPause() {
        saveProcess();
        super.onPause();
    }

    @Override
    public void saveProcess(){
        String percent;
        int currentPage = pdfView.getCurrentPage();
        int totalPage = pdfView.getPageCount();

        float formatTemp = (currentPage * 100) / totalPage;

        percent = String.format("%.1f%%", formatTemp);

        database.saveProgress(path, currentPage, percent);
    }

    @Override
    public void initSetting() {

    }
}
