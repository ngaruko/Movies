package net.kiwigeeks.moviesondemand.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.toolbox.ImageLoader;
import com.androidquery.AQuery;

import net.kiwigeeks.moviesondemand.data.MoviesContract;
import net.kiwigeeks.moviesondemand.data.MoviesHelper;
import net.kiwigeeks.moviesondemand.MainActivity;
import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.VolleySingleton;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class WidgetIntentService extends IntentService {


    private VolleySingleton mVolleySingleton;
    private ImageLoader mImageLoader;
    private Context context;
    // A "projection" defines the columns that will be returned for each row
    private static final String[] SCORE_COLUMNS = {

            MoviesContract.InTheater._ID,
            MoviesContract.InTheater.COLUMN_TITLE,
            //MoviesContract.InTheater.COLUMN_GENRES,
            MoviesContract.InTheater.COLUMN_RATED,
            MoviesContract.InTheater.COLUMN_URL_THUMBNAIL,
            MoviesContract.InTheater.COLUMN_RELEASE_DATE,
            MoviesContract.InTheater.COLUMN_RATING
    };


//    // Defines a string to contain the selection clause
//    String mSelectionClause = null;
//
//    // Initializes an array to contain selection arguments
//    String[] mSelectionArgs = {""};

    public double _ID = 0;
    public static final int COL_TITLE = 1;
    //public static final int COL_GENRES = 2;
    public static final int COL_RATED = 2;
    public static final int COL_THUMBNAIL = 3;
    public static final int COL_RELEASE_DATE = 4;
    public static final int COL_RATING = 5;
    private AQuery aq;

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

        SQLiteDatabase db = new MoviesHelper(getBaseContext()).getReadableDatabase();
        Cursor data = db.query(MoviesContract.InTheater.TABLE_IN_THEATERS, SCORE_COLUMNS, null, null, null, null, MoviesContract.InTheater.COLUMN_TITLE + " ASC");


        if (data == null) {

            return;
        } else if (data.getCount() < 1) {
            data.close();
            Log.e("Data Empty", "No data");

        }
        else if (data.moveToFirst()) {


            String title = data.getString(COL_TITLE);
            String releaseDate = data.getString(COL_RELEASE_DATE);

            String thumbnailUrl=data.getString(COL_THUMBNAIL);
//            int away_goals = data.getInt(COL_AWAY_GOALS);
//            int home_goals = data.getInt(COL_HOME_GOALS);
//            int league = data.getInt(COL_LEAGUE);
//            String mTime = data.getString(COL_MATCHTIME);
//            int match_day = data.getInt(COL_MATCHDAY);
//            String mDate = data.getString(COL_DATE);


            data.close();

            // Perform this loop procedure for each Today widget
            for (
                    int appWidgetId
                    : appWidgetIds)

            {
                int layoutId = R.layout.appwidget;
                //RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
                final RemoteViews views = new RemoteViews(getPackageName(), layoutId);

                //TODO change the widget icon to generic icon

//                views.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(home));
//                views.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(away));
                views.setTextViewText(R.id.widgetMovieTitle, title);
                views.setTextViewText(R.id.wigetMovieReleaseDate, releaseDate);
//                views.setTextViewText(R.id.data_textview, mTime);
//                views.setTextViewText(R.id.score_textview, Utilies.getScores(home_goals, away_goals));
//            views.setTextViewText(R.id.league_textview,Utilies.getLeague(league));
//            views.setTextViewText(R.id.matchday_textview,Utilies.getMatchDay(match_day,league));

                aq = new AQuery(this);

                aq.id(R.id.widgetThumbnail).image(thumbnailUrl);
               Log.e("Imagethumbnail",thumbnailUrl);
                // Create an Intent to launch MainActivity
                Intent launchIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
                views.setOnClickPendingIntent(R.id.appWidget, pendingIntent);

                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, views);

            }
        }
    }


}
