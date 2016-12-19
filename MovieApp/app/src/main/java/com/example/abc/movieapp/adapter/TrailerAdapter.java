package com.example.abc.movieapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.abc.movieapp.R;
import com.example.abc.movieapp.data.MovieContract;

/**
 * Created by rashi on 19/12/16.
 */

public class TrailerAdapter extends CursorAdapter {

    static final String[] videosProjection={
            MovieContract.VideoEntry.TABLE_NAME+"."+MovieContract.VideoEntry._ID,
            MovieContract.VideoEntry.TABLE_NAME+"."+MovieContract.VideoEntry.COL_MOVIE_ID,
            MovieContract.VideoEntry.COL_KEY,
            MovieContract.VideoEntry.COL_NAME
    };
    static final int COL_KEY=2;
    static final int COL_NAME=3;

    public static final String LOG_TAG=TrailerAdapter.class.getSimpleName();

    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView)view.findViewById(R.id.name_trailer);

        name.setText(cursor.getString(COL_NAME));
    }
}
