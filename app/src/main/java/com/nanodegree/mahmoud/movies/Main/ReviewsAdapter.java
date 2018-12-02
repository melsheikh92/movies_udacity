package com.nanodegree.mahmoud.movies.Main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nanodegree.mahmoud.movies.Main.enteties.Review;
import com.nanodegree.mahmoud.movies.R;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Mahmoud on 07/04/2017.
 */

public class ReviewsAdapter extends ArrayAdapter implements View.OnClickListener {
    private Context mContext;
    LayoutInflater inflater;
    ArrayList<Review> data;


    public ReviewsAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<Review> data) {
        super(context, resource);
        mContext = context;
        this.data = data;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = inflater.inflate(R.layout.listitem_review, null);

        TextView tv_author = (TextView) view.findViewById(R.id.tv_author);
        tv_author.setText("_ by : " + data.get(position).getAuthor());

        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_content.setText(data.get(position).getContent());
        view.setTag(position);
        view.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(data.get(Integer.parseInt(v.getTag().toString())).getUrl()));

        mContext.startActivity(webIntent);
    }
}
