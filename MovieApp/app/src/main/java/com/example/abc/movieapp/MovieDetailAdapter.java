package com.example.abc.movieapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by abc on 6/13/16.
 */
public class MovieDetailAdapter extends ArrayAdapter<MovieDetail> {
    private Context context;

    public MovieDetailAdapter(Context context, List<MovieDetail> movies) {
        super(context, 0, movies);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        MovieDetail movie = getItem(position);

        if (convertView == null) {


        }
        else {

        }

        return convertView;
    }
}
