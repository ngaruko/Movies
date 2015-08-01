package net.kiwigeeks.moviesondemand.data;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

/**
 * Helper for loading a list of articles or a single article.
 */
public class MovieLoader extends CursorLoader {
    public static MovieLoader newAllInTheatersMoviesInstance(Context context) {
        return new MovieLoader(context, MoviesContract.InTheater.buildDirUri());
    }

    public static MovieLoader newAllTopMoviesInstance(Context context) {
        return new MovieLoader(context, MoviesContract.TopMovies.buildDirUri());
    }

    public static MovieLoader newAllComingSoonMoviesInstance(Context context) {
        return new MovieLoader(context, MoviesContract.ComingSoon.buildDirUri());
    }



    public static MovieLoader newInstanceForItemId(Context context, long itemId) {
        return new MovieLoader(context, MoviesContract.InTheater.buildItemUri(itemId));
    }

    private MovieLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, MoviesContract.InTheater.getSortString());
    }

    public static MovieLoader newAllBottomMoviesInstance(Context context) {
        return new MovieLoader(context, MoviesContract.BottomMovies.buildDirUri());
    }




    public interface Query {
        String[] PROJECTION = {

                MoviesContract.InTheater._ID,
                MoviesContract.InTheater.COLUMN_TITLE,
                MoviesContract.InTheater.COLUMN_RELEASE_DATE,
                MoviesContract.InTheater.COLUMN_RATING,
                MoviesContract.InTheater.COLUMN_PLOT,
                MoviesContract.InTheater.COLUMN_URL_THUMBNAIL,
                MoviesContract.InTheater.COLUMN_RATED,
                MoviesContract.InTheater.COLUMN_GENRES,
                MoviesContract.InTheater.COLUMN_IMDB_ID,
                MoviesContract.InTheater.COLUMN_RUNTIME
        };


     int _ID=0;
      int COLUMN_TITLE=1;
      int COLUMN_RELEASE_DATE=2;
      int COLUMN_RATING=3;
      int COLUMN_PLOT=4;
      int COLUMN_URL_THUMBNAIL=5;
      int COLUMN_RATED=6;
        int COLUMN_GENRES=7;
       int COLUMN_IMDB_ID=8;
        int COLUMN_RUNTIME=9;
       

    }
}
