package uci.atcnea.student.activity;


import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;


import org.greenrobot.eventbus.Subscribe;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;

public class VideoStreamingActivity extends AppCompatActivity {


    private VideoView myVideoView;


    private int position = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LibsChecker.checkVitamioLibs(this))
            return;

        //  GlobalBus.getBus().register(this);
        setContentView(R.layout.activity_video_streming);


        MainApp.setCurrentActivity(this);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);

        String path = getIntent().getExtras().getString("path", "0");
        // String path = "ss";

        if (!path.equals("0")) {

            String urlPath = "rtsp://"+MainApp.getCurrentServer().getHost()+path;

            myVideoView = (VideoView) findViewById(R.id.vitamio_videoView);

            DisplayMetrics metrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(metrics);
            android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) myVideoView.getLayoutParams();
            params.width =  metrics.widthPixels;
            params.height = metrics.heightPixels;
            params.leftMargin = 0;
            myVideoView.setLayoutParams(params);

            myVideoView.setVideoPath(urlPath);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            myVideoView.setMediaController(null);


            myVideoView.requestFocus();



            myVideoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {

                    myVideoView.isPlaying();
                    mp.start();
                }
            });
            myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    //mediaPlayer.setPlaybackSpeed(1.0f);
                    myVideoView.isPlaying();
                    mediaPlayer.start();
                }
            });

            myVideoView.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    myVideoView.isPlaying();
                    mp.start();
                }
            });

            //myVideoView.start();
/*
            //initialize the VideoView
            myVideoView = (android.widget.VideoView) findViewById(R.id.video_view);


            //urlPath = "/storage/emulated/0/ATcnea/video.mp4";

           // myVideoView.setVideoPath(urlPath);

            myVideoView.setVideoURI(Uri.parse(urlPath));

            myVideoView.start();

            myVideoView.requestFocus();*/

            //we also set an setOnPreparedListener in order to know when the video file is ready for playback

       /*     myVideoView.setOnErrorListener(this);

            myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mediaPlayer) {

                    //lose the progress bar and play the video

                    //if we have a position on savedInstanceState, the video playback should start from here

                    myVideoView.seekTo(position);

                    if (position == 0) {

                        myVideoView.start();

                    } else {
                        //if we come from a resumed activity, video playback will be paused

                        myVideoView.pause();
                    }
                }
            });

            */
        }

    }

    @Override
    protected void onResume() {
        // Toast.makeText(this, "RESUME", Toast.LENGTH_SHORT).show();

        MainApp.setCurrentActivity(this);
        //   GlobalBus.getBus().register(this);
        super.onResume();


    }
    @com.squareup.otto.Subscribe
    public void eventSTOP(Events.EventStopStreaming eventStopShareScreem){
        //   GlobalBus.getBus().unregister(VideoStreamingActivity.this);
        VideoStreamingActivity.this.finish();
        Log.e("eventStopShareScreem","eventStopShareScreem");

    }


    @Override
    protected void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GlobalBus.getBus().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //GlobalBus.getBus().unregister(this);
    }

    @Override
    public void onBackPressed() {

    }

    @com.squareup.otto.Subscribe
    public void eventDisconnect(Events.EventDisconect event) {

        //Desconectar los socket

        //Ejecutar acciones
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }
}
