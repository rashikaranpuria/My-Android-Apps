package com.example.abc.movieapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by abc on 6/12/16.
 */
public class MovieDetail implements Parcelable{
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

    public MovieDetail() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]
            {
                this.poster_path,
                this.overview,
                this.releaseDate,
                this.id,
                this.title ,
                this.vote_average
            });
    }

    private MovieDetail(Parcel in)
    {
        String[] data = new String[6];
        in.readStringArray(data);
        this.poster_path = data[0];
        this.overview = data[1];
        this.releaseDate = data[2];
        this.id = data[3];
        this.title = data[4];
        this.vote_average = data[5];
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MovieDetail createFromParcel(Parcel in) {
            return new MovieDetail(in);
        }

        public MovieDetail[] newArray(int size) {
            return new MovieDetail[size];
        }
    };
}
