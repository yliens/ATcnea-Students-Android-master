package uci.atcnea.student.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.squareup.otto.Subscribe;

import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.fragment.IQMultipleChoiceFragment;
import uci.atcnea.student.fragment.IQSimpleChoiceFragment;
import uci.atcnea.student.fragment.IQTrueFalseFragment;
import uci.atcnea.student.fragment.IQVerbalFragment;
import uci.atcnea.student.interfaces.InteractiveQuestionBase;
import uci.atcnea.student.service.LockScreenService;
import uci.atcnea.student.utils.ATcneaUtil;
import uci.atcnea.student.utils.Lock;

public class InteractiveQuestionActivity extends AppCompatActivity {

    /*private Fragment multiple;
    private Fragment simple;
    private Fragment true_false;
    private Fragment verbal;*/
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive_question);

        MainApp.setCurrentActivity(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (ATcneaUtil.question.getType()) {
            case MULTIPLE_CHOICE:
                fragment = new IQMultipleChoiceFragment();
                ft.replace(R.id.fragment_question, fragment);
                ft.commit();
                break;
            case SIMPLE_CHOICE:
                fragment = new IQSimpleChoiceFragment();
                ft.replace(R.id.fragment_question, fragment);
                ft.commit();
                break;
            case TRUE_FALSE:
                fragment = new IQTrueFalseFragment();
                ft.replace(R.id.fragment_question, fragment);
                ft.commit();
                break;
            case VERBAL_QUESTION:
                fragment = new IQVerbalFragment();
                ft.replace(R.id.fragment_question, fragment);
                ft.commit();
                break;
        }

    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        // Toast.makeText(this, "RESUME", Toast.LENGTH_SHORT).show();

        MainApp.setCurrentActivity(this);
        super.onResume();
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

        //Limpiar los hilos creados
        ((InteractiveQuestionBase)fragment).ClearFragment();

        //Cerrar activity
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }
}
