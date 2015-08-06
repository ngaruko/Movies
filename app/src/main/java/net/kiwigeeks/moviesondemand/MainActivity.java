package net.kiwigeeks.moviesondemand;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.kiwigeeks.moviesondemand.activities.HomeFragment;
import net.kiwigeeks.moviesondemand.activities.MovieSearchActivity;
import net.kiwigeeks.moviesondemand.fragments.BottomMoviesFragment;
import net.kiwigeeks.moviesondemand.fragments.ComingSoonFragment;
import net.kiwigeeks.moviesondemand.fragments.InTheatersFragment;
import net.kiwigeeks.moviesondemand.fragments.TopMoviesFragment;
import net.kiwigeeks.moviesondemand.tabs.SlidingTabLayout;
import net.kiwigeeks.moviesondemand.utilities.LogHelper;
import net.kiwigeeks.moviesondemand.utilities.SortListener;


public class MainActivity extends AppCompatActivity {


    public static final int TAB_HOME = 0;
    public static final int TAB_IN_THEATERS = 1;
    public static final int TAB_TOP_MOVIES = 2;

    public static final int TAB_COMING_SOON = 3;

    public static final int TAB_BOTTOM_MOVIES = 4;

    public static final int TAB_COUNT = 5;


    private Toolbar toolbar;

    private ViewPager mPager;
    private SlidingTabLayout mTabs;


    private MyPagerAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= 21) {
            Slide slide = new Slide();
            slide.setDuration(5000);
            getWindow().setEnterTransition(slide);
            getWindow().setReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.content_transition_a));
            getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition_a));

        }


        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);


        //Initialise mpager and mTabs

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(5);
        //For tabs
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());


        mPager.setAdapter(mAdapter);
        // Attach the page change listener inside the activity


        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);


        mTabs.setViewPager(mPager);



    }

    @Override
    protected void onStart() {
        super.onStart();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w("Internet", "Not online, not refreshing.");


            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Connection Error");
            alertDialog.setMessage("Please check your internet connection!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.getContext();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            try {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        handleSearchView(searchView);


        return true;
    }

    private void handleSearchView(final SearchView search) {
        search.setQueryHint("Search Movies");
        search.setSubmitButtonEnabled(true);
        //*** setOnQueryTextFocusChangeListener ***
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO To add more code in Phase 2

            }
        });

        //*** setOnQueryTextListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
//todo here
                //mTitle=query;
                search.clearFocus();
                //display the Progress bar
//                progressbarView.setVisibility(View.VISIBLE);

                searchForMovies(query);

                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO To do more tweaks in phase two or later

                return false;
            }


        });
    }


    public void searchForMovies(String searchQuery) {

        //todo a asunc tast to get movies


//validation here; ask user to enter the artist name


        if (searchQuery != null) {


            LogHelper.log("Sending intent from Main: " + searchQuery);
            Intent intent = new Intent(this, MovieSearchActivity.class);

            intent.putExtra("title_extra", searchQuery);
            startActivity(intent);
        } else
            Toast.makeText(this, "Please enter a valid, non-empty search query!! ", Toast.LENGTH_LONG).show();


        //todo call fragment


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //call instantiate item since getItem may return null depending on whether the PagerAdapter is of type FragmentPagerAdapter or FragmentStatePagerAdapter
        Fragment fragment = (Fragment) mAdapter.instantiateItem(mPager, mPager.getCurrentItem());

        if (fragment instanceof SortListener) {


            switch (id) {

                case R.id.refresh:
                    try {
                        ((SortListener) fragment).onRefresh();
                        Log.e("rfress", "refereshin");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.action_sort_title:
                    try {
                        Log.e("title", "tile");
                        ((SortListener) fragment).onSortTitle();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;


                case R.id.action_sort_date:
                    try {
                        Log.e("date", "date");
                        ((SortListener) fragment).onSortByDate();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;


                case R.id.action_sort_rating:
                    try {
                        Log.e("rating", "rating");
                        ((SortListener) fragment).onSortByRating();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }


        return super.onOptionsItemSelected(item);
    }


    //Construct an adapter

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        //Initialise string-array for tabs
        String[] tabs = getResources().getStringArray(R.array.tabs);


        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);

        }

        @Override
        public Fragment getItem(int position) {


            Fragment fragment = null;


            switch (position) {

                case TAB_HOME:
                    fragment = HomeFragment.newInstance("", "");

                    break;


                case TAB_IN_THEATERS:
                    fragment = InTheatersFragment.newInstance("", "");
                    break;

                case TAB_TOP_MOVIES:
                    fragment = TopMoviesFragment.newInstance("", "");


                    break;

                case TAB_COMING_SOON:
                    fragment = ComingSoonFragment.newInstance("", "");

                    break;

                case TAB_BOTTOM_MOVIES:
                    fragment = BottomMoviesFragment.newInstance("", "");


                    break;

            }
            return fragment;
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {

            return TAB_COUNT;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }


}
