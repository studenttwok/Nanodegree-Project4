package com.udacity.gradle.builditbigger;

import android.test.AndroidTestCase;


public class AsyncTaskTest extends AndroidTestCase {

    public void testAsyncTest() {

        final MainActivity.EndpointsAsyncTask eat = new MainActivity().new EndpointsAsyncTask();

        String result = eat.doInBackground(0);
        assertEquals("Joke 1", result);

    }
}
