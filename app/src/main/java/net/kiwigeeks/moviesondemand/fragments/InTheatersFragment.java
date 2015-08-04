package net.kiwigeeks.moviesondemand.fragments;


import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.adapters.AdapterMoviesInTheaters;
import net.kiwigeeks.moviesondemand.data.MovieLoader;
import net.kiwigeeks.moviesondemand.services.MoviesService;
import net.kiwigeeks.moviesondemand.utilities.MovieSorter;
import net.kiwigeeks.moviesondemand.utilities.SortListener;


public class InTheatersFragment extends Fragment implements   LoaderManager.LoaderCallbacks<Cursor>,SortListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    private static final String TAG_SORT_TITLE = "sortTitle";
    //tag associated with the  menu button that sorts by date
    private static final String TAG_SORT_DATE = "sortDate";
    //tag associated with the  menu button that sorts by ratings

    private static final String TAG_SORT_RATING ="sortRating";
    private static final String CONNECTIVITY_SERVICE = "connectivity";

    private RecyclerView mRecyclerView;
    private View mView;


    public InTheatersFragment() {
        // Required empty public constructor
    }

    public static InTheatersFragment newInstance(String param1, String param2) {
        InTheatersFragment fragment = new InTheatersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static String getRequest() {
        return " ";

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getActivity().getLoaderManager().initLoader(0, null, this);


        if (savedInstanceState == null) {
            refresh();

        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mView = inflater.inflate(R.layout.fragment_in_theaters, container, false);


        mRecyclerView = (RecyclerView) mView.findViewById(R.id.in_theaters_recyclerView);


        return mView;
    }

    private void refresh() {
        getActivity().startService(new Intent(getActivity(), MoviesService.class));
        getActivity().getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }



    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {


        return MovieLoader.newAllInTheatersMoviesInstance(getActivity());


    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {


        if (isAdded()) {
            AdapterMoviesInTheaters adapter = new AdapterMoviesInTheaters(cursor, getActivity());
            adapter.setHasStableIds(true);
            try {
                mRecyclerView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int columnCount = getResources().getInteger(R.integer.list_column_count);
            StaggeredGridLayoutManager sglm =
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(sglm);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }


    @Override
    public void onSortTitle() {
        MovieSorter.Sort.setSortString(TAG_SORT_TITLE);

        getActivity().getLoaderManager().restartLoader(0, null, this);
        Snackbar
                .make(getView(), "Sorted by Title", Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .show();

    }

    @Override
    public void onSortByDate() {
        MovieSorter.Sort.setSortString(TAG_SORT_DATE);

        getActivity().getLoaderManager().restartLoader(0, null, this);
        Snackbar
                .make(getView(), "Sorted by Date", Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .show();

    }

    @Override
    public void onSortByRating() {

        MovieSorter.Sort.setSortString(TAG_SORT_RATING);
        getActivity().getLoaderManager().restartLoader(0, null, this);


        Snackbar
                .make(getView(), "Sorted by Rating", Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .show();

    }

    @Override
    public void onRefresh() {
        refresh();
    }
}



