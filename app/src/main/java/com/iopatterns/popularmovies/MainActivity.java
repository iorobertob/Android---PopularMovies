package com.iopatterns.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iopatterns.popularmovies.movieDataSystem.DataBaseContract;

import org.json.JSONException;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>
{

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

    private static final int FAVOURITES_LOADER_ID = 1;

    private int mPage = 1;

    // This array will hold the URLs to movie posters. I think I dont need it...
    private String mMoviesJSON;

    // true = Popularity, false = Rated
    private boolean mPopular        = true;
    private boolean mFromFavourites = false;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start all the Views
        initGUI();

        // If we are coming back from another Activity restore the type of sorting and page we were at
        if(savedInstanceState != null)
        {
            mPopular = savedInstanceState.getBoolean("PUPULAR");
            mPage = savedInstanceState.getInt("PAGE");

        }

        // Display differently depending on user selection
        if(mPopular)
        {
            getMovies(BY_POPULARITY);
        }
        else
        {
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
    protected void onSaveInstanceState(Bundle outState)
    {
        // Save the type of sorting and page we are at
        outState.putBoolean("POPULAR", mPopular);
        outState.putInt("PAGE", mPage);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(int clickedItemIndex)
    {
        /**
         *  A click on the item of the Recycler view sends us to the next Activity.
         *  we pass the JSON Array with the index of the film we clicked on to show
         *  its details.
          */
        Class destinationActivity = MovieDetails.class;

        Intent goToOtherActivity =  new Intent(MainActivity.this, destinationActivity);

        goToOtherActivity.putExtra("INDEX", clickedItemIndex);
        goToOtherActivity.putExtra("MOVIES_JSON", mMoviesJSON);
        goToOtherActivity.putExtra("FROM_FAVOURITES", mFromFavourites);
        goToOtherActivity.putExtra("MOVIE_ID", JSONUtils.ids[clickedItemIndex]);

        startActivity(goToOtherActivity);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Select from the menu how to sort the list of movies.
        int itemID = item.getItemId();
        // Execute the AsyncTask with this URL to get movies sorted by votes
        if (itemID == R.id.sort_rating)
        {
            if(mPopular == true)
            {
                mPage = 1;
                getMovies(BY_RATING);

                mPopular = false;
                mNextButton.setEnabled(true);
                mPrevButton.setEnabled(false);
                mPageNumber.setText(String.valueOf(mPage));
                mscroll.fullScroll(ScrollView.FOCUS_UP);
            }
        }

        // Execute the AsyncTask with this URL to get movies sorted by popularity
        if (itemID == R.id.sort_popularity)
        {

            if(mPopular == false)
            {

                mPage = 1;
                getMovies(BY_POPULARITY);

                mPopular = true;
                mNextButton.setEnabled(true);
                mPrevButton.setEnabled(false);
                mPageNumber.setText(String.valueOf(mPage));
                mscroll.fullScroll(ScrollView.FOCUS_UP);
            }
        }

        if (itemID == R.id.sort_favourites)
        {
            getMovies("byFavourites");

            // TODO: do all the pages enabling logic
            mscroll.fullScroll(ScrollView.FOCUS_UP);

        }

        // Show an About Dialog Box to show off
        if (itemID == R.id.about_menu)
        {
            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage(R.string.about_dialog)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialise all the Views in the Layout, set its click listener and initial values if needed
     */
    public void initGUI()
    {

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
    public void getMovies(String sortBy)
    {

        if(sortBy == "byFavourites")
        {
            // TODO: get the data from the content provider and put images urls into a String[]
            /*
            * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
            * created and (if the activity/fragment is currently started) starts the loader. Otherwise
            * the last created loader is re-used.
            */
            getSupportLoaderManager().initLoader(FAVOURITES_LOADER_ID, null, this);

            // Fill the String[] with urls for the jpg of movie posters from the Cursor obtained
            // from the query made with the Content Provider from the Loader called above.
            mMovieAdapter.setMoviePosterURLsData(null, true);
        }
        else
        {
            // Get the jpg posters url's from the Async task and a JSON request based on the sortBy criteria.
            new FetchMovieTask().execute(sortBy);
        }

    }

    /**
     * Show the Recycler View and hide all other Views
     */
    public void showMovieDataView()
    {
        mRecyclerViewMovies.setVisibility(View.VISIBLE);
        mTextViewError.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    /**
     * Show the Error TextView and hid all other Views
     */
    public void showErrorMessage()
    {
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this)
        {

            // Initialize a Cursor, this will hold all the task data
            Cursor mFavouriteData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading()
            {
                if (mFavouriteData != null)
                {
                    // Delivers any previously loaded data immediately
                    deliverResult(mFavouriteData);
                }
                else
                {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // Query and load all favourite data in the background; sort by priority
                try {
                    return getContentResolver().query(DataBaseContract.FavouriteEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            DataBaseContract.FavouriteEntry._ID);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mFavouriteData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        // Update the data that the adapter uses to create ViewHolder via a Cursor
        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        // TODO: check the behaviour of this
        mMovieAdapter.swapCursor(null);
    }

    /**
     * Asynchronous Task class that handles with the API request
     */
    public class FetchMovieTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            mLoadingIndicator.setVisibility(View.VISIBLE);
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
            URL movieRequestURL = NetworkUtils.buildMovieAPIURL(params[0], mPage);
            Log.d("REQUEST URL", movieRequestURL.toString());

            try
            {
                // This returns a long string from the URL connection to an API for example
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestURL);

                return jsonMovieResponse;

            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String movieData)
        {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            // Store the JSON query in a string for further use to pass to the next activity
            mMoviesJSON = movieData;

            if (movieData != null)
            {
                showMovieDataView();

                try
                {
                    // This method fills variables with data in the JSONUtils class
                    JSONUtils.getDataFromJSON(movieData);

                    // Use such variables to set the size of the grid and the posters to display
                    mMovieAdapter.setNumberItems(JSONUtils.titles.length);

                    mMovieAdapter.setMoviePosterURLsData(JSONUtils.postersURLs, false);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                showErrorMessage();
            }
        }
    }
}
