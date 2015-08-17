package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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

    private ProgressBar progressbar;
    private String joke = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_main, container, false);
        root.findViewById(R.id.button_tellJoke).setOnClickListener(this);

        progressbar = (ProgressBar) root.findViewById(R.id.progressbar_loading);

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
                goShowJoke();
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

}
