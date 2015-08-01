package net.kiwigeeks.moviesondemand.data;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.format.Time;
import android.util.Log;

import com.android.volley.RequestQueue;

import net.kiwigeeks.moviesondemand.VolleySingleton;
import net.kiwigeeks.moviesondemand.json.JSonParser;
import net.kiwigeeks.moviesondemand.json.Requestor;
import net.kiwigeeks.moviesondemand.utilities.EndPoints;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class UpdaterService extends IntentService {

    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;

    private static final String TAG = "UpdaterService";

    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "net.kiwigeeks.moviesondemand.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "net.kiwigeeks.moviesondemand.intent.extra.REFRESHING";
    private static DateFormat dateFormatter = new SimpleDateFormat("ddMMyyyy");



    public UpdaterService() {
        super(TAG);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Time time = new Time();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
            return;
        }

        sendStickyBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));

        // Don't even inspect the intent, we only do one thing, and that's fetch content.
        ArrayList<ContentProviderOperation> cpoTheaters = new ArrayList<ContentProviderOperation>();
        ArrayList<ContentProviderOperation> cpoTop = new ArrayList<ContentProviderOperation>();
        ArrayList<ContentProviderOperation> cpoBottom = new ArrayList<ContentProviderOperation>();
        ArrayList<ContentProviderOperation> cpoComing = new ArrayList<ContentProviderOperation>();


        Uri theatersUri = MoviesContract.InTheater.buildDirUri();
        Uri topMoviesUri = MoviesContract.TopMovies.buildDirUri();
        Uri bottomMoviesUri = MoviesContract.BottomMovies.buildDirUri();
        Uri comingSoonUri = MoviesContract.ComingSoon.buildDirUri();


        // Delete all items
        cpoTheaters.add(ContentProviderOperation.newDelete(theatersUri).build());
        cpoTop.add(ContentProviderOperation.newDelete(topMoviesUri).build());
        cpoBottom.add(ContentProviderOperation.newDelete(bottomMoviesUri).build());
        cpoComing.add(ContentProviderOperation.newDelete(comingSoonUri).build());


       // saveTheatersMoviesData(cpo, theatersUri);


        JSONArray theatersResponse = Requestor.requestMoviesJSON(requestQueue, EndPoints.getRequestUrlInTheatersMovies(30));
        JSONArray comingSoonResponse = Requestor.requestMoviesJSON(requestQueue, EndPoints.getRequestUrlComingSoon());
        JSONArray topMoviesResponse = Requestor.requestMoviesJSON(requestQueue, EndPoints.getRequestUrlTopMovies());
        JSONArray bottomMoviesResponse = Requestor.requestMoviesJSON(requestQueue, EndPoints.getRequestUrlBottomMovies());



      new JSonParser(this).parseAndSaveData(cpoTheaters, theatersUri, theatersResponse);
        new JSonParser(this).parseAndSaveData(cpoComing,comingSoonUri,comingSoonResponse);
        new JSonParser(this).parseAndSaveData(cpoTop,topMoviesUri,topMoviesResponse);
        new JSonParser(this).parseAndSaveData(cpoBottom,bottomMoviesUri,bottomMoviesResponse);


        sendStickyBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
    }


}
