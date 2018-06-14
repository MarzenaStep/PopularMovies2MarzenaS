package android.example.com.popularmovies2marzena.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.example.com.popularmovies2marzena.data.MovieContract.MovieEntry;

//The main idea how to create adapter comes from Udacity course Android Basic: Data Storage
//{@link ContentProvider} for PopularMovies 2 Marzena app
public class MovieProvider extends ContentProvider {

    //Tag for the log messages
    public static final String LOG_TAG = MovieProvider.class.getSimpleName();

    //URI matcher code for the content URI for the whole movies table
    private static final int MOVIES = 100;

    //URI matcher code for the content URI for a single movie in the movies table
    private static final int MOVIE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://android.example.com.popularmovies2.data/movies" will map to the
        // integer code {@link #MOVIES}. This URI is used to provide access to MULTIPLE rows
        // of the movies table.
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);

        // The content URI of the form "content://android.example.com.popularmovies2.data/movies/#" will map to the
        // integer code {@link #MOVIE_ID}. This URI is used to provide access to ONE single row
        // of the movies table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://android.example.com.popularmovies2.data/movies/3" matches, but
        // "content://android.example.com.popularmovies2.data/movies" (without a number at the end) doesn't match.
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_ID);
    }

    //Database helper that will provide access to the database
    private MovieDbHelper movieDbHelper;

    //Initialize the provider and the database helper object
    @Override
    public boolean onCreate() {
        // Create and initialize a MovieDbHelper object to gain access to the movies database
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }


    //Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get readable database
        SQLiteDatabase database = movieDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int matcher = sUriMatcher.match(uri);
        switch (matcher) {
            case MOVIES:
                // For the MOVIES code, query the movies table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the movies table.
                cursor = database.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MOVIE_ID:
                // For the MOVIE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://android.example.com.popularmovies2.data/movies/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the movies table where the _id equals 3 to return a cursor containing that row of the table
                cursor = database.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default: throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor to know what content URI the Cursor was created for.
        // If the data at this URI changes, then we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    //Returns the MIME type of data for the content URI
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return MovieEntry.CONTENT_LIST_TYPE;

            case MOVIE_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    //Insert new data into the provider with the given ContentValues
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return insertMovie(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    //Insert a movie into the database with the given content values. Return the new content URI for that specific row in the database
    private Uri insertMovie(Uri uri, ContentValues values) {

        // Get writable database
        SQLiteDatabase database = movieDbHelper.getWritableDatabase();

        // Insert the new movie with the given values
        long id = database.insert(MovieEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the movie content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table, return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    //Delete the data at the given selection and selection arguments
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        // Get writable database
        SQLiteDatabase database = movieDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_ID:
                // Delete a single row given by the ID in the URI
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    //Updates the data at the given selection and selection arguments, with the new ContentValues
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return updateMovie(uri, contentValues, selection, selectionArgs);
            case MOVIE_ID:
                // For the MOVIE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateMovie(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    /**
     * Update movies in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more movies).
     * Return the number of rows that were successfully updated.
     */
    private int updateMovie(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Get writable database to update the data
        SQLiteDatabase database = movieDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);


        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

}
