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

public class ReadPDFActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_pdf);

        PDFView pdfView = findViewById(R.id.pdfView);
        ScrollBar scrollBar = findViewById(R.id.pdfScrollBar);

        pdfView.setScrollBar(scrollBar);

        scrollBar.setHorizontal(false);

        Intent i = this.getIntent();
        String path = i.getExtras().getString("PATH");
        String process = i.getExtras().getString("PROCESS");

        File file = new File(path);

        if(file.canRead()){
            pdfView.fromFile(file).defaultPage(1).onLoad(new OnLoadCompleteListener() {
                @Override
                public void loadComplete(int nbPages) {
                    Toast.makeText(ReadPDFActivity.this, String.valueOf(nbPages), Toast.LENGTH_SHORT).show();
                }
            }).load();
        }
    }
}
