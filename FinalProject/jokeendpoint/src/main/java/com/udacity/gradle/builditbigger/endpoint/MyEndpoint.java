/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.udacity.gradle.builditbigger.endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.udacity.gradle.builditbigger.lib.JokeFactory;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "jokeApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "endpoint.builditbigger.gradle.udacity.com",
                ownerName = "endpoint.builditbigger.gradle.udacity.com",
                packagePath = ""
        )
)
public class MyEndpoint {
    private JokeFactory jokeFactory;

    // constructor
    public MyEndpoint() {
        jokeFactory = new JokeFactory();
    }


    @ApiMethod(name = "getJoke")
    public JokeWrapper getJoke() {
        String joke = jokeFactory.getJoke();
        JokeWrapper response = new JokeWrapper();
        response.setJoke(joke);

        return response;
    }

    @ApiMethod(name = "getJokeById")
    public JokeWrapper getJokeById(@Named("id") int id) {
        String joke = jokeFactory.getJokeById(id);
        JokeWrapper response = new JokeWrapper();
        response.setJoke(joke);

        return response;
    }


}
