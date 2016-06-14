package com.example.abc.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
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

        MovieDetail movie = getItem(position);
        ImageView imageView;

        if (convertView == null) {
            imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.poster,parent,false).findViewById(R.id.poster);
        }
        else {
            imageView = (ImageView) convertView;
        }
        String url = base_path + movie.poster_path;
        Picasso.with(context).load(url).into(imageView);
        return imageView;
    }

}
