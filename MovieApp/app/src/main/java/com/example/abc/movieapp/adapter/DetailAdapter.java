package com.example.abc.movieapp.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    public static final String FAVOURITE_BUTTON_LABEL = "Remove from favorites";
    public static final String NOT_FAVOURITE_BUTTON_LABEL = "Add to favorites";
    //all views received using butterknife
    @BindView(R.id.detail_movie_title) TextView movie_title;
    @BindView(R.id.detail_movie_image) ImageView imageView;
    @BindView(R.id.detail_movie_release_date) TextView movie_release_date;
    @BindView(R.id.detail_movie_ratings) TextView movie_ratings;
    @BindView(R.id.detail_movie_overview) TextView movie_overview;
    @BindView(R.id.fav_button) Button fav_button;

    static final String[] movieProjections = {
            MovieContract.MovieEntry.TABLE_NAME+"."+ MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME+"."+ MovieContract.MovieEntry.COL_MOVIE_ID,
            MovieContract.MovieEntry.COL_TITLE,
            MovieContract.MovieEntry.COL_OVERVIEW,
            MovieContract.MovieEntry.COL_VOTE_AVERAGE,
            MovieContract.MovieEntry.COL_RELEASE_DATE,
            MovieContract.MovieEntry.COL_POSTER_PATH,
            MovieContract.MovieEntry.COL_FAVORITE};
    static final int COL_MOVIE_ID = 1;
    static final int COL_TITLE = 2;
    static final int COL_OVERVIEW = 3;
    static final int COL_VOTE_AVERAGE = 4;
    static final int COL_RELEASE_DATE = 5;
    static final int COL_POSTER_PATH = 6;
    static final int COL_FAVORITE = 7;

    public DetailAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_overview, parent, false);
        this.view = view;
        return view;
    }

    private Context mcontext = null;
    private long movie_id = -1;

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        mcontext = context;
        movie_id = cursor.getLong(COL_MOVIE_ID);

        ButterKnife.bind(this, view);

        String title = cursor.getString(COL_TITLE);

        String release_date = cursor.getString(COL_RELEASE_DATE);

        String poster_path = cursor.getString(COL_POSTER_PATH);

        String ratings = cursor.getString(COL_VOTE_AVERAGE);

        String overview = cursor.getString(COL_OVERVIEW);

        final Long favorite = cursor.getLong(COL_FAVORITE);

        movie_title.setText(title);
        String url = "http://image.tmdb.org/t/p/w185/" + poster_path;
        Picasso.with(context).load(url).into(imageView);
        movie_release_date.setText(release_date);
        movie_ratings.setText(ratings + "/10");
        movie_overview.setText(overview);

        if(favorite == 1){
            fav_button.setText(FAVOURITE_BUTTON_LABEL);
        }
        else{
            fav_button.setText(NOT_FAVOURITE_BUTTON_LABEL);
        }

        fav_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String isFavourite= (String) fav_button.getText();
                if(isFavourite.equals(NOT_FAVOURITE_BUTTON_LABEL)){
                    ContentValues cvalue=new ContentValues();
                    cvalue.put(MovieContract.MovieEntry.COL_FAVORITE,1);
                    StringBuilder where = new StringBuilder();
                    where.append(MovieContract.MovieEntry.COL_MOVIE_ID);
                    where.append(" = ?");
                    Log.d("favo", "add fav" + movie_id);
                    int numRowUpdated = mcontext.getContentResolver().update(MovieContract.MovieEntry.buildMovieUri(movie_id), cvalue, where.toString(), new String[]{ Long.toString(movie_id)});
                    Log.d("favo", "add fav rows updated " + numRowUpdated);
                    if (numRowUpdated > 0) fav_button.setText(FAVOURITE_BUTTON_LABEL);
                }
                else {
                    ContentValues cvalue=new ContentValues();
                    cvalue.put(MovieContract.MovieEntry.COL_FAVORITE,0);
                    StringBuilder where = new StringBuilder();
                    where.append(MovieContract.MovieEntry.COL_MOVIE_ID);
                    where.append(" = ?");
                    Log.d("favo", "remove fav" + movie_id);
                    int numRowUpdated = mcontext.getContentResolver().update(MovieContract.MovieEntry.buildMovieUri(movie_id), cvalue, where.toString(), new String[]{ Long.toString(movie_id)});
                    Log.d("favo", "remove fav rows updated " + numRowUpdated);
                    if (numRowUpdated > 0) fav_button.setText(NOT_FAVOURITE_BUTTON_LABEL);
                }
            }
        });

    }
}
