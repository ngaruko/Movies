package net.kiwigeeks.moviesondemand.tasks;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;

import net.kiwigeeks.moviesondemand.Callbacks.MoviesFoundListener;
import net.kiwigeeks.moviesondemand.VolleySingleton;
import net.kiwigeeks.moviesondemand.data.MoviesContract;
import net.kiwigeeks.moviesondemand.json.JSonParser;
import net.kiwigeeks.moviesondemand.json.Requestor;
import net.kiwigeeks.moviesondemand.utilities.EndPoints;

import org.json.JSONArray;

import java.util.ArrayList;


public class SearchMoviesTask extends AsyncTask<String, Void, Void> {

    public JSONArray response;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private MoviesFoundListener myComponent;
    private Context mContext;


    public SearchMoviesTask(MoviesFoundListener myComponent, Context context) {
        this.myComponent = myComponent;
        this.mContext = context;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected Void doInBackground(String... params) {

        String title = params[0];

        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        Uri uri = MoviesContract.FoundMovies.buildDirUri();


        // Delete all items
        cpo.add(ContentProviderOperation.newDelete(uri).build());


        JSONArray response = null;

        try {
            response = Requestor.requestMoviesJSON(requestQueue, EndPoints.getRequestUrlFoundMovies(title));

        } catch (Exception e) {
            e.printStackTrace();
        }


        new JSonParser(mContext).parseAndSaveData(cpo, uri, response);


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        // super.onPostExecute(aVoid);

        if (myComponent != null) {
            myComponent.onMoviesFound();
        }
    }

}