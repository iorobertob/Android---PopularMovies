package com.iopatterns.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener{

    private RecyclerView    mRecyclerViewMovies;
    private TextView        mTextViewError;
    private MovieAdapter    mMovieAdapter;
    private ProgressBar     mLoadingIndicator;
    private TextView        mPageNumber;
    private Button          mNextButton;
    private Button          mPrevButton;
    private ScrollView      mscroll;

    private static final String BY_POPULARITY   = "popularity.desc";
    private static final String BY_RATING       = "vote_average.desc";

    private int mPage = 1;

    // This array will hold the URLs to movie posters. I think I dont need it...
    private String mMoviesJSON;

    // true = Popularity, false = Rated
    private boolean mPopular = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start all the Views
        initGUI();

        // If we are coming back from another Activity restore the type of sorting and page we were at
        if(savedInstanceState != null){
            mPopular = savedInstanceState.getBoolean("PUPULAR");
            mPage = savedInstanceState.getInt("PAGE");

        }

        // Display differently depending on user selection
        if(mPopular){
            getMovies(BY_POPULARITY);
        }
        else{
            getMovies(BY_RATING);
        }

        // Vertical Grid with 3 Columns
        GridLayoutManager grid = new GridLayoutManager (this, 3, LinearLayoutManager.VERTICAL, false);

        mRecyclerViewMovies.setLayoutManager(grid);

        // Initially with 10 item slots, modified later acording to the items per page in the movieDB
        mMovieAdapter = new MovieAdapter(10, this);

        mRecyclerViewMovies.setAdapter(mMovieAdapter);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // Save the type of sorting and page we are at
        outState.putBoolean("POPULAR", mPopular);
        outState.putInt("PAGE", mPage);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

        /**
         *  A click on the item of the Recycler view sends us to the next Activity.
         *  we pass the JSON Array with the index of the film we clicked on to show
         *  its details.
          */


        Class destinationActivity = MovieDetails.class;

        Intent goToOtherActivity =  new Intent(MainActivity.this, destinationActivity);

        goToOtherActivity.putExtra("INDEX", clickedItemIndex);
        goToOtherActivity.putExtra("MOVIES_JSON", mMoviesJSON);

        startActivity(goToOtherActivity);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Select from the menu how to sort the list of movies.

        if (item.getItemId() == R.id.sort_rating){
            // Execute the AsyncTask with this URL to get movies sorted by votes
            if(mPopular == true){

                mPage = 1;
                getMovies(BY_RATING);

                mPopular = false;
                mNextButton.setEnabled(true);
                mPrevButton.setEnabled(false);
                mPageNumber.setText(String.valueOf(mPage));
                mscroll.fullScroll(ScrollView.FOCUS_UP);
            }

        }

        if (item.getItemId() == R.id.sort_popularity){
            // Execute the AsyncTask with this URL to get movies sorted by popularity
            if(mPopular == false){

                mPage = 1;
                getMovies(BY_POPULARITY);

                mPopular = true;
                mNextButton.setEnabled(true);
                mPrevButton.setEnabled(false);
                mPageNumber.setText(String.valueOf(mPage));
                mscroll.fullScroll(ScrollView.FOCUS_UP);
            }

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialise all the Views in the Layout, set its click listener and initial values if needed
     */
    public void initGUI(){

        mRecyclerViewMovies = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mTextViewError      = (TextView)     findViewById(R.id.tv_error_message);
        mLoadingIndicator   = (ProgressBar)  findViewById(R.id.pb_loading_indicator);
        mNextButton         = (Button)       findViewById(R.id.button_forward);
        mPrevButton         = (Button)       findViewById(R.id.button_back);
        mPageNumber         = (TextView)     findViewById(R.id.tv_page_number);
        mscroll             = (ScrollView)   findViewById(R.id.scroll_view);

        mNextButton.setOnClickListener(buttonsListener);
        mPrevButton.setOnClickListener(buttonsListener);

        mPageNumber.setText(String.valueOf(mPage));

    }

    /**
     * Method called from different parts of the Activity to start the Asynchronous Task that
     * fetches the data from the movie API
     * @param sortBy The criteria by which to sort, this is added to the query URL
     */
    public void getMovies(String sortBy){

        new FetchMoviewTask().execute(sortBy);

    }

    /**
     * Show the Recycler View and hide all other Views
     */
    public void showMovieDataView(){
        mRecyclerViewMovies.setVisibility(View.VISIBLE);
        mTextViewError.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    /**
     * Show the Error TextView and hid all other Views
     */
    public void showErrorMessage(){
        mRecyclerViewMovies.setVisibility(View.INVISIBLE);
        mTextViewError.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    /**
     * Create an anonymous implementation of OnClickListener
     * The Next Page and Previous Page buttons are handled here.
     */

    private View.OnClickListener buttonsListener = new View.OnClickListener() {
        public void onClick(View v) {

            // Move to the next page
            if(v.getId() ==  R.id.button_forward){

                mPage += 1;

                if(mPage > 1){
                    mPrevButton.setEnabled(true);
                }
                if(mPage == 1000){
                    mNextButton.setEnabled(false);
                }

                if(mPopular){
                    getMovies(BY_POPULARITY);
                }
                else{
                    getMovies(BY_RATING);
                }
                mPageNumber.setText(String.valueOf(mPage));

                mscroll.fullScroll(ScrollView.FOCUS_UP);
            }

            if(v.getId() ==  R.id.button_back){
                mPage -= 1;
                if (mPage == 1){
                    mPrevButton.setEnabled(false);
                }
                if(mPage < 1000){
                    mNextButton.setEnabled(true);
                }

                if(mPopular){
                    getMovies(BY_POPULARITY);
                }
                else{
                    getMovies(BY_RATING);
                }

                mPageNumber.setText(String.valueOf(mPage));

                mscroll.fullScroll(ScrollView.FOCUS_UP);
            }

        }
    };

    /**
     * Asynchronous Task class that handles with the API request
     */
    public class FetchMoviewTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            /* If there's no string, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            // Use the sortyBy criteria and current page to build an URL for the API request.
            URL movieRequestURL = NetworkUtils.buildMovieAPIURL(params[0], mPage);

            try {
                // This returns a long string from the URL connection to an API for example
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestURL);

                return jsonMovieResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            // Store the JSON query in a string for further use to pass to the next activity
            mMoviesJSON = movieData;

            if (movieData != null) {
                showMovieDataView();

                try {

                    // This method fills variables with data in the JSONUtils class
                    JSONUtils.getDataFromJSON(movieData);

                    // Use such variables to set the size of the grid and the posters to display
                    mMovieAdapter.setNumberItems(JSONUtils.titles.length);
                    mMovieAdapter.setMovieData(JSONUtils.postersURLs);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {

                showErrorMessage();
            }
        }
    }

}
