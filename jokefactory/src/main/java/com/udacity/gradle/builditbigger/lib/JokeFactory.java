package com.udacity.gradle.builditbigger.lib;

import java.util.ArrayList;
import java.util.Random;

public class JokeFactory {
    private ArrayList<String> jokes = new ArrayList<String>();
    private Random random;

    public JokeFactory() {

        random = new Random(System.currentTimeMillis());

        // insert jokes
        jokes.add("Joke 1");
        jokes.add("Joke 2");
        jokes.add("Joke 3");
        jokes.add("Joke 4");
        jokes.add("Joke 5");
        jokes.add("Joke 6");
        jokes.add("Joke 7");
        jokes.add("Joke 8");
        jokes.add("Joke 9");
        jokes.add("Joke 10");
    }

    public String getJoke() {
        int index = random.nextInt(jokes.size());
        return jokes.get(index);
    }
    public String getJokeById(int index) {
        if (index < 0 || index >= jokes.size()) {
            index = 0;  // return first joke
        }
        return jokes.get(index);
    }

    public int getNumOfJokes() {
        return jokes.size();
    }
}
