package main.command;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.activity.MainActivity;

/**
 * Created by guillermo on 2/11/16.
 */
public class CloseApp implements CommandInterface, Serializable {

    private static final long serialVersionUID = 951358L;

    private String[] nameApp;

    public CloseApp(String[] nameApp) {
        this.nameApp = nameApp;
    }

    public CloseApp() {
    }

    public String[] getNameApp() {
        return nameApp;
    }

    public void setNameApp(String[] nameApp) {
        this.nameApp = nameApp;
    }

    @Override
    public OperationResult execute(Context applicationContext) {
        Log.e("CloseApp", "CloseApp");

        ActivityManager activity = (ActivityManager) MainApp.getCurrentActivity().getSystemService(Context.ACTIVITY_SERVICE);


        //Close list off app
        for (String app : nameApp) {
            Log.e("CloseApp", app);
            try {

                Intent startMain = new Intent(applicationContext, MainActivity.class);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                applicationContext.startActivity(startMain);
//                MainApp.getCurrentActivity().startActivity(startMain);

                activity.killBackgroundProcesses(app);
                int result = -1;
                if (activity != null) {
                    for (ActivityManager.RunningAppProcessInfo pi : activity.getRunningAppProcesses()) {
                        if (pi.processName.equalsIgnoreCase(app)) {
                            result = pi.pid;
                        }
                        if (result != -1) break;
                    }
                } else {
                    result = -1;
                }
                Process.killProcess(result);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return null;
    }


}
