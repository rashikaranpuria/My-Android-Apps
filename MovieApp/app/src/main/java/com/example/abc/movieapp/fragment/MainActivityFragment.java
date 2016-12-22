package com.example.abc.movieapp.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.abc.movieapp.BuildConfig;
import com.example.abc.movieapp.MovieDetail;
import com.example.abc.movieapp.R;
import com.example.abc.movieapp.activity.DetailActivity;
import com.example.abc.movieapp.adapter.MovieAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.abc.movieapp.data.MovieContract;
import com.example.abc.movieapp.data.MovieContract.*;
import com.example.abc.movieapp.sync.MovieSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

//    private MovieGridAdapter movieGridAdapter;
    private MovieAdapter movieAdapter;

    public static final String FAVORITE = "favorite";

    MovieDetail[] movies = {};
    public static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private static final int MOVIE_LOADER = 0;

    private static final String SELECTED_KEY = "selected_position";

    private int mPosition;

    @BindView(R.id.gridview)
    GridView gridview;


    static final String[] movieProjections = {
            MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME+"."+ MovieEntry.COL_MOVIE_ID,
            MovieEntry.COL_TITLE,
            MovieEntry.COL_OVERVIEW,
            MovieEntry.COL_VOTE_AVERAGE,
            MovieEntry.COL_RELEASE_DATE,
            MovieEntry.COL_POSTER_PATH,
            MovieEntry.COL_FAVORITE};
    static final int COL_MOVIE_ID=1;
    static final int COL_TITLE=2;
    static final int COL_OVERVIEW=3;
    static final int COL_VOTE_AVERAGE=4;
    static final int COL_RELEASE_DATE=5;
    static final int COL_POSTER_PATH=6;
    static final int COL_FAVORITE=7;

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        movieAdapter = new MovieAdapter(getContext(), null, 0);

        // for detail page another adapter
        gridview.setAdapter(movieAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    Uri movieUriWithId = MovieContract.MovieEntry.buildMovieUri(cursor.getLong(COL_MOVIE_ID));
                    ((Callback) getActivity())
                            .onItemSelected(movieUriWithId);
                }
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void restartLoader(){
        Log.d(LOG_TAG,"reinitialized");
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    private void updateView() {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String sort = prefs.getString(getString(R.string.pref_sort_key),
//                getString(R.string.pref_sort_popular));
//
//        //alarm intent for broadcast receiver
//        Intent alarmIntent = new Intent(getActivity(), MovieService.AlarmReceiver.class);
//        alarmIntent.putExtra(MovieService.SORT_QUERY_EXTRA, sort);
//
//        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0 , alarmIntent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000, pi);
//
//
//        Intent intent = new Intent(getActivity(), MovieService.class);
//        intent.putExtra(MovieService.SORT_QUERY_EXTRA,
//                sort);
//        getActivity().startService(intent);
        MovieSyncAdapter.syncImmediately(getActivity());
        restartLoader();

    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
    }

    public void onSortOrderChange() {
        updateView();
    }

    private String getSort() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular));
        Log.d(LOG_TAG, "sort type" + sort);
        return sort;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sort = getSort();
        Uri movieWithSort;
        if(sort.equals("popular")){
            movieWithSort = PopularEntry.CONTENT_URI;
        }
        else if(sort.equals(FAVORITE)){
            movieWithSort = MovieEntry.FAVORITE_URI;
        }
        else{
            movieWithSort = TopRatedEntry.CONTENT_URI;
        }
        Log.d(LOG_TAG,"loader: oncreate: "+movieWithSort);
        return new CursorLoader(getActivity(),
                movieWithSort,
                movieProjections,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG,"loader: on load finished : "+data.getCount());
        movieAdapter.swapCursor(data);
        if(mPosition != GridView.INVALID_POSITION){
            gridview.setSelection(mPosition);
        }
        movieAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG,"loader: on load reset : ");
        movieAdapter.swapCursor(null);
        movieAdapter.notifyDataSetChanged();
    }

}
