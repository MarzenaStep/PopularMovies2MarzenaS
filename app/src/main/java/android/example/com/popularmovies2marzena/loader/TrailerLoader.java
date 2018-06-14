package android.example.com.popularmovies2marzena.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.example.com.popularmovies2marzena.QueryUtils;
import android.example.com.popularmovies2marzena.object.Trailer;
import android.util.Log;

import java.util.List;

public class TrailerLoader extends AsyncTaskLoader<List<Trailer>> {

    //Tag for log messages
    private static final String LOG_TAG = TrailerLoader.class.getSimpleName();

    //Query URL
    private String mUrl;

    /**
     * Constructs a new {@link TrailerLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public TrailerLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST: onStartLoading() called ...");
        forceLoad();
    }

    @Override
    public List<Trailer> loadInBackground() {
        Log.i(LOG_TAG, "TEST: loadInBackground() called ...");
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of trailers
        List<Trailer> trailers = QueryUtils.fetchTrailerData(mUrl);
        return trailers;
    }
}
