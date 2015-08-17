package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.endpoint.jokeApi.JokeApi;
import com.udacity.gradle.builditbigger.jokeviewer.JokeViewerActivity;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    private InterstitialAd mInterstitialAd;
    private int isPaid;
    private String joke = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPaid = getResources().getInteger(R.integer.isPaid);

        if (isPaid == 0) {
            // load the init ad
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.banner_ad_unit_id));
            requestNewInterstitial();

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                    goShowJoke();
                }
            });

        }

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("YOUR_DEVICE_HASH")
                .build();

        mInterstitialAd.loadAd(adRequest);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view){
        // start async task..
        findViewById(R.id.progressbar_loading).setVisibility(View.VISIBLE);
        EndpointsAsyncTask endpointsAsyncTask = new EndpointsAsyncTask();
        endpointsAsyncTask.execute();

    }

    public void showJoke() {
        int isPaid = getResources().getInteger(R.integer.isPaid);
        if (isPaid == 0) {
            // show an dinitial ad before go to activity...
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                goShowJoke();
            }
        } else {
            // go activity..
            //Toast.makeText(this, joke, Toast.LENGTH_SHORT).show();
            goShowJoke();
        }
    }

    public void goShowJoke() {
        Intent jokeViewerIntent = new Intent(this, JokeViewerActivity.class);
        jokeViewerIntent.putExtra(JokeViewerActivity.EXTRA_JOKE_TO_DISPLAY, joke);
        startActivity(jokeViewerIntent);
    }

    public class EndpointsAsyncTask extends AsyncTask<Integer, Void, String> {
        private JokeApi myApiService = null;

        @Override
        protected String doInBackground(Integer... params) {
            if(myApiService == null) {  // Only do this once
                JokeApi.Builder builder = new JokeApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver

                myApiService = builder.build();
            }


            try {
                if (params.length > 0) {
                    int id = params[0];
                    return myApiService.getJokeById(id).execute().getJoke();
                }
                return myApiService.getJoke().execute().getJoke();

            } catch (IOException e) {
                //return e.getMessage();
                return "";
            }
        }


        @Override
        protected void onPostExecute(String result) {

            findViewById(R.id.progressbar_loading).setVisibility(View.GONE);

            if (result.length() > 0) {
                joke = result;
                showJoke();
            } else {
                Toast.makeText(MainActivity.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
