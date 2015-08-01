package net.kiwigeeks.moviesondemand.json;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;

import net.kiwigeeks.moviesondemand.VolleySingleton;
import net.kiwigeeks.moviesondemand.data.Movie;
import net.kiwigeeks.moviesondemand.utilities.Constants;
import net.kiwigeeks.moviesondemand.utilities.EndPoints;
import net.kiwigeeks.moviesondemand.utilities.LogHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_GENRES;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_ID;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_PLOT;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_QUALITIES;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_RATED;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_RATINGS;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_RELEASE_DATE;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_RUNTIME;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_TITLE;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_TRAILER;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_URLPOSTER;
import static net.kiwigeeks.moviesondemand.utilities.Keys.InTheatersEndpoint.KEY_VIDEO_URL;

/**
 * Created by itl on 30/07/2015.
 */
public class TrailerParser {


    public static RequestQueue requestQueue;
    public Context context;

    public VolleySingleton volleySingleton;
   // public RequestQueue requestQueue;


    public TrailerParser(Context context) {
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.context = context;
    }

    public static String getTrailerUrl(String title) {

        JSONArray response = Requestor.requestMoviesJSON(requestQueue, EndPoints.getRequestMovieUrl(title));


        String videoUrl = Constants.NA;
        if (response != null && response.length() > 0) {


            try {


                    JSONObject body = (JSONObject) response.get(0);

                    try {


                        JSONObject trailer;
                        if (body.has(KEY_TRAILER)) {
                            trailer = body.getJSONObject(KEY_TRAILER);


                            JSONArray qualities = trailer.getJSONArray(KEY_QUALITIES);
                            JSONObject videoUrlObject = (JSONObject) qualities.get(0);
                            videoUrl = videoUrlObject.getString(KEY_VIDEO_URL);
                        }
                        return videoUrl;


                    } catch (JSONException e) {
                        Log.e("Error updating content.", "Error updating content.", e);
                    }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return videoUrl;
    }


    public static Movie parseSearchMovie(JSONArray response) {
        Movie movie = new Movie();

        LogHelper.log("parser called: response == " + response);
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
        String thumbnailUrl = Constants.NA;

        String videoUrl = Constants.NA;
        if (response != null && response.length() > 0) {


            try {

                JSONObject currentMovie = (JSONObject) response.get(0);
                Log.e(" RESPONSE BODY", String.valueOf(currentMovie.length()));
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

                JSONObject trailer;
                if (currentMovie.has(KEY_TRAILER)) {
                    trailer = currentMovie.getJSONObject(KEY_TRAILER);


                    JSONArray qualities = trailer.getJSONArray(KEY_QUALITIES);
                    JSONObject videoUrlObject = (JSONObject) qualities.get(0);
                    videoUrl = videoUrlObject.getString(KEY_VIDEO_URL);
                }
                else{
                    LogHelper.log("No Trailers available");
                }


                rated = currentMovie.getString(KEY_RATED);
                plot = currentMovie.getString(KEY_PLOT);
                urlPoster = currentMovie.getString(KEY_URLPOSTER);
                if (currentMovie.has(KEY_RATINGS) && !currentMovie.isNull(KEY_RATINGS)) {
                    ratingString = currentMovie.getString(KEY_RATINGS);
                }
                rating = getRating(ratingString);
                Log.e("ratings ", Double.toString(rating));


                //make movie object

                movie.setGenres(genres);
                movie.setPlot(plot);
                movie.setTitle(title);
                movie.setRated(rated);
                movie.setReleaseDate(released);
                movie.setTrailerUrl(videoUrl);
                movie.setRuntime(runtime);
                movie.setUrlPoster(urlPoster);

                return movie;

                } catch (JSONException e) {
                    e.printStackTrace();
                }




            LogHelper.log("parser done...returns: " + movie.getPlot());

        }
        return null;
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
