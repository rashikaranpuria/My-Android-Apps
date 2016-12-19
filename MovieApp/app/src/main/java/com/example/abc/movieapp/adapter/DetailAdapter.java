package com.example.abc.movieapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abc.movieapp.R;
import com.example.abc.movieapp.data.MovieContract;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rashi on 19/12/16.
 */

public class DetailAdapter extends CursorAdapter {

    View view;
    //all views received using butterknife
    @BindView(R.id.detail_movie_title) TextView movie_title;
    @BindView(R.id.detail_movie_image) ImageView imageView;
    @BindView(R.id.detail_movie_release_date) TextView movie_release_date;
    @BindView(R.id.detail_movie_ratings) TextView movie_ratings;
    @BindView(R.id.detail_movie_overview) TextView movie_overview;

    static final String[] movieProjections = {
            MovieContract.MovieEntry.TABLE_NAME+"."+ MovieContract.MovieEntry._ID,
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

    public DetailAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_overview, parent, false);
        this.view = view;
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ButterKnife.bind(this, view);

        String title = cursor.getString(COL_TITLE);

        String release_date = cursor.getString(COL_RELEASE_DATE);

        String poster_path = cursor.getString(COL_POSTER_PATH);

        String ratings = cursor.getString(COL_VOTE_AVERAGE);

        String overview = cursor.getString(COL_OVERVIEW);

        movie_title.setText(title);
        String url = "http://image.tmdb.org/t/p/w185/" + poster_path;
        Picasso.with(context).load(url).into(imageView);
        movie_release_date.setText(release_date);
        movie_ratings.setText(ratings + "/10");
        movie_overview.setText(overview);

    }
}
