package com.example.abc.movieapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.abc.movieapp.R;
import com.example.abc.movieapp.fragment.DetailActivityFragment;
import com.example.abc.movieapp.fragment.MainActivityFragment;
import com.example.abc.movieapp.sync.MovieSyncAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

//    @BindView(R.id.toolbar) Toolbar toolbar;

    boolean mTwoPane;
    private static String mSortBy;

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    public final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        setSupportActionBar(toolbar);
        if(findViewById(R.id.movie_detail_container) != null){
            mTwoPane = true;
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment())
                        .commit();
            }
        }
        else{
            mTwoPane = false;
        }

        MovieSyncAdapter.initializeSyncAdapter(this);
    }

    public String getSortBy(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy=prefs.getString("sort_by_key","popular");
        return sortBy;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        String sortBy=getSortBy();
        MainActivityFragment ff = null;
        if(sortBy!=null||!mSortBy.equals(sortBy)){
            ff = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if(ff!=null){
                Log.d(LOG_TAG,"ff not null: ");
                ff.onSortOrderChange();
            }
            mSortBy=sortBy;
        }
        Log.d(LOG_TAG,"fas----1");


    }

    @Override
    public void onItemSelected(Uri movieUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, movieUri);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(movieUri);
            startActivity(intent);
        }
    }
}
