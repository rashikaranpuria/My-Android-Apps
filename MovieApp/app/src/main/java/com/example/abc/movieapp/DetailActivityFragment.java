package com.example.abc.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    //all views received using butterknife
    @BindView(R.id.detail_movie_title) TextView movie_title;
    @BindView(R.id.detail_movie_image) ImageView imageView;
    @BindView(R.id.detail_movie_release_date) TextView movie_release_date;
    @BindView(R.id.detail_movie_ratings) TextView movie_ratings;
    @BindView(R.id.detail_movie_overview) TextView movie_overview;

    public DetailActivityFragment() {
    }
    public static final String LOG_TAG=DetailActivityFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.movie_fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        Intent intent = getActivity().getIntent();

        if (intent != null) {
            MovieDetail movieDetailObj = (MovieDetail) intent.getParcelableExtra("movieDetailObj");
            if(movieDetailObj != null){
                movie_title.setText(movieDetailObj.title);
                String url = "http://image.tmdb.org/t/p/w185/" + movieDetailObj.poster_path;
                Picasso.with(getContext()).load(url).into(imageView);
                movie_release_date.setText(movieDetailObj.releaseDate);
                movie_ratings.setText(movieDetailObj.vote_average + "/10");
                movie_overview.setText(movieDetailObj.overview);
            }
        }
        return rootView;
    }
}
