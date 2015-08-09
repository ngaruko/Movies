package net.kiwigeeks.moviesondemand.activities;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.data.MovieLoader;
import net.kiwigeeks.moviesondemand.data.MoviesContract;
import net.kiwigeeks.moviesondemand.data.TopMovieLoader;


public class MoviePosterActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;
    private long mStartId;

    private long mSelectedItemId;
    private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;
    private int mTopInset;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private View mUpButtonContainer;
    private View mUpButton;
    private View mProgressbar;
    private String fragmentIdentifier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition_a));
        }
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_movie_poster);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mUpButtonContainer = findViewById(R.id.up_container);
        if (getIntent().hasExtra("fragment")) {
            fragmentIdentifier = getIntent().getStringExtra("fragment");
        }// else fragmentIdentifier="top";


        if (savedInstanceState != null)
            fragmentIdentifier = savedInstanceState.getString("fragment");

        switch (fragmentIdentifier) {
            case "theaters":
                getLoaderManager().initLoader(0, null, this);
                break;

            case "top":
                getLoaderManager().initLoader(1, null, this);
                break;
            case "coming":
                getLoaderManager().initLoader(2, null, this);
                break;
            case "bottom":
                getLoaderManager().initLoader(3, null, this);
                break;
            case "found":
                getLoaderManager().initLoader(6, null, this);
                break;


        }

        mProgressbar = findViewById(R.id.progressbar);

        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.detail_pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                mUpButtonContainer.animate().alpha((state == ViewPager.SCROLL_STATE_IDLE) ? 1f : 0f).setDuration(300);
            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                }
                if (mCursor != null) {
                    mSelectedItemId = mCursor.getLong(MovieLoader.Query._ID);
                }
                //updateUpButtonPosition();
            }
        });



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUpButtonContainer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
                @Override
                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    view.onApplyWindowInsets(windowInsets);
                    mTopInset = windowInsets.getSystemWindowInsetTop();
                    mUpButtonContainer.setTranslationY(mTopInset);


                    return windowInsets;
                }
            });
        }


        if (savedInstanceState != null && savedInstanceState.containsKey("id")) {
            mSelectedItemId = savedInstanceState.getLong("id");
        } else {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = MoviesContract.InTheater.getItemId(getIntent().getData());
                mSelectedItemId = mStartId;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("id", mSelectedItemId);
        outState.putString("fragment", fragmentIdentifier);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        switch (fragmentIdentifier) {
            case "theaters":
                return MovieLoader.newAllInTheatersMoviesInstance(this);


            case "top":
                return TopMovieLoader.newAllTopMoviesInstance(this);


            case "bottom":
                return MovieLoader.newAllBottomMoviesInstance(this);


            case "coming":
                return MovieLoader.newAllComingSoonMoviesInstance(this);

            case "found":
                return MovieLoader.newAllFoundMoviesInstance(this);


        }


        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        mProgressbar.setVisibility(View.GONE);


        mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mStartId > 0) {
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(MovieLoader.Query._ID) == mStartId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mCursor.moveToNext();
            }
            mStartId = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    public void onUpButtonFloorChanged(long itemId, MoviePosterFragment fragment) {
        if (itemId == mSelectedItemId) {
            mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            MoviePosterFragment fragment = (MoviePosterFragment) object;
            if (fragment != null) {
                mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
                //updateUpButtonPosition();
            }
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return MoviePosterFragment.newInstance(mCursor.getLong(MovieLoader.Query._ID));
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }
}
