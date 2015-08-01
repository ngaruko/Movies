package net.kiwigeeks.moviesondemand.json;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.RequestQueue;

import net.kiwigeeks.moviesondemand.VolleySingleton;
import net.kiwigeeks.moviesondemand.data.MoviesContract;
import net.kiwigeeks.moviesondemand.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static net.kiwigeeks.moviesondemand.data.MoviesContract.CONTENT_AUTHORITY;
import static net.kiwigeeks.moviesondemand.data.MoviesContract.MoviesColumns.COLUMN_TITLE;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_GENRES;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_ID;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_MOVIES;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_PLOT;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_RATED;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_RATINGS;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_RELEASE_DATE;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_RUNTIME;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_TITLE;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_URLPOSTER;

/**
 * Created by itl on 28/07/2015.
 */
public class JSonParser {


    public Context context;

    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;


    public JSonParser(Context context) {
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.context = context;
    }

    public void parseAndSaveData(ArrayList<ContentProviderOperation> cpo, Uri uri, JSONArray response) {


        if (response != null && response.length() > 0) {


            try {
                for (int j = 0; j < response.length(); j++) {


                    JSONObject body = (JSONObject) response.get(j);
                    Log.e(" RESPONSE BODY", String.valueOf(body.length()));
                    try {


                        JSONArray movies;
                        if (body.has(KEY_MOVIES)) {
                            movies = body.getJSONArray(KEY_MOVIES);
                        } else {

                            movies = response;
                        }


                        Log.e("Movies ", String.valueOf(movies.length()));
                        for (int i = 0; i < movies.length(); i++) {

                            //Initialise all the fields

                            String title = Constants.NA;
                            String id = Constants.NA;
                            String released = Constants.NA;
                            Double rating = -1.0;
                            String rated = Constants.NA;
                            String urlPoster = Constants.NA;
                            String ratingString = Constants.NA;
                            String runtime = Constants.NA;
                            String plot = Constants.NA;
                            String genres = "";
                            String tthumbnailUrl = Constants.NA;

                            JSONObject currentMovie = movies.getJSONObject(i);
                            if (currentMovie.has(KEY_ID) && !currentMovie.isNull(KEY_ID)) {
                                id = currentMovie.getString(KEY_ID);
                            }
                            released = currentMovie.getString(KEY_RELEASE_DATE);
                            int releaseDate = 0;
                            releaseDate = Integer.parseInt(released);

                            if (currentMovie.has(KEY_TITLE) && !currentMovie.isNull(KEY_TITLE)) {
                                title = currentMovie.getString(KEY_TITLE);


                            }
                            if (currentMovie.has(KEY_RUNTIME) && !currentMovie.isNull(KEY_RUNTIME) && currentMovie.length() >= 1) {
                                try {
                                    runtime = currentMovie.getJSONArray(KEY_RUNTIME).get(0).toString();
                                } catch (JSONException e) {
                                    Log.e("Parse error", e.getMessage());
                                }
                            }
                            if (currentMovie.has(KEY_GENRES) && !currentMovie.isNull(KEY_GENRES)) {

                                //TODO serialise this
                                JSONArray genresArray = currentMovie.getJSONArray(KEY_GENRES);
                                for (int g = 0; g < genresArray.length(); g++) {
                                    String genre = genresArray.get(g).toString();
                                    genres = genres.concat(", " + genre).substring(1);

                                }


                            }


                            rated = currentMovie.getString(KEY_RATED);
                            plot = currentMovie.getString(KEY_PLOT);
                            urlPoster = currentMovie.getString(KEY_URLPOSTER);
                            if (currentMovie.has(KEY_RATINGS) && !currentMovie.isNull(KEY_RATINGS)) {
                                ratingString = currentMovie.getString(KEY_RATINGS);
                            }
                            rating = getRating(ratingString);
                            Log.e("ratings ", Double.toString(rating));


                            //INSERT THIS TO DB
                            ContentValues values = new ContentValues();

                            values.put(COLUMN_TITLE, title);
                            values.put(MoviesContract.MoviesColumns.COLUMN_IMDB_ID, id);
                            values.put(MoviesContract.MoviesColumns.COLUMN_PLOT, plot);
                            values.put(MoviesContract.MoviesColumns.COLUMN_RATED, rated);
                            values.put(MoviesContract.MoviesColumns.COLUMN_RATING, rating);
                            values.put(MoviesContract.MoviesColumns.COLUMN_RELEASE_DATE, releaseDate);
                            values.put(MoviesContract.MoviesColumns.COLUMN_URL_THUMBNAIL, urlPoster);
                            values.put(MoviesContract.MoviesColumns.COLUMN_GENRES, genres);
                            values.put(MoviesContract.MoviesColumns.COLUMN_RUNTIME, runtime);

                            if (!title.equals(Constants.NA)) {
                                cpo.add(ContentProviderOperation.newInsert(uri).withValues(values).build());


                            }

                        }
                        context.getContentResolver().applyBatch(CONTENT_AUTHORITY, cpo);
                    } catch (JSONException | RemoteException | OperationApplicationException e) {
                        Log.e("Error updating content.", "Error updating content.", e);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }





    private static Double getRating(String rating) {

        if (rating.contains(",")) {
            rating = rating.replace(rating.charAt(1), '.');
        }
        try {

            return Double.parseDouble(rating);
        } catch (NumberFormatException e) {
            // e.printStackTrace();
            Log.e("NumberFormatException", e.getMessage());
        }
        return -1.0;
    }



}
