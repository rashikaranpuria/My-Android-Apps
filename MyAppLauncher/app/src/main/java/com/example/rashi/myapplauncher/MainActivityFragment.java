package com.example.rashi.myapplauncher;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Button button1 = (Button) rootView.findViewById(R.id.button1);
        Button button2 = (Button) rootView.findViewById(R.id.button2);
        Button button3 = (Button) rootView.findViewById(R.id.button3);
        Button button4 = (Button) rootView.findViewById(R.id.button4);
        Button button5 = (Button) rootView.findViewById(R.id.button5);
        Button button6 = (Button) rootView.findViewById(R.id.button6);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence text = "This button will launch my Spotify Streamer App!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(getActivity(), text.toString(), duration);
                toast.show();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence text = "This button will launch my Scores App!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(getActivity(), text.toString(), duration);
                toast.show();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence text = "This button will launch my Library App!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(getActivity(), text.toString(), duration);
                toast.show();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence text = "This button will launch my Build It Bigger App!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(getActivity(), text.toString(), duration);
                toast.show();
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence text = "This button will launch my XYZ Reader App!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(getActivity(), text.toString(), duration);
                toast.show();
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence text = "This button will launch my Capstone App!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(getActivity(), text.toString(), duration);
                toast.show();
            }
        });

        return rootView;
    }
}
