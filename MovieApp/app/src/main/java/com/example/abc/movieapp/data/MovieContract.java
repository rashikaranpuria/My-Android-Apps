package com.example.abc.movieapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by theseus on 4/12/16.
 */

public class MovieContract {

    public static final String LOG_TAG = MovieContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.example.abc.movieapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final String PATH_REVIEW = "review";

    public static final String PATH_VIDEO = "video";

    public static final String PATH_TOP_RATED = "top_rated";

    public static final String PATH_POPULAR = "popular";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COL_POSTER_PATH = "poster_path";

        public static final String COL_OVERVIEW = "overview";

        public static final String COL_RELEASE_DATE = "release_date";

        public static final String COL_MOVIE_ID = "movie_id";

        public static final String COL_TITLE = "title";

        public static final String COL_VOTE_AVERAGE = "vote_average";

        public static final String COL_FAVORITE = "favorite";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Long getMovieIdFromUri(Uri uri){
            Log.d(LOG_TAG, "getMovieIdFromUri: " + uri);
            return Long.parseLong(uri.getPathSegments().get(1));
        }

//        public static Uri buildMoviewith

    }


    //Contract for Review table
    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "review";

        //foreign key
        public static final String COL_MOVIE_ID = "movie_id";

        public static final String COL_AUTHOR = "author";

        public static final String COL_CONTENT = "content";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Long getMovieIdFromUri(Uri uri){
            Log.d(LOG_TAG, "getMovieFromReviewUri: " + uri);
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    //Contract for Video table
    public  static final class VideoEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        public static final String TABLE_NAME = "video";
        public static final String COL_NAME = "name";
        public static final String COL_KEY = "key";
        //foreign key
        public static final String COL_MOVIE_ID = "movie_id";

        public static Uri buildVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Long getMovieIdFromUri(Uri uri){
            Log.d(LOG_TAG, "getMovieFromVideoUri: " + uri);
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public  static final class PopularEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;

        public static final String TABLE_NAME = "popular";

        public static final String COL_MOVIE_ID = "movie_id";

        public static Uri buildPopularUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Long getMovieIdFromUri(Uri uri){
            Log.d(LOG_TAG, "getMovieFromVideoUri: " + uri);
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public  static final class TopRatedEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;

        public static final String TABLE_NAME = "top_rated";
        public static final String COL_MOVIE_ID = "movie_id";

        public static Uri buildTopRatedUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Long getMovieIdFromUri(Uri uri){
            Log.d(LOG_TAG, "getMovieFromVideoUri: " + uri);
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
}
