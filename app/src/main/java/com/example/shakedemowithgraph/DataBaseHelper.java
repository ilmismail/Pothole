package com.example.shakedemowithgraph;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.Date;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String POTHOLE_TABLE = "POTHOLE_TABLE";
    public static final String COLUMN_POTHOLE_ACCEL = "POTHOLE_ACCEL";
//    public static final String COLUMN_POTHOLE_DETECT = "POTHOLE_DETECT";
    public static final String COLUMN_POTHOLE_TIMESTAMP = "POTHOLE_TIMESTAMP";
    public static final String COLUMN_LATITUDE = "LATITUDE";
    public static final String COLUMN_LONGITUDE = "LONGITUDE";


    private static DataBaseHelper mInstance;

    public DataBaseHelper(@Nullable Context context) {
        super(context, "pothole.db", null, 1);

    }

//    public static DataBaseHelper getInstance() {
//        if (mInstance == null) {
//            synchronized (DataBaseHelper.class) {
//                if (mInstance == null) {
//                    mInstance = new DataBaseHelper(BaseApp.getApp());
//                }
//            }
//        }
//
//        return mInstance;
//    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + POTHOLE_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_POTHOLE_ACCEL + " REAL, " + COLUMN_LATITUDE + " DOUBLE, " + COLUMN_LONGITUDE + " DOUBLE, " + COLUMN_POTHOLE_TIMESTAMP + " REAL)";

        db.execSQL(createTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(float changeInAcceleration, double latitude, double longitude) {

        ContentValues cv = new ContentValues();

        cv.put(COLUMN_POTHOLE_ACCEL, changeInAcceleration);
        cv.put(COLUMN_LATITUDE, latitude);
        cv.put(COLUMN_LONGITUDE, longitude);
        cv.put(COLUMN_POTHOLE_TIMESTAMP, java.text.DateFormat.getDateTimeInstance().format(new Date()));

       getWritableDatabase().insert(POTHOLE_TABLE, null, cv);


    }
}
