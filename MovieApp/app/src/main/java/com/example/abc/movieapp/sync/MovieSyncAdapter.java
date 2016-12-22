package com.example.abc.movieapp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.abc.movieapp.BuildConfig;
import com.example.abc.movieapp.MovieDetail;
import com.example.abc.movieapp.R;
import com.example.abc.movieapp.activity.MainActivity;
import com.example.abc.movieapp.data.MovieContract;

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

/**
 * Created by rashi on 22/12/16.
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    private final String FAVORITE = "favorite";

    private String sort = null;

    // Interval at which to sync with the weather, in milliseconds.
    // 60 seconds (1 minute)  180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    private static final String[] NOTIFY_MOVIE_PROJECTION = new String[] {
            MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME+"."+ MovieContract.MovieEntry.COL_MOVIE_ID,
            MovieContract.MovieEntry.COL_TITLE,
            MovieContract.MovieEntry.COL_OVERVIEW,
            MovieContract.MovieEntry.COL_VOTE_AVERAGE,
            MovieContract.MovieEntry.COL_RELEASE_DATE,
            MovieContract.MovieEntry.COL_POSTER_PATH,
            MovieContract.MovieEntry.COL_FAVORITE
    };
    // these indices must match the projection
    static final int COL_MOVIE_ID=1;
    static final int COL_TITLE=2;
    static final int COL_OVERVIEW=3;
    static final int COL_VOTE_AVERAGE=4;
    static final int COL_RELEASE_DATE=5;
    static final int COL_POSTER_PATH=6;
    static final int COL_FAVORITE=7;

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int MOVIE_NOTIFICATION_ID = 3004;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(LOG_TAG, "starting sync");
        sort = getSort();

        if(sort.equals(FAVORITE)){
            return;
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
            final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/" + sort;
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
                Log.d(LOG_TAG, "input strem null");
                // Nothing to do.
                return;
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
                Log.d(LOG_TAG, "buffer empty");
                return;
            }
            MovieJsonStr = buffer.toString();

            Log.e(LOG_TAG, "movie jsonstr " + MovieJsonStr.toString());

            getMovieDataFromJson(MovieJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "handle" +  "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
//                return null;
        }
        catch (JSONException e) {
            Log.e(LOG_TAG, "handle" +  e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
//                    restartLoader();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "handle" +  "Error closing stream", e);
                }
            }
        }



        // This will only happen if there was an error getting or parsing the forecast.
        return;
    }

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

        Vector<ContentValues> cVReviews = new Vector<ContentValues>(reviewArray.length());

        for (int i=0;i<reviewArray.length();i++) {
            ContentValues reviewValue = new ContentValues();
            JSONObject review_item = reviewArray.getJSONObject(i);

            reviewValue.put(MovieContract.ReviewEntry.COL_AUTHOR, review_item.getString("author"));
            reviewValue.put(MovieContract.ReviewEntry.COL_CONTENT, review_item.getString("content"));
            reviewValue.put(MovieContract.ReviewEntry.COL_MOVIE_ID, Long.toString(movie_id));

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

            videoValue.put(MovieContract.VideoEntry.COL_NAME, video_item.getString("name"));
            videoValue.put(MovieContract.VideoEntry.COL_KEY, video_item.getString("key"));
            videoValue.put(MovieContract.VideoEntry.COL_MOVIE_ID, Long.toString(movie_id));

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
    private void getMovieDataFromJson(String MovieJsonStr)
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

            movieValues.put(MovieContract.MovieEntry.COL_POSTER_PATH, movie_item.getString("poster_path"));
            movieValues.put(MovieContract.MovieEntry.COL_OVERVIEW, movie_item.getString("overview"));
            movieValues.put(MovieContract.MovieEntry.COL_RELEASE_DATE, movie_item.getString("release_date"));
            movieValues.put(MovieContract.MovieEntry.COL_MOVIE_ID, movie_item.getString("id"));
            movieValues.put(MovieContract.MovieEntry.COL_TITLE, movie_item.getString("title"));
            movieValues.put(MovieContract.MovieEntry.COL_VOTE_AVERAGE, movie_item.getString("vote_average"));

//                Log.d(LOG_TAG, "movieValues" + movieValues.getAsString(MovieEntry.COL_TITLE));

            cVVector.add(movieValues);
            Log.d(LOG_TAG, sort);
            if(sort.equals("popular")){
                sortValues.put(MovieContract.PopularEntry.COL_MOVIE_ID, movie_item.getString("id"));
            }
            else if(sort.equals("top_rated")){
                sortValues.put(MovieContract.TopRatedEntry.COL_MOVIE_ID, movie_item.getString("id"));
            }

            cVVectorSort.add(sortValues);

            // try catch block for fetching and storing reviews related to this movie
            try {
                ContentValues[] cVReviews = getReviews(Long.parseLong(movie_item.getString("id")));
                // change vector to array
                if ( cVReviews != null) {
                    int review_insert = getContext().getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cVReviews);
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
                    int video_insert = getContext().getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, cVVideos);
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
            inserted = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            notifyMovie();
            if(sort.equals("popular")){
                insertedSort = getContext().getContentResolver().bulkInsert(MovieContract.PopularEntry.CONTENT_URI, cvArraySort);
//                    Log.d(LOG_TAG, "inserted popular entry "+insertedSort);
            }
            else if(sort.equals("top_rated"))
            {
                insertedSort = getContext().getContentResolver().bulkInsert(MovieContract.TopRatedEntry.CONTENT_URI, cvArraySort);
//                    Log.d(LOG_TAG, getSort() + "inserted top rated entry "+insertedSort);
            }
        }

        Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted" + insertedSort + "Inserted Sort");

//            return resultStrs;

    }

    private String getSort() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sort = prefs.getString("sort_by_key","popular");
        Log.d(LOG_TAG, "sort type in movie sync adapter " + sort);
        return sort;
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private void notifyMovie() {
        Context context = getContext();
        //checking the last update and notify if it' the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lastNotificationKey = context.getString(R.string.pref_last_notification);

        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));


        if(displayNotifications){
            long lastSync = prefs.getLong(lastNotificationKey, 0);

            if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
                // Last sync was more than 1 day ago, let's send a notification with the weather.
                String sort = getSort();

                if(sort.equals(FAVORITE)){
                    return;
                }

                Uri movieUri = null;


                if(sort.equals("popular")){
                    movieUri = MovieContract.PopularEntry.CONTENT_URI;

                }
                else {
                    movieUri = MovieContract.TopRatedEntry.CONTENT_URI;

                }


                // we'll query our contentProvider, as always
                Cursor cursor = context.getContentResolver().query(movieUri, NOTIFY_MOVIE_PROJECTION, null, null, null);

                if (cursor.moveToFirst()) {
                    String mtitle = cursor.getString(COL_TITLE);
                    String overview = cursor.getString(COL_OVERVIEW);
                    String ratings = cursor.getString(COL_VOTE_AVERAGE) + "/10";

                    String poster_path = cursor.getString(COL_POSTER_PATH);
                    String title = context.getString(R.string.app_name);

                    // Define the text of the forecast.
                    String contentText = String.format(mtitle + " " + ratings);

                    //build your notification here.
                    // NotificationCompatBuilder is a very convenient way to build backward-compatible
                    // notifications.  Just throw in some data.
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
                                    .setSmallIcon(R.drawable.default_image)
                                    .setContentTitle(title)
                                    .setContentText(contentText);

                    // Make something interesting happen when the user clicks on the notification.
                    // In this case, opening the app is sufficient.
                    Intent resultIntent = new Intent(context, MainActivity.class);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
                    mNotificationManager.notify(MOVIE_NOTIFICATION_ID, mBuilder.build());

                    //refreshing last sync
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(lastNotificationKey, System.currentTimeMillis());
                    editor.commit();
                }
                cursor.close();
            }

        }

    }
}
