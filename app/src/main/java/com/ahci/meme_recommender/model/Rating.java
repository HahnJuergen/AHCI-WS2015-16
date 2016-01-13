package com.ahci.meme_recommender.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonbr on 12.01.2016.
 */
public class Rating implements BaseColumns {

    public static final String TABLE_NAME = "ratings";
    public static final String COLUMN_NAME_RATING_MEME_ID = "meme_id";
    public static final String COLUMN_NAME_RATING_VALUE = "rating";
    public static final String COLUMN_NAME_SENT_RATING_TO_SERVER = "sent";

    public static final String SQL_CREATE_RATINGS =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME_RATING_MEME_ID + " INTEGER," +
                    COLUMN_NAME_RATING_VALUE + " INTEGER," +
                    COLUMN_NAME_SENT_RATING_TO_SERVER + " INTEGER)";

    private int id;
    private int memeId;
    private int ratingValue;
    private int sentToServer;

    /**
     * @param storage
     * If the db has been opened, this method will just use it.
     * Otherwise, it will open the connection and close it afterwards.
     * @return
     * A complete list of ratings in the db. Never null, but may be empty.
     */
    public static List<Rating> loadRatingsToSendToServer(Storage storage) {
        List<Rating> ratings = new ArrayList<>();

        // ensure db is open
        boolean dbWasOpen = true;
        SQLiteDatabase db = storage.getDb();
        if(db == null || !db.isOpen()) {
            dbWasOpen = false;
            storage.openConnection(false);
            db = storage.getDb();
        }

        loadRatingsFromDB(ratings, db);

        if(!dbWasOpen) {
            db.close();
        }

        return ratings;
    }

    private static void loadRatingsFromDB(List<Rating> ratings, SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_NAME, new String[]{_ID, COLUMN_NAME_RATING_MEME_ID,
                        COLUMN_NAME_RATING_VALUE, COLUMN_NAME_SENT_RATING_TO_SERVER},
                COLUMN_NAME_SENT_RATING_TO_SERVER+"=0", null, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                Rating rating = new Rating();
                rating.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
                rating.setMemeId(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_RATING_MEME_ID)));
                rating.setRatingValue(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_RATING_VALUE)));
                rating.setSentToServer(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_SENT_RATING_TO_SERVER)));
                ratings.add(rating);
            } while(cursor.moveToNext());
        }

        cursor.close();
    }

    public static String toUrlParam(List<Rating> ratings, boolean addAmpersandBefore) {
        StringBuilder urlParam = new StringBuilder();

        for(int i = 0; i < ratings.size(); i++) {
            urlParam.append(ratings.get(i).getMemeId()).append(":").append(ratings.get(i).getRatingValue());
            if(i != ratings.size() - 1) urlParam.append(",");
        }

        return urlParam.length() == 0? "" : (addAmpersandBefore? "&" + urlParam.toString() : urlParam.toString());
    }

    public Rating() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemeId() {
        return memeId;
    }

    public void setMemeId(int memeId) {
        this.memeId = memeId;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    private int getSentToServer() {
        return sentToServer;
    }

    private void setSentToServer(int sentToServer) {
        this.sentToServer = sentToServer;
    }

    public void save(Storage storage) {
        SQLiteDatabase db = storage.getDb();
        boolean wasOpen = true;
        if(db == null || !db.isOpen()) {
            wasOpen = false;
            storage.openConnection(true);
            db = storage.getDb();
        }

        db.insert(TABLE_NAME, null, toContentValues());

        if(!wasOpen) {
            db.close();
        }
    }

    public ContentValues toContentValues() {
        ContentValues value = new ContentValues();
        value.put(COLUMN_NAME_RATING_MEME_ID, memeId);
        value.put(COLUMN_NAME_RATING_VALUE, ratingValue);
        value.put(COLUMN_NAME_SENT_RATING_TO_SERVER, sentToServer);
        return value;
    }

}
