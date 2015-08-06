package net.kiwigeeks.moviesondemand.activities;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import net.kiwigeeks.moviesondemand.Callbacks.MovieLoadedListener;
import net.kiwigeeks.moviesondemand.MainActivity;
import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.data.Movie;
import net.kiwigeeks.moviesondemand.data.MovieLoader;
import net.kiwigeeks.moviesondemand.tasks.FetchMovieTask;
import net.kiwigeeks.moviesondemand.utilities.DrawInsetsFrameLayout;
import net.kiwigeeks.moviesondemand.utilities.ImageLoaderHelper;
import net.kiwigeeks.moviesondemand.utilities.ObservableScrollView;


public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    public static final String ARG_ITEM_ID = "in_theaters_id";
    private static final String TAG = "MovieDetailFragment";
    private static final float PARALLAX_FACTOR = 1.25f;
    public int mTitleId;
    public String mTitle;
    public String mVideoUrl;
    private Cursor mCursor;
    private long mItemId;
    private View mRootView;
    // private int mMutedColor = 0xFF333333;
    private ObservableScrollView mScrollView;
    private DrawInsetsFrameLayout mDrawInsetsFrameLayout;
    private ColorDrawable mStatusBarColorDrawable;
    private int mTopInset;
    private View mPhotoContainerView;
    private ImageView mPhotoView;
    private int mScrollY;
    private boolean mIsCard = false;
    private int mStatusBarFullOpacityBottom;
    private TextView bodyView;
    private CharSequence mUrlIMDB;
    private Movie mMovie;
    private String mPlot;
    private long mReleaseDate;
    private String mRated;
    private String mGenres;
    private String mRuntime;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    public static MovieDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        MovieDetailFragment fragment = new MovieDetailFragment();
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


        //for transition

        if (Build.VERSION.SDK_INT >= 21) {
            getActivity().getWindow().requestFeature(android.view.Window.FEATURE_CONTENT_TRANSITIONS);
            getActivity().getWindow().requestFeature(android.view.Window.FEATURE_ACTIVITY_TRANSITIONS);
            getActivity().getWindow().requestFeature(android.view.Window.FEATURE_ACTION_BAR_OVERLAY);
        }


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

    public MovieDetailActivity getActivityCast() {
        return (MovieDetailActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putLong("id", mItemId);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);


        if (Build.VERSION.SDK_INT >= 21) {
            Bundle extras = getActivity().getIntent().getExtras();
            int mPositionRef = extras.getInt("position");
            View base = mRootView.findViewById(R.id.detail_layout);
            View image = mRootView.findViewById(R.id.movieThumbnail);
            TextView text = (TextView) mRootView.findViewById(R.id.movie_title);
            text.setText(extras.getString("text"));
            ViewCompat.setTransitionName(base, "cardViewTransition" + mPositionRef);
            ViewCompat.setTransitionName(image, "imageTransition" + mPositionRef);
            ViewCompat.setTransitionName(text, "textTransition" + mPositionRef);
        }


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
                getActivityCast().onUpButtonFloorChanged(mItemId, MovieDetailFragment.this);
                mPhotoContainerView.setTranslationY((int) (mScrollY - mScrollY / PARALLAX_FACTOR));
                updateStatusBar();
            }
        });

        mPhotoView = (ImageView) mRootView.findViewById(R.id.movieThumbnail);
        mPhotoContainerView = mRootView.findViewById(R.id.photo_container);

        mPhotoContainerView.setOnClickListener(new View.OnClickListener() {
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


        mRootView.findViewById(R.id.view_trailer_button).setOnClickListener(this);
        mRootView.findViewById(R.id.full_movie_button).setOnClickListener(this);
        mRootView.findViewById(R.id.full_synopsis_button).setOnClickListener(this);

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
            mReleaseDate = mCursor.getLong(MovieLoader.Query.COLUMN_RELEASE_DATE);
            mRated = mCursor.getString(MovieLoader.Query.COLUMN_RATED);
            mGenres = mCursor.getString(MovieLoader.Query.COLUMN_GENRES);
            mRuntime = mCursor.getString(MovieLoader.Query.COLUMN_RUNTIME);
            titleView.setText(mTitle);

            mUrlIMDB = "http://www.imdb.com/title/" + mCursor.getString(MovieLoader.Query.COLUMN_IMDB_ID);

            bylineView.setText(mCursor.getLong(MovieLoader.Query.COLUMN_RELEASE_DATE)
                            + "  "
                            + mCursor.getString(MovieLoader.Query.COLUMN_RATED)
            );
            mPlot = mCursor.getString(MovieLoader.Query.COLUMN_PLOT);

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
        return MovieLoader.newInstanceForItemId(getActivity(), mItemId);
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
            case R.id.view_trailer_button:

                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.isConnected()) {
                    new FetchMovieTask(new MovieLoadedListener() {
                        @Override
                        public void onMovieLoded(Movie movie) {

                            if (movie == null) return;
                            mVideoUrl = movie.getTrailerUrl();
                            Log.e("Trailer", mVideoUrl);

                            Log.e("Clikced", "Trailer coming");

                            if (HomeFragment.signedIn) {
                                if (mVideoUrl != null && !mVideoUrl.isEmpty()) {

                                    if (isAdded()) {
                                        startActivity(new Intent(Intent.ACTION_VIEW,
                                                Uri.parse(mVideoUrl)));
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "No trailers available for this movie!",
                                            Toast.LENGTH_LONG).show();
                                }

                            } else promptUserToSignIn();


                        }
                    }).execute(mTitle);
                } else {

                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle(" No internet");
                    alertDialog.setMessage("Please check your internet connection");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                }


                break;
            case R.id.full_synopsis_button:
                bodyView.setText(Html.fromHtml(mPlot));
                break;


            case R.id.full_movie_button:
                showFullMovie();
                break;

        }
    }

    private void showMovieDetails() {


        String title = mTitle.toUpperCase();
        String details = mReleaseDate + "\t" + "\t" + mRated + "\t" + "\t" + mRuntime + "\n"
                + mGenres + "\t" + "\t" + "\n" + "\n"
                + "PLOT: " + "\n" + mPlot;


        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(details);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showFullMovie() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(" UNDER CONSTRUCTION!");
        alertDialog.setMessage("This feature is NOT available YET. Developer still applying for copy right from copy rigt holders. Sorry!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void promptUserToSignIn() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Premium Feature!");
        alertDialog.setMessage("This feature is only available for authenticated users. Touch OK to sign in or Cancel");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(intent);

                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();

    }


}
