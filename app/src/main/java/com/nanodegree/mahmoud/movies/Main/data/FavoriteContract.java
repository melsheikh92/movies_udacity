package com.nanodegree.mahmoud.movies.Main.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mahmoud on 05/04/2017.
 */

public class FavoriteContract {
    private static final String AUTHORITY = "com.nanodegree.mahmoud.movies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES = "FAVORITES";


    public static final class FavEntry implements BaseColumns {

        public static final String TABLE_NAME = "favorites";


        public static final String KEY_ID = "movie_id";
        public static final String KEY_NAME = "name";
        public static final String KEY_VOTE_AVG = "vote_average";
        public static final String KEY_POSTER = "poster_path";
        public static final String KEY_RELEASE_DATE = "release_date";
        public static final String KEY_OVERVIEW = "overview";
    }
}