package net.kiwigeeks.moviesondemand.data;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

public class TopMovieLoader extends CursorLoader {
   

    private TopMovieLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, MoviesContract.InTheater.getSortString());
    }

    public static TopMovieLoader newAllTopMoviesInstance(Context context) {
        return new TopMovieLoader(context, MoviesContract.TopMovies.buildDirUri());
    }

    public static TopMovieLoader newInstanceForItemId(Context context, long itemId) {
        return new TopMovieLoader(context, MoviesContract.InTheater.buildItemUri(itemId));
    }

   




    public interface Query {
        String[] PROJECTION = {

                MoviesContract.TopMovies._ID,
                MoviesContract.TopMovies.COLUMN_TITLE,
                MoviesContract.TopMovies.COLUMN_RELEASE_DATE,
                MoviesContract.TopMovies.COLUMN_RATING,
                MoviesContract.TopMovies.COLUMN_PLOT,
                MoviesContract.TopMovies.COLUMN_URL_THUMBNAIL,
                MoviesContract.TopMovies.COLUMN_RATED,
                MoviesContract.TopMovies.COLUMN_GENRES,
                MoviesContract.TopMovies.COLUMN_IMDB_ID,
                MoviesContract.TopMovies.COLUMN_RUNTIME
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
