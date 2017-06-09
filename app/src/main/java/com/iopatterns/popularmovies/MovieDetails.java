package com.iopatterns.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iopatterns.popularmovies.databinding.ContentMovieDetailsBinding;
import com.iopatterns.popularmovies.movieDataSystem.DataBaseContract;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.URL;

/**
 * This class is the Child activity that shows the Detailed view for a film, including the
 * Title, Release Data, Language, Rating and Overview
 */
public class MovieDetails extends AppCompatActivity{

    // This is an automatically generated class for the data binding. Note how the name is created
    // from the name of the layout it works with.
    ContentMovieDetailsBinding mBinding;

    private int         mIndexYouCameFrom;
    private int         mMovieID;
    private boolean     mFromFavourites;

    private String      mMovieTrailerJSON;
    private String      mMovieReviewsJSON;
    private String      mMovieDetailJSON;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_movie_details);

        // TODO: see if this is overriding the previous setContentView call just above
        mBinding = DataBindingUtil.setContentView(this, R.layout.content_movie_details);

        //initGUIDetail();

        Intent fromPrevActivity = getIntent();

        /**
         * This tells us the index for the arrays so we can refer to the film data in that position
         */
        if(fromPrevActivity.hasExtra("INDEX")){
            mIndexYouCameFrom   = fromPrevActivity.getIntExtra("INDEX", 0);
        }

        /**
         * This tells us if we come from a Favourites case
         */
        if(fromPrevActivity.hasExtra("FROM_FAVOURITES")){
            mFromFavourites     = fromPrevActivity.getBooleanExtra("FROM_FAVOURITES", false);
        }

        /**
         * This tells us if we come from a Favourites case
         */
        if(fromPrevActivity.hasExtra("MOVIE_ID")){
            mMovieID     = fromPrevActivity.getIntExtra("MOVIE_ID", 0);
            Log.d("MOVIE ID", String.valueOf(mMovieID));
        }


        // TODO: this if statement is not good here anymore, since we might come from a Favourites case
        if(fromPrevActivity.hasExtra("MOVIES_JSON")) {
            try {

                //String data = fromPrevActivity.getStringExtra("MOVIES_JSON");

                /**
                 * Extract the data from Json and fill the static values in the JSONUtils class
                 * that can be used here
                 */
                // TODO: Check if this is necessary, because those static values are already filled
                // from the main activity
                //JSONUtils.getDataFromJSON(data);

                if(mFromFavourites)
                {
                    getDataAndSetDetailsFavourites(mMovieID);
                    // this should fetch details with a json call all over because this hasnt been done
                    // at this point for this case. This is done with the id of the movie.

                }
                else
                {
                    getDataAndSetDetails(mIndexYouCameFrom);
                }

//            } catch (JSONException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void addToFavourites(View view)
    {
        Log.d("INTO THE DREAM WORLD", "OOOOOOOO");
        // Insert new Favourite film to dataBase via a ContentResolver

        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();

        // Put the task description and selected mPriority into the ContentValues
//        contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, input);
        //TODO: add to the database the url of the poster
        contentValues.put(DataBaseContract.FavouriteEntry.COLUMN_MOVIE_ID, mMovieID);
        contentValues.put(DataBaseContract.FavouriteEntry.COLUMN_JPG_URL, JSONUtils.postersURLs[mIndexYouCameFrom]);

        // Insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(DataBaseContract.FavouriteEntry.CONTENT_URI, contentValues);

        // Display the URI that's returned with a Toast
        // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
        if(uri != null)
        {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getBaseContext(), "Nothing happened", Toast.LENGTH_LONG).show();
        }
    }

    public void getDataAndSetDetails(int indexYouCameFrom)
    {
        /**
         * Execute an Async Task to fetch the keys for the movies' previews
         */
        new FetchMovieTrailerTask().execute(JSONUtils.ids[indexYouCameFrom]);

        /**
         * Execute another Async Task to get the reviews for this film
         */
        // TODO: see if its a good idea to merge this and the last AsynTask
        new FetchMovieReviewsTask().execute(JSONUtils.ids[indexYouCameFrom]);

        /**
         * Load the image from the static array stored in the JSONUtils class
         */
        Picasso.with(this).load(JSONUtils.postersURLs[indexYouCameFrom]).into(mBinding.movieDetailHeader.ivMovieThumb);//

        /**
         * Set the title for the film from the static array in JSONUtils class
         */
        mBinding.tvMovieTitle.setText(JSONUtils.titles[indexYouCameFrom]);

        /**
         * Set the movie Release Date from the static array in JSONUtils class
         */
        mBinding.movieDetailHeader.tvRelease.setText("Release Date: " + JSONUtils.releaseDates[indexYouCameFrom]);

        /**
         * Set the movie Rating Average from the static array in JSONUtils class
         */
        mBinding.movieDetailHeader.tvMovieRating.setText("User Rating:    " + JSONUtils.ratings[indexYouCameFrom] + "/10");

        /**
         * Set the movie description from the static array in JSONUtils class
         */
        mBinding.tvMovieOverview.setText(JSONUtils.overviews[indexYouCameFrom]);

        /**
         * Set the movie description from the static array in JSONUtils class
         */
        mBinding.movieDetailHeader.tvMovieLanguage.setText("Language:       " + JSONUtils.languages[indexYouCameFrom]);
    }

    public void getDataAndSetDetailsFavourites(int movieID)
    {
        String thisMovieID = String.valueOf(movieID);
        /**
         * Execute an Async Task to fetch the keys for the movies' previews
         */
        new FetchMovieTrailerTask().execute(thisMovieID);

        /**
         * Execute another Async Task to get the reviews for this film
         */
        // TODO: see if its a good idea to merge this and the last AsynTask
        new FetchMovieReviewsTask().execute(thisMovieID);

        // Do a JSON request on an AsynTask to get the details for this film
        new FetchMovieDetailTask().execute(thisMovieID);

        /**
         * Load the image from the static array stored in the JSONUtils class
         */
        Picasso.with(this).load(JSONUtils.postersURLs[0]).into(mBinding.movieDetailHeader.ivMovieThumb);//

        /**
         * Set the title for the film from the static array in JSONUtils class
         */
        mBinding.tvMovieTitle.setText(JSONUtils.titles[0]);

        /**
         * Set the movie Release Date from the static array in JSONUtils class
         */
        mBinding.movieDetailHeader.tvRelease.setText("Release Date: " + JSONUtils.releaseDates[0]);

        /**
         * Set the movie Rating Average from the static array in JSONUtils class
         */
        mBinding.movieDetailHeader.tvRelease.setText("User Rating:    " + JSONUtils.ratings[0] + "/10");

        /**
         * Set the movie description from the static array in JSONUtils class
         */
        mBinding.tvMovieOverview.setText(JSONUtils.overviews[0]);

        /**
         * Set the movie description from the static array in JSONUtils class
         */
        mBinding.movieDetailHeader.tvMovieLanguage.setText("Language:       " + JSONUtils.languages[0]);

    }


    public class FetchMovieTrailerTask extends AsyncTask<String, Void, String>
    {
        // On Pre execute hide the content of the layout

        @Override
        protected String doInBackground(String... params)
        {
            /* If there's no string, there's nothing to look up. */
            if (params.length == 0)
            {
                return null;
            }

            // Build the URL with the id of the movie we are seeing the details for
            URL movieTrailerRequestURL = NetworkUtils.buildMovieTrailerURL(params[0]);
            Log.d("TRAILER URL", movieTrailerRequestURL.toString());

            try
            {
                // This returns a long string from the URL connection to the Movie DB API
                String jsonMovieTrailerResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieTrailerRequestURL);

                return jsonMovieTrailerResponse;

            } catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String JsonData)
        {
            mMovieTrailerJSON = JsonData;

            if (mMovieTrailerJSON != null)
            {
                //showMovieDataView();
                // TODO: create this method
                try
                {
                    // This method fills variables with data in the JSONUtils class
                    JSONUtils.getTrailerDataFromJSON(mMovieTrailerJSON);
                    // Use such variables to set for the implicit intents when opening the
                    // trailers in youtube.
                }
//                catch (JSONException e)
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                //showErrorMessage();
            }
        }
    }

    public class FetchMovieReviewsTask extends AsyncTask<String, Void, String>
    {
        // TODO: do on preExecute hiding of views.
        @Override
        protected String doInBackground(String... params)
        {
            /* If there's no string, there's nothing to look up. */
            if (params.length == 0)
            {
                return null;
            }

            // Build the URL with the id of the movie we are seeing the details for
            URL movieRreviewsRequestURL = NetworkUtils.buildMovieReviewsURL(params[0]);
            Log.d("REVIEWS URL", movieRreviewsRequestURL.toString());

            try
            {
                // This returns a long string from the URL connection to the Movie DB API
                String jsonMovieReviewsResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRreviewsRequestURL);
                return jsonMovieReviewsResponse;
            } catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String JsonData)
        {
            mMovieReviewsJSON = JsonData;

            if (mMovieReviewsJSON != null)
            {
                //showMovieDataView();
                // TODO: create this method
                try
                {
                    // This method fills variables with data in the JSONUtils class
                    JSONUtils.getReviewDataFromJSON(mMovieReviewsJSON);
                    // Use such variables to set for the implicit intents when opening the
                    // trailers in youtube.
                }
//                catch (JSONException e)
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                //showErrorMessage();
                // TODO: Implement this
            }
        }
    }

    /**
     * Asynchronous Task class that handles with the API request
     */
    public class FetchMovieDetailTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

//            mLoadingIndicator.setVisibility(View.VISIBLE);
            // TODO: add such loading indicator
        }

        @Override
        protected String doInBackground(String... params)
        {
            /* If there's no string, there's nothing to look up. */
            if (params.length == 0)
            {
                return null;
            }

            // Use the sortyBy criteria and current page to build an URL for the API request.
            URL movieDetailRequestURL = NetworkUtils.buildMovieDetailURL(params[0]);
            Log.d("REQUEST DETAIL URL", movieDetailRequestURL.toString());

            try
            {
                // This returns a long string from the URL connection to an API for example
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieDetailRequestURL);

                return jsonMovieResponse;

            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String movieDetailData)
        {
            //mLoadingIndicator.setVisibility(View.INVISIBLE);
            // TODO: Implement this loading indicator

            // Store the JSON query in a string for further use to pass to the next activity
            mMovieDetailJSON = movieDetailData;

            if (movieDetailData != null)
            {
                //showMovieDataView();
                // TODO: impelement this

                try
                {
                    // This method fills variables with data in the JSONUtils class
                    JSONUtils.getDetailDataFromJSON(mMovieDetailJSON);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
//                showErrorMessage();
                // TODO: implement this
            }
        }
    }

}
