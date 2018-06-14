package android.example.com.popularmovies2marzena.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

//This class defines table and column names for the movies database
public class MovieContract {

    // To prevent someone from accidentally instantiating the contract class, give it an empty constructor
    private MovieContract() {}


    //The Content authority is a name for the entire content provider A convenient string to use for the
    public static final String CONTENT_AUTHORITY = "android.example.com.popularmovies2marzena";

    //Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact the content provider for Popular Movies
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    //Inner class that defines constant values for the movies database table. Each entry in the table represents a single movie.
    public static final class MovieEntry implements BaseColumns {

        //The content URI to access the movie data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        //The MIME type of the {@link #CONTENT_URI} for a list of movies
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        //The MIME type of the {@link #CONTENT_URI} for a single movie
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        //Used internally as the name of our movie table
        public static final String TABLE_NAME = "movies";

        //Unique ID number for the movie (only for use in the database table). Type: INTEGER
        public final static String _ID = BaseColumns._ID;

        //Column names
        public final static String COLUMN_MOVIE_ID = "movie_id";
        public final static String COLUMN_MOVIE_TITLE = "title";
        public final static String COLUMN_MOVIE_ORIGINAL_TITLE = "original_title";
        public final static String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public final static String COLUMN_MOVIE_VOTE_AVERAGE = "vote_average";
        public final static String COLUMN_MOVIE_OVERVIEW = "overview";
        public final static String COLUMN_MOVIE_POSTER_URL = "poster_url";
        public final static String COLUMN_MOVIE_BACKDROP_URL= "backdrop_url";

        //Builds a URI that adds the movie date to the end of the movie content URI path. This is used to query details about a single movie entry by id.
        public static Uri buildMovieUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}

