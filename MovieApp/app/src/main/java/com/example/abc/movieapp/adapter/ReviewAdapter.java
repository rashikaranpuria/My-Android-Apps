package com.example.abc.movieapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.abc.movieapp.R;
import com.example.abc.movieapp.data.MovieContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rashi on 18/12/16.
 */

public class ReviewAdapter extends CursorAdapter {

    static final String[] reviewsProjection={
            MovieContract.ReviewEntry.TABLE_NAME+"."+MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.TABLE_NAME+"."+MovieContract.ReviewEntry.COL_MOVIE_ID,
            MovieContract.ReviewEntry.COL_AUTHOR,
            MovieContract.ReviewEntry.COL_CONTENT
    };
    static final int COL_AUTHOR=2;
    static final int COL_CONTENT=3;
    public static final String LOG_TAG=ReviewAdapter.class.getSimpleName();

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.review, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView author = (TextView)view.findViewById(R.id.author);
        TextView content = (TextView)view.findViewById(R.id.content);

        author.setText(cursor.getString(COL_AUTHOR));
        content.setText(cursor.getString(COL_CONTENT));
    }
}

