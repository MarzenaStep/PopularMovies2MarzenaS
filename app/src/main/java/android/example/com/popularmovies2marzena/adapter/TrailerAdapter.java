package android.example.com.popularmovies2marzena.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.example.com.popularmovies2marzena.R;
import android.example.com.popularmovies2marzena.object.Trailer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

//Creates basic adapter which extends from RecycleView.Adapter
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    private final Context mContext;
    private List<Trailer> mTrailers;
    //Defining final ClickHandler variable to make it easy for Activity to interface with RecycleView
    final private TrailerAdapterOnClickHandler mClickHandler;

    //Defining interface that receives onClick messages
    public interface  TrailerAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }

    //Creates TrailerAdapter
    public TrailerAdapter(Context context, List<Trailer> trailers, TrailerAdapterOnClickHandler clickHandler) {
        mContext = context;
        mTrailers = trailers;
        mClickHandler = clickHandler;
    }

    //This method sets videos on TrailerAdapter. It also helps to get new videos from the web without creating new TrailerAdapter
    public void updateTrailerList(List<Trailer> trailers) {
        mTrailers = trailers;
        notifyDataSetChanged();
    }

    // Inflating a layout from XML and returning new holder
    @NonNull
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_item, parent, false);
        rootView.setFocusable(true);
        return new TrailerAdapterViewHolder(rootView);
    }

    //OnBindViewHolder is called by the RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(@NonNull TrailerAdapterViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        //Get data model based on position
        Trailer currentTrailer = mTrailers.get(position);
        String videoScreenshotUrl;
        if (currentTrailer !=null) {
            videoScreenshotUrl = currentTrailer.getVideoScreenshotBaseUrl();
            Picasso.with(mContext).load(videoScreenshotUrl).into(holder.videoIv);
            Log.e(LOG_TAG, "This is final URL for showing trailer image");
            holder.videoTitleTv.setText(currentTrailer.getTrailerTitle());
            Log.e(LOG_TAG, "This is trailer title");
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        if (mTrailers == null) {
            return 0;
        } else {
            return mTrailers.size();
        }
    }
    //Implement OnClickListener in the TrailerAdapterViewHolder class
    /**
     * Cache of the children views for a trailer list item.
     */
    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final ImageView videoIv;
        final TextView videoTitleTv;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            videoIv = itemView.findViewById(R.id.iv_video_thumbnail);
            videoTitleTv = itemView.findViewById(R.id.tv_video_title);
            itemView.setOnClickListener(this);
        }

        //Override onClick, passing the clicked trailers data to clickHandler via its onClick method
        /**
         * This gets called by the child views during a click.
         *
         * @param view The View that was clicked
         */
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Trailer currentTrailer = mTrailers.get(adapterPosition);
            mClickHandler.onClick(currentTrailer);
        }
    }
}

