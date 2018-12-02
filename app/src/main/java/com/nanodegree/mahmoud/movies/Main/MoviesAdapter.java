package com.nanodegree.mahmoud.movies.Main;

import android.app.LoaderManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nanodegree.mahmoud.movies.Main.enteties.Movie;
import com.nanodegree.mahmoud.movies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Mahmoud on 25/02/2017.
 */

public class MoviesAdapter extends BaseAdapter {
    private Context mContext;
    LayoutInflater inflater;
    JSONArray mArr;

    public MoviesAdapter(Context c, JSONArray arr) {
        mContext = c;
        mArr = arr;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    ArrayList<Movie> data;

    public MoviesAdapter(Context c, ArrayList<Movie> data) {
        mContext = c;
        this.data = data;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        // ==ImageView imageView;
        View view = inflater.inflate(R.layout.custom_movie, null);
        TextView tv_rate = (TextView) view.findViewById(R.id.tv_movie_rate);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_movie_name);
        ImageView iv_poster = (ImageView) view.findViewById(R.id.iv_movie);
        Movie movie = data.get(position);

        tv_rate.setText(movie.getVote_average());
        tv_name.setText(movie.getTitle());

        Glide.with(mContext).load("http://image.tmdb.org/t/p/w185/" + movie.getPoster_path()).into(iv_poster);
/*  try {
           // JSONObject mov = (JSONObject) mArr.get(position);

            tv_name.setText(mov.getString("title"));
            tv_rate.setText(mov.getString("vote_average"));

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        return view;
    }


}