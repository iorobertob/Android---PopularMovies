package com.iopatterns.popularmovies;

/**
 * Created by IOPatterns 2017
 *
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is used to extract data from the JSON string that is obtained from an API request,
 * in this case to theMovieDB.
 * It contains a number of String[] arrays where to store fields for each of the films in the result
 */
public final class JSONUtils {

    public static String[] postersURLs;
    public static String[] titles;
    public static String[] overviews;
    public static String[] releaseDates;
    public static String[] ratings;
    public static String[] languages;


    /**
     * Perform the actual extraction of the data from the JSON String
     * @param sourceJSON String with the rad data
     * @throws JSONException
     */
    public static void getDataFromJSON(String sourceJSON) throws JSONException{

        final String POSTER     = "poster_path";
        final String RESULTS    = "results";
        final String OVERVIEW   = "overview";
        final String ORIGINAL   = "original_title";
        final String RELEASE    = "release_date";
        final String RATING     = "vote_average";
        final String LANG       = "original_language";

        final String BASE = "http://image.tmdb.org/t/p/w185/";

        JSONObject moviesJSON = new JSONObject(sourceJSON);

        JSONArray moviesArray = moviesJSON.getJSONArray(RESULTS);

        postersURLs  = new String[moviesArray.length()];
        titles       = new String[moviesArray.length()];
        overviews    = new String[moviesArray.length()];
        releaseDates = new String[moviesArray.length()];
        ratings      = new String[moviesArray.length()];
        languages    = new String[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++){

            postersURLs[i]  = BASE + moviesArray.getJSONObject(i).getString(POSTER);

            titles[i]       = moviesArray.getJSONObject(i).getString(ORIGINAL);

            overviews[i]    = moviesArray.getJSONObject(i).getString(OVERVIEW);

            releaseDates[i] = moviesArray.getJSONObject(i).getString(RELEASE);

            ratings[i]      = moviesArray.getJSONObject(i).getString(RATING);

            languages[i]    = moviesArray.getJSONObject(i).getString(LANG);

        }


    }

}
