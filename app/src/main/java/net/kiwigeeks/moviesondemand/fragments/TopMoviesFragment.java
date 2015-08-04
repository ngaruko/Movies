package net.kiwigeeks.moviesondemand.fragments;


import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.adapters.AdapterTopMovies;
import net.kiwigeeks.moviesondemand.data.TopMovieLoader;
import net.kiwigeeks.moviesondemand.services.MoviesService;
import net.kiwigeeks.moviesondemand.utilities.MovieSorter;
import net.kiwigeeks.moviesondemand.utilities.SortListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopMoviesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,SortListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG_SORT_TITLE = "sortTitle";
    //tag associated with the  menu button that sorts by date
    private static final String TAG_SORT_DATE = "sortDate";
    //tag associated with the  menu button that sorts by ratings

    private static final String TAG_SORT_RATING = "sortRating";

    private RecyclerView mRecyclerView;

    public TopMoviesFragment() {
        // Required empty public constructor
    }

    public static android.support.v4.app.Fragment newInstance(String param1, String param2) {
        TopMoviesFragment fragment = new TopMoviesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getActivity().getLoaderManager().initLoader(1, null, this);

        if (savedInstanceState == null) {

            getActivity().startService(new Intent(getActivity(), MoviesService.class));

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view= inflater.inflate(R.layout.fragment_top_movies, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.top_movies_recyclerView);

        return view;
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return TopMovieLoader.newAllTopMoviesInstance(getActivity());
        //  return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {

        if (isAdded()) {
            Log.e("Loading....", "Top movies!");
            AdapterTopMovies adapter = new AdapterTopMovies(cursor, getActivity());
            adapter.setHasStableIds(true);
            try {
                mRecyclerView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int columnCount = getResources().getInteger(R.integer.list_column_count);

            StaggeredGridLayoutManager sglm2 =
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(sglm2);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public void onSortTitle() {
        MovieSorter.Sort.setSortString(TAG_SORT_TITLE);

        getActivity().getLoaderManager().restartLoader(1, null, this);
        Snackbar
                .make(getView(), "Sorted by Title", Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .show();

    }

    @Override
    public void onSortByDate() {
        MovieSorter.Sort.setSortString(TAG_SORT_DATE);

        getActivity().getLoaderManager().restartLoader(1, null, this);
        Snackbar
                .make(getView(), "Sorted by Date", Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .show();

    }

    @Override
    public void onSortByRating() {

        MovieSorter.Sort.setSortString(TAG_SORT_RATING);
        getActivity().getLoaderManager().restartLoader(1, null, this);


        Snackbar
                .make(getView(), "Sorted by Rating", Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .show();

    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {
        getActivity().startService(new Intent(getActivity(), MoviesService.class));
        getActivity().getLoaderManager().restartLoader(1, null, this);
    }
}