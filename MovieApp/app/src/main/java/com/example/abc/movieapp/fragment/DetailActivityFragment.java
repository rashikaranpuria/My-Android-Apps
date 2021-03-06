package com.example.abc.movieapp.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;
import com.example.abc.movieapp.MovieDetail;
import com.example.abc.movieapp.R;
import com.example.abc.movieapp.activity.DetailActivity;
import com.example.abc.movieapp.adapter.DetailAdapter;
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

    public static final String DETAIL_URI = "URI";
    private Uri mUri;
    private long mMovieId;

    DetailAdapter detailAdapter;
    ReviewAdapter reviewAdapter;
    TrailerAdapter trailerAdapter;
    MergeAdapter mergeAdapter = new MergeAdapter();

    static final String[] movieProjections = {
            MovieContract.MovieEntry.TABLE_NAME+"."+ MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME+"."+ MovieEntry.COL_MOVIE_ID,
            MovieEntry.COL_TITLE,
            MovieEntry.COL_OVERVIEW,
            MovieEntry.COL_VOTE_AVERAGE,
            MovieEntry.COL_RELEASE_DATE,
            MovieEntry.COL_POSTER_PATH,
            MovieEntry.COL_FAVORITE};
    static final int COL_MOVIE_ID = 1;
    static final int COL_TITLE = 2;
    static final int COL_OVERVIEW = 3;
    static final int COL_VOTE_AVERAGE = 4;
    static final int COL_RELEASE_DATE = 5;
    static final int COL_POSTER_PATH = 6;
    static final int COL_FAVORITE = 7;

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

    @BindView(R.id.listMovieDetail)
    ListView listMovieDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
            mMovieId = MovieEntry.getMovieIdFromUri(mUri);
        }
        View rootView =  inflater.inflate(R.layout.movie_fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        reviewAdapter = new ReviewAdapter(getContext(), null, 0);
        trailerAdapter = new TrailerAdapter(getContext(), null, 0);
        detailAdapter = new DetailAdapter(getContext(), null, 0);

        mergeAdapter.addAdapter(detailAdapter);
        mergeAdapter.addAdapter(trailerAdapter);
        mergeAdapter.addAdapter(reviewAdapter);

        listMovieDetail.setAdapter(mergeAdapter);

        listMovieDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                Log.d("is same", cursor.getColumnName(COL_KEY) + " " + videosProjection[2]);
                if (cursor != null && cursor.getColumnName(COL_KEY).equals(videosProjection[2])){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+cursor.getString(COL_KEY))));
                    Log.d(LOG_TAG, " movie id passed to video watch view " + cursor.getLong(COL_MOVIE_ID));
                }
            }
        });

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
        long movie_id;
        if (mUri == null) {
            return null;
        }
        Log.v(LOG_TAG, "In onCreateLoader");
        CursorLoader newCursorLoader=null;
        switch (id){
            case DETAIL_LOADER:
                newCursorLoader = new CursorLoader(getActivity(), mUri, movieProjections, null, null, null );
                break;
            case REVIEW_LOADER:
                movie_id = mMovieId;
                newCursorLoader = new CursorLoader(getActivity(), ReviewEntry.buildReviewUri(movie_id), reviewsProjection, null, null, null );
                break;
            case TRAILER_LOADER:
                movie_id = mMovieId;
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
                detailAdapter.swapCursor(data);
//                detailAdapter.notifyDataSetChanged();
                break;
            case REVIEW_LOADER:
                reviewAdapter.swapCursor(data);
//                reviewAdapter.notifyDataSetChanged();
                break;
            case TRAILER_LOADER:
                trailerAdapter.swapCursor(data);
//                trailerAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()){
            case DETAIL_LOADER:
                detailAdapter.swapCursor(null);
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
