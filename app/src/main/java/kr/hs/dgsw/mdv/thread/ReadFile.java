package kr.hs.dgsw.mdv.thread;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import kr.hs.dgsw.mdv.activity.ReadTXTActivity;

/**
 * Created by DH on 2018-05-08.
 */

public class ReadFile implements Runnable {
    private int process;
    private String path;

    Callback c;

    public ReadFile(Callback c){
        this.c = c;
    }

    @Override
    public void run() {
        readFile(path);
        this.c.callback();
    }

    private void readFile(String path) {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(path));
            String line = br.readLine();
            StringBuilder result = new StringBuilder();
            String resultString;
            while (line != null) {
                result.append(line);
                result.append("\n");
                //readTextView.append(line);
                //readTextView.append("\n");
                line = br.readLine();
            }
            resultString = result.toString();
            ReadTXTActivity.readTextView.setText(resultString);

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
}
