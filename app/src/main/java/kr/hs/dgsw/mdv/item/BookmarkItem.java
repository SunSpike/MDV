package kr.hs.dgsw.mdv.item;

/**
 * Created by DH on 2018-05-14.
 */

public class BookmarkItem {

    private String bookmarkName;
    private String bookmarkPath;
    private int bookmarkProcess;
    private String bookmarkPercent;

    public String getBookmarkName() {
        return bookmarkName;
    }

    public void setBookmarkName(String bookmarkName) {
        this.bookmarkName = bookmarkName;
    }

    public String getBookmarkPath() {
        return bookmarkPath;
    }

    public void setBookmarkPath(String bookmarkPath) {
        this.bookmarkPath = bookmarkPath;
    }

    public int getBookmarkProcess() {
        return bookmarkProcess;
    }

    public void setBookmarkProcess(int bookmarkProcess) {
        this.bookmarkProcess = bookmarkProcess;
    }

    public String getBookmarkPercent() {
        return bookmarkPercent;
    }

    public void setBookmarkPercent(String bookmarkPercent) {
        this.bookmarkPercent = bookmarkPercent;
    }
}
