package uci.atcnea.student.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.otto.Subscribe;

import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.utils.QuizAdapter;

/**
 * Created by guillermo on 9/11/16.
 */
public class PresentationActivity extends AppCompatActivity {

    private ImageView presentation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_presentation );

        //Registrar el activity actual en la base global
        MainApp.setCurrentActivity(this);

        //Registrar para recibir los broadcast


        presentation = (ImageView) findViewById( R.id.img_presentation );

        //Mostrar imagen inicial
        byte img[] = getIntent().getByteArrayExtra("IMG");
        presentation.setImageBitmap( BitmapFactory.decodeByteArray(img,0,img.length) );

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    /**
     * Evento de recibir alguna accion del servidor
     */
    private final BroadcastReceiver result = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("Presentation",intent.getAction());

            if(intent.hasExtra("IMG")){

                byte[]img = intent.getByteArrayExtra("IMG");

                presentation.setImageBitmap(BitmapFactory.decodeByteArray(img,0,img.length));

            }else if(intent.hasExtra("CLOSE")){
                finish();
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(result, new IntentFilter("PRESENTATION"));

    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(result);

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

    @Subscribe
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

    @Override
    public void onBackPressed() {

    }
}
