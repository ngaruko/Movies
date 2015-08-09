package net.kiwigeeks.moviesondemand.activities;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.ShareCompat;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.data.Movie;
import net.kiwigeeks.moviesondemand.data.MovieLoader;
import net.kiwigeeks.moviesondemand.data.TopMovieLoader;
import net.kiwigeeks.moviesondemand.utilities.DrawInsetsFrameLayout;
import net.kiwigeeks.moviesondemand.utilities.ImageLoaderHelper;
import net.kiwigeeks.moviesondemand.utilities.ObservableScrollView;


public class MoviePosterFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    public static final String ARG_ITEM_ID = "in_theaters_id";
    public static final String TAG = "MoviePosterFragment";
    public static final float PARALLAX_FACTOR = 1.25f;
    public int mTitleId;
    public String mTitle;
    public String mVideoUrl;
    public Cursor mCursor;
    public long mItemId;
    public View mRootView;
    // public int mMutedColor = 0xFF333333;
    public ObservableScrollView mScrollView;
    public DrawInsetsFrameLayout mDrawInsetsFrameLayout;
    public ColorDrawable mStatusBarColorDrawable;
    public int mTopInset;
    public View mPhotoContainerView;
    public ImageView mPhotoView;
    public int mScrollY;
    public boolean mIsCard = false;
    public int mStatusBarFullOpacityBottom;
    public TextView bodyView;
    public CharSequence mUrlIMDB;
    public Movie mMovie;
    public String mPlot;
    public long mReleaseDate;
    public String mRated;
    public String mGenres;
    public String mRuntime;
    public View mDetailLayout;
    public String mpPhotoUrl;
    public String mRating;
    private String fragmentIdentifier;


    public MoviePosterFragment() {
    }

    public static MoviePosterFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        MoviePosterFragment fragment = new MoviePosterFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    static float progress(float v, float min, float max) {
        return constrain((v - min) / (max - min), 0, 1);
    }

    static float constrain(float val, float min, float max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey("id")) {
            mItemId = savedInstanceState.getLong("id");
        }

        //todo: savee instabcve

        mIsCard = getResources().getBoolean(R.bool.detail_is_card);
        mStatusBarFullOpacityBottom = getResources().getDimensionPixelSize(
                R.dimen.detail_card_top_margin);
        setHasOptionsMenu(true);
    }

    public MoviePosterActivity getActivityCast() {
        return (MoviePosterActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fragmentIdentifier = getActivity().getIntent().getStringExtra("fragment");

        switch (fragmentIdentifier) {
            case "theaters":
                getLoaderManager().initLoader(0, null, this);
                break;

            case "top":
                getLoaderManager().initLoader(1, null, this);
                break;
            case "coming":
                getLoaderManager().initLoader(2, null, this);
                break;
            case "bottom":
                getLoaderManager().initLoader(3, null, this);
                break;
            case "found":
                getLoaderManager().initLoader(6, null, this);
                break;

//                default:getLoaderManager().initLoader(0, null, this);
//                break;


        }

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putLong("id", mItemId);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_movie_poster, container, false);


        mDrawInsetsFrameLayout = (DrawInsetsFrameLayout)
                mRootView.findViewById(R.id.draw_insets_frame_layout);
        mDrawInsetsFrameLayout.setOnInsetsCallback(new DrawInsetsFrameLayout.OnInsetsCallback() {
            @Override
            public void onInsetsChanged(Rect insets) {
                mTopInset = insets.top;
            }
        });

        mScrollView = (ObservableScrollView) mRootView.findViewById(R.id.scrollview);
        mScrollView.setCallbacks(new ObservableScrollView.Callbacks() {
            @Override
            public void onScrollChanged() {
                mScrollY = mScrollView.getScrollY();
                getActivityCast().onUpButtonFloorChanged(mItemId, MoviePosterFragment.this);
                mPhotoContainerView.setTranslationY((int) (mScrollY - mScrollY / PARALLAX_FACTOR));
                updateStatusBar();
            }
        });

        mPhotoView = (ImageView) mRootView.findViewById(R.id.movieThumbnail);
        mPhotoContainerView = mRootView.findViewById(R.id.photo_container);
        mDetailLayout = mRootView.findViewById(R.id.detail_layout);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMovieDetails();
            }
        });


        mStatusBarColorDrawable = new ColorDrawable(0);

        mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(mVideoUrl != null ? mVideoUrl : mUrlIMDB)
                        .getIntent(), getString(R.string.action_share)));
            }
        });


        mRootView.findViewById(R.id.full_movie_detail).setOnClickListener(this);


        bindViews();
        updateStatusBar();
        return mRootView;
    }

    private void updateStatusBar() {
        int color = 0;
        if (mPhotoView != null && mTopInset != 0 && mScrollY > 0) {
            float f = progress(mScrollY,
                    mStatusBarFullOpacityBottom - mTopInset * 3,
                    mStatusBarFullOpacityBottom - mTopInset);

        }
        mStatusBarColorDrawable.setColor(color);
        mDrawInsetsFrameLayout.setInsetBackground(mStatusBarColorDrawable);
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }


        TextView titleView = (TextView) mRootView.findViewById(R.id.movie_title);
        TextView bylineView = (TextView) mRootView.findViewById(R.id.movie_byline);
        bylineView.setMovementMethod(new LinkMovementMethod());
        bodyView = (TextView) mRootView.findViewById(R.id.movie_synopsis);
        //bodyView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));

        if (mCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);

            mTitleId = MovieLoader.Query.COLUMN_TITLE;
            mTitle = mCursor.getString(mTitleId);

            getActivity().setTitle(mTitle);
            mReleaseDate = mCursor.getLong(MovieLoader.Query.COLUMN_RELEASE_DATE);
            mRated = mCursor.getString(MovieLoader.Query.COLUMN_RATED);
            mGenres = mCursor.getString(MovieLoader.Query.COLUMN_GENRES);
            mRuntime = mCursor.getString(MovieLoader.Query.COLUMN_RUNTIME);
            mpPhotoUrl = mCursor.getString(MovieLoader.Query.COLUMN_URL_THUMBNAIL);
            mRating = mCursor.getString(MovieLoader.Query.COLUMN_RATING);
            mPlot = mCursor.getString(MovieLoader.Query.COLUMN_PLOT);

            titleView.setText(mTitle);

            mUrlIMDB = "http://www.imdb.com/title/" + mCursor.getString(MovieLoader.Query.COLUMN_IMDB_ID);

            bylineView.setText(mCursor.getLong(MovieLoader.Query.COLUMN_RELEASE_DATE)
                            + "  "
                            + mCursor.getString(MovieLoader.Query.COLUMN_RATED)
            );


            if (mPlot.length() > 200) {
                bodyView.setText(Html.fromHtml(mPlot.substring(0, 200)));
            } else {
                bodyView.setText(Html.fromHtml(mPlot));
            }

            ImageLoaderHelper.getInstance(getActivity()).getImageLoader()
                    .get(mCursor.getString(MovieLoader.Query.COLUMN_URL_THUMBNAIL), new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            Bitmap bitmap = imageContainer.getBitmap();
                            if (bitmap != null) {
                                Palette p = Palette.generate(bitmap, 12);
                                //  mMutedColor = p.getDarkMutedColor(0xFF333333);
                                mPhotoView.setImageBitmap(imageContainer.getBitmap());


                                mRootView.findViewById(R.id.meta_bar);
                                //  .setBackgroundColor(mMutedColor);
                                updateStatusBar();
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    });


        } else {
            mRootView.setVisibility(View.GONE);
            titleView.setText("N/A");
            bylineView.setText("N/A");
            bodyView.setText("N/A");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //return MovieLoader.newInstanceForItemIdgetActivity(), mItemId);

        switch (fragmentIdentifier) {
            case "theaters":
                return MovieLoader.newInstanceForItemId(getActivity(), mItemId);


            case "top":
                return TopMovieLoader.newInstanceForItemId(getActivity(), mItemId);


            case "bottom":
                return MovieLoader.newBootomMovieForItemId(getActivity(), mItemId);


            case "coming":
                return MovieLoader.newComingSoonForItemId(getActivity(), mItemId);

            case "found":
                return MovieLoader.newFoundForItemId(getActivity(), mItemId);


        }


        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }

    public int getUpButtonFloor() {
        if (mPhotoContainerView == null || mPhotoView.getHeight() == 0) {
            return Integer.MAX_VALUE;
        }

        // account for parallax
        return mIsCard
                ? (int) mPhotoContainerView.getTranslationY() + mPhotoView.getHeight() - mScrollY
                : mPhotoView.getHeight() - mScrollY;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.full_movie_detail:
                showMovieDetails();
                break;

        }
    }


    private void showMovieDetails() {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Log.e("Title", mTitle);
        Log.e("Plot", mPlot);
        mMovie = new Movie();
        mMovie.setPlot(mPlot);
        mMovie.setTitle(mTitle);

        mMovie.setTrailerUrl(mVideoUrl);
        mMovie.setGenres(mGenres);
        mMovie.setReleaseDate(String.valueOf(mReleaseDate));
        mMovie.setUrlPoster(mpPhotoUrl);
        mMovie.setId(String.valueOf(mItemId));
        mMovie.setRuntime(mRuntime);
        mMovie.setRating(mRating);
        mMovie.setRated(mRated);


        Log.e("genre", mGenres);
//        Log.e("trailer", mVideoUrl);
        Log.e("Plot", mMovie.getPlot());


        //startActivity(new Intent(this, DestinationActivity.class).putExtras(b));


        intent.putExtra("movie", mMovie);


//        startActivity(intent);
        // Get the transition name from the string
        String transitionName = getString(R.string.transition_string);
//
        // Define the view that the animation will start from
        View viewStart = mRootView.findViewById(R.id.photo_container);

        ActivityOptionsCompat options =

                ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        viewStart,
                        transitionName
                );

        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());

    }


}
