package com.nanodegree.mahmoud.movies.Main.utilit;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Mahmoud on 18/04/2017.
 */

public class ConnectionIssues {


    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

}
