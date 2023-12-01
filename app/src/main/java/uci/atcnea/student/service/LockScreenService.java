package uci.atcnea.student.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.otto.Subscribe;

import main.command.StudentNodeCmd;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.utils.Lock;
import uci.atcnea.student.utils.TaskHelper;

/**
 * Created by koyi on 11/05/16.
 */
public class LockScreenService extends Service {
    View mView;

    LayoutInflater inflate;
    ImageButton btn_raice;
    ImageButton btn_down;
    TextView text_hand;
    Button btn_ok;
    WindowManager wm;
    KeyguardManager km;
    KeyguardManager.KeyguardLock kl;
    LinearLayout hand_msg_layout;


    @Override
    public void onCreate() {
        super.onCreate();


        //Toast.makeText(getBaseContext(), "onCreate", Toast.LENGTH_LONG).show();


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

        mView = inflate.inflate(R.layout.lock_screen, null);

        hand_msg_layout = (LinearLayout) mView.findViewById(R.id.hand_msg_layout);
        btn_ok = (Button) mView.findViewById(R.id.btn_ok);

        btn_raice = (ImageButton) mView.findViewById(R.id.btn_hand_rice);
        btn_down = (ImageButton) mView.findViewById(R.id.btn_hand_down);
        text_hand = (TextView) mView.findViewById(R.id.text_hand);

        btn_raice.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Sending cmd RISE_HAND
                hand_msg_layout.setVisibility(View.GONE);
                SendMessageService msg = new SendMessageService(MainApp.getCurrentServer());
                StudentNodeCmd lock = new StudentNodeCmd(StudentNodeCmd.ItemNodeStatus.RAISE_HAND);
                msg.setWaitForResponse(true);
                msg.setCommand(lock);
                TaskHelper.execute(msg);
//                msg.execute();
                Log.e("MY_DEBUG", "RAISE HAND SEND");
                btn_raice.setVisibility(View.GONE);
                btn_down.setVisibility(View.VISIBLE);
                text_hand.setText(getString(R.string.down_hand_title));
                Lock.hand_raiced = true;
            }
        });

        btn_down.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Sending cmd DOWN_HAND
                hand_msg_layout.setVisibility(View.GONE);
                SendMessageService msg = new SendMessageService(MainApp.getCurrentServer());
                StudentNodeCmd lock = new StudentNodeCmd(StudentNodeCmd.ItemNodeStatus.DOWN_HAND);
                msg.setWaitForResponse(true);
                msg.setCommand(lock);
                TaskHelper.execute(msg);
//                msg.execute();
                Log.e("MY_DEBUG", "RAISE HAND SEND");
                btn_down.setVisibility(View.GONE);
                btn_raice.setVisibility(View.VISIBLE);
                text_hand.setText(getString(R.string.raice_hand_title));
                Lock.hand_raiced = false;
            }
        });

        if (Lock.hand_raiced){
            btn_raice.setVisibility(View.GONE);
            btn_down.setVisibility(View.VISIBLE);
            text_hand.setText(getString(R.string.down_hand_title));
        }else{
            btn_down.setVisibility(View.GONE);
            btn_raice.setVisibility(View.VISIBLE);
            text_hand.setText(getString(R.string.raice_hand_title));
        }

        if (Lock.lockHand) {
            btn_down.setVisibility(View.GONE);
            btn_raice.setVisibility(View.VISIBLE);
            text_hand.setText(getString(R.string.raice_hand_title));
            btn_raice.setEnabled(false);
            Lock.hand_raiced = false;
        } else {
            btn_raice.setEnabled(true);
            btn_down.setEnabled(true);
//            Lock.hand_raiced = false;
        }

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hand_msg_layout.setVisibility(View.GONE);
            }
        });

        TextView text = (TextView) mView.findViewById(R.id.title);
//        text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopSelf();
//            }
//        });

//        if (Build.VERSION.SDK_INT > 14){
//            mView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
//        }else {
//            mView.setSystemUiVisibility(View.FLAG_FULLSCREEN);
//        }

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

        registerReceiver(lockHand, new IntentFilter("LOCK_HAND_LOCK_SCREEN"));
        registerReceiver(msgHand, new IntentFilter("MSG_HAND_LOCK_SCREEN"));
//        new CheckLockHand().start();

    }

    @Override
    public void onDestroy() {
        kl.reenableKeyguard();
        unregisterReceiver(lockHand);
        unregisterReceiver(msgHand);
        wm.removeView(mView);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    private final BroadcastReceiver lockHand = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (Lock.lockHand) {
                btn_raice.setEnabled(false);
            } else {
                btn_raice.setEnabled(true);
            }
            btn_down.setVisibility(View.GONE);
            btn_raice.setVisibility(View.VISIBLE);
            text_hand.setText(getString(R.string.raice_hand_title));
            Lock.hand_raiced = false;
        }
    };

    private final BroadcastReceiver msgHand = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean good = intent.getBooleanExtra("GOOD_HAND", false);
            String msg = intent.getStringExtra("MSG_HAND");

            TextView text = (TextView) mView.findViewById(R.id.text_msg);
            ImageView img = (ImageView) mView.findViewById(R.id.img_good);

            text.setText(msg);
            if (good){
                img.setImageResource(R.drawable.ic_accept);
            }else {
                img.setImageResource(R.drawable.ic_deny);
            }

            hand_msg_layout.setVisibility(View.VISIBLE);

            YoYo.with(Techniques.Shake)
                    .duration(800)
                    .playOn(hand_msg_layout);
        }
    };

    private class CheckLockHand extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                YoYo.with(Techniques.Shake)
                        .duration(700)
                        .playOn(hand_msg_layout);
            }
        }
    }
}