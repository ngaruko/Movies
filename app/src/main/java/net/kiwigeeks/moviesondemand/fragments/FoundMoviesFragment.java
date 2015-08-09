package net.kiwigeeks.moviesondemand.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.kiwigeeks.moviesondemand.Callbacks.MoviesFoundListener;
import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.adapters.AdapterFoundMovies;
import net.kiwigeeks.moviesondemand.data.MovieLoader;
import net.kiwigeeks.moviesondemand.tasks.SearchMoviesTask;
import net.kiwigeeks.moviesondemand.utilities.LogHelper;

import org.json.JSONArray;

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
    private boolean mHasResults = false;


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


        }


        if (savedInstanceState != null) {


            getActivity().getLoaderManager().initLoader(6, null, this);


        } else new SearchMoviesTask(this, getActivity()).execute(mTitle, "&limit=10");
//        mProgressbar.setVisibility(View.GONE);


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
        mProgressbar.setVisibility(View.VISIBLE);

        // getActivity().getLoaderManager().initLoader(6, null, this);


        if (savedInstanceState != null) {
            mProgressbar.setVisibility(View.GONE);

            //getActivity().startService(new Intent(getActivity(), MoviesService.class));


        }


        return view;


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mHasResults) {
            outState.putString("searhc", mTitle);
            mProgressbar.setVisibility(View.GONE);
        }
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
    public void onMoviesFound(JSONArray response) {

        mProgressbar.setVisibility(View.GONE);

        mHasResults = true;

        if (response == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity().getApplicationContext()).create();
            alertDialog.setTitle(" No Movies");
            alertDialog.setMessage("Please check your internet connection and/or try another search!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            try {
                alertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

//            Toast.makeText(getActivity().getBaseContext(), "Please check your internet connection and/or try another search!",
//                    Toast.LENGTH_SHORT).show();

        } else {
            mHasResults = false;
            mProgressbar.setVisibility(View.GONE);

            getActivity().getLoaderManager().initLoader(6, null, this);
        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        mProgressbar.setVisibility(View.GONE);
        return MovieLoader.newAllFoundMoviesInstance(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        if (isAdded()) {


            //mProgressbar.setVisibility(View.GONE);

            AdapterFoundMovies adapter = new AdapterFoundMovies(cursor, getActivity());
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
