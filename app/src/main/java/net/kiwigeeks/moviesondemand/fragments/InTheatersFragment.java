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

import net.kiwigeeks.moviesondemand.adapters.AdapterMovies;
import net.kiwigeeks.moviesondemand.data.MovieLoader;
import net.kiwigeeks.moviesondemand.data.UpdaterService;
import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.utilities.MovieSorter;
import net.kiwigeeks.moviesondemand.utilities.SortListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InTheatersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InTheatersFragment extends Fragment implements   LoaderManager.LoaderCallbacks<Cursor>,SortListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    private static final String TAG_SORT_TITLE = "sortTitle";
    //tag associated with the  menu button that sorts by date
    private static final String TAG_SORT_DATE = "sortDate";
    //tag associated with the  menu button that sorts by ratings

    private static final String TAG_SORT_RATING ="sortRating";

    private RecyclerView mRecyclerView;



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

    public InTheatersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_in_theaters, container, false);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_movies_view);



     getActivity().getLoaderManager().initLoader(0, null,this);



        if (savedInstanceState == null) {
            // refresh();
            Log.e("No data", "null data");
            refresh();
            Log.e("savedInstanceState", "service updated");
        }


        return view;
    }

    private void refresh() {
        getActivity().startService(new Intent(getActivity(), UpdaterService.class));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

       // outState.putParcelableArrayList(STATE_MOVIES, listMovies);
    }



    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return MovieLoader.newAllInTheatersMoviesInstance(getActivity());
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        AdapterMovies adapter = new AdapterMovies(cursor,getActivity(),R.layout.movie_item_layout);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }


    @Override
    public void onSortTitle() {
        MovieSorter.Sort.setSortString(TAG_SORT_TITLE);
        getLoaderManager().restartLoader(0, null, null);

        Snackbar
                .make(getView(), "Sorted by Title", Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .show();

    }

    @Override
    public void onSortByDate() {
        MovieSorter.Sort.setSortString(TAG_SORT_DATE);
        getLoaderManager().restartLoader(0, null, null);
        Snackbar
                .make(getView(), "Sorted by Date", Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .show();

    }

    @Override
    public void onSortByRating() {

        MovieSorter.Sort.setSortString(TAG_SORT_RATING);
        getLoaderManager().restartLoader(0, null, null);


        Snackbar
                .make(getView(), "Sorted by Author", Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .show();

    }

    @Override
    public void onRefresh() {
        refresh();
    }
}



