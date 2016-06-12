package com.example.abc.movieapp;

/**
 * Created by abc on 6/12/16.
 */
public class MovieDetail {
    String poster_path;
    String overview;
    String releaseDate;
    String id;
    String title;
    String vote_average;

    public MovieDetail(String poster_path, String overview, String releaseDate, String id, String title, String vote_average){
        this.poster_path = poster_path;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.id = id;
        this.title = title;
        this.vote_average = vote_average;
    }
}
