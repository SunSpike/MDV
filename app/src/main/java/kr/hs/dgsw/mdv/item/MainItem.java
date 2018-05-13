package kr.hs.dgsw.mdv.item;

/**
 * Created by DH on 2018-03-29.
 */

public class MainItem {
    private String fileName;
    private String filePath;
    private String filePercent;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePercent() {
        return filePercent;
    }

    public void setFilePercent(String fileProcess) {
        this.filePercent = fileProcess;
    }
}
