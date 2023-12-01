package uci.atcnea.student.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import de.hdodenhof.circleimageview.CircleImageView;
import main.command.StudentNodeCmd;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.utils.DbBitmapUtility;
import uci.atcnea.student.utils.DialogBuilder;
import uci.atcnea.student.utils.Lock;
import uci.atcnea.student.utils.TaskHelper;

/**
 * @author Adrian Arencibia Herrera
 *         Copyright (c) 2016-2017 FORTES, UCI.
 * @version 1.0
 * @class HomeFragment
 * @date 11/07/16
 * @description
 * @rf
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    TextView tvTeacher;
    TextView tvLesson;
    TextView textHand;
    TextView textDescription;
    //    AppCompatButton btnDisconnect;
    ImageButton btnRaiseHand;
    ImageButton btnDownHand;
    CircleImageView imgProfesor;
    ImageView imgClass;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.conected_home, null);

        tvTeacher = (TextView) root.findViewById(R.id.textViewTeacher);
        tvLesson = (TextView) root.findViewById(R.id.textViewAulaName);
        textDescription = (TextView) root.findViewById(R.id.tx_description);
        imgProfesor = (CircleImageView) root.findViewById(R.id.img_profesor);
        imgClass = (ImageView) root.findViewById(R.id.img_class);
//        btnDisconnect = (AppCompatButton) root.findViewById(R.id.btn_disconect);

        tvLesson.setText(MainApp.getCurrentLesson().getLessonName());
        tvTeacher.setText(MainApp.getCurrentLesson().getTeacherName());
        textDescription.setText(MainApp.getCurrentLesson().getLessonDescription());
        if (MainApp.getCurrentLesson().getImageTeacher().length > 2)
            imgProfesor.setImageBitmap(DbBitmapUtility.getImage(MainApp.getCurrentLesson().getImageTeacher()));
        if(MainApp.getCurrentLesson().getLessonColor()!=null){
            imgClass.setBackgroundColor(Color.parseColor(MainApp.getCurrentLesson().getLessonColor()));
        }
//        btnDisconnect.setOnClickListener(this);

        btnRaiseHand = (ImageButton) root.findViewById(R.id.btn_hand_rice);
        btnDownHand = (ImageButton) root.findViewById(R.id.btn_hand_down);
        textHand = (TextView) root.findViewById(R.id.text_hand);

        btnRaiseHand.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Sending cmd RISE_HAND
                SendMessageService msg = new SendMessageService(MainApp.getCurrentServer());
                StudentNodeCmd lock = new StudentNodeCmd(StudentNodeCmd.ItemNodeStatus.RAISE_HAND);
                msg.setWaitForResponse(true);
                msg.setCommand(lock);
                TaskHelper.execute(msg);
//                msg.execute();
                Log.e("MY_DEBUG", "RAISE HAND SEND");
                btnRaiseHand.setVisibility(View.GONE);
                btnDownHand.setVisibility(View.VISIBLE);
                textHand.setText(getString(R.string.down_hand_title));
                Lock.hand_raiced = true;
            }
        });


        btnDownHand.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Sending cmd DOWN_HAND
                SendMessageService msg = new SendMessageService(MainApp.getCurrentServer());
                StudentNodeCmd lock = new StudentNodeCmd(StudentNodeCmd.ItemNodeStatus.DOWN_HAND);
                msg.setWaitForResponse(true);
                msg.setCommand(lock);
                TaskHelper.execute(msg);
//                msg.execute();
                Log.e("MY_DEBUG", "DOWN HAND SEND");
                btnDownHand.setVisibility(View.GONE);
                btnRaiseHand.setVisibility(View.VISIBLE);
                textHand.setText(getString(R.string.raice_hand_title));
                Lock.hand_raiced = false;

            }
        });

        if (Lock.hand_raiced) {
            btnRaiseHand.setVisibility(View.GONE);
            btnDownHand.setVisibility(View.VISIBLE);
            textHand.setText(getString(R.string.down_hand_title));
        } else {
            btnDownHand.setVisibility(View.GONE);
            btnRaiseHand.setVisibility(View.VISIBLE);
            textHand.setText(getString(R.string.raice_hand_title));
        }

        if (Lock.lockHand) {
            btnDownHand.setVisibility(View.GONE);
            btnRaiseHand.setVisibility(View.VISIBLE);
            textHand.setText(getString(R.string.raice_hand_title));
            btnRaiseHand.setEnabled(false);
//            Lock.hand_raiced = false;
        } else {
            btnRaiseHand.setEnabled(true);
            btnDownHand.setEnabled(true);
//            Lock.hand_raiced = false;
        }

        updateViewPermitDisconnection();

         /*   if (MainApp.hand_broadcast_register) {
             getActivity().unregisterReceiver(lockHand);
             getActivity().registerReceiver(lockHand, new IntentFilter("LOCK_HAND_HOME_FRAGMENT"));
                MainApp.hand_broadcast_register = true;
            } else {
                        getActivity().registerReceiver(lockHand, new IntentFilter("LOCK_HAND_HOME_FRAGMENT"));
                MainApp.hand_broadcast_register = true;
            }
        */
        MainApp.sendBatteryLevel();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        GlobalBus.getBus().unregister(this);
    }

    @Override
    public void onResume() {

        getActivity().registerReceiver(lockHand, new IntentFilter("LOCK_HAND_HOME_FRAGMENT"));
        MainApp.hand_broadcast_register = true;

        super.onResume();
        // if (MainApp.hand_broadcast_register) {
        //     getActivity().unregisterReceiver(lockHand);

        //     getActivity().registerReceiver(lockHand, new IntentFilter("LOCK_HAND_HOME_FRAGMENT"));
        //     MainApp.hand_broadcast_register = true;
        // } else {
        //     getActivity().registerReceiver(lockHand, new IntentFilter("LOCK_HAND_HOME_FRAGMENT"));
        //    MainApp.hand_broadcast_register = true;
        // }
    }

    @Override
    public void onPause() {

        getActivity().unregisterReceiver(lockHand);
        MainApp.hand_broadcast_register = false;

        super.onPause();

    }

    @Override
    public void onClick(View v) {
//        Para quitarse
        /*if (MainApp.isPermitDisconnection()) {
            ((MainActivity) getActivity()).disconnectToLesson();

            //limpiar listado del timelist
            MainApp.SumaryList.clear();
            //end timeline

        } else {
            updateViewPermitDisconnection();
            DialogBuilder.dialogInformation(getContext(), getResources().getString(R.string.msg_not_disconnect));
        }*/
    }

    //arreglar
    private final BroadcastReceiver lockHand = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Lock.lockHand) {
                btnRaiseHand.setEnabled(false);
            } else {
                btnRaiseHand.setEnabled(true);
            }
            if (Lock.keep_hand_down) {
                btnRaiseHand.setVisibility(View.GONE);
                btnDownHand.setVisibility(View.VISIBLE);
                textHand.setText(getString(R.string.down_hand_title));
                Lock.hand_raiced = true;
                Lock.keep_hand_down = false;
            } else {
                btnDownHand.setVisibility(View.GONE);
                btnRaiseHand.setVisibility(View.VISIBLE);
                textHand.setText(getString(R.string.raice_hand_title));
                Lock.hand_raiced = false;
            }
        }
    };

    public void updateViewPermitDisconnection() {
//        btnDisconnect.setEnabled(MainApp.isPermitDisconnection());

    }

    @Subscribe
    public synchronized void eventBoard(final Events.EventTeacherChange event){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MainApp.getCurrentLesson().getImageTeacher().length > 2)
                    imgProfesor.setImageBitmap(DbBitmapUtility.getImage(MainApp.getCurrentLesson().getImageTeacher()));
            }
        });
    }

}
