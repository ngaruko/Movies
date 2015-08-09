package net.kiwigeeks.moviesondemand.Callbacks;

import org.json.JSONArray;

/**
 * Created by itl on 6/08/2015.
 */
public interface MoviesFoundListener {
    void onMoviesFound(JSONArray mResponse);
}
