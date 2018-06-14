package android.example.com.popularmovies2marzena.object;


import android.os.Parcel;
import android.os.Parcelable;

// An {@link Review} object that contains information about selected movie reviews
public class Review implements Parcelable {

    //Review id
    private String reviewId;

    //Review content
    private String content;

    //Review author
    private String author;

    public Review(String reviewId, String content, String author) {
        this.reviewId = reviewId;
        this.content = content;
        this.author = author;
    }

    protected Review(Parcel in) {
        reviewId = in.readString();
        content = in.readString();
        author = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    //Returns review Id
    public String getReviewId() {
        return reviewId;
    }

    //Returns review content
    public String getContent() {
        return content;
    }

    //Returns review author
    public String getAuthor() {
        return author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(reviewId);
        parcel.writeString(content);
        parcel.writeString(author);
    }
}
