package com.example.abc.movieapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by rashi on 11/12/16.
 */

public class MovieProvider extends ContentProvider {

    public static final UriMatcher mURIMatcher = buildUriMatcher();

    private MovieDBHelper mOpenHelper;

    public static final String FAVORITE = "favorites";

    static final int MOVIES = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int FAVORITE_MOVIES = 103;

    static final int REVIEWS = 200;
    static final int REVIEW_WITH_ID = 201;

    static final int VIDEOS = 300;
    static final int VIDEOS_WITH_ID = 301;
    
    static final int POPULAR = 400;
    static final int POPULAR_WITH_ID = 401;
    
    static final int TOP_RATED = 500;
    static final int TOP_RATED_WITH_ID = 401;
    
    private static SQLiteQueryBuilder movieQueryBuilder;
    private static SQLiteQueryBuilder reviewsQueryBuilder;
    private static SQLiteQueryBuilder videosQueryBuilder;
    private static SQLiteQueryBuilder popularMoviesQueryBuilder;
    private static SQLiteQueryBuilder topRatedMoviesQueryBuilder;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE , MOVIES);
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        uriMatcher.addURI(authority, "/" + FAVORITE , FAVORITE_MOVIES);

        uriMatcher.addURI(authority, MovieContract.PATH_REVIEW , REVIEWS);
        uriMatcher.addURI(authority, MovieContract.PATH_REVIEW + "/#" , REVIEW_WITH_ID);

        uriMatcher.addURI(authority, MovieContract.PATH_VIDEO , VIDEOS);
        uriMatcher.addURI(authority, MovieContract.PATH_VIDEO + "/#" , VIDEOS_WITH_ID);

        uriMatcher.addURI(authority, MovieContract.PATH_POPULAR , POPULAR);
        uriMatcher.addURI(authority, MovieContract.PATH_POPULAR + "/#" , POPULAR_WITH_ID);

        uriMatcher.addURI(authority, MovieContract.PATH_TOP_RATED , TOP_RATED);
        uriMatcher.addURI(authority, MovieContract.PATH_TOP_RATED + "/#" , TOP_RATED_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
