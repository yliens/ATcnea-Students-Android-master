package uci.atcnea.student.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.otto.Subscribe;

import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;

public class ShareScreenActivity extends AppCompatActivity {
    ImageView shareScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_share_screen);
        shareScreen=(ImageView)findViewById(R.id.iv_share_screen);

        MainApp.getIntance().setShareScreenActivity(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onStop() {

        GlobalBus.getBus().unregister(this);
        super.onStop();
    }



    @Override
    protected void onStart() {
        GlobalBus.getBus().register(this);
        super.onStart();
    }



    @Subscribe
    public void eventDisconnect(Events.EventDisconect event) {

        //Desconectar los socket

        //Ejecutar acciones
        MainApp.getIntance().setShareScreenActivity(null);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }


    public ImageView getShareScreen() {
        return shareScreen;
    }



    //Evento de stop
    @Subscribe
    public void eventStopShareScreen(Events.EventStopShareScreem stopShareScreem){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ShareScreenActivity.this.finishAndRemoveTask();
                }
                else
                    ShareScreenActivity.this.finish();
            }
        });
        Log.e("eventStopShareScreen","eventStopShareScreen");
    }
}
