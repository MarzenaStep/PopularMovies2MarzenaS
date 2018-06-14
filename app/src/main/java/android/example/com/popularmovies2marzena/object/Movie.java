package android.example.com.popularmovies2marzena.object;

import android.os.Parcel;
import android.os.Parcelable;

// An {@link Movie} object that contains information about single movie which should be displayed in the screen.
public class Movie implements Parcelable {

    //Movie title in english
    private String title;
    //Original title of the movie
    private String originalTitle;
    //Movie release date
    private String releaseDate;
    //Poster image thumbnail
    private String posterUrl;
    //Background image
    private String backdropUrl;
    //Average user voting
    private double voteAverage;
    //A plot synopsis
    private String overview;
    //Movie id
    private int id;


    // Website base Url for displaying thumbnail movie image poster
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";

    //Website base Url for displaying background image
    private static final String BACK_DROP_BASE_URL = "http://image.tmdb.org/t/p/w342";

    /**Constructs a new {@link Movie} object.
     *
     * @param title is the movie tile in english
     * @param originalTitle is the movie original title
     * @param releaseDate is the movie release date
     * @param posterUrl is the file path for the poster image
     * @param backdropUrl is the file path for the background image
     * @param voteAverage is the average voting for the movie
     * @param overview is the movie overview
     * @param id is the movie id
     */
    public Movie(String title, String originalTitle, String releaseDate, String posterUrl, String backdropUrl, double voteAverage, String overview, int id) {
        this.title = title;
        this.originalTitle = originalTitle;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.id = id;
    }


    public Movie(Parcel in) {
        title = in.readString();
        originalTitle = in.readString();
        releaseDate = in.readString();
        posterUrl = in.readString();
        backdropUrl = in.readString();
        voteAverage = in.readDouble();
        overview = in.readString();
        id = in.readInt();
    }

    public static final Parcelable.Creator<Movie>CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    //Returns movie title in english
    public String getTitle() {
        return title;
    }

    //Returns original movie title
    public String getOriginalTitle() {
        return originalTitle;
    }

    //Returns movie release date
    public String getReleaseDate() {
        return releaseDate;
    }

    //Returns final poster image url
    public String getPosterUrl() {
        return POSTER_BASE_URL + posterUrl;
    }

    public String getBasePosterUrl() {return posterUrl;}

    public String getBaseBackdropUrl() {return backdropUrl;}

    //Returns final background image url
    public String getBackdropUrl() {
        return BACK_DROP_BASE_URL + backdropUrl;
    }

    //Returns vote average
    public double getVoteAverage() {
        return voteAverage;
    }

    //Returns movie overview
    public String getOverview() {
        return overview;
    }

    //Returns movie id
    public int getId() {return id;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeString(originalTitle);
        parcel.writeString(releaseDate);
        parcel.writeString(posterUrl);
        parcel.writeString(backdropUrl);
        parcel.writeDouble(voteAverage);
        parcel.writeString(overview);
        parcel.writeInt(id);
    }
}

