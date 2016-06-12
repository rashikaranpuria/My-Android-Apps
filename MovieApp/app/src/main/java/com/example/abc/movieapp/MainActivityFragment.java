package com.example.abc.movieapp;

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
import android.widget.Toast;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieGridAdapter movieGridAdapter;

    MovieDetail[] movies = {};

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        movieGridAdapter= new MovieGridAdapter(getContext(),new ArrayList<MovieDetail>());

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(movieGridAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getContext(), "hey i am at " + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    private void updateView() {

        FetchMoviesTask movieTask = new FetchMoviesTask();
        //here you fetch prefrence and send in to update
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular));
        Log.d("Type of sort",sort);

        movieTask.execute(sort);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateView();
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieDetail[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

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

            MovieDetail[] resultStrs = new MovieDetail[movieArray.length()];

            for(int i = 0; i < movieArray.length(); i++) {

                JSONObject movie_item = movieArray.getJSONObject(i);
                resultStrs[i] = new MovieDetail();
                resultStrs[i].poster_path = movie_item.getString("poster_path");
                resultStrs[i].overview = movie_item.getString("overview");
                resultStrs[i].releaseDate = movie_item.getString("release_date");
                resultStrs[i].id = movie_item.getString("id");
                resultStrs[i].title = movie_item.getString("title");
                resultStrs[i].vote_average = movie_item.getString("vote_average");

            }

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

            // Will contain the raw JSON response as a string.
            String Mo = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + params[0];
                final String APPID_PARAM = "api_key";
                final
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.d(LOG_TAG, "hey"+String.valueOf(url));
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
                    //Log.d(LOG_TAG,"---"+result[i].title+"\n");
                    movieGridAdapter.add(result[i]);
                }
            }

        }
    }

}
