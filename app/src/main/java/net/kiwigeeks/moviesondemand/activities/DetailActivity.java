package net.kiwigeeks.moviesondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import net.kiwigeeks.moviesondemand.utilities.DrawInsetsFrameLayout;
import net.kiwigeeks.moviesondemand.utilities.ImageLoaderHelper;
import net.kiwigeeks.moviesondemand.utilities.ObservableScrollView;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {


    public int mTitleId;
    public String mTitle;
    public String mVideoUrl;
    // private int mMutedColor = 0xFF333333;
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
    public String mPlot;
    public String mReleaseDate;
    public String mRated;
    public String mGenres;
    public String mRuntime;
    public View mDetailLayout;
    public String mPhotoUrl;
    public String mRating;
    private Cursor mCursor;
    private long mItemId;
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovie = new Movie();


//        if (getIntent() != null && getIntent().hasExtra("movie")) {
        mMovie = getIntent().getExtras().getParcelable("movie");
        Log.e("Received in extar", mMovie.getPlot());
        if (mMovie != null) {
            //mTitleId = mMovie.getTitle();
            mTitle = mMovie != null ? mMovie.getTitle() : null;
//        mReleaseDate = mMovie.getReleaseDate();
            mRated = mMovie != null ? mMovie.getRated() : null;
            mGenres = mMovie != null ? mMovie.getGenres() : null;
            mRuntime = mMovie != null ? mMovie.getRuntime() : null;
            mPhotoUrl = mMovie != null ? mMovie.getUrlPoster() : null;
            mRating = mMovie != null ? mMovie.getRating() : null;
            mPlot = mMovie != null ? mMovie.getPlot() : null;
            mVideoUrl = mMovie.getTrailerUrl();


            setContentView(R.layout.activity_detail);

        }


        mDrawInsetsFrameLayout = (DrawInsetsFrameLayout)
                findViewById(R.id.draw_insets_frame_layout);
        mDrawInsetsFrameLayout.setOnInsetsCallback(new DrawInsetsFrameLayout.OnInsetsCallback() {
            @Override
            public void onInsetsChanged(Rect insets) {
                mTopInset = insets.top;
            }
        });


        mPhotoView = (ImageView) findViewById(R.id.movieThumbnail);
        mPhotoContainerView = findViewById(R.id.photo_container);
        mDetailLayout = findViewById(R.id.detail_layout);


        mStatusBarColorDrawable = new ColorDrawable(0);


        findViewById(R.id.view_trailer_button).setOnClickListener(this);


        TextView titleView = (TextView) findViewById(R.id.movie_title);
        TextView bylineView = (TextView) findViewById(R.id.movie_byline);
        bylineView.setMovementMethod(new LinkMovementMethod());
        bodyView = (TextView) findViewById(R.id.movie_synopsis);


        titleView.setText(mTitle);

        mUrlIMDB = "http://www.imdb.com/title/" + mMovie.getUrlIMDB();


        bylineView.setText(mRated + "\t" + "\t" + mRuntime + "\n"
                        + mGenres + "\t" + "\t" + "\n" + "\n"
        );


        Log.e("Plot", mPlot);
        bodyView.setText(Html.fromHtml(mPlot));


        ImageLoaderHelper.getInstance(this).getImageLoader()
                .get(mPhotoUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        Bitmap bitmap = imageContainer.getBitmap();
                        if (bitmap != null) {
                            Palette p = Palette.generate(bitmap, 12);
                            //  mMutedColor = p.getDarkMutedColor(0xFF333333);
                            mPhotoView.setImageBitmap(imageContainer.getBitmap());


                            findViewById(R.id.meta_bar);


                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.view_trailer_button:

                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.isConnected()) {
                    new FetchMovieTask(new MovieLoadedListener() {
                        @Override
                        public void onMovieLoded(Movie movie) {

                            if (movie == null) return;
                            mVideoUrl = movie.getTrailerUrl();
                            Log.e("Trailer", mVideoUrl);

                            Log.e("Clikced", "Trailer coming");
                            Log.e("URL", mVideoUrl);
                            if (HomeFragment.signedIn) {
                                if (mVideoUrl != null && !mVideoUrl.isEmpty()) {


                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(mVideoUrl)));

                                } else {
                                    Toast.makeText(getBaseContext(), "No trailers available for this movie!",
                                            Toast.LENGTH_LONG).show();
                                }

                            } else promptUserToSignIn();


                        }
                    }).execute(mTitle);
                } else {

                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
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


        }

    }

    private void promptUserToSignIn() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Premium Feature!");
        alertDialog.setMessage("This feature is only available for authenticated users. Touch OK to sign in or Cancel");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

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