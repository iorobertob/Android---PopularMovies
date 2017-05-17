package com.iopatterns.popularmovies;

/**
 * Created by IOPatterns 2017
 *
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * This class, bsed in the Udacity JSONUtils one,
 * is used to extract data from the JSON string that is obtained from an API request,
 * in this case to theMovieDB.
 * It contains a number of String[] arrays where to store fields for each of the films in the result
 */
public final class JSONUtils {

    public static String[] ids;
    public static String[] postersURLs;
    public static String[] titles;
    public static String[] overviews;
    public static String[] releaseDates;
    public static String[] ratings;
    public static String[] languages;
    public static String[] reviews;
    public static String[] trailersURLStrings;

    public static URL[] trailersURLs; // TODO: see if this is slower than having a strings only array, not URLs

    public static final String IDS          = "id";
    public static final String POSTER       = "poster_path";
    public static final String OVERVIEW     = "overview";
    public static final String ORIGINAL     = "original_title";
    public static final String RELEASE      = "release_date";
    public static final String RATING       = "vote_average";
    public static final String LANG         = "original_language";
    public static final String RESULTS      = "results";
    public static final String TRAILER_SITE = "site";
    public static final String TOTAL_RESULTS= "total_results";
    public static final String YOUTUBE_KEY  = "key";
    public static final String CONTENT      = "content";
    public static final String POSTER_BASE  = "http://image.tmdb.org/t/p/w185/";
    public static final String YOUTUBE_BASE = "https://www.youtube.com/watch?v=";

    /**
     * Perform the actual extraction of the data from the JSON String
     * @param sourceJSON String with the rad data
     * @throws JSONException
     */
    public static void getDataFromJSON(String sourceJSON) throws JSONException{

        JSONObject moviesJSON = new JSONObject(sourceJSON);

        JSONArray moviesArray = moviesJSON.getJSONArray(RESULTS);

        ids          = new String[moviesArray.length()];
        postersURLs  = new String[moviesArray.length()];
        titles       = new String[moviesArray.length()];
        overviews    = new String[moviesArray.length()];
        releaseDates = new String[moviesArray.length()];
        ratings      = new String[moviesArray.length()];
        languages    = new String[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++)
        {
            ids[i]          = moviesArray.getJSONObject(i).getString(IDS);

            postersURLs[i]  = POSTER_BASE + moviesArray.getJSONObject(i).getString(POSTER);

            titles[i]       = moviesArray.getJSONObject(i).getString(ORIGINAL);

            overviews[i]    = moviesArray.getJSONObject(i).getString(OVERVIEW);

            releaseDates[i] = moviesArray.getJSONObject(i).getString(RELEASE);

            ratings[i]      = moviesArray.getJSONObject(i).getString(RATING);

            languages[i]    = moviesArray.getJSONObject(i).getString(LANG);
        }
    }

    public static void getTrailerDataFromJSON(String trailerJSON) throws JSONException
    {
        JSONObject trailerJSONObject = new JSONObject(trailerJSON);

        JSONArray trailerArray = trailerJSONObject.getJSONArray(RESULTS);

        for (int i = 0; i < 2; i++)
        {// Only 2 trailers

            // Build these YouTube's URLs only if we know they are for that website
            // TODO: test for when there aren't 2 objects in the arary
            if((trailerArray.getJSONObject(i).getString(TRAILER_SITE)) == "YouTube")
            {
                URL trailerURL = NetworkUtils.buildYouTubeURL(trailerArray.getJSONObject(i).getString(YOUTUBE_KEY));
                trailersURLs[i] = trailerURL;
            }
            else
            {
                // TODO: Handle this!
            }
        }
    }

    public static void getReviewDataFromJSON(String reviewsJSON) throws JSONException
    {
        JSONObject reviewsJSONObject = new JSONObject(reviewsJSON);
        JSONArray  reviewsArray      = reviewsJSONObject.getJSONArray(RESULTS);
        int totalResults             = Integer.parseInt(reviewsJSONObject.getString(TOTAL_RESULTS));

        for (int i = 0; i < totalResults; i++)
        {
            // TODO: test for when there aren't objects in the arary
            String review = reviewsArray.getJSONObject(i).getString("content");
            Log.d("CONTENT", " " + String.valueOf(i) + ": " + review);
            reviews[i] = review;
        }
    }

    public static void getDetailDataFromJSON(String detailJSON) throws JSONException
    {

        JSONObject moviesJSON = new JSONObject(detailJSON);

        ids[0]          = moviesJSON.getString(IDS);

        titles[0]       = moviesJSON.getString(ORIGINAL);

        overviews[0]    = moviesJSON.getString(OVERVIEW);

        releaseDates[0] = moviesJSON.getString(RELEASE);

        ratings[0]      = moviesJSON.getString(RATING);

        languages[0]    = moviesJSON.getString(LANG);

        postersURLs[0]  = POSTER_BASE + moviesJSON.getString(POSTER);

    }

}
