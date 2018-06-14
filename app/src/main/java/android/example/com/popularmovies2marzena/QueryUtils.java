package android.example.com.popularmovies2marzena;


import android.example.com.popularmovies2marzena.object.Movie;
import android.example.com.popularmovies2marzena.object.Review;
import android.example.com.popularmovies2marzena.object.Trailer;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


//Prepared based on course Android Basic: Networking and especially ud843 Quake Report App
public class QueryUtils {

    //Tag for log messages
    private static final String LOG_TAG = QueryUtils.class.getName();


    //Returns new URL object from the given string URL
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    //Make an HTTP request to the given URL and return a String as the response
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200), then read the input stream and parse the response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Movie> extractMovieFeaturesFromJson(String movieJson) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJson)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding movies to
        List<Movie> movies = new ArrayList<>();

        try {

            //Create new root JSONObject from json String.
            JSONObject jsonRootObject = new JSONObject(movieJson);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of information about movies.
            JSONArray resultsArray = jsonRootObject.optJSONArray("results");

            // For each movie in the resultsArray, create an {@link Movie} object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single movie at position i within the list of movies
                JSONObject currentMovie = resultsArray.optJSONObject(i);

                //Extract the value for the key called "title"
                String title = currentMovie.optString("title");

                // Extract the value for the key called "original_title"
                String originalTitle = currentMovie.optString("original_title");

                // Extract the value for the key called "release_date"
                String releaseDate = currentMovie.optString("release_date");

                // Extract the value for the key called "poster_path"
                String posterPath = currentMovie.optString("poster_path");

                //Extract the value for the key called "backdrop_path"
                String backdropPath = currentMovie.optString("backdrop_path");

                // Extract the value for the key called "vote_average"
                double voteAverage = currentMovie.optDouble("vote_average");

                // Extract the value for the key called "overview"
                String overview = currentMovie.optString("overview");

                int id = currentMovie.getInt("id");

                // Create a new {@link Movie} object with the title, originalTitle, releaseDate, posterPath, voteAverage
                // and overview from the JSON response.
                Movie movie = new Movie(title, originalTitle, releaseDate, posterPath, backdropPath, voteAverage, overview, id);

                // Add the new {@link Movie} to the list of movies.
                movies.add(movie);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movie JSON results", e);
        }

        // Return the list of movies
        return movies;
    }

    public static List<Trailer> extractTrailerFeaturesFromJson(String trailerJson) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(trailerJson)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding trailer to
        List<Trailer> trailers = new ArrayList<>();

        try {

            //Create new root JSONObject from json String.
            JSONObject trailerRootObject = new JSONObject(trailerJson);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of information about movie trailers.
            JSONArray resultsArray = trailerRootObject.optJSONArray("results");

            // For each movie in the resultsArray, create an {@link Trailer} object
            for (int i = 0; i < resultsArray.length(); i++) {
                // Get a single trailer at position i within the list of trailers
                JSONObject currentTrailer = resultsArray.optJSONObject(i);

                String trailerKey = currentTrailer.optString("key");
                String trailerTitle = currentTrailer.optString("name");

                // Create a new {@link Trailer} object with the trailerKey and trailerTitle from the JSON response.
                Trailer trailer = new Trailer(trailerKey, trailerTitle);

                // Add the new {@link Trailer} to the list of trailers.
                trailers.add(trailer);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the trailer JSON results", e);
        }

        return trailers;
    }

    public static List<Review> extractReviewFeaturesFromJson(String reviewJson) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(reviewJson)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding trailer to
        List<Review> reviews = new ArrayList<>();

        try {

            //Create new root JSONObject from json String.
            JSONObject reviewRootObject = new JSONObject(reviewJson);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of information about movie reviews.
            JSONArray resultsArray = reviewRootObject.optJSONArray("results");

            // For each movie in the resultsArray, create an {@link Review} object
            for (int i = 0; i < resultsArray.length(); i++) {
                // Get a single review at position i within the list of trailers
                JSONObject currentReview = resultsArray.optJSONObject(i);


                String reviewId = currentReview.optString("id");
                String content = currentReview.optString("content");
                String author = currentReview.optString("author");


                // Create a new {@link Review} object with the author, content and reviewId from the JSON response.
                Review review = new Review(reviewId, content, author);

                // Add the new {@link Movie} to the list of movies.
                reviews.add(review);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the Review JSON results", e);
        }

        return reviews;

    }

    //Query the Movie DB and return a list of {@link Movie} object
    public static List<Movie> fetchMovieData(String requestUrl) {

        Log.i(LOG_TAG, "TEST: fetchMovieData() called ...");
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Movie}s
        List<Movie> movies = extractMovieFeaturesFromJson(jsonResponse);

        // Return the list of {@link Movie}s
        return movies;
    }

    //Query the Movie DB and return a list of (@link Trailer) object
    public static List<Trailer> fetchTrailerData(String requestUrl) {
        Log.i(LOG_TAG, "TEST: fetchTrailerData() called ...");
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Trailer}s
        List<Trailer> trailers = extractTrailerFeaturesFromJson(jsonResponse);

        // Return the list of {@link Trailer}s
        return trailers;
    }


    public static List<Review> fetchReviewData(String requestUrl) {
        Log.i(LOG_TAG, "TEST: fetchReviewData() called ...");
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Review}s
        List<Review> reviews = extractReviewFeaturesFromJson(jsonResponse);

        // Return the list of {@link Review}s
        return reviews;
    }
}
