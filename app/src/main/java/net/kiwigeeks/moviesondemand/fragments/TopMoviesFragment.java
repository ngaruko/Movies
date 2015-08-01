package net.kiwigeeks.moviesondemand.fragments;


import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.kiwigeeks.moviesondemand.adapters.AdapterMovies;
import net.kiwigeeks.moviesondemand.data.MovieLoader;
import net.kiwigeeks.moviesondemand.data.UpdaterService;
import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.utilities.SortListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopMoviesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,SortListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView mRecyclerView;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TopMoviesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static android.support.v4.app.Fragment newInstance(String param1, String param2) {
        TopMoviesFragment fragment = new TopMoviesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TopMoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //display in long period of time
        Toast.makeText(getActivity(), "Top Movies Fragment!",
                Toast.LENGTH_LONG).show();
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_top_movies, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_movies_view);


        getActivity().getLoaderManager().initLoader(0, null, this);


        if (savedInstanceState == null) {
            // refresh();
            Log.e("No data", "null data");
            getActivity().startService(new Intent(getActivity(), UpdaterService.class));
            Log.e("savedInstanceState", "service updated");
        }


        return view;
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return MovieLoader.newAllTopMoviesInstance(getActivity());
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        AdapterMovies adapter = new AdapterMovies(cursor, getActivity(), R.layout.top_movie_item_layout);
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

    }

    @Override
    public void onSortByDate() {

    }

    @Override
    public void onSortByRating() {

    }

    @Override
    public void onRefresh() {

    }
}