package net.kiwigeeks.moviesondemand.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.kiwigeeks.moviesondemand.Callbacks.MoviesFoundListener;
import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.adapters.AdapterComingSoonMovies;
import net.kiwigeeks.moviesondemand.data.MovieLoader;
import net.kiwigeeks.moviesondemand.tasks.SearchMoviesTask;
import net.kiwigeeks.moviesondemand.utilities.LogHelper;

public class FoundMoviesFragment extends Fragment implements MoviesFoundListener, LoaderManager.LoaderCallbacks<Cursor> {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TITLE_EXTRA = "title_extra";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private String mTitle;
    private RecyclerView mRecyclerView;
    private View mProgressbar;


    public FoundMoviesFragment() {
        // Required empty public constructor
    }

    public static FoundMoviesFragment newInstance(String param1, String param2) {
        FoundMoviesFragment fragment = new FoundMoviesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(TITLE_EXTRA)) {

            mTitle = intent.getStringExtra(TITLE_EXTRA);


            LogHelper.log("Intent received: " + mTitle);

            new SearchMoviesTask(this, getActivity()).execute(mTitle, "&limit=10");


        } else mTitle = getArguments().getString("title");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_found_movies, container, false);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getActivity().setTitle(mTitle);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mProgressbar = view.findViewById(R.id.progressbar);

        getActivity().getLoaderManager().initLoader(6, null, this);


        if (savedInstanceState == null) {

            //getActivity().startService(new Intent(getActivity(), MoviesService.class));


        }


        return view;


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMoviesFound() {

        mProgressbar.setVisibility(View.GONE);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return MovieLoader.newAllFoundMoviesInstance(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
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
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
