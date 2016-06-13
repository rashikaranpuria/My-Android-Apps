package com.example.abc.movieapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by abc on 6/12/16.
 */
public class MovieGridAdapter extends ArrayAdapter<MovieDetail> {
    private Context context;
    String base_path = "http://image.tmdb.org/t/p/w185/";
    public MovieGridAdapter(Context context,  List<MovieDetail> movies) {
        super(context, 0, movies);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        MovieDetail movie = getItem(position);
        ImageView imageView;
        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
        else {
            imageView = (ImageView) convertView;
        }
        String url = base_path + movie.poster_path;
        Picasso.with(context).load(url).into(imageView);
        return imageView;
    }

}
