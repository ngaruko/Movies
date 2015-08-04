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
import net.kiwigeeks.moviesondemand.adapters.AdapterComingSoonMovies;
import net.kiwigeeks.moviesondemand.data.MovieLoader;
import net.kiwigeeks.moviesondemand.services.MoviesService;
import net.kiwigeeks.moviesondemand.utilities.MovieSorter;
import net.kiwigeeks.moviesondemand.utilities.SortListener;


public class ComingSoonFragment extends Fragment implements   LoaderManager.LoaderCallbacks<Cursor>, SortListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private static final String TAG_SORT_TITLE = "sortTitle";
    //tag associated with the  menu button that sorts by date
    private static final String TAG_SORT_DATE = "sortDate";
    //tag associated with the  menu button that sorts by ratings

    private static final String TAG_SORT_RATING = "sortRating";
    private String mParam1;
    private String mParam2;

    private RecyclerView mRecyclerView;


    public ComingSoonFragment() {
        // Required empty public constructor
    }

    public static ComingSoonFragment newInstance(String param1, String param2) {
        ComingSoonFragment fragment = new ComingSoonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (savedInstanceState == null) {

            getActivity().startService(new Intent(getActivity(), MoviesService.class));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_coming_soon, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.coming_soon_movies_recyclerView);

        getActivity().getLoaderManager().initLoader(3, null, this);


        if (savedInstanceState == null) {

            getActivity().startService(new Intent(getActivity(), MoviesService.class));

        }


        return view;
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return MovieLoader.newAllComingSoonMoviesInstance(getActivity());
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        if (isAdded()) {
            AdapterComingSoonMovies adapter = new AdapterComingSoonMovies(cursor, getActivity());
            adapter.setHasStableIds(true);
            mRecyclerView.setAdapter(adapter);
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

        getActivity().getLoaderManager().restartLoader(3, null, this);
        Snackbar
                .make(getView(), "Sorted by Title", Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .show();

    }

    @Override
    public void onSortByDate() {
        MovieSorter.Sort.setSortString(TAG_SORT_DATE);

        getActivity().getLoaderManager().restartLoader(3, null, this);
        Snackbar
                .make(getView(), "Sorted by Date", Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .show();

    }

    @Override
    public void onSortByRating() {

        MovieSorter.Sort.setSortString(TAG_SORT_RATING);
        getActivity().getLoaderManager().restartLoader(3, null, this);


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
        getActivity().getLoaderManager().restartLoader(3, null, this);
    }
}
