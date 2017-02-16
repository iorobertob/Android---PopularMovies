package com.iopatterns.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONException;

/**
 * This class is the Child activity that shows the Detailed view for a film, including the
 * Title, Release Data, Language, Rating and Overview
 */
public class MovieDetails extends AppCompatActivity {

    private ImageView   mImageThumbnail;
    private TextView    mMovieTitle;
    private TextView    mMovieRelease;
    private TextView    mMovieRating;
    private TextView    mMovieOverview;
    private TextView    mMovieLanguage;

    private int         mIndexYouCameFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_movie_details);

        initGUIDetail();

        Intent fromPrevActivity = getIntent();

        /**
         * This tells us the index for the arrays so we can refer to the film data in that position
         */
        if(fromPrevActivity.hasExtra("INDEX")){
            mIndexYouCameFrom = fromPrevActivity.getIntExtra("INDEX", 0);
        }

        if(fromPrevActivity.hasExtra("MOVIES_JSON")) {
            try {

                String data = fromPrevActivity.getStringExtra("MOVIES_JSON");

                /**
                 * Extract the data from Json and fill the static values in the JSONUtils class
                 * that can be used here
                 */
                JSONUtils.getDataFromJSON(data);

                /**
                 * Load the image from the static array stored in the JSONUtils class
                 */
                Picasso.with(this).load(JSONUtils.postersURLs[mIndexYouCameFrom]).into(mImageThumbnail);//

                /**
                 * Set the title for the film from the static array in JSONUtils class
                 */
                mMovieTitle.setText(JSONUtils.titles[mIndexYouCameFrom]);

                /**
                 * Set the movie Release Date from the static array in JSONUtils class
                 */
                mMovieRelease.setText("Release Date: " + JSONUtils.releaseDates[mIndexYouCameFrom]);

                /**
                 * Set the movie Rating Average from the static array in JSONUtils class
                 */
                mMovieRating.setText("User Rating:    " + JSONUtils.ratings[mIndexYouCameFrom] + "/10");

                /**
                 * Set the movie description from the static array in JSONUtils class
                 */
                mMovieOverview.setText(JSONUtils.overviews[mIndexYouCameFrom]);

                /**
                 * Set the movie description from the static array in JSONUtils class
                 */
                mMovieLanguage.setText("Language:       " + JSONUtils.languages[mIndexYouCameFrom]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void initGUIDetail(){
        mImageThumbnail = (ImageView) findViewById(R.id.iv_movie_thumb);
        mMovieTitle     = (TextView)  findViewById(R.id.tv_movieTitle);
        mMovieRelease   = (TextView)  findViewById(R.id.tv_movieRelease);
        mMovieRating    = (TextView)  findViewById(R.id.tv_movieRating);
        mMovieOverview  = (TextView)  findViewById(R.id.tv_movieOverview);
        mMovieLanguage  = (TextView)  findViewById(R.id.tv_movieLanguage);
    }

}
