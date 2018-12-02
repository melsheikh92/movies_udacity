package com.nanodegree.mahmoud.movies.Main;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
import com.nanodegree.mahmoud.movies.Main.data.FavoriteContract;
import com.nanodegree.mahmoud.movies.Main.data.FavoriteProvider;
import com.nanodegree.mahmoud.movies.Main.enteties.Movie;
import com.nanodegree.mahmoud.movies.Main.utilit.ConnectionIssues;
import com.nanodegree.mahmoud.movies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements Mainview, AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<Movie>> {
    private static final String KEY_OF_FIRST_VISIBLE_ITEM = "FELEMENT";
    private ProgressBar progressBar;
    GridView gridview;
    com.github.clans.fab.FloatingActionButton fab;
    static int mfiltertype = 0;
    RequestQueue queue;
    Context mcontext;
    final String KEY_OF_KEEP_FILTERING = "mfilter";
    final String KEY_OF_SELECTED_MOVIE = "movieId";
    final int Movies_Loader_key = 125689;
    String url = "http://api.themoviedb.org/3/movie/popular?api_key=ec298f72dc8c9ad364fda6f08cc2056e";
    LoaderManager loadermanager;
    Loader<ArrayList<Movie>> mloader;


    int firstindex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ConnectionIssues.isNetworkConnected(this)) {
            setContentView(R.layout.activity_main);

            mcontext = this;
            queue = Volley.newRequestQueue(getApplicationContext());

            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey(KEY_OF_KEEP_FILTERING)) {
                    mfiltertype = savedInstanceState.getInt(KEY_OF_KEEP_FILTERING);
                    // firstindex = savedInstanceState.getInt(KEY_OF_FIRST_VISIBLE_ITEM);
                    //gridview.onRestoreInstanceState(savedInstanceState.getParcelable("state"));
                }

            }
            if (savedInstanceState != null) {

                index = savedInstanceState.getInt("index");
            }
            progressBar = (ProgressBar) findViewById(R.id.progress);
            gridview = (GridView) findViewById(R.id.gridview);
            gridview.setOnItemClickListener(this);
            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterby(mfiltertype);

                }
            });
            loadermanager = getLoaderManager();
            mloader = loadermanager.getLoader(Movies_Loader_key);
            Bundle b = new Bundle();
            if (mloader == null) {
                mloader = loadermanager.initLoader(Movies_Loader_key, b, this);
                Toast.makeText(mcontext, "initloader", Toast.LENGTH_LONG).show();
            } else {

                mloader = loadermanager.restartLoader(Movies_Loader_key, b, MainActivity.this);

            }
        } else {

            setContentView(R.layout.noconnection_layout);
        }

    }

    public void filterby(final int filterType) {
        final String sorttype[] = {"Most Popular", "High Rate", "Favorite"};
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(MainActivity.this);
        alertdialogbuilder.setTitle("Sort By  ");

        alertdialogbuilder.setSingleChoiceItems(sorttype, filterType, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mfiltertype = which;
                dialog.dismiss();
                index = 0;
                mloader.forceLoad();

            }
        });


        AlertDialog dialog = alertdialogbuilder.create();
        dialog.show();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    JSONArray jr;
    ArrayList<Movie> mMovies;

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(mcontext) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                showProgress();
                //   Toast.makeText(mcontext, "startLoading", Toast.LENGTH_SHORT).show();


                if (mMovies != null) {
                    deliverResult(mMovies);
                } else {
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(ArrayList<Movie> data) {
                this.mymovies = data;
                super.deliverResult(data);
            }

            ArrayList<Movie> mymovies;

            @Override
            public ArrayList<Movie> loadInBackground() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Toast.makeText(mcontext, "background", Toast.LENGTH_SHORT).show();

                    }

                });
                mymovies = new ArrayList<Movie>();

                if (mfiltertype == 0) {
                    url = "http://api.themoviedb.org/3/movie/popular?api_key=ec298f72dc8c9ad364fda6f08cc2056e";

                } else if (mfiltertype == 1) {
                    url = "http://api.themoviedb.org/3/movie/top_rated?api_key=ec298f72dc8c9ad364fda6f08cc2056e";
                }
                if (mfiltertype == 2) {

                    try {
                        return parseFavCursor(getContentResolver().query(FavoriteProvider.CONTENT_URI,
                                null,
                                null,
                                null,
                                null));

                    } catch (Exception e) {
                        Log.e("vvg", "Failed to asynchronously load data.");
                        e.printStackTrace();
                        return null;
                    }

                } else {

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

                    // Log.d("checksss", mymovies.size() + "");
                    return ParseMovise(mresponse);
                }

            }
        };
    }

    private ArrayList<Movie> parseFavCursor(Cursor query) {
        jr = null;
        ArrayList<Movie> movies = new ArrayList<Movie>();
        while (query.moveToNext()) {
            // movies.add(new Movie());
            Movie m = new Movie();
            m.setId(query.getString(0));
            m.setTitle(query.getString(1));
            m.setPoster_path(query.getString(2));
            m.setRelease_date(query.getString(4));
            m.setOverview(query.getString(5));
            m.setVote_average(query.getString(3));
/*
            m.setId(query.getString(1));
            m.setTitle(query.getString(2));
            m.setPoster_path(query.getString(3));
            m.setOverview(query.getString(5));
            m.setVote_average(query.getString(4));
*/


            movies.add(m);
        }
        return movies;
    }

    private ArrayList<Movie> ParseMovise(String s) {
        ArrayList<Movie> cmovies = new ArrayList<Movie>();
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(s);
            Log.d("response", s);

            String movies = jObject.getString("results");
            jr = new JSONArray(movies);
            for (int i = 0; i < jr.length(); i++) {

                JSONObject obj = (JSONObject) jr.get(i);
                String title = obj.getString("title");
                String poster = obj.getString("poster_path");
                String release = obj.getString("release_date");
                String id = obj.getString("id");
                String avg = obj.getString("vote_average");
                String overview = obj.getString("overview");
                cmovies.add(new Movie(title, poster, release, id, avg, overview));

            }

//

        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
                    alert.setTitle("Err");
                    alert.setMessage("Error during fetching data .");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                }
            });

        }

        return cmovies;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        //   Toast.makeText(mcontext, "loadfinish", Toast.LENGTH_SHORT).show();
        mMovies = data;
        gridview.setAdapter(new MoviesAdapter(getApplicationContext(), data));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            gridview.setSelectionFromTop(index,top);
//        }
        gridview.setSelection(index);
        hideProgress();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }
/*
    private class LongOperation extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {


            return "";
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }
*/

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //   intent.putExtra("type", (mfiltertype == 2) ? true : false);

        if (mfiltertype != 2) {
            try {
                Intent intent = new Intent(MainActivity.this, MovieActivity.class);
                intent.putExtra(KEY_OF_SELECTED_MOVIE, jr.get(position).toString());
                startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Intent intent = new Intent(MainActivity.this, MovieFavActivity.class);
            intent.putExtra("movie", mMovies.get(position));
            startActivity(intent);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_OF_KEEP_FILTERING, mfiltertype);
        // Toast.makeText(this, "" + gridview.getFirstVisiblePosition(), Toast.LENGTH_SHORT).show();
        if (gridview != null) {
            int index = gridview.getFirstVisiblePosition();
            View v = gridview.getChildAt(0);
            int top = (v == null) ? 0 : (v.getTop() - gridview.getPaddingTop());
            outState.putInt("index", index);
            outState.putInt("top", top);
        }
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Bundle outState = new Bundle();
    /*    outState.putInt(KEY_OF_KEEP_FILTERING, mfiltertype);

        int index = gridview.getFirstVisiblePosition();
        View v = gridview.getChildAt(0);
        outState.putInt("index", index);*/
        onSaveInstanceState(outState);


    }

    int index;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


    }

    @Override
    protected void onResume() {
        super.onResume();
//        mloader = loadermanager.restartLoader(Movies_Loader_key, null, MainActivity.this);
    }

}
