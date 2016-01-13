package com.ahci.meme_recommender.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Storage {

    private StorageDBHelper dbHelper;
    private SQLiteDatabase db;

    public Storage(Context context) {
        dbHelper = new StorageDBHelper(context);
    }

    public void openConnection(boolean writable) {
        if(writable) {
            db = dbHelper.getWritableDatabase();
        } else {
            db = dbHelper.getReadableDatabase();
        }
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void closeConnection() {
        db.close();
        dbHelper.close();
    }

    private class StorageDBHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "meme_recommender.db";

        public StorageDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Rating.SQL_CREATE_RATINGS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }


}
