package com.example.abc.movieapp.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.abc.movieapp.MovieDetail;
import com.example.abc.movieapp.R;
import com.example.abc.movieapp.adapter.ReviewAdapter;
import com.example.abc.movieapp.adapter.TrailerAdapter;
import com.example.abc.movieapp.data.MovieContract;
import com.example.abc.movieapp.data.MovieContract.*;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>   {

    private static final int DETAIL_LOADER = 0;
    private static final int REVIEW_LOADER = 1;
    private static final int TRAILER_LOADER = 2;
    public static final String LOG_TAG=DetailActivityFragment.class.getSimpleName();

    ReviewAdapter reviewAdapter;

    TrailerAdapter trailerAdapter;


    //all views received using butterknife
    @BindView(R.id.detail_movie_title) TextView movie_title;
    @BindView(R.id.detail_movie_image) ImageView imageView;
    @BindView(R.id.detail_movie_release_date) TextView movie_release_date;
    @BindView(R.id.detail_movie_ratings) TextView movie_ratings;
    @BindView(R.id.detail_movie_overview) TextView movie_overview;

    static final String[] movieProjections = {
            MovieContract.MovieEntry.TABLE_NAME+"."+ MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME+"."+ MovieEntry.COL_MOVIE_ID,
            MovieEntry.COL_TITLE,
            MovieEntry.COL_OVERVIEW,
            MovieEntry.COL_VOTE_AVERAGE,
            MovieEntry.COL_RELEASE_DATE,
            MovieEntry.COL_POSTER_PATH};
    static final int COL_MOVIE_ID=1;
    static final int COL_TITLE=2;
    static final int COL_OVERVIEW=3;
    static final int COL_VOTE_AVERAGE=4;
    static final int COL_RELEASE_DATE=5;
    static final int COL_POSTER_PATH=6;

    static final String[] reviewsProjection={
            MovieContract.ReviewEntry.TABLE_NAME+"."+MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.TABLE_NAME+"."+MovieContract.ReviewEntry.COL_MOVIE_ID,
            MovieContract.ReviewEntry.COL_AUTHOR,
            MovieContract.ReviewEntry.COL_CONTENT
    };
    static final int COL_AUTHOR=2;
    static final int COL_CONTENT=3;

    static final String[] videosProjection={
            MovieContract.VideoEntry.TABLE_NAME+"."+MovieContract.VideoEntry._ID,
            MovieContract.VideoEntry.TABLE_NAME+"."+MovieContract.VideoEntry.COL_MOVIE_ID,
            VideoEntry.COL_KEY,
            VideoEntry.COL_NAME
    };
    static final int COL_KEY=2;
    static final int COL_NAME=3;

    public DetailActivityFragment() {

    }

    @BindView(R.id.listViewReviews)
    ListView listViewReviews;

    @BindView(R.id.listViewTrailers)
    ListView listViewTrailers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.movie_fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        reviewAdapter = new ReviewAdapter(getContext(), null, 0);
        trailerAdapter = new TrailerAdapter(getContext(), null, 0);
        listViewReviews.setAdapter(reviewAdapter);
        listViewTrailers.setAdapter(trailerAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        long movie_id;
        if (intent == null) {
            return null;
        }
        Log.v(LOG_TAG, "In onCreateLoader");
        CursorLoader newCursorLoader=null;
        switch (id){
            case DETAIL_LOADER:
                newCursorLoader = new CursorLoader(getActivity(), intent.getData(), movieProjections, null, null, null );
                break;
            case REVIEW_LOADER:
                movie_id = intent.getLongExtra("movie_id", 1L);
                newCursorLoader = new CursorLoader(getActivity(), ReviewEntry.buildReviewUri(movie_id), reviewsProjection, null, null, null );
                break;
            case TRAILER_LOADER:
                movie_id = intent.getLongExtra("movie_id", 1L);
                newCursorLoader = new CursorLoader(getActivity(), VideoEntry.buildVideoUri(movie_id), videosProjection, null, null, null );
                break;
        }
        return newCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }

        switch (loader.getId()){
            case DETAIL_LOADER:
                String title = data.getString(COL_TITLE);

                String release_date = data.getString(COL_RELEASE_DATE);

                String poster_path = data.getString(COL_POSTER_PATH);

                String ratings = data.getString(COL_VOTE_AVERAGE);

                String overview = data.getString(COL_OVERVIEW);

                movie_title.setText(title);
                String url = "http://image.tmdb.org/t/p/w185/" + poster_path;
                Picasso.with(getContext()).load(url).into(imageView);
                movie_release_date.setText(release_date);
                movie_ratings.setText(ratings + "/10");
                movie_overview.setText(overview);
                break;
            case REVIEW_LOADER:
                reviewAdapter.swapCursor(data);
                reviewAdapter.notifyDataSetChanged();
                break;
            case TRAILER_LOADER:
                trailerAdapter.swapCursor(data);
                trailerAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()){
            case DETAIL_LOADER:
                //
                break;
            case REVIEW_LOADER:
                reviewAdapter.swapCursor(null);
                break;
            case TRAILER_LOADER:
                trailerAdapter.swapCursor(null);
                break;
        }
    }
}
