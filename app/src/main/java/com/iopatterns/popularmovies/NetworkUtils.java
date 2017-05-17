package com.iopatterns.popularmovies;
/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities, based in Udacity's class,
 * will be used to communicate with the API services
 */
public final class NetworkUtils {

    //TODO: REPLACE THIS API KEY WITH A VALID STRING!
    // KEY: 857a9205586fa1240dc580f4185577fa
    private  static final String API_KEY        = "857a9205586fa1240dc580f4185577fa";

    /**
     * This method builds an URL, from a string, by first constructing an URI.
     * The values for the scheme, authority, paths and queries are fixed, except for the
     * sortBy and page field which are changed from the Main Activity.
     * @param sortBy this can by by Popularity or Vote Average
     * @param page the page on the long list of films in the Movie DB
     * @return the URL for the API query
     */
    public static URL buildMovieAPIURL(String sortBy, int page){

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", "en-US")
                .appendQueryParameter("sort_by", sortBy)
                .appendQueryParameter("include_adult", "false")
                .appendQueryParameter("include_video", "false")
                .appendQueryParameter("page", String.valueOf(page));

        String movieDBURL = builder.build().toString();
        // This resturns an URL that looks something like this:
        // https://api.themoviedb.org/3/discover/movie?api_key=857a9205586fa1240dc580f4185577fa&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1

        URL url = null;

        try {
            url = new URL(movieDBURL.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildMovieTrailerURL(String id)
    {

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(id)
                .appendPath("videos")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", "en-US");

        String movieDBURL = builder.build().toString();
        // This will return an URL that looks something like this:
        // https://api.themoviedb.org/3/movie/283995/videos?api_key=857a9205586fa1240dc580f4185577fa&language=en-US
        Log.d("TRAILERS URL", movieDBURL);

        URL url = null;

        try {
            url = new URL(movieDBURL.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildMovieReviewsURL(String id)
    {
        Uri.Builder builder = new Uri.Builder();

        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(id)
                .appendPath("reviews")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", "en-US");

        String movieDBURL = builder.build().toString();
        // This will return an URL that looks something like this:
        // https://api.themoviedb.org/3/movie/{movie_id}/reviews?api_key=<<api_key>>&language=en-US
        Log.d("REVIEWS URL", movieDBURL);

        URL url = null;

        try {
            url = new URL(movieDBURL.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildYouTubeURL(String key)
    {
        Uri.Builder builder = new Uri.Builder(); // TODO: Abstract to have less instances? it repeats

        builder.scheme("https")
                .authority("youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v",key);

        String youTubeURL = builder.build().toString();
        Log.d("YOUTUBE URL", youTubeURL);
        // This will return an URL that will look like this:
        // https://www.youtube.com/watch?v=key

        URL url = null;

        try
        {
            url = new URL(youTubeURL.toString());
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildMovieDetailURL(String id)
    {
        // This has to look something like this:
        //https://api.themoviedb.org/3/movie/{movie_id}?api_key=<<api_key>>&language=en-US
        Uri.Builder builder = new Uri.Builder();

        builder.scheme("http")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(id)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", "en-US");

        String movieDetailURL = builder.build().toString();

        URL url = null;

        try
        {
            url = new URL(movieDetailURL.toString());
        }
        catch(MalformedURLException e )
        {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}