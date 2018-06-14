package android.example.com.popularmovies2marzena.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.example.com.popularmovies2marzena.BuildConfig;
import android.example.com.popularmovies2marzena.R;
import android.example.com.popularmovies2marzena.adapter.MovieAdapter;
import android.example.com.popularmovies2marzena.data.MovieContract.MovieEntry;
import android.example.com.popularmovies2marzena.databinding.ActivityMainBinding;
import android.example.com.popularmovies2marzena.loader.MovieLoader;
import android.example.com.popularmovies2marzena.object.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity  extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, MovieAdapter.PosterAdapterOnClickHandler {

    //Tag for logging. It's best to use the class's name using getSimpleName as it helps to identify the location from which logs are being posted.
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String API_KEY = BuildConfig.MY_MOVIE_DB_API_KEY;

    //URL for movie data sorted by popularity key
    public static final String POPULAR_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;

    //URL for movie data sorted by top rated key
    public static final String TOP_RATED_URL = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;

    // This constant String will be used to store movies list
    private static final String MOVIES_KEY = "movies";

    //This constant String will be used to store RecycleView state
    private static final String RECYCLEVIEW_STATE = "recycleView_state";

    // Constant value for the movie loader ID. It can be any integer.
    public static final int INTERNET_MOVIE_LOADER_ID = 1;

    //Constant value for the favourites movies loader ID
    public static final int DATABASE_FAVOURITE_LOADER_ID = 2;

    //Adapter for the RecycleView
    private MovieAdapter movieAdapter;
    private GridLayoutManager layoutManager;
    private ActivityMainBinding mainBinding;
    private ArrayList<Movie> moviesList = new ArrayList<>();




    // FAVOURITES_MOVIES_PROJECTION defines the columns from the table that will be returned for each row
    String[] FAVOURITES_MOVIES_PROJECTION = {
            MovieEntry._ID,
            MovieEntry.COLUMN_MOVIE_BACKDROP_URL,
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_MOVIE_ORIGINAL_TITLE,
            MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MovieEntry.COLUMN_MOVIE_POSTER_URL,
            MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieEntry.COLUMN_MOVIE_TITLE,
            MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE};


    //https://stackoverflow.com/questions/15414206/use-different-asynctask-loaders-in-one-activity
    private class MovieCallback implements LoaderManager.LoaderCallbacks<List<Movie>> {

        @Override
        public Loader<List<Movie>> onCreateLoader(int loaderId, Bundle bundle) {
            // Create a new loader for the given URL
            Log.i(LOG_TAG, "TEST: onCreateLoader() called ...");

            mainBinding.ivNoMovies.setVisibility(View.INVISIBLE);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String keyForSortOrder = getApplicationContext().getString(R.string.preferences_sort_order_key);
            String defaultSortOrder = getApplicationContext().getString(R.string.preferences_sort_order_by_default);
            String preferredSortOrder = sharedPref.getString(keyForSortOrder, defaultSortOrder);
            String popular = getApplicationContext().getString(R.string.preferences_sort_order_by_default);

            layoutManager.scrollToPosition(0);


            if (popular.equals(preferredSortOrder)) {
                setTitle(getString(R.string.preferences_most_popular_label));
                return new MovieLoader(getApplicationContext(), POPULAR_URL);
            }

            else  {
                setTitle(getString(R.string.preferences_top_rated_label));
                return new MovieLoader(getApplicationContext(), TOP_RATED_URL);
            }

        }

        @Override
        public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {

            Log.i(LOG_TAG, "TEST: onLoadFinished() called ...");

            // Hide loading indicator and errorMessageTv because the data has been loaded
            mainBinding.pbLoadingIndicator.setVisibility(View.GONE);
            mainBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
            mainBinding.ivNoMovies.setVisibility(View.INVISIBLE);

            //Clear out existing data
            movieAdapter.clearMovieList();

            //If there is a valid list of {@link Movie}s, then add them to the adapter's data set otherwise display error message - no movies found
            if (movies != null && !movies.isEmpty()) {
                movieAdapter.updateMoveList(movies);

            } else {
                mainBinding.tvErrorMessage.setVisibility(View.VISIBLE);
                mainBinding.tvErrorMessage.setText(R.string.no_movies);
                mainBinding.ivNoMovies.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onLoaderReset(Loader<List<Movie>> loader) {

            Log.i(LOG_TAG, "TEST: onLoaderReset() called ...");
            // Clear out existing data
            movieAdapter.clearMovieList();

        }
    }

    private class FavouritesCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

            // This loader will execute the ContentProvider's query method on a background thread
            return new CursorLoader(getApplicationContext(),    // Parent activity context
                    MovieEntry.CONTENT_URI,                     // Provider content URI to query
                    FAVOURITES_MOVIES_PROJECTION,               // Columns to include in the resulting Cursor
                    null,                              // No selection clause
                    null,                          // No selection arguments
                    null);                            // Default sort order
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

            // Hide loading indicator and errorMessageTv because the data has been loaded
            mainBinding.pbLoadingIndicator.setVisibility(View.GONE);
            mainBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
            mainBinding.ivNoMovies.setVisibility(View.INVISIBLE);

            // Update {@link MovieAdapter} with this new cursor containing updated favourites movies data
            movieAdapter.clearMovieList();
            movieAdapter.swapCursor(cursor);

            List<Movie> favouritesList = new ArrayList<>();

            cursor = getApplicationContext().getContentResolver().query(MovieEntry.CONTENT_URI,
                    FAVOURITES_MOVIES_PROJECTION, null, null, null);

            // Find the columns of movies attributes that we're interested in
            assert cursor != null;
            int movieIdColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID);
            int backdropColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_BACKDROP_URL);
            int originalTitleColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ORIGINAL_TITLE);
            int overviewColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_OVERVIEW);
            int posterUrlColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_POSTER_URL);
            int releaseDateColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RELEASE_DATE);
            int titleColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE);
            int voteAverageColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE);

            if (cursor == null || cursor.getCount() < 1) {
                return;
            }
            else {
                while (cursor.moveToNext()) {
                    // Extract out the value from the Cursor for the given column index
                    int movieId = cursor.getInt(movieIdColumnIndex);
                    String backdrop = cursor.getString(backdropColumnIndex);
                    String originalTitle = cursor.getString(originalTitleColumnIndex);
                    String overview = cursor.getString(overviewColumnIndex);
                    String posterUrl = cursor.getString(posterUrlColumnIndex);
                    String releaseDate = cursor.getString(releaseDateColumnIndex);
                    String title = cursor.getString(titleColumnIndex);
                    double voteAverage = cursor.getDouble(voteAverageColumnIndex);
                    Log.v("Cursor", movieId + "-" + backdrop + "-" + originalTitle + "-" + overview + "-" + posterUrl + "-" + releaseDate + "-" + title + "-" + voteAverage);

                    Movie movie = new Movie(title, originalTitle, releaseDate, posterUrl, backdrop, voteAverage, overview, movieId);
                    favouritesList.add(movie);
                }

                movieAdapter.updateMoveList(favouritesList);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // Callback called when the data needs to be deleted
            movieAdapter.swapCursor(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(LOG_TAG, "TEST: MainActivity onCreate() called ");

        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

            /*
         * If savedInstanceState is not null, that means our Activity is not being started for the
         * first time. Even if the savedInstanceState is not null, it is smart to check if the
         * bundle contains the key we are looking for. In our case, the key we are looking for maps
         * to the contents of the posterAdapter that displays list of movies. If the bundle
         * contains that key, we set the contents of the poster list accordingly.
         */
        //If the savedInstanceState bundle is not null, update trailer list
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MOVIES_KEY)) {
                moviesList = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
            }
            else if (savedInstanceState.containsKey(RECYCLEVIEW_STATE)) {
                Parcelable recycleViewPosition = savedInstanceState.getParcelable(RECYCLEVIEW_STATE);
                mainBinding.rvPoster.getLayoutManager().onRestoreInstanceState(recycleViewPosition);
            }
        }

        //https://discussions.udacity.com/t/autofit-grid-recycler-view/188314
        Resources resources = getResources();
        int numOfColumns = resources.getInteger(R.integer.list_columns_number);

        layoutManager = new GridLayoutManager(this, numOfColumns);

        // Attach layout manager to the RecyclerView
        mainBinding.rvPoster.setLayoutManager(layoutManager);
        mainBinding.rvPoster.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(this, new ArrayList<Movie>(), this);

        mainBinding.rvPoster.setAdapter(movieAdapter);

        movieAdapter.notifyDataSetChanged();


        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connMgr != null;

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders
            LoaderManager loaderManager = getLoaderManager();

            Log.i(LOG_TAG, "TEST: calling initLoader() ...");

            // Initialize the loader
            if (loaderManager.getLoader(1) == null) {
                loaderManager.initLoader(INTERNET_MOVIE_LOADER_ID, null, new MovieCallback());
            }
            else if (loaderManager.getLoader(2) == null) {
                loaderManager.initLoader(DATABASE_FAVOURITE_LOADER_ID, null, new FavouritesCallback());
            }

        } else {

            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();

        }

        String preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(getString(R.string.preferences_sort_order_key), getString(R.string.preferences_sort_order_by_default));
        String popular = getApplicationContext().getString(R.string.preferences_sort_order_by_default);
        String top = getApplicationContext().getString(R.string.preferences_top_rated_value);

        if (preference.equals(popular) || preference.equals(top)) {

            getLoaderManager().restartLoader(INTERNET_MOVIE_LOADER_ID, null, new MovieCallback());
            getLoaderManager().destroyLoader(DATABASE_FAVOURITE_LOADER_ID);

        }else {
            setTitle(getString(R.string.preferences_favourites_label));
            getLoaderManager().restartLoader(DATABASE_FAVOURITE_LOADER_ID, null, new FavouritesCallback());
            getLoaderManager().destroyLoader(INTERNET_MOVIE_LOADER_ID);
        }
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        //Return true so that the menu is displayed in the Toolbar
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Respond to the click on the "Settings" menu option
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        String preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(getString(R.string.preferences_sort_order_key), getString(R.string.preferences_sort_order_by_default));
        String favourite = getApplicationContext().getString(R.string.preferences_favourites_value);

        if (preference.equals(favourite)) {
            setTitle(getString(R.string.preferences_favourites_label));
            getLoaderManager().restartLoader(DATABASE_FAVOURITE_LOADER_ID, null, new FavouritesCallback());
            getLoaderManager().destroyLoader(INTERNET_MOVIE_LOADER_ID);
        }else{
            getLoaderManager().restartLoader(INTERNET_MOVIE_LOADER_ID, null, new MovieCallback());
            getLoaderManager().destroyLoader(DATABASE_FAVOURITE_LOADER_ID);
        }
    }

    //This method is for responding to clicks from movie list
    @Override
    public void onClick(Movie movie) {
        //Launch DetailsActivity when movie poster is clicked
        Intent startDetailActivityIntent = new Intent(MainActivity.this, DetailActivity.class);
        startDetailActivityIntent.putExtra(DetailActivity.SELECTED_MOVIE_INFORMATION, movie);
        startActivity(startDetailActivityIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(MOVIES_KEY, moviesList);
        savedInstanceState.putParcelable(RECYCLEVIEW_STATE, mainBinding.rvPoster.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(savedInstanceState);
    }


}


