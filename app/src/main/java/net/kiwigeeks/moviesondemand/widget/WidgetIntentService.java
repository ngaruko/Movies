package net.kiwigeeks.moviesondemand.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.android.volley.toolbox.ImageLoader;

import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.VolleySingleton;
import net.kiwigeeks.moviesondemand.activities.DetailActivity;
import net.kiwigeeks.moviesondemand.data.Movie;
import net.kiwigeeks.moviesondemand.data.MoviesContract;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class WidgetIntentService extends IntentService {

    public static final int COL_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_GENRES = 2;
    public static final int COL_RATED = 3;
    public static final int COL_THUMBNAIL = 4;
    public static final int COL_RELEASE_DATE = 5;
    public static final int COL_RATING = 6;
    public static final int COL_PLOT = 7;
    public static final int COL_TRAILER = 8;


    // A "projection" defines the columns that will be returned for each row
    private static final String[] MOVIES_COLUMNS = {

            MoviesContract.InTheater._ID,
            MoviesContract.InTheater.COLUMN_TITLE,
            MoviesContract.InTheater.COLUMN_GENRES,
            MoviesContract.InTheater.COLUMN_RATED,
            MoviesContract.InTheater.COLUMN_URL_THUMBNAIL,
            MoviesContract.InTheater.COLUMN_RELEASE_DATE,
            MoviesContract.InTheater.COLUMN_RATING,
            MoviesContract.InTheater.COLUMN_PLOT,
            MoviesContract.InTheater.COLUMN_TRAILER_URL
    };
    public long id = 0;
    private VolleySingleton mVolleySingleton;
    private ImageLoader mImageLoader;
    private Context context;

    private String thumbnailUrl;
    private ImageView mPhotoView;
    private String title;


    private String mPlot;
    private String mVideoUrl;
    private String mGenres;
    private String mReleaseDate;
    private String mRuntime;
    private String mRating;
    private String mRated;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */

    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mVolleySingleton = VolleySingleton.getInstance();
        mImageLoader = mVolleySingleton.getImageLoader();

        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                WidgetProvider.class));
        Uri movieUri = MoviesContract.InTheater.buildDirUri();
        Cursor data = getContentResolver().query(movieUri, MOVIES_COLUMNS, null,
                null, MoviesContract.InTheater.COLUMN_RELEASE_DATE + " ASC");

        if (data == null) {

            return;
        } else if (data.getCount() < 1) {
            data.close();
            Log.e("Data Empty", "No data");

        } else if (data.moveToFirst()) {


            title = data.getString(COL_TITLE);
            mReleaseDate = data.getString(COL_RELEASE_DATE);

            thumbnailUrl = data.getString(COL_THUMBNAIL);
            mGenres = data.getString(COL_GENRES);
            mRated = data.getString(COL_RATED);
            mPlot = data.getString(COL_PLOT);
            mVideoUrl = data.getString(COL_TRAILER);
            mRating = data.getString(COL_RATING);
            id = data.getLong(COL_ID);

            data.close();

            for (int appWidgetId : appWidgetIds)

                populateWidget(appWidgetManager, appWidgetId);

        }

    }

    private void populateWidget(AppWidgetManager appWidgetManager, int appWidgetId) {
        int layoutId = R.layout.appwidget;

        final RemoteViews views = new RemoteViews(getPackageName(), layoutId);

        //TODO change the widget icon to generic icon

        views.setImageViewResource(R.id.widgetThumbnail, R.drawable.movie);

        views.setTextViewText(R.id.widgetMovieTitle, title);
        views.setTextViewText(R.id.widgetMovieReleaseDate, mReleaseDate);

        views.setTextViewText(R.id.wigetGenres, mGenres);


        try {

            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(thumbnailUrl).getContent());
            views.setImageViewBitmap(R.id.widgetThumbnail, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create an Intent to launch MainActivity
        Intent launchIntent = new Intent(this, DetailActivity.class);

//        Uri uri = MoviesContract.InTheater.buildItemUri(id);
//        launchIntent.setData(uri);


        Movie mMovie = new Movie();
        mMovie.setPlot(mPlot);
        mMovie.setTitle(title);


        mMovie.setTrailerUrl(mVideoUrl);
        mMovie.setGenres(mGenres);
        mMovie.setReleaseDate(String.valueOf(mReleaseDate));
        mMovie.setUrlPoster(thumbnailUrl);

        mMovie.setRuntime(mRuntime);
        mMovie.setRating(mRating);
        mMovie.setRated(mRated);


        Log.e("genre", mGenres);
//        Log.e("trailer", mVideoUrl);
        Log.e("Plot", mMovie.getPlot());


        //startActivity(new Intent(this, DestinationActivity.class).putExtras(b));


        launchIntent.putExtra("movie", mMovie);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);

        views.setOnClickPendingIntent(R.id.appWidget, pendingIntent);

        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


}
