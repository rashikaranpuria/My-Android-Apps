package com.example.abc.movieapp.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
import com.example.abc.movieapp.adapter.MovieGridAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.abc.movieapp.data.MovieContract.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieGridAdapter movieGridAdapter;

    MovieDetail[] movies = {};
    public static final String LOG_TAG=MainActivityFragment.class.getSimpleName();

    @BindView(R.id.gridview)
    GridView gridview;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        movieGridAdapter= new MovieGridAdapter(getContext(),new ArrayList<MovieDetail>());
        // for detail page another adapter
        gridview.setAdapter(movieGridAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                MovieDetail movieDetail=movieGridAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("movieDetailObj",movieDetail);
                Log.d(LOG_TAG, String.valueOf("started-intent: "));

                startActivity(intent);
            }
        });
        return rootView;
    }

    private void updateView() {

        FetchMoviesTask movieTask = new FetchMoviesTask();
        //here you fetch preference and send in to update
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular));
        Log.d("Type of sort",sort);

        movieTask.execute(sort);
    }

    private String getSort() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular));
        Log.d("Type of sort function ",sort);
        return sort;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateView();
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieDetail[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


        private ContentValues[] getReviews(long movie_id) throws JSONException {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String ReviewJsonStr = null;

            try {
                // Construct the URL for the reviews link of particular movie_id
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String APPID_PARAM = "api_key";


                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                                .appendPath(Long.toString(movie_id))
                                .appendPath("reviews")
                                .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY).build();

                URL url = new URL(builtUri.toString());
                Log.d(LOG_TAG, String.valueOf(url));
                // Create the request to MoviedbAPI, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                ReviewJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the reviews data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            //converting jsonstr to content value now

            JSONObject ReviewJson = new JSONObject(ReviewJsonStr);
            JSONArray reviewArray = ReviewJson.getJSONArray("results");

            Vector <ContentValues> cVReviews = new Vector<ContentValues>(reviewArray.length());

            for (int i=0;i<reviewArray.length();i++) {
                ContentValues reviewValue = new ContentValues();
                JSONObject review_item = reviewArray.getJSONObject(i);

                reviewValue.put(ReviewEntry.COL_AUTHOR, review_item.getString("author"));
                reviewValue.put(ReviewEntry.COL_CONTENT, review_item.getString("content"));
                reviewValue.put(ReviewEntry.COL_MOVIE_ID, Long.toString(movie_id));

                cVReviews.add(reviewValue);
            }

            if (cVReviews.size()>0){
                ContentValues[] cVReviewsArray = new ContentValues[cVReviews.size()];
                cVReviews.toArray(cVReviewsArray);
                return cVReviewsArray;
            }
            else{
                return null;
            }
        }


        private ContentValues[] getVideos(long movie_id) throws JSONException {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String VideoJsonStr = null;

            try {
                // Construct the URL for the reviews link of particular movie_id
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String APPID_PARAM = "api_key";


                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(Long.toString(movie_id))
                        .appendPath("videos")
                        .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY).build();

                URL url = new URL(builtUri.toString());
                Log.d(LOG_TAG, String.valueOf(url));
                // Create the request to MoviedbAPI, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                Log.d("value of trailers", "movie_id "+ movie_id +String.valueOf(buffer));

                VideoJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the videos data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            //converting json str to content value now

            JSONObject VideosJson = new JSONObject(VideoJsonStr);
            JSONArray videosArray = VideosJson.getJSONArray("results");

            Vector <ContentValues> cVVideos = new Vector<ContentValues>(videosArray.length());

            for (int i = 0 ; i < videosArray.length() ; i++) {
                ContentValues videoValue = new ContentValues();
                JSONObject video_item = videosArray.getJSONObject(i);

                videoValue.put(VideoEntry.COL_NAME, video_item.getString("name"));
                videoValue.put(VideoEntry.COL_KEY, video_item.getString("key"));
                videoValue.put(VideoEntry.COL_MOVIE_ID, Long.toString(movie_id));

                cVVideos.add(videoValue);
            }

            if (cVVideos.size()>0){
                ContentValues[] cVVideosArray = new ContentValues[cVVideos.size()];
                cVVideos.toArray(cVVideosArray);
                return cVVideosArray;
            }
            else{
                return null;
            }
        }
        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private MovieDetail[] getMovieDataFromJson(String MovieJsonStr)
                throws JSONException {

            JSONObject MovieJson = new JSONObject(MovieJsonStr);
            JSONArray movieArray = MovieJson.getJSONArray("results");

            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            Vector<ContentValues> cVVectorSort = new Vector<ContentValues>(movieArray.length());

            MovieDetail[] resultStrs = new MovieDetail[movieArray.length()];

            for(int i = 0; i < movieArray.length(); i++) {

                ContentValues movieValues = new ContentValues();
                ContentValues sortValues = new ContentValues();

                JSONObject movie_item = movieArray.getJSONObject(i);
                resultStrs[i] = new MovieDetail();
                resultStrs[i].poster_path = movie_item.getString("poster_path");
                resultStrs[i].overview = movie_item.getString("overview");
                resultStrs[i].releaseDate = movie_item.getString("release_date");
                resultStrs[i].id = movie_item.getString("id");
                resultStrs[i].title = movie_item.getString("title");
                resultStrs[i].vote_average = movie_item.getString("vote_average");

                movieValues.put(MovieEntry.COL_POSTER_PATH, movie_item.getString("poster_path"));
                movieValues.put(MovieEntry.COL_OVERVIEW, movie_item.getString("overview"));
                movieValues.put(MovieEntry.COL_RELEASE_DATE, movie_item.getString("release_date"));
                movieValues.put(MovieEntry.COL_MOVIE_ID, movie_item.getString("id"));
                movieValues.put(MovieEntry.COL_TITLE, movie_item.getString("title"));
                movieValues.put(MovieEntry.COL_VOTE_AVERAGE, movie_item.getString("vote_average"));

                cVVector.add(movieValues);

                if(getSort() == "popular"){
                    sortValues.put(PopularEntry.COL_MOVIE_ID, movie_item.getString("id"));
                }
                else{
                    sortValues.put(TopRatedEntry.COL_MOVIE_ID, movie_item.getString("id"));
                }

                cVVectorSort.add(sortValues);

                // try catch block for fetching and storing reviews related to this movie
                try {
                    ContentValues[] cVReviews = getReviews(Long.parseLong(movie_item.getString("id")));
                    // change vector to array
                    if ( cVReviews != null) {
                        int review_insert = getContext().getContentResolver().bulkInsert(ReviewEntry.CONTENT_URI, cVReviews);
                        Log.d(LOG_TAG, "Fetch Reviews Complete. " + review_insert + " Inserted");

                    }
                    else{
                        Log.d(LOG_TAG, "Fetch Reviews empty. 0 " + " Inserted");
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

                // try catch block to fetch and store videos related to this movie
                try {
                    ContentValues[] cVVideos = getVideos(Long.parseLong(movie_item.getString("id")));
                    // check if array is not null
                    if ( cVVideos != null) {
                        int video_insert = getContext().getContentResolver().bulkInsert(VideoEntry.CONTENT_URI, cVVideos);
                        Log.d(LOG_TAG, "Fetch Videos Complete. " + video_insert + " Inserted");
                    }
                    else{
                        Log.d(LOG_TAG, "Fetch Videos empty. 0 " + " Inserted");
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

            }
            int inserted = 0;
            int insertedSort = 0;
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                ContentValues[] cvArraySort = new ContentValues[cVVectorSort.size()];
                cVVector.toArray(cvArray);
                cVVectorSort.toArray(cvArraySort);
                inserted = getContext().getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
                if(getSort() == "popular"){
                    insertedSort = getContext().getContentResolver().bulkInsert(PopularEntry.CONTENT_URI, cvArraySort);
                }
                else
                {
                    insertedSort = getContext().getContentResolver().bulkInsert(TopRatedEntry.CONTENT_URI, cvArraySort);
                }
            }

            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted" + insertedSort + "Inserted Sort");

            return resultStrs;

        }
        @Override
        protected MovieDetail[] doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            String MovieJsonStr = null;

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0];
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.d(LOG_TAG, String.valueOf(url));
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                MovieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(MovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }


        @Override
        protected void onPostExecute(MovieDetail[] result) {
            movies=result;
            if (result != null) {
                movieGridAdapter.clear();
                for (int i=0;i<result.length;i++){
                    movieGridAdapter.add(result[i]);
                }
            }

        }
    }

}
