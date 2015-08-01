package net.kiwigeeks.moviesondemand.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;

import net.kiwigeeks.moviesondemand.Callbacks.MovieLoadedListener;
import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.data.Movie;
import net.kiwigeeks.moviesondemand.utilities.LogHelper;

public class MovieSearchActivity extends AppCompatActivity implements MovieLoadedListener{


    private static final String TITLE_EXTRA ="title_extra" ;
    private String mTitle;
    public Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            TransitionInflater inflater = TransitionInflater.from(this);
            Transition transition = inflater.inflateTransition(R.transition.content_transition_a);
            getWindow().setExitTransition(transition);
            Slide slide = new Slide();
            slide.setDuration(5000);
            getWindow().setReenterTransition(slide);
        }



        setContentView(R.layout.activity_movie_search);

//        Intent intent = getIntent();
//        if (intent != null && intent.hasExtra(TITLE_EXTRA)) {
//
//            mTitle = intent.getStringExtra(TITLE_EXTRA);
//        new FetchMovieTask(this).execute(mTitle);
//
//            LogHelper.log("Intent received: " + mTitle);
//
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieLoded(Movie movie) {

        mMovie=new Movie();

//    mMovie.setTitle(movie.getTitle());
//    mMovie.setPlot(movie.getPlot());

    LogHelper.log("Movie laoded!");
    }
}
