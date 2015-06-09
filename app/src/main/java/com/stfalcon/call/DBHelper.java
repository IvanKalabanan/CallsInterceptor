package com.stfalcon.call;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by root on 06.06.15.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static String NAME_TABLE = "allinformation";

    public DBHelper(Context context) {
        super(context, "phoneDatabase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table allinformation ("
                + "id integer primary key autoincrement,"
                + "phone text,"
                + "text text,"
                + "time text,"
                + "state text,"
                + "date text,"
                + "name text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
