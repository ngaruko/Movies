package net.kiwigeeks.moviesondemand.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.VolleySingleton;
import net.kiwigeeks.moviesondemand.activities.MoviePosterActivity;
import net.kiwigeeks.moviesondemand.data.MoviesContract;
import net.kiwigeeks.moviesondemand.data.TopMovieLoader;
import net.kiwigeeks.moviesondemand.utilities.Constants;


public class AdapterFoundMovies extends RecyclerView.Adapter<AdapterFoundMovies.ViewHolderMovies> {
    private Cursor mCursor;
    private LayoutInflater mLayoutInflater;

    private VolleySingleton mVolleySingleton;
    private ImageLoader mImageLoader;
    private Context context;


    public AdapterFoundMovies(Cursor cursor, Context context) {
        this.context = context;

        mCursor = cursor;
        try {
            mLayoutInflater = LayoutInflater.from(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mVolleySingleton = VolleySingleton.getInstance();
        mImageLoader = mVolleySingleton.getImageLoader();


    }


    @Override
    public long getItemId(int position) {
        try {
            mCursor.moveToPosition(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCursor.getLong(TopMovieLoader.Query._ID);
    }

    @Override
    public ViewHolderMovies onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mLayoutInflater.inflate(R.layout.found_movie_item_layout, parent, false);

        final ViewHolderMovies vh = new ViewHolderMovies(view);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {

                    Intent i = new Intent(context, MoviePosterActivity.class);


                    Uri uri = MoviesContract.TopMovies.buildItemUri(getItemId(vh.getAdapterPosition()));
                    i.setData(uri);


                    context.startActivity(i);


                } catch (Exception e) {
                    Log.e("Intent Error", e.getMessage());
                }

                Log.e("position", String.valueOf(getItemId(vh.getAdapterPosition())));
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolderMovies holder, int position) {


        //holder.movieThumbnail.setTransitionName("imageTransition");

        mCursor.moveToPosition(position);
        holder.movieTitle.setText(mCursor.getString(TopMovieLoader.Query.COLUMN_TITLE));

        //    holder.movieTitle.setText("TOP MOVIES");

        // holder.type.setText(mCursor.getString(TopMovieLoader.Query.COLUMN_RELEASE_DATE));
        holder.movieReleaseDate.setText(mCursor.getString(TopMovieLoader.Query.COLUMN_GENRES));
        Double rating = mCursor.getDouble(TopMovieLoader.Query.COLUMN_RATING);

        if (rating == -1) {
            holder.movieRating.setRating(0.0F);
            holder.movieRating.setAlpha(0.5F); //only 50% visible
        } else {
            holder.movieRating.setRating((float) (rating / 2.0F));
            holder.movieRating.setAlpha(1.0F);
        }

        //load url

        String thummbailUrl = mCursor.getString(TopMovieLoader.Query.COLUMN_URL_THUMBNAIL);

        loadImages(holder, thummbailUrl);
    }

    private void loadImages(final ViewHolderMovies holder, String thumbailUrl) {
        if (!thumbailUrl.equals(Constants.NA)) {
            mImageLoader.get(thumbailUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.movieThumbnail.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //have a default image here

                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }


    public class ViewHolderMovies extends RecyclerView.ViewHolder {

        ImageView movieThumbnail;
        TextView movieTitle;
        TextView movieReleaseDate;
        RatingBar movieRating;


        public ViewHolderMovies(View itemView) {
            super(itemView);

            movieThumbnail = (ImageView) itemView.findViewById(R.id.movieThumbnail);
            movieTitle = (TextView) itemView.findViewById(R.id.movie_title);
            movieReleaseDate = (TextView) itemView.findViewById(R.id.movieReleaseDate);
            movieRating = (RatingBar) itemView.findViewById(R.id.movieRating);
        }
    }
}