package net.kiwigeeks.moviesondemand.data;

import android.content.ContentResolver;
import android.net.Uri;

import net.kiwigeeks.moviesondemand.utilities.MovieSorter;

/**
 * Created by itl on 24/07/2015.
 */
public class MoviesContract {


    public static final String CONTENT_AUTHORITY = "net.kiwigeeks.moviesondemand";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TOP_MOVIES = "top_movies";
    public static final String PATH_COMING_SOON = "coming_soon";

    public static final String PATH_INTHEATERS = "in_theaters";
    public static final String PATH_BOTTOM_MOVIES = "bottom_movies";
    public static final String PATH_FOUND_MOVIES = "found_movies";

    private static String sortString;


    public interface MoviesColumns {
        /**
         * Type: INTEGER PRIMARY KEY AUTOINCREMENT
         */
        String _ID = "_id";
        String COLUMN_TITLE = "title";
        String COLUMN_RELEASE_DATE = "release_date";
        String COLUMN_RATING = "audience_score";
        String COLUMN_PLOT = "plot";
        String COLUMN_URL_THUMBNAIL = "url_thumbnail";
        String COLUMN_RATED = "rated";
        String COLUMN_GENRES = "genres";
        String COLUMN_RUNTIME = "runtime";
        String COLUMN_SIMPLE_PLOT = "simple_plot";
        String COLUMN_IMDB_ID = "idIMDB";
        String COLUMN_URLIMDB="urlIMDB";
        String COLUMN_TRAILER_URL="trailerUrl";
    }


    /* Inner class that defines the table contents of the in theaters table */
    public static final class InTheater implements MoviesColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INTHEATERS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INTHEATERS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INTHEATERS;

        public static final String TABLE_IN_THEATERS = "movies_in_theaters";

        public static String DEFAULT_SORT = COLUMN_RELEASE_DATE + " DESC";
        public static String TITLE_SORT = COLUMN_TITLE + " ASC";
        public static String RATING_SORT = COLUMN_RATING + " DESC";


        /**
         * Matches: /movies/
         */
        public static Uri buildDirUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_INTHEATERS).build();
        }

        /**
         * Matches: /movies/[_id]/
         */
        public static Uri buildItemUri(long _id) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_INTHEATERS).appendPath(Long.toString(_id)).build();
        }

        /**
         * Read item ID movie detail URI.
         */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
        public static String getSortString() {
            sortString= MovieSorter.Sort.getSortString();
            switch (sortString){

                case "sortTitle":
                    sortString= TITLE_SORT;
                    break;

                case "sortDate":
                    sortString= DEFAULT_SORT;
                    break;

                case "sortRating":
                    sortString= RATING_SORT;
                    break;

                default: sortString=DEFAULT_SORT;
            }
            return sortString;
        }


    }

    public static final class ComingSoon implements MoviesColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMING_SOON).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COMING_SOON;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COMING_SOON;



        public static String DEFAULT_SORT = COLUMN_RELEASE_DATE + " DESC";
        public static String TITLE_SORT = COLUMN_TITLE + " ASC";
        public static String RATING_SORT = COLUMN_RATING + " ASC";


        /**
         * Matches: /movies/
         */
        public static Uri buildDirUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMING_SOON).build();
        }

        /**
         * Matches: /movies/[_id]/
         */
        public static Uri buildItemUri(long _id) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMING_SOON).appendPath(Long.toString(_id)).build();
        }

        /**
         * Read item ID movie detail URI.
         */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }


    }

    public static final class TopMovies implements MoviesColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_MOVIES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_MOVIES;



        public static String DEFAULT_SORT = COLUMN_RELEASE_DATE + " DESC";
        public static String TITLE_SORT = COLUMN_TITLE + " ASC";
        public static String RATING_SORT = COLUMN_RATING + " ASC";


        /**
         * Matches: /movies/
         */
        public static Uri buildDirUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_MOVIES).build();
        }

        /**
         * Matches: /movies/[_id]/
         */
        public static Uri buildItemUri(long _id) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_MOVIES).appendPath(Long.toString(_id)).build();
        }

        /**
         * Read item ID movie detail URI.
         */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }


    }


//toto customise
    public static final class BottomMovies implements MoviesColumns {

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOTTOM_MOVIES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOTTOM_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOTTOM_MOVIES;


        /**
         * Matches: /movies/
         */
        public static Uri buildDirUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOTTOM_MOVIES).build();
        }

        /**
         * Matches: /movies/[_id]/
         */
        public static Uri buildItemUri(long _id) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOTTOM_MOVIES).appendPath(Long.toString(_id)).build();
        }

        /**
         * Read item ID movie detail URI.
         */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }


    }


    public static final class FoundMovies implements MoviesColumns {


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FOUND_MOVIES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FOUND_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FOUND_MOVIES;


        /**
         * Matches: /movies/
         */
        public static Uri buildDirUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_FOUND_MOVIES).build();
        }

        /**
         * Matches: /movies/[_id]/
         */
        public static Uri buildItemUri(long _id) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_FOUND_MOVIES).appendPath(Long.toString(_id)).build();
        }

        /**
         * Read item ID movie detail URI.
         */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }


    }

}
