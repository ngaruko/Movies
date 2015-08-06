package net.kiwigeeks.moviesondemand.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.kiwigeeks.moviesondemand.Callbacks.MovieLoadedListener;
import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.data.Movie;
import net.kiwigeeks.moviesondemand.utilities.LogHelper;

public class MovieSearchActivity extends AppCompatActivity implements MovieLoadedListener {


    public Movie mMovie;
    private View mProgressbar;

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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }


        setContentView(R.layout.activity_movie_search);
//mProgressbar=findViewById(R.id.progressbar);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_movie_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieLoded(Movie movie) {
//mProgressbar.setVisibility(View.GONE);
        mMovie = new Movie();


        LogHelper.log("Movie laoded!");
    }
}
