package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.endpoint.jokeApi.JokeApi;
import com.udacity.gradle.builditbigger.jokeviewer.JokeViewerActivity;

import java.io.IOException;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener {

    private InterstitialAd mInterstitialAd;
    private ProgressBar progressbar;
    private String joke = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // load the init ad
        mInterstitialAd = new InterstitialAd(this.getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.banner_ad_unit_id));
        requestNewInterstitial();

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                goShowJoke();
            }
        });

        View root = inflater.inflate(R.layout.fragment_main, container, false);
        root.findViewById(R.id.button_tellJoke).setOnClickListener(this);

        progressbar = (ProgressBar) root.findViewById(R.id.progressbar_loading);

        // add ad
        FrameLayout framdLayout = (FrameLayout)root.findViewById(R.id.framelayout_adView);
        framdLayout.setVisibility(View.VISIBLE);

        AdView mAdView = new AdView(this.getActivity());
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));

        framdLayout.addView(mAdView);

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        return root;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_tellJoke) {
            // start async task..
            progressbar.setVisibility(View.VISIBLE);
            EndpointsAsyncTask endpointsAsyncTask = new EndpointsAsyncTask();
            endpointsAsyncTask.execute();
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("YOUR_DEVICE_HASH")
                .build();

        mInterstitialAd.loadAd(adRequest);
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

            progressbar.setVisibility(View.GONE);

            if (result.length() > 0) {
                joke = result;
                showJoke();
            } else {
                Toast.makeText(getActivity(), R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void goShowJoke() {
        Intent jokeViewerIntent = new Intent(this.getActivity(), JokeViewerActivity.class);
        jokeViewerIntent.putExtra(JokeViewerActivity.EXTRA_JOKE_TO_DISPLAY, joke);
        startActivity(jokeViewerIntent);
    }

    public void showJoke() {
        // show an dinitial ad before go to activity...
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            goShowJoke();
        }
    }
}
