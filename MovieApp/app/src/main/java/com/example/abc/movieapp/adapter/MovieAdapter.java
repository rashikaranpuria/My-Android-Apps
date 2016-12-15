package com.example.abc.movieapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.abc.movieapp.R;
import com.example.abc.movieapp.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by rashi on 15/12/16.
 */

public class MovieAdapter extends CursorAdapter {

    static final String[] movieProjections={
            MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME+"."+ MovieContract.MovieEntry.COL_MOVIE_ID,
            MovieContract.MovieEntry.COL_TITLE,
            MovieContract.MovieEntry.COL_OVERVIEW,
            MovieContract.MovieEntry.COL_VOTE_AVERAGE,
            MovieContract.MovieEntry.COL_RELEASE_DATE,
            MovieContract.MovieEntry.COL_POSTER_PATH};
    static final int COL_MOVIE_ID=1;
    static final int COL_TITLE=2;
    static final int COL_OVERVIEW=3;
    static final int COL_VOTE_AVERAGE=4;
    static final int COL_RELEASE_DATE=5;
    static final int COL_POSTER_PATH=6;

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.poster, parent, false);
        return view.findViewById(R.id.poster);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String poster_path = cursor.getString(COL_POSTER_PATH);
        String url="http://image.tmdb.org/t/p/w185/"+poster_path;

        Picasso.with(context).load(url).into((ImageView) view);

    }
}
