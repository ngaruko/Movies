package net.kiwigeeks.moviesondemand.data;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;

import com.android.volley.RequestQueue;

import net.kiwigeeks.moviesondemand.VolleySingleton;
import net.kiwigeeks.moviesondemand.json.JSonParser;
import net.kiwigeeks.moviesondemand.json.Requestor;
import net.kiwigeeks.moviesondemand.utilities.DialogClass;
import net.kiwigeeks.moviesondemand.utilities.EndPoints;

import org.json.JSONArray;

import java.util.ArrayList;

public class MoviesService extends IntentService {

    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;
    public Context context;

    private static final String TAG = "MoviesService";

    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "net.kiwigeeks.moviesondemand.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "net.kiwigeeks.moviesondemand.intent.extra.REFRESHING";




    public MoviesService() {
        super(TAG);

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected void onHandleIntent(Intent intent) {


        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
      // notifyUser("Connection Error", "Please check your internet connection!");
           startActivity(new Intent(this, DialogClass.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

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


        JSONArray theatersResponse = null;
        JSONArray comingSoonResponse = null;
        JSONArray topMoviesResponse = null;
        JSONArray bottomMoviesResponse = null;
        try {
            theatersResponse = Requestor.requestMoviesJSON(requestQueue, EndPoints.getRequestUrlInTheatersMovies(30));


            comingSoonResponse = Requestor.requestMoviesJSON(requestQueue, EndPoints.getRequestUrlComingSoon());
            topMoviesResponse = Requestor.requestMoviesJSON(requestQueue, EndPoints.getRequestUrlTopMovies());
            bottomMoviesResponse = Requestor.requestMoviesJSON(requestQueue, EndPoints.getRequestUrlBottomMovies());
        } catch (Exception e) {
            e.printStackTrace();
        }


        new JSonParser(this).parseAndSaveData(cpoTheaters, theatersUri, theatersResponse);
        new JSonParser(this).parseAndSaveData(cpoComing,comingSoonUri,comingSoonResponse);
        new JSonParser(this).parseAndSaveData(cpoTop,topMoviesUri,topMoviesResponse);
        new JSonParser(this).parseAndSaveData(cpoBottom,bottomMoviesUri,bottomMoviesResponse);


        sendStickyBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
    }

    private void notifyUser(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getBaseContext()).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
       //alertDialog.getContext();
    }


}
