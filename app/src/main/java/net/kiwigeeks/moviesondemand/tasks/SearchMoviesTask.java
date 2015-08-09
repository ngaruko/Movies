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
    private JSONArray mResponse;


    public SearchMoviesTask(MoviesFoundListener myComponent, Context context) {
        this.myComponent = myComponent;
        this.mContext = context;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected Void doInBackground(String... params) {

        String title = params[0];
        String limit = params[1];

        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        Uri uri = MoviesContract.FoundMovies.buildDirUri();


        // Delete all items
        cpo.add(ContentProviderOperation.newDelete(uri).build());


        mResponse = null;

        try {
            mResponse = Requestor.requestMoviesJSON(requestQueue, EndPoints.getRequestUrlFoundMovies(title, limit));

        } catch (Exception e) {
            e.printStackTrace();
        }


        new JSonParser(mContext).parseAndSaveData(cpo, uri, mResponse);


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        // super.onPostExecute(aVoid);
//        if ( mResponse == null)  {
//            AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
//            alertDialog.setTitle(" No Movies");
//            alertDialog.setMessage("Please check your internet connection and/or try another search!");
//            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            alertDialog.show();
//        }
//        else
        if (myComponent != null) {

            myComponent.onMoviesFound(mResponse);
        }


    }

}