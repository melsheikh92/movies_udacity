package com.nanodegree.mahmoud.movies.Main;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.nanodegree.mahmoud.movies.Main.enteties.Video;
import com.nanodegree.mahmoud.movies.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Exchanger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VideoActivity extends AppCompatActivity implements Mainview, LoaderManager.LoaderCallbacks<ArrayList<Video>> {

    int MovieId;
    ProgressBar progress;
    ListView listView_videos;
    String sggg;
    final static String KEY_ID = "125";
    final static int KEY_LOAD_VIDEOS = 123789456;
    Context mContext;
    LoaderManager loaderManager;
    Loader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mContext = this;
        if (getIntent().getExtras().getString(MovieActivity.KEY_SELECTED_MOVIE_ID) != null) {
            Intent intent = getIntent();
            sggg = (String) intent.getExtras().get(MovieActivity.KEY_SELECTED_MOVIE_ID);
        } else {
            if (savedInstanceState != null)
                sggg = savedInstanceState.getString(KEY_ID);
            else
                finish();

        }
        Toast.makeText(this, sggg, Toast.LENGTH_SHORT).show();
        progress = (ProgressBar) findViewById(R.id.progress_video);
        listView_videos = (ListView) findViewById(R.id.listview_videos);

        MovieId = Integer.parseInt(sggg);
        loaderManager = getLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putString("bundleid", MovieId + "");
        loader = loaderManager.initLoader(KEY_LOAD_VIDEOS, bundle, this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_ID, sggg);
        super.onSaveInstanceState(outState);

    }


    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public Loader<ArrayList<Video>> onCreateLoader(int id, Bundle args) {
        final String vid = (String) args.get("bundleid");
        return new AsyncTaskLoader<ArrayList<Video>>(mContext) {
            String url = "http://api.themoviedb.org/3/movie/" + vid + "/videos?api_key=ec298f72dc8c9ad364fda6f08cc2056e";


            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public ArrayList<Video> loadInBackground() {

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String mresponse = "";
                try {

                    if (response.isSuccessful()) {
                        mresponse = response.body().string();

                    } else {
                        return null;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;

                }

                return parseVideos(mresponse);

            }


        };
    }

    private ArrayList<Video> parseVideos(String mresponse) {

        ArrayList<Video> videos = new ArrayList<Video>();
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(mresponse);
            //Log.d("response", s);

            String movies = jObject.getString("results");
            JSONArray jr = new JSONArray(movies);

            for (int i = 0; i < jr.length(); i++) {

                JSONObject obj = (JSONObject) jr.get(i);
                String key = obj.getString("key");
                String name = obj.getString("name");
                String id = obj.getString("id");
                String type = obj.getString("type");
                videos.add(new Video(id, key, name, type));

            }


        } catch (Exception c) {

            c.printStackTrace();
        }

        return videos;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Video>> loader, ArrayList<Video> data) {
        if (data.size() != 0)
            listView_videos.setAdapter(new VideoAdapter(mContext, R.id.listview_videos, data));
        else
            Toast.makeText(this, "Sorry No Videos For this Movie", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Video>> loader) {

    }
}
