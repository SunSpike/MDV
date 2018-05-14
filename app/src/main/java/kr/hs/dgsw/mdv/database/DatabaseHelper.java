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

    public static final String BOOK_IDX = "IDX";
    public static final String BOOK_NAME = "NAME";
    public static final String BOOK_PATH = "PATH";
    public static final String BOOK_PROCESS = "PROCESS";
    public static final String BOOK_PERCENT = "PERCENT";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + BOOK_TABLE + " (NAME TEXT, PATH TEXT PRIMARY KEY, PROCESS INTEGER, PERCENT TEXT)");
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

    public boolean insertData(String name, String path, int process, String percent) {
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

    public boolean saveProgress(String path, int process, String percent){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BOOK_PATH, path);
        contentValues.put(BOOK_PROCESS, process);
        contentValues.put(BOOK_PERCENT, percent);
        db.update(BOOK_TABLE, contentValues, "PATH = ?", new String[] { path });
        return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + BOOK_TABLE, null);
        return res;
    }

    public String getProcess(String path){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT PROCESS FROM " + BOOK_TABLE + " WHERE path = " + "'" + path + "'", null);

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
    }

    public void createTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE " + BOOK_TABLE + " (NAME TEXT, PATH TEXT PRIMARY KEY, PROCESS INTEGER, PERCENT TEXT)");
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