package uci.atcnea.student.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;


import com.guo.duoduo.randomtextview.RandomTextView;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import main.command.LessonConnection;
import uci.atcnea.core.interfaces.SyncTaskResultListenerInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.core.networking.ServerObject;
import uci.atcnea.core.sockedserver.SockedDiscoveryService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;
import main.model.Lesson;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.utils.ConfigPreferences;
import uci.atcnea.student.utils.DialogBuilder;
import uci.atcnea.student.utils.LessonAdapter;
import uci.atcnea.student.utils.TaskHelper;

/**
 * @author Adrian Arencibia Herrera
 *         Copyright (c) 2016-2017 FORTES, UCI.
 * @version 1.0
 * @class LessonDiscoveryFragment
 * @date 11/07/16
 * @description
 * @rf
 */
public class LessonDiscoveryFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, RandomTextView.OnRippleViewClickListener {

    private static final String TAG = "LessonDiscoveryFragment";


    public static LessonAdapter[] lessonAdapter = new LessonAdapter[1];

    private FloatingActionButton btnServerScan;

    private boolean isScanStarted = false;

    List<String> ipProcessing;

    private Lesson selectedLesson;

    private boolean startWithDiscover;


    private RandomTextView randomTextView;
    private AlertDialog radarDialog;
    private List<Lesson> lessonList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.seleccionar_aula, null);
        GridView gridView = (GridView) viewRoot.findViewById(R.id.gridViewAulas);

        lessonAdapter[0] = new LessonAdapter(getContext());

        gridView.setAdapter(lessonAdapter[0]);
        gridView.setVisibility(View.VISIBLE);

        gridView.setOnItemClickListener(this);

        btnServerScan = (FloatingActionButton) viewRoot.findViewById(R.id.btnServerScan);
        btnServerScan.setOnClickListener(this);


        lessonList = new LinkedList<>();

        ipProcessing = new LinkedList<>();


//        if(isStartWithDiscover()) {
//            discoverServer();
//        }
        return viewRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver),
                new IntentFilter(SockedDiscoveryService.BROADCAST_DISCOVERY_ACTION)
        );

        // isScanStarted = true;

        // discoverServer();

        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);


        //  endDiscover();

        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final OperationResult result = (OperationResult) intent.getSerializableExtra(MainApp.PREFERENCE_SERVER_ADDRESS);

            addLessonResult(result);
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        selectedLesson = lessonAdapter[0].getLessons().get(position);

        lessonAdapter[0].offItem( position );

        /*getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lessonAdapter[0].notifyDataSetChanged();
            }
        });*/
        // showLoginAuthLessonPass(MainActivity.selectedLesson[0].getLessonName());

        //if(MainApp.isSendCommand()) {
        procesarSelectedLesson(selectedLesson);
        // MainApp.setSendCommand(false);
        //  }

        //startActivity(new Intent(SelectClassRoomActivity.this,Main2Activity.class));

    }

    private void procesarSelectedLesson(Lesson selectedLesson) {

        Log.i(TAG, selectedLesson.getAuthenticationType());
        if (!MainApp.checkConnectivity()) {
            MainApp.showDialogWifi();
            return;
        }
        //Update app port
        try {

            if(selectedLesson.getJsonObject().has("serverPort")) {
                ConfigPreferences.getInstance().setServerPort(Integer.parseInt(selectedLesson.getJsonObject().getString("serverPort")));
                ConfigPreferences.getInstance().setClienPort(Integer.parseInt(selectedLesson.getJsonObject().getString("clientPort")));
                ConfigPreferences.getInstance().setServerFilePort(Integer.parseInt(selectedLesson.getJsonObject().getString("fileServerPort")));
                ConfigPreferences.getInstance().setClienFilePort(Integer.parseInt(selectedLesson.getJsonObject().getString("fileClientPort")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        switch (selectedLesson.getAuthenticationType()) {
            case "authentication.automatic":
                sendMSGLogin("");
                break;
            case "authentication.automatic.permission": {
                sendMSGLogin("");
                break;
            }
            case "authentication.lesson.password": {
                showLoginAuthLessonPass(selectedLesson.getLessonName());
                break;
            }
            case "authentication.user.lms": {
                break;
            }
            default:
                break;
        }

    }

    private void dialogUserNick() {

 /*       AlertDialog.Builder builder = DialogBuilder.dialogProfile(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.add_profile, null);
        final EditText editText = (EditText) view.findViewById(R.id.et_p_name);
        builder.setView(view);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().length() > 0) {

                    MainApp.getCurrentUser().setUsername(editText.getText().toString());
                    ((MainActivity) getActivity()).addProfile(MainApp.getCurrentUser());
                    sendMSGLogin();
                    dialog.dismiss();
                }

            }
        });

        builder.setCancelable(false);

        builder.show();*/

    }

    private void sendMSGLogin(final String psw) {

        final int socketTimeOut = Integer.parseInt(getString(R.string.socket_timeout_discovery));
        final int udpPort = ConfigPreferences.getInstance().getDiscoveryPort();
        final int tcpPort = ConfigPreferences.getInstance().getServerPort();


        MainApp.setCurrentServer(new ServerObject(selectedLesson.getIpDireccion(), tcpPort));
        MainApp.setCurrentLesson(selectedLesson);

        InetAddress byAddress = null;
        try {
            byAddress = InetAddress.getByName(selectedLesson.getIpDireccion().replace("/", ""));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        final InetAddress finalByAddress = byAddress;
        TaskHelper.execute(new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    Log.i("doInBackground", "doInBackground");
                    MainApp.client.start();
                    MainApp.client.connect(5000, finalByAddress, 3030, udpPort);

                    LessonConnection sConnect = new LessonConnection(MainApp.getCurrentLesson().getAuthenticationType(), psw);
                    // connection.sendTCP(studentConnect);

                    SendMessageService s = new SendMessageService();
                    s.setCommand(sConnect);
                    s.setWaitForResponse(true);
                    s.setCallback(new SyncTaskResultListenerInterface() {
                        @Override
                        public void onSyncTaskEventCompleted(final OperationResult result, String ip) {
                            MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainApp.getIntance().autenticationProces(result);
                                }
                            });

                        }
                    });

                    TaskHelper.execute(s);

                    Log.i("doInBackground", "Log.i(\"doInBackground\",\"doInBackground\");");


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });


    }

    synchronized public boolean isAddedLesson(String ipDireccion) {
        for (Lesson lesson : lessonList) {
            if (lesson.getIpDireccion().equals(ipDireccion)) {
                return true;
            }

        }
        return false;
    }

    public void addLesson(List<Lesson> lesson) {
        if (!MainApp.isConnected()) {
            lessonAdapter[0].getLessons().clear();
            lessonAdapter[0].getLessons().addAll(lesson); //setLessons(lesson);
            lessonAdapter[0].UpdateEnableList();
            //lessonAdapter[0].getLessons().addAll(lesson);
            lessonAdapter[0].notifyDataSetChanged();
            ((MainActivity) getActivity()).updateBadge(lessonAdapter[0].getCount());
        }
    }

    public void showLoginAuthLessonPass(String lessonName) {

        // con este tema personalizado evitamos los bordes por defecto
        final Dialog customDialog = new Dialog(getContext());

        customDialog.setCancelable(false);
        View view = this.getActivity().getLayoutInflater().inflate(R.layout.activity_login, null);
        customDialog.setContentView(view);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


/*        final AutoCompleteTextView userTextView = (AutoCompleteTextView) view.findViewById(R.id.user);
        userTextView.setText(MainApp.getCurrentUser().getUsername());*/

        //userTextView.setText(activeStudent.getUserNick());

        final TextView className = (TextView) view.findViewById(R.id.className);
        className.setText("Clase:" + "\n" + lessonName);

        final TextView pswTextView = (TextView) view.findViewById(R.id.password);
        final CircleImageView cancelar = (CircleImageView) view.findViewById(R.id.btn_login_deny);
        final CircleImageView enviar = (CircleImageView) view.findViewById(R.id.btn_login_acept);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psw = pswTextView.getText().toString();
                if(!psw.equals("")) {
                    sendMSGLogin(psw);
                    customDialog.dismiss();
                }else{
                    pswTextView.setError(MainApp.getCurrentActivity().getResources().getString(R.string.error_not_null));
                }
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
//        customDialog.setPositiveButton(R.string.login_btn, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                /*String user = userTextView.getText().toString();*/
//               /* MainApp.getCurrentUser().setUsername(user);*/
//                String psw = pswTextView.getText().toString();
//               // MainApp.getCurrentUser().setPassword(psw);
//               // ((MainActivity) getActivity()).addProfile(MainApp.getCurrentUser());
//                sendMSGLogin(psw);
//                dialog.dismiss();
//            }
//        });
//
//        customDialog.setNegativeButton(R.string.negativeButton, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                dialog.dismiss();
//            }
//        });


        customDialog.show();
    }

    private void discoverServer() {

        if (MainApp.getDialogRadar() == null) {
            lessonList.clear();
            AlertDialog.Builder b = new AlertDialog.Builder(getContext());
            View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_radar, null);
            randomTextView = (RandomTextView) v.findViewById(R.id.random_textview);
            randomTextView.setOnRippleViewClickListener(this);

            b.setView(v);
            b.setCancelable(true);
            b.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    btnServerScan.show();
                    GlobalBus.getBus().post(new Events.EventStopDiscover());
                }
            });
            radarDialog = b.create();
            radarDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            radarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            MainApp.setDialogRadar(radarDialog);
            MainApp.getDialogRadar().show();
            btnServerScan.hide();

            lessonAdapter[0].getLessons().clear();
            lessonAdapter[0].notifyDataSetChanged();
            ((MainActivity) getActivity()).updateBadge(lessonAdapter[0].getCount());

            final Intent service = new Intent(getActivity(), SockedDiscoveryService.class);

            getActivity().startService(service);
        }

    }

    /*private void autenticationProces(OperationResult result) {

        switch (result.getCode()) {
            case OK:
                //MainApp.setCurrentServer(new ServerObject(selectedLesson.getIpDireccion(), Integer.parseInt(getResources().getString(R.string.server_port))));
                MainApp.setCurrentLesson(selectedLesson);
                ((MainActivity) getActivity()).connectToLesson();//setUpDrawerConnectConfig();
                Log.e(TAG, "Result OK");


                break;
            case WAITING_CONFIRMATION:

                MainApp.setWaitingConfirmation(selectedLesson);
                showDialogProgres();

                Log.e(TAG, "Result WAITING_CONFIRMATION");
                break;
            case CANCELLED:
                DialogBuilder.dialogInformation(getContext(), getString(R.string.cancelled)).show();
                Log.e(TAG, "Result CANCELLED");
                break;
            case FULL_ERROR:
                Log.e(TAG, "Result FULL_ERROR");
                DialogBuilder.dialogInformation(getContext(), getString(R.string.full_error)).show();
                break;
            case AUTHENTICATION_ERROR:
                Log.e(TAG, "Result AUTHENTICATION_ERROR");
                DialogBuilder.dialogInformation(getContext(), getString(R.string.authentication_error)).show();

                break;
            case CONFLICT:
                Log.e(TAG, "Result CONFLICT");
                DialogBuilder.dialogInformation(getContext(), getString(R.string.conflict)).show();

                break;
            default:
                Log.e(TAG, "Result " + result.getCode());
                break;

        }


        MainApp.setSendCommand(true);

    }*/

    private void showDialogProgres() {
        AlertDialog.Builder builder = DialogBuilder.showDialogProgres(getContext());
        AlertDialog alertDialog = builder.create();
        MainApp.setDialogProgress(alertDialog);
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnServerScan:

                final Intent service = new Intent(getActivity(), SockedDiscoveryService.class);

                getActivity().stopService(service);
                discoverServer();


                break;

            default:
                break;
        }

    }

    public void endDiscover() {
        try {
            if (MainApp.getDialogRadar() != null) {
                MainApp.getDialogRadar().cancel();

            }
            MainApp.setDialogRadar(null);
            btnServerScan.show();
            addLesson(lessonList);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }


    public void addLessonResult(OperationResult result) {
        if (result.isSuccess()) {
            for (Object info : result.getData()) {

                final JSONObject tmpInfo = (JSONObject) info;
                String hostName = "";

                Log.d("TESTTTT",tmpInfo.toString());

                try {
                    hostName = tmpInfo.getString("host");
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }

                try {

                    if (!isAddedLesson(hostName)) {
                        if (result.getData() != null && result.getData().size() > 0) {
                            byte[] imageT = new byte[0];
                            try {
                                if (tmpInfo.has("teacherImage"))
                                    imageT = Base64.decode(tmpInfo.getString("teacherImage"), Base64.DEFAULT);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // if(result.getData().size()>=2 && result.getData().get(1)!=null)
                            //    imageT=((LessonInformation.ImageTeacher)result.getData().get(1)).getImageTeacher();


                            Lesson lesson = null;
                            try {
                                //acomodar este atributo a Lesson
                                String description = tmpInfo.getString("lessonDescription");
                                lesson = new Lesson(hostName, tmpInfo.getString("lessonName"), tmpInfo.getString("capacityState"), tmpInfo.getString("teacherName"), tmpInfo.getString("authenticationType"), imageT);
                                lesson.setJsonObject(tmpInfo);
                                lesson.setLessonDescription(description);
                                if (tmpInfo.has("teacherMac"))
                                    lesson.setMacDireccion(tmpInfo.getString("teacherMac"));
                                if (tmpInfo.has("lessonID"))
                                    lesson.setIdLesson(tmpInfo.getLong("lessonID"));
                                if (tmpInfo.has("lessonColor"))
                                    lesson.setLessonColor("#" + tmpInfo.getString("lessonColor"));

                                Log.e("Lesson FOUND", lesson.getTeacherName() + " " + lesson.getLessonName());
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                            }

                            //com.guo.duoduo.randomtextview.Lesson lview=new com.guo.duoduo.randomtextview.Lesson(ip, data.get("lessonName"), data.get("capacityState"), data.get("teacherName"), data.get("authenticationType"),imageT );
                            //addLesson(lesson);
                            randomTextView.addKeyWord(lesson);
                            lessonList.add(lesson);
                            randomTextView.show();
                        } else {
                            endDiscover();
                        }


                  /*  SendMessageService msg = new SendMessageService(new ServerObject(hostName, Integer.parseInt(getResources().getString(R.string.server_port))));
                    final LessonInformation information = new LessonInformation();
                    msg.setCommand(information);

                    msg.setCallback(new SyncTaskResultListenerInterface() {
                        @Override
                        public void onSyncTaskEventCompleted(OperationResult result, String ip) {

                            if (!isAddedLesson(ip)) {
                                if (result.getData() != null && result.getData().size() > 0) {

                                    Map<String, String> data = (HashMap) result.getData().get(0);
                                    byte[] imageT=new byte[0];
                                    if(result.getData().size()>=2 && result.getData().get(1)!=null)
                                        imageT=((LessonInformation.ImageTeacher)result.getData().get(1)).getImageTeacher();


                                    Lesson lesson = new Lesson(ip, data.get("lessonName"), data.get("capacityState"), data.get("teacherName"), data.get("authenticationType"),imageT );
                                    lessonList.add(lesson);
                                    //com.guo.duoduo.randomtextview.Lesson lview=new com.guo.duoduo.randomtextview.Lesson(ip, data.get("lessonName"), data.get("capacityState"), data.get("teacherName"), data.get("authenticationType"),imageT );
                                    //addLesson(lesson);
                                    randomTextView.addKeyWord(lesson);
                                    randomTextView.show();
                                    Log.i("RESPUESTA::::::", result.getData().get(0).toString());
                                }else{
                                    endDiscover();
                                }

                                // isScanStarted = false;
                            }
                        }
                    });
                    TaskHelper.execute(msg);
                    */
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        } else {
            endDiscover();
        }
    }

    @Override
    public void onRippleViewClicked(View view, com.guo.duoduo.randomtextview.Lesson lesson) {
        if (MainApp.isSendCommand()) {
            selectedLesson = (Lesson) lesson;
            // showLoginAuthLessonPass(MainActivity.selectedLesson[0].getLessonName());
            endDiscover();
            procesarSelectedLesson(selectedLesson);
            MainApp.setSendCommand(false);
            //Toast.makeText(getContext(), lesson.getLessonName(), Toast.LENGTH_LONG).show();
        }
    }


    public boolean isStartWithDiscover() {
        return startWithDiscover;
    }

    public void setStartWithDiscover(boolean startWithDiscover) {
        this.startWithDiscover = startWithDiscover;
    }
}
