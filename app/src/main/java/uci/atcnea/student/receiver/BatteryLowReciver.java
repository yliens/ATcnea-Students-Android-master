package uci.atcnea.student.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Handler;
import android.util.Log;

import main.command.StudentNodeCmd;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.utils.TaskHelper;

/**
 * Created by koyi on 8/06/16.
 */
public class BatteryLowReciver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (MainApp.isConnected()) {
            MainApp.sendBatteryLevel();
        }
    }
}
