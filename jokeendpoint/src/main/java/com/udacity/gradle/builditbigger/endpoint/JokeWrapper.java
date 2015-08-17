package com.udacity.gradle.builditbigger.endpoint;

/**
 * The object model for the data we are sending through endpoints
 */
public class JokeWrapper {

    private String joke;

    public String getJoke() {
        return joke;
    }

    public void setJoke(String joke) {
        this.joke = joke;
    }
}