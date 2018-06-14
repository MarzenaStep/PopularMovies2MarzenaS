package android.example.com.popularmovies2marzena.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.example.com.popularmovies2marzena.QueryUtils;
import android.example.com.popularmovies2marzena.object.Movie;
import android.util.Log;
import java.util.List;


//Idea how to use and why to use MovieLoader comes from course Android Basic: Networking and especially ud843 Quake Report App
//MovieLoader loads a list of movies by using an AsyncTask to perform the network request to the given URL
public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    //Tag for log messages
    private static final String LOG_TAG = MovieLoader.class.getName();

    //Query URL
    private String mUrl;

    /**
     * Constructs a new {@link MovieLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public MovieLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST: onStartLoading() called ...");
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        Log.i(LOG_TAG, "TEST: loadInBackground() called ...");
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of movies
        List<Movie> movieList = QueryUtils.fetchMovieData(mUrl);
        return movieList;
    }

}

