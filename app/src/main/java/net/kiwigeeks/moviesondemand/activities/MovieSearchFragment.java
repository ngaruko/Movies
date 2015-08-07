package net.kiwigeeks.moviesondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
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
import net.kiwigeeks.moviesondemand.tasks.FetchMovieTask;
import net.kiwigeeks.moviesondemand.utilities.Constants;
import net.kiwigeeks.moviesondemand.utilities.DrawInsetsFrameLayout;
import net.kiwigeeks.moviesondemand.utilities.ImageLoaderHelper;
import net.kiwigeeks.moviesondemand.utilities.LogHelper;
import net.kiwigeeks.moviesondemand.utilities.ObservableScrollView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieSearchFragment extends Fragment implements MovieLoadedListener, View.OnClickListener {

    public static final String ARG_ITEM_ID = "in_theaters_id";
    private static final String TITLE_EXTRA ="title_extra" ;
    private static final String TAG = "SearchMovieFragment";
    private static final float PARALLAX_FACTOR = 1.25f;
    public Movie mMovie;
    private String mTitle;
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
    private String mVideoUrl;
    private TextView bodyView;
    private TextView titleView;
    private TextView bylineView;
    private java.lang.String mPlot;
    private String mUrlIMDB;

    public MovieSearchFragment() {
    }




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(TITLE_EXTRA)) {

            mTitle = intent.getStringExtra(TITLE_EXTRA);


            LogHelper.log("Intent received: " + mTitle);


            new FetchMovieTask(this).execute(mTitle);


        } else mTitle = getArguments().getString("title");


    }




    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save current data


        savedInstanceState.putParcelable("movie", mMovie);


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_found_movies, container, false);

        titleView = (TextView) mRootView.findViewById(R.id.movie_title);
        bylineView = (TextView) mRootView.findViewById(R.id.movie_byline);
        bylineView.setMovementMethod(new LinkMovementMethod());
        bodyView = (TextView) mRootView.findViewById(R.id.movie_synopsis);


        mDrawInsetsFrameLayout = (DrawInsetsFrameLayout)
                mRootView.findViewById(R.id.draw_insets_frame_layout);
        mDrawInsetsFrameLayout.setOnInsetsCallback(new DrawInsetsFrameLayout.OnInsetsCallback() {
            @Override
            public void onInsetsChanged(Rect insets) {
                mTopInset = insets.top;
            }
        });


        mPhotoView = (ImageView) mRootView.findViewById(R.id.movieThumbnail);
        mPhotoContainerView = mRootView.findViewById(R.id.photo_container);

        mStatusBarColorDrawable = new ColorDrawable(0);

        mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(mVideoUrl != Constants.NA ? mVideoUrl : mUrlIMDB)
                        .getIntent(), getString(R.string.action_share)));
            }
        });


        mRootView.findViewById(R.id.view_trailer_button).setOnClickListener(this);
        mRootView.findViewById(R.id.full_movie_button).setOnClickListener(this);
        mRootView.findViewById(R.id.full_synopsis_button).setOnClickListener(this);

        return mRootView;
    }

    @Override
    public void onMovieLoded(Movie movie) {


        if (movie != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);


            mVideoUrl=movie.getTrailerUrl();

            mUrlIMDB = movie.getUrlIMDB();



LogHelper.log("movie here: " + mVideoUrl);
            titleView.setText(movie.getTitle());

            bylineView.setText( movie.getReleaseDate()
                            + "  "
                            + movie.getRated()
            );
            mPlot= movie.getPlot();


            if (mPlot.length()>200)

            bodyView.setText(Html.fromHtml(mPlot.substring(0, 200)));

else bodyView.setText(Html.fromHtml(mPlot));



            ImageLoaderHelper.getInstance(getActivity()).getImageLoader()


                    .get(movie.getUrlPoster(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    Bitmap bitmap = imageContainer.getBitmap();
                    if (bitmap != null) {
                        Palette p = Palette.generate(bitmap, 12);
                        //  mMutedColor = p.getDarkMutedColor(0xFF333333);
                        mPhotoView.setImageBitmap(imageContainer.getBitmap());
                        mRootView.findViewById(R.id.meta_bar);
                        //  .setBackgroundColor(mMutedColor);
                        // updateStatusBar();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });




        } else {
            mRootView.setVisibility(View.GONE);
            titleView.setText("N/A");
            bylineView.setText("N/A" );
            bodyView.setText("N/A");
        }


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_trailer_button:


                Log.e("Clikced", "Trailer coming");

                if(HomeFragment.signedIn) {


                    if (mVideoUrl != Constants.NA && !mVideoUrl.isEmpty()) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(mVideoUrl)));
                    } else {
                        Toast.makeText(getActivity(), "No trailers available for this movie!",
                                Toast.LENGTH_LONG).show();
                    }


                } else promptUserToSignIn();


                break;
            case R.id.full_synopsis_button:
                bodyView.setText(Html.fromHtml(mPlot));


                break;
            case R.id.full_movie_button:
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
                break;
        }
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

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();

    }
}
