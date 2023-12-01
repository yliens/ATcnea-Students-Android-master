package uci.atcnea.student.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import uci.atcnea.student.MainApp;
import uci.atcnea.student.activity.MainActivity;

public class AplicationExitService extends Service {
    public AplicationExitService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(AplicationExitService.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
                |Intent.FLAG_ACTIVITY_TASK_ON_HOME
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        stopSelf();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (MainApp.isPermitDisconnection()) {

                    ActivityManager manager;
                    manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

                    List<ActivityManager.RunningAppProcessInfo> runningTasks = manager.getRunningAppProcesses();
                    for (ActivityManager.RunningAppProcessInfo info : runningTasks) {
                        ComponentName topActivity = info.importanceReasonComponent;


                        if (info.processName.startsWith("uci.atcnea.student")) {
                          //  Log.i("Top Activity", topActivity.getPackageName());
//                    if (MainApp.getCurrentActivity().isForceHome()) {
                        //    manager.moveTaskToFront(info.pid, ActivityManager.MOVE_TASK_WITH_HOME);
                        Intent intent = new Intent(AplicationExitService.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
                                |Intent.FLAG_ACTIVITY_TASK_ON_HOME
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
//                    }
                        }

                    }

                }
            }
        });

       // thread.start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
