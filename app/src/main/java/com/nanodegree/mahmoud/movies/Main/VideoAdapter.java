package com.nanodegree.mahmoud.movies.Main;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nanodegree.mahmoud.movies.Main.enteties.Video;
import com.nanodegree.mahmoud.movies.R;

import java.util.ArrayList;

/**
 * Created by Mahmoud on 07/04/2017.
 */

public class VideoAdapter extends ArrayAdapter implements View.OnClickListener {
    Context mContext;
    ArrayList<Video> data;
    LayoutInflater inflater;

    public VideoAdapter(Context mContext, int listview_videos, ArrayList<Video> data) {
        super(mContext, listview_videos, data);

        this.mContext = mContext;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.video_listitem, null);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_video_name);
        tv_name.setText(data.get(position).getName());
        view.setTag(position);
        view.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {

        String id = data.get(Integer.parseInt(v.getTag().toString())).getKey();
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            mContext.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            mContext.startActivity(webIntent);

        }
    }
}
