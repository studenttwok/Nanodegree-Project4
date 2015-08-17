package com.udacity.gradle.builditbigger.jokeviewer;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class JokeViewerActivity extends ActionBarActivity {
    public static final String EXTRA_JOKE_TO_DISPLAY = "jokeToDisplay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String joke = getIntent().getStringExtra(EXTRA_JOKE_TO_DISPLAY);
        if (joke == null) {
            finish();
        }

        setContentView(R.layout.activity_jokeviewer);
        TextView jokeTextView = (TextView) findViewById(R.id.textview_joke);
        jokeTextView.setText(joke);


    }
}
