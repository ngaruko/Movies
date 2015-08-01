package net.kiwigeeks.moviesondemand.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.VideoView;

import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.utilities.LogHelper;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final String URL_EXTRA ="url_extra" ;
    String TAG = "com.ebookfrenzy.videoplayer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        final VideoView videoView =
                (VideoView) findViewById(R.id.videoView);


        String link="";//http://www.imdb.com/video/imdb/vi2306715673/imdb/single?vPage=1";

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(URL_EXTRA)) {

            link = intent.getStringExtra(URL_EXTRA);


            LogHelper.log("video received: " + URL_EXTRA);


        }

        Uri videoUri=Uri.parse(link);

       try{     videoView.setVideoURI(videoUri);

    } catch (Exception e) {
        Log.e("Video Error", e.getMessage() + e.toString() + e.getLocalizedMessage());

        e.printStackTrace();
    }
//            MediaController mediaController = new
//                    MediaController(this);
//            mediaController.setAnchorView(videoView);
//            videoView.setMediaController(mediaController);

//        videoView.setOnPreparedListener(new
//                                                MediaPlayer.OnPreparedListener()  {
//                                                    @Override
//                                                    public void onPrepared(MediaPlayer mp) {
//                                                        Log.i(TAG, "Duration = " +
//                                                                videoView.getDuration());
//                                                    }
//                                                });

        try {  videoView.start();
        } catch (Exception e) {
            Log.e("Video Error", e.getMessage() + e.toString() + e.getLocalizedMessage());

            e.printStackTrace();
        }

    }

}