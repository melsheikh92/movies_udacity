package com.nanodegree.mahmoud.movies.Main.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mahmoud on 05/04/2017.
 */

public class SqlDataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";

    // If you change the database schema, you must increment the database version
    private static final int DB_VERSION = 2;

    public SqlDataHelper(Context context) {

        super(context, DATABASE_NAME, null, DB_VERSION);


    }

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + FavoriteContract.FavEntry.TABLE_NAME + " (" +
                    FavoriteContract.FavEntry.KEY_ID + " integer PRIMARY KEY," +
                    FavoriteContract.FavEntry.KEY_NAME + "," +
                    FavoriteContract.FavEntry.KEY_POSTER + "," +
                    FavoriteContract.FavEntry.KEY_VOTE_AVG + "," +
                    FavoriteContract.FavEntry.KEY_RELEASE_DATE + "," +
                    FavoriteContract.FavEntry.KEY_OVERVIEW + "," +
                    " UNIQUE (" + FavoriteContract.FavEntry.KEY_ID + "));";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavEntry.TABLE_NAME);
        onCreate(db);
    }
}
