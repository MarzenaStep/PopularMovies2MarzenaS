package android.example.com.popularmovies2marzena.adapter;

import android.content.Context;
import android.example.com.popularmovies2marzena.R;
import android.example.com.popularmovies2marzena.object.Review;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();
    private final Context mContext;
    private List<Review> mReviews;

    //Creates ReviewAdapter
    public ReviewAdapter(Context context, List<Review> reviews) {
        mContext = context;
        mReviews = reviews;
    }

    //This method sets reviews on ReviewAdapter. It also helps to get new reviews from the website without creating new TrailerAdapter
    public void updateReviewList(List<Review> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);
        rootView.setFocusable(true);
        return new ReviewAdapterViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder holder, int position) {
        Review currentReview = mReviews.get(position);
        if (currentReview != null){
            holder.tvAuthor.setText(currentReview.getAuthor());
            holder.tvReview.setText(currentReview.getContent());
        }
    }

    @Override
    public int getItemCount() {
        if (mReviews == null) {
            return 0;
        } else {
            return mReviews.size();
        }
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView tvAuthor;
        final TextView tvReview;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);

            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvReview = itemView.findViewById(R.id.tv_review);
        }
    }
}
