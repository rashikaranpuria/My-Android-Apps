package com.example.abc.movieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.abc.movieapp.data.MovieContract.*;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by theseus on 4/12/16.
 */

public class MovieDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " ( " +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COL_MOVIE_ID + " TEXT UNIQUE NOT NULL, " +
                MovieEntry.COL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COL_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COL_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COL_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COL_VOTE_AVERAGE + " TEXT NOT NULL, " +
                MovieEntry.COL_FAVORITE + " INTEGER DEFAULT 0 " +
                ");";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " ( " +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReviewEntry.COL_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COL_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.COL_MOVIE_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY ( " + ReviewEntry.COL_MOVIE_ID + " ) REFERENCES " +
                MovieEntry.TABLE_NAME + " ( " + MovieEntry._ID + "), " +
                " UNIQUE ( " + ReviewEntry.COL_MOVIE_ID + ", " +
                ReviewEntry.COL_AUTHOR + ", " + ReviewEntry.COL_AUTHOR + " ) ON CONFLICT REPLACE);";

        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " ( " +
                VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VideoEntry.COL_NAME + " TEXT NOT NULL, " +
                VideoEntry.COL_KEY + " TEXT UNIQUE NOT NULL, " +
                VideoEntry.COL_MOVIE_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY ( " + VideoEntry.COL_MOVIE_ID + " ) REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " +
                ");";

        final String SQL_CREATE_TOP_RATED_TABLE = "CREATE TABLE " + TopRatedEntry.TABLE_NAME + " ( " +
                TopRatedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TopRatedEntry.COL_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                " FOREIGN KEY ( " + TopRatedEntry.COL_MOVIE_ID + " ) REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " +
                ");";

        final String SQL_CREATE_POPULAR_TABLE = "CREATE TABLE " + PopularEntry.TABLE_NAME + " ( " +
                PopularEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PopularEntry.COL_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                " FOREIGN KEY ( " + PopularEntry.COL_MOVIE_ID + " ) REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEO_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TOP_RATED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_POPULAR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TopRatedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
