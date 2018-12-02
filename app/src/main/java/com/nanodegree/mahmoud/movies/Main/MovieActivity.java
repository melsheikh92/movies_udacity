package com.nanodegree.mahmoud.movies.Main;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nanodegree.mahmoud.movies.Main.data.FavoriteContract;
import com.nanodegree.mahmoud.movies.Main.data.FavoriteProvider;
import com.nanodegree.mahmoud.movies.Main.enteties.Movie;
import com.nanodegree.mahmoud.movies.Main.enteties.Review;
import com.nanodegree.mahmoud.movies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.id.input;

public class MovieActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<ArrayList<Review>> {
    private static final int KEY_REVIEWS = 6;
    android.support.v7.widget.Toolbar toolbar;
    TextView tv_rate;
    TextView tv_date;
    ImageView iv_poster;
    TextView tv_details;
    String jsonString;
    final String KEY_OF_SELECTED_MOVIE = "movieId";
    final String KEY_OF_KEEP_MOVIE = "MYOBJ";
    static final String KEY_SELECTED_MOVIE_ID = "MID";
    ListView lv_reviews;
    String MovieId = "";
    Button btnvideo;
    LoaderManager mloadermanager;
    Loader mloader;
    CheckBox star;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_rate = (TextView) findViewById(R.id.tv_rate);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_details = (TextView) findViewById(R.id.tv_details);
        iv_poster = (ImageView) findViewById(R.id.iv_poster);
        star = (CheckBox) findViewById(R.id.star);
        lv_reviews = (ListView) findViewById(R.id.listview_reviews);
        btnvideo = (Button) findViewById(R.id.btnvideo);
        Intent intent = getIntent();


        if (intent.hasExtra("movieId")) {
            jsonString = intent.getStringExtra(KEY_OF_SELECTED_MOVIE);
        } else {
            if (jsonString == null) {
                savedInstanceState.getString(KEY_OF_KEEP_MOVIE);
            }
        }


        try {
            JSONObject jObj = new JSONObject(jsonString);

            toolbar.setTitle(jObj.getString("title"));
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.back);
            toolbar.setNavigationOnClickListener(this);
            Glide.with(this).load("http://image.tmdb.org/t/p/w185/" + jObj.getString("poster_path")).into(iv_poster);
            MovieId = jObj.getString("id");
            tv_date.setText(jObj.getString("release_date"));
            tv_rate.setText(jObj.getString("vote_average"));
            tv_details.setText(jObj.getString("overview"));
            star.setChecked(isExist(MovieId));


        } catch (JSONException e) {
            e.printStackTrace();

        }

        btnvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MovieActivity.this, VideoActivity.class);
                intent.putExtra(KEY_SELECTED_MOVIE_ID, MovieId);
                startActivity(intent);
            }
        });
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (star.isChecked()) {
                    try {
                        JSONObject jObj = new JSONObject(jsonString);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(FavoriteContract.FavEntry.KEY_ID, jObj.getString("id"));
                        contentValues.put(FavoriteContract.FavEntry.KEY_NAME, jObj.getString("title"));
                        contentValues.put(FavoriteContract.FavEntry.KEY_POSTER, jObj.getString("poster_path"));
                        contentValues.put(FavoriteContract.FavEntry.KEY_RELEASE_DATE, jObj.getString("release_date"));
                        contentValues.put(FavoriteContract.FavEntry.KEY_VOTE_AVG, jObj.getString("vote_average"));
                        contentValues.put(FavoriteContract.FavEntry.KEY_OVERVIEW, jObj.getString("overview"));
                        Uri uri = getContentResolver().insert(FavoriteProvider.CONTENT_URI, contentValues);
                        if (uri != null) {
                            Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else {
                    Uri uri = FavoriteProvider.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(MovieId).build();
                    getContentResolver().delete(uri, null, null);

                    finish();
                }
            }
        });


        mloadermanager = getLoaderManager();
        mloader = mloadermanager.initLoader(KEY_REVIEWS, null, this);
    }

    private boolean isExist(String movieId) {
        String selection = FavoriteContract.FavEntry.KEY_ID + " = '"
                + MovieId + "'";

        return getContentResolver().query(FavoriteProvider.CONTENT_URI,
                null,
                selection,
                null,
                null).getCount() > 0 ? true : false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(KEY_OF_KEEP_MOVIE, jsonString);

    }

    @Override
    public void onClick(View v) {
        finish();
    }


    @Override
    public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Review>>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public ArrayList<Review> loadInBackground() {


                String url = "http://api.themoviedb.org/3/movie/" + MovieId + "/reviews?api_key=ec298f72dc8c9ad364fda6f08cc2056e";
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
                    mresponse = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return parseReaviews(mresponse);
            }
        };
    }

    private ArrayList<Review> parseReaviews(String s) {

        ArrayList<Review> reviews = new ArrayList<Review>();
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(s);
            //   Log.d("response", s);

            String movies = jObject.getString("results");
            JSONArray jr = new JSONArray(movies);
            for (int i = 0; i < jr.length(); i++) {
                JSONObject obj = (JSONObject) jr.get(i);

                String id = obj.getString("id");
                String author = obj.getString("author");
                String content = obj.getString("content");
                String murl = obj.getString("url");
                reviews.add(new Review(id, author, content, murl));
            }

//

        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
                    alert.setTitle("Err");
                    alert.setMessage("Error during fetching data .");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                }
            });

        }

        return reviews;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Review>> loader, ArrayList<Review> data) {

        lv_reviews.setAdapter(new ReviewsAdapter(this, R.layout.listitem_review, data));
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Review>> loader) {

    }
}
