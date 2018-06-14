package android.example.com.popularmovies2marzena.object;


import android.os.Parcel;
import android.os.Parcelable;

// An {@link Trailer} object that contains information about single movie trailers.
public class Trailer implements Parcelable {


    //Trailer key
    private String trailerKey;

    //Trailer title
    private String trailerTitle;

    // Website base URL for displaying movie trailer
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    //Website base URL for displaying background image
    private static final String VIDEO_SCREENSHOT_BASE_URL = "https://img.youtube.com/vi/";

    /**Constructs a new {@link Trailer} object.
     *
     * @param trailerKey is the ID which is needed to build full youtube Url
     * @param trailerTitle is title of the trailer
     */
    public Trailer (String trailerKey, String trailerTitle) {
        this.trailerKey = trailerKey;
        this.trailerTitle = trailerTitle;
    }

    public Trailer(Parcel in) {
        trailerKey = in.readString();
        trailerTitle = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    //Returns trailer key
    public String getTrailerKey() {
        return trailerKey;
    }

    //Returns trailer title name
    public String getTrailerTitle() {
        return trailerTitle;
    }

    //Return final trailer image Url
    public String getVideoScreenshotBaseUrl() {
        return VIDEO_SCREENSHOT_BASE_URL + trailerKey + "/0.jpg";
    }
    //Returns final YouTube Url for watching videos
    public String getYouTubeFinalUrl() {
        return YOUTUBE_BASE_URL + trailerKey;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(trailerKey);
        parcel.writeString(trailerTitle);
    }
}

