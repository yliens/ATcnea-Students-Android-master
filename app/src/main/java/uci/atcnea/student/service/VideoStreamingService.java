package uci.atcnea.student.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import main.command.StudentNodeCmd;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.utils.Lock;
import uci.atcnea.student.utils.TaskHelper;

/**
 * Created by koyi on 11/05/16.
 */
public class VideoStreamingService extends Service {
    View mView;
    LayoutInflater inflate;
    WindowManager wm;
    KeyguardManager km;
    KeyguardManager.KeyguardLock kl;

    private VideoView myVideoView;

    private int position = 0;


    private MediaController mediaControls;

    private String path;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

         path = intent.getExtras().getString("path", "0");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {
        super.onCreate();


        //   Toast.makeText(getBaseContext(), "onCreate", Toast.LENGTH_LONG).show();


        wm = (WindowManager) getSystemService(WINDOW_SERVICE);


        Display display = wm.getDefaultDisplay();  //get phone display size
        int width = display.getWidth();  // deprecated - get phone display width
        int height = display.getHeight(); // deprecated - get phone display height


        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);



//                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
//                PixelFormat.TRANSPARENT);


        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = height;
        params.gravity = Gravity.TOP;


//        params.gravity = Gravity.LEFT | Gravity.CENTER;
        params.setTitle("Load Average");

        inflate = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mView = inflate.inflate(R.layout.activity_video_streming, null);






        // String path = "ss";

        if (!path.equals("0")) {

            if (mediaControls == null) {

                mediaControls = new MediaController(this);

            }

            //initialize the VideoView
            myVideoView = (VideoView) mView.findViewById(R.id.video_view);

            try {
                myVideoView.setVideoPath("rtsp://@" + path);

                //myVideoView.setVideoPath("rtsp://@" + path + "/atcnea");

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            myVideoView.requestFocus();

            //we also set an setOnPreparedListener in order to know when the video file is ready for playback

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
        }



        mView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        wm.addView(mView, params);

//        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
//                | PowerManager.ACQUIRE_CAUSES_WAKEUP
//                | PowerManager.ON_AFTER_RELEASE, "INFO");
//        wl.acquire();
//
        km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("name");
        kl.disableKeyguard();



    }

    @Override
    public void onDestroy() {
        kl.reenableKeyguard();

        wm.removeView(mView);

        super.onDestroy();
    }



    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }




}