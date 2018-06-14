package android.example.com.popularmovies2marzena.activity;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.example.com.popularmovies2marzena.BuildConfig;
import android.example.com.popularmovies2marzena.R;
import android.example.com.popularmovies2marzena.adapter.ReviewAdapter;
import android.example.com.popularmovies2marzena.adapter.TrailerAdapter;
import android.example.com.popularmovies2marzena.data.MovieContract.MovieEntry;
import android.example.com.popularmovies2marzena.databinding.ActivityDetailBinding;
import android.example.com.popularmovies2marzena.loader.ReviewLoader;
import android.example.com.popularmovies2marzena.loader.TrailerLoader;
import android.example.com.popularmovies2marzena.object.Movie;
import android.example.com.popularmovies2marzena.object.Review;
import android.example.com.popularmovies2marzena.object.Trailer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


//The idea how to display selected movie information comes from Sandwich Club Project
public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler {

    //Tag for logging. It's best to use the class's name using getSimpleName as it helps to identify the location from which logs are being posted.
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    public static final String SELECTED_MOVIE_INFORMATION = "selected_movie_information";

    //Url for MovieDB trailers http://api.themoviedb.org/3/movie/157336/videos?api_key=###
    //Url for MovieDB reviews http://api.themoviedb.org/3/movie/83542/reviews?api_key=###
    public static final String BASE_MOVIE_DB = "https://api.themoviedb.org/3/movie/";

    // This constant String will be used to store trailers list
    private static final String TRAILERS_KEY = "trailers";

    //This constant String will be used to store reviews list
    private static final String REVIEWS_KEY = "reviews";

    private Movie selectedMovie;
    //Declare an ActivityDetailBinding field called detailBinding
    private ActivityDetailBinding detailBinding;
    // Constant value for the trailer loader ID
    private static final int TRAILER_LOADER_ID = 2;
    // Constant value for the review loader ID
    private static final int REVIEW_LOADER_ID = 3;

    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private String movieIdAsString;
    private static final String API_KEY = BuildConfig.MY_MOVIE_DB_API_KEY;
    private ArrayList<Trailer> trailersList;
    private ArrayList<Review> reviewsList;


    //https://stackoverflow.com/questions/15414206/use-different-asynctask-loaders-in-one-activity
    private class ReviewCallback implements LoaderManager.LoaderCallbacks<List<Review>> {

        @Override
        public Loader<List<Review>> onCreateLoader(int i, Bundle bundle) {
            // Create a new loader for the given URL
            Log.i(LOG_TAG, "TEST: onCreateLoader() called ...");
            movieIdAsString = String.valueOf(selectedMovie.getId());
            Log.e(LOG_TAG, "This is the movie id");
            String reviewUrl = BASE_MOVIE_DB + movieIdAsString + "/reviews?api_key=" + API_KEY;
            Log.e(LOG_TAG, "This is review final URL");
            return new ReviewLoader(getApplicationContext(), reviewUrl);
        }

        @Override
        public void onLoadFinished(Loader<List<Review>> loader, List<Review> reviews) {
            //If there is a valid list of {@link Review}s, then add them to the adapter's data set otherwise display error message - no movies found
            if (reviews != null && !reviews.isEmpty()) {
                reviewAdapter.updateReviewList(reviews);
                int numberOfReviews = reviews.size();
                String amountOfReviews = getResources().getQuantityString(R.plurals.reviews, numberOfReviews, numberOfReviews);
                detailBinding.tvReviewNumber.setText(amountOfReviews);
            } else {
                detailBinding.tvReviewNumber.setText(getString(R.string.details_no_reviews));
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Review>> loader) {
        }
    }

    private class TrailerCallback implements LoaderManager.LoaderCallbacks<List<Trailer>> {
        @Override
        public Loader<List<Trailer>> onCreateLoader(int loaderId, Bundle bundle) {
            // Create a new loader for the given URL
            Log.i(LOG_TAG, "TEST: onCreateLoader() called ...");
            movieIdAsString = String.valueOf(selectedMovie.getId());
            Log.e(LOG_TAG, "This is the movie id");
            String trailerUrl = BASE_MOVIE_DB + movieIdAsString + "/videos?api_key=" + API_KEY;
            Log.e(LOG_TAG, "This is movie final URL");
            return new TrailerLoader(getApplicationContext(), trailerUrl);
        }


        @Override
        public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> trailers) {

            //If there is a valid list of {@link Movie}s, then add them to the adapter's data set otherwise display error message - no movies found
            if (trailers != null && !trailers.isEmpty()) {
                trailerAdapter.updateTrailerList(trailers);
                int numberOfTrailers = trailers.size();
                String amountOfTrailers = getResources().getQuantityString(R.plurals.videos, numberOfTrailers, numberOfTrailers);
                detailBinding.tvVideosNumber.setText(amountOfTrailers);

            }
            else detailBinding.tvVideosNumber.setText(getString(R.string.details_no_trailers));
        }

        @Override
        public void onLoaderReset(Loader<List<Trailer>> loader) {
            Log.i(LOG_TAG, "TEST: onLoaderReset() called ...");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Instantiate mDetailBinding using DataBindingUtil
        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        /*
         * If savedInstanceState is not null, that means Activity is not being started for the
         * first time. Even if the savedInstanceState is not null, it is smart to check if the
         * bundle contains the key. In our case, the key we are looking for maps
         * to the contents of the trailerAdapter that displays our list of trailers. If the bundle
         * contains that key, we set the contents of the trailer list accordingly.
         */
        //If the savedInstanceState bundle is not null, update trailer list
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TRAILERS_KEY)) {
                trailersList = savedInstanceState.getParcelableArrayList(TRAILERS_KEY);
            } else if (savedInstanceState.containsKey(REVIEWS_KEY)) {
                reviewsList = savedInstanceState.getParcelableArrayList(REVIEWS_KEY);
            }
        }

        Intent movieIntent = getIntent();
        if (movieIntent != null && movieIntent.hasExtra(SELECTED_MOVIE_INFORMATION)) {
            selectedMovie = movieIntent.getParcelableExtra(SELECTED_MOVIE_INFORMATION);
        } else {
            closeOnError();
        }

        LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        detailBinding.rvTrailers.setLayoutManager(trailersLayoutManager);
        detailBinding.rvReview.setLayoutManager(reviewsLayoutManager);
        detailBinding.rvTrailers.setHasFixedSize(true);
        detailBinding.rvReview.setHasFixedSize(true);
        detailBinding.rvTrailers.setNestedScrollingEnabled(false);
        detailBinding.rvReview.setNestedScrollingEnabled(false);
        final Cursor favouritesCursor = getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                MovieEntry.COLUMN_MOVIE_ID
                        + " = " + selectedMovie.getId(),
                null,
                null);

        assert favouritesCursor != null;
        if (favouritesCursor.getCount() > 0) {
            detailBinding.tbFavourites.setChecked(true);
            detailBinding.tbFavourites.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_red_full));
            favouritesCursor.close();
        }
        else {
            detailBinding.tbFavourites.setChecked(false);
            detailBinding.tbFavourites.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorites_red_border));
        }

        detailBinding.tbFavourites.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    detailBinding.tbFavourites.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_red_full));
                    insertMovieToFavourites();
                }
                else {
                    detailBinding.tbFavourites.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorites_red_border));
                    deleteMovieFromFavourites();
                }
            }
        });


        trailerAdapter = new TrailerAdapter(this, new ArrayList<Trailer>(), this);
        detailBinding.rvTrailers.setAdapter(trailerAdapter);
        trailerAdapter.notifyDataSetChanged();

        reviewAdapter = new ReviewAdapter(this, new ArrayList<Review>());
        detailBinding.rvReview.setAdapter(reviewAdapter);
        reviewAdapter.notifyDataSetChanged();

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connMgr != null;

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders
            android.app.LoaderManager loaderManager = getLoaderManager();

            Log.i(LOG_TAG, "TEST: calling initLoader() ...");

            // Initialize the loader
            loaderManager.initLoader(TRAILER_LOADER_ID, null, new TrailerCallback());
            loaderManager.initLoader(REVIEW_LOADER_ID, null, new ReviewCallback());

        } else {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
        }


        populateUI();
        Picasso.with(this).load(selectedMovie.getPosterUrl()).error(R.drawable.poster_error).into(detailBinding.ivPoster);
        setTitle(selectedMovie.getTitle());
        Picasso.with(this).load(selectedMovie.getBackdropUrl()).error(R.drawable.backdrop_error).into(detailBinding.ivBackground);


    }

    //Method to insert movie data to data base
    private void insertMovieToFavourites() {

        // Create a ContentValues object where column names are the keys and selected movie attributes are the values
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_MOVIE_ID, selectedMovie.getId());
        Log.e(LOG_TAG, "This is the movie id: " + selectedMovie.getId());
        movieValues.put(MovieEntry.COLUMN_MOVIE_ORIGINAL_TITLE, selectedMovie.getOriginalTitle());
        Log.e(LOG_TAG, "This is the movie original title: " + selectedMovie.getOriginalTitle());
        movieValues.put(MovieEntry.COLUMN_MOVIE_TITLE, selectedMovie.getTitle());
        Log.e(LOG_TAG, "This is the movie title: " + selectedMovie.getTitle());
        movieValues.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, selectedMovie.getOverview());
        Log.e(LOG_TAG, "This is the movie overview: " + selectedMovie.getOverview());
        movieValues.put(MovieEntry.COLUMN_MOVIE_BACKDROP_URL, selectedMovie.getBaseBackdropUrl());
        Log.e(LOG_TAG, "This is the movie backdrop url: " + selectedMovie.getBaseBackdropUrl());
        movieValues.put(MovieEntry.COLUMN_MOVIE_POSTER_URL, selectedMovie.getBasePosterUrl());
        Log.e(LOG_TAG, "This is the movie poster url: " + selectedMovie.getBasePosterUrl());
        movieValues.put(MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, selectedMovie.getVoteAverage());
        Log.e(LOG_TAG, "This is the movie vote average: " + selectedMovie.getVoteAverage());
        movieValues.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, selectedMovie.getReleaseDate());
        Log.e(LOG_TAG, "This is the movie release date: " + selectedMovie.getReleaseDate());


        // Insert a new row for selected movie into the provider using the ContentResolver.
        // Use the {@link MovieEntry#CONTENT_URI} to indicate that we want to insert
        // into the movies database table.
        // Receive the new content URI that will allow us to access selected movie data in the future.
        Uri newUri = getContentResolver().insert(MovieEntry.CONTENT_URI, movieValues);

        if (newUri == null) {
            // If the new content URI is null, then there was an error with adding movie to favourites
            Toast.makeText(this, getString(R.string.details_add_movie_failed),
                    Toast.LENGTH_SHORT).show();
        }  else {
            // Otherwise, adding movie to favourites was successful
            Toast.makeText(this, selectedMovie.getTitle() + " " + getString(R.string.details_add_movie_successful),
                    Toast.LENGTH_SHORT).show();

        }
    }

    private void deleteMovieFromFavourites() {
        int rowsDeleted = getContentResolver().delete(MovieEntry.CONTENT_URI, (MovieEntry.COLUMN_MOVIE_ID + "=" + selectedMovie.getId()), null);
        Log.v("DetailsActivity", rowsDeleted + " rows deleted from movies database");

        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with removing movie from favourites.
            Toast.makeText(this, getString(R.string.details_remove_movie_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, selectedMovie.getTitle() + " " + getString(R.string.details_remove_movie_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void populateUI() {
        detailBinding.tvOriginalTitle.setText(selectedMovie.getOriginalTitle());
        detailBinding.tvOverview.setText(selectedMovie.getOverview());
        detailBinding.tvReleaseDate.setText(selectedMovie.getReleaseDate());

        String averageRating = String.valueOf(selectedMovie.getVoteAverage()) + getString(R.string.details_vote_average_max);
        detailBinding.tvVotesAverage.setText(averageRating);

    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.details_error_message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(TRAILERS_KEY, trailersList);
        savedInstanceState.putParcelableArrayList(REVIEWS_KEY, reviewsList);
        super.onSaveInstanceState(savedInstanceState);
    }



    //https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
    @Override
    public void onClick(Trailer trailer) {
        String video = trailer.getYouTubeFinalUrl();
        String trailerKey = trailer.getTrailerKey();
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerKey));
        Log.e(LOG_TAG, "appIntent: ");
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(video));
        Log.e(LOG_TAG, "webIntent: ");
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
}

