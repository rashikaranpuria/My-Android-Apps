package com.example.abc.movieapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.abc.movieapp.R;
import com.example.abc.movieapp.fragment.DetailActivityFragment;

import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailActivityFragment())
                    .commit();
        }

        ButterKnife.bind(this);

    }

}
