package net.kiwigeeks.moviesondemand.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


public class MoviesProvider extends ContentProvider {
    private static final int IN_THEATERS = 0;
    private static final int IN_THEATERS__ID = 1;
    private static final int COMING_SOON = 2;
    private static final int COMING_SOON__ID = 3;
    private static final int TOP_MOVIES = 4;
    private static final int TOP_MOVIES__ID = 5;
    private static final int BOTTOM_MOVIES = 6;
    private static final int BOTTOM_MOVIES__ID = 7;
    private static final int FOUND_MOVIES = 8;
    private static final int FOUND_MOVIES__ID = 9;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SQLiteOpenHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, "in_theaters", IN_THEATERS);
        matcher.addURI(authority, "in_theaters/#", IN_THEATERS__ID);



        matcher.addURI(authority, "coming_soon", COMING_SOON);
        matcher.addURI(authority, "coming_soon/#", COMING_SOON__ID);

        matcher.addURI(authority, "top_movies", TOP_MOVIES);
        matcher.addURI(authority, "top_movies/#", TOP_MOVIES__ID);

        matcher.addURI(authority, "bottom_movies", BOTTOM_MOVIES);
        matcher.addURI(authority, "bottom_movies/#", BOTTOM_MOVIES__ID);

        matcher.addURI(authority, "found_movies", FOUND_MOVIES);
        matcher.addURI(authority, "found_movies/#", FOUND_MOVIES__ID);


        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case IN_THEATERS:
                return MoviesContract.InTheater.CONTENT_TYPE;
            case IN_THEATERS__ID:
                return MoviesContract.InTheater.CONTENT_ITEM_TYPE;


            case COMING_SOON:
                return MoviesContract.ComingSoon.CONTENT_TYPE;
            case COMING_SOON__ID:
                return MoviesContract.ComingSoon.CONTENT_ITEM_TYPE;

            case TOP_MOVIES:
                return MoviesContract.TopMovies.CONTENT_TYPE;
            case TOP_MOVIES__ID:
                return MoviesContract.TopMovies.CONTENT_ITEM_TYPE;

            case BOTTOM_MOVIES:
                return MoviesContract.BottomMovies.CONTENT_TYPE;
            case BOTTOM_MOVIES__ID:
                return MoviesContract.BottomMovies.CONTENT_ITEM_TYPE;


            case FOUND_MOVIES:
                return MoviesContract.FoundMovies.CONTENT_TYPE;
            case FOUND_MOVIES__ID:
                return MoviesContract.FoundMovies.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        Cursor cursor = builder.where(selection, selectionArgs).query(db, projection, sortOrder);
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case IN_THEATERS: {
                final long _id = db.insertOrThrow(Tables.IN_THEATERS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return MoviesContract.InTheater.buildItemUri(_id);
            }




            case COMING_SOON: {
                final long _id = db.insertOrThrow(Tables.COMING_SOON, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return MoviesContract.ComingSoon.buildItemUri(_id);
            }

            case TOP_MOVIES: {
                final long _id = db.insertOrThrow(Tables.TOP_MOVIES, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return MoviesContract.TopMovies.buildItemUri(_id);
            }

            case BOTTOM_MOVIES: {
                final long _id = db.insertOrThrow(Tables.BOTTOM_MOVIES, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return MoviesContract.BottomMovies.buildItemUri(_id);
            }


            case FOUND_MOVIES: {
                final long _id = db.insertOrThrow(Tables.FOUND_MOVIES, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return MoviesContract.FoundMovies.buildItemUri(_id);
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return builder.where(selection, selectionArgs).update(db, values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return builder.where(selection, selectionArgs).delete(db);
    }

    private SelectionBuilder buildSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        return buildSelection(uri, match, builder);
    }

    private SelectionBuilder buildSelection(Uri uri, int match, SelectionBuilder builder) {
        final List<String> paths = uri.getPathSegments();
        switch (match) {
            case IN_THEATERS: {
                return builder.table(Tables.IN_THEATERS);
            }
            case IN_THEATERS__ID: {
                final String _id = paths.get(1);
                return builder.table(Tables.IN_THEATERS).where(MoviesContract.InTheater._ID + "=?", _id);
            }

            case COMING_SOON:{
                return builder.table(Tables.COMING_SOON);
            }


            case COMING_SOON__ID: {
                final String _id = paths.get(1);
                return builder.table(Tables.COMING_SOON).where(MoviesContract.ComingSoon._ID + "=?", _id);
            }


            case TOP_MOVIES: {
                return builder.table(Tables.TOP_MOVIES);
            }
            case TOP_MOVIES__ID: {
                final String _id = paths.get(1);
                return builder.table(Tables.TOP_MOVIES).where(MoviesContract.TopMovies._ID + "=?", _id);
            }




            case BOTTOM_MOVIES: {
                return builder.table(Tables.BOTTOM_MOVIES);
            }
            case BOTTOM_MOVIES__ID: {
                final String _id = paths.get(1);
                return builder.table(Tables.BOTTOM_MOVIES).where(MoviesContract.BottomMovies._ID + "=?", _id);
            }


            case FOUND_MOVIES: {
                return builder.table(Tables.FOUND_MOVIES);
            }
            case FOUND_MOVIES__ID: {
                final String _id = paths.get(1);
                return builder.table(Tables.FOUND_MOVIES).where(MoviesContract.BottomMovies._ID + "=?", _id);
            }



            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    interface Tables {

        String IN_THEATERS = "in_theaters";
        String COMING_SOON = "coming_soon";
        String TOP_MOVIES = "top_movies";
        String BOTTOM_MOVIES = "bottom_movies";
        String FOUND_MOVIES = "found_movies";
    }
}
