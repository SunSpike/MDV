package kr.hs.dgsw.mdv.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by DH on 2018-03-29.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    Context context;

    public static final String DATABASE_NAME = "mdv.db";
    public static final String BOOK_TABLE = "book_table";
    public static final String BOOKMARK_TABLE = "bookmark_table";

    public static final String BOOK_NAME = "B_NAME";
    public static final String BOOK_PATH = "B_PATH";
    public static final String BOOK_PROCESS = "B_PROCESS";
    public static final String BOOK_PERCENT = "B_PERCENT";

    public static final String BOOKMARK_IDX = "BM_IDX";
    public static final String BOOKMARK_NAME = "BM_NAME";
    public static final String BOOKMARK_PATH = "BM_PATH";
    public static final String BOOKMARK_PROCESS = "BM_PROCESS";
    public static final String BOOKMARK_PERCENT = "BM_PERCENT";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + BOOK_TABLE + " (B_NAME TEXT, B_PATH TEXT PRIMARY KEY, B_PROCESS INTEGER, B_PERCENT TEXT)");
        db.execSQL("CREATE TABLE " + BOOKMARK_TABLE + " (BM_IDX INTEGER PRIMARY KEY AUTOINCREMENT, BM_NAME TEXT, BM_PATH TEXT, BM_PROCESS INTEGER, BM_PERCENT TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BOOK_TABLE);
        onCreate(db);
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    public boolean insertBookData(String name, String path, int process, String percent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BOOK_NAME, name);
        contentValues.put(BOOK_PATH, path);
        contentValues.put(BOOK_PROCESS, process);
        contentValues.put(BOOK_PERCENT, percent);
        long result = db.insert(BOOK_TABLE, null, contentValues);

        if(result == -1){
            Toast.makeText(context, "같은 이름의 파일이 이미 존재합니다.", Toast.LENGTH_SHORT).show();
            return false;
        }

        else
            return true;
    }

    public boolean insertBookmarkData(String name, String path, int process, String percent){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BOOKMARK_NAME, name);
        contentValues.put(BOOKMARK_PATH, path);
        contentValues.put(BOOKMARK_PROCESS, process);
        contentValues.put(BOOKMARK_PERCENT, percent);
        long result = db.insert(BOOKMARK_TABLE, null, contentValues);

        if(result == -1){
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            return false;
        }

        else
            return true;
    }

    public Cursor getBookData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + BOOK_TABLE, null);
        return res;
    }

    public Cursor getBookmarkData(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + BOOKMARK_TABLE + " WHERE bm_path = " + "'" + path + "'", null);
        return res;
    }

    public boolean saveProgress(String path, int process, String percent){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BOOK_PATH, path);
        contentValues.put(BOOK_PROCESS, process);
        contentValues.put(BOOK_PERCENT, percent);
        db.update(BOOK_TABLE, contentValues, "B_PATH = ?", new String[] { path });
        return true;
    }


    public String getProcess(String path){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT B_PROCESS FROM " + BOOK_TABLE + " WHERE B_PATH = " + "'" + path + "'", null);

        res.moveToNext();
        Log.e("file", res.getString(0));
        return res.getString(0);
    }

    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ BOOK_TABLE);
    }

    public void dropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + BOOK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + BOOKMARK_TABLE);
    }

    public void createTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE " + BOOK_TABLE + " (B_NAME TEXT, B_PATH TEXT PRIMARY KEY, B_PROCESS INTEGER, B_PERCENT TEXT)");
        db.execSQL("CREATE TABLE " + BOOKMARK_TABLE + " (BM_IDX INTEGER PRIMARY KEY AUTOINCREMENT, BM_PATH TEXT, BM_NAME TEXT, BM_PROCESS INTEGER, BM_PERCENT TEXT)");
    }
}
/*
Cursor res = myDb.getAllData();
if(res.getCount() == 0){
    return;
}
StringBuffer buffer = new StringBuffer();
while(res.moveToNext()){
    buffer.append(res.getString(0));
    buffer.append(res.getString(1));
    buffer.append(res.getString(2));
    buffer.append(res.getString(3));
}
 */