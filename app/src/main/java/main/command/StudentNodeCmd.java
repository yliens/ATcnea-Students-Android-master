package main.command;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.Serializable;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.service.LockScreenService;
import uci.atcnea.student.utils.ATcneaUtil;
import uci.atcnea.student.utils.Lock;

/**
 * @class   StudentNodeCmd
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf Iniciar aplicación remota por el profesor
 * @rf Ver listado de aplicaciones remotas
 * @rf Abrir sitio web remoto
 * @rf Configurar lenguaje del terminal remoto


 */
public class StudentNodeCmd implements CommandInterface,Serializable {

    private static final long serialVersionUID = 236762L;

    private static final String TAG = "StudentNodeCmd";


    public enum ItemNodeStatus {
        RAISE_HAND,
        DOWN_HAND,
        LOCK_SCREEN,
        UNLOCK_SCREEN,
        LOCK_HAND,
        UNLOCK_HAND,
        ALLOW_HAND,
        DENY_HAND,
        LOCK_DISCONNECT,
        UNLOCK_DISCONNECT,
        LOCK_CHAT,
        UNLOCK_CHAT,
        BATTERY_REQUEST,
        BATTERY_RESPONSE
    }

    public enum BatteryResponseState {
        BATTERY_CONNECTED, BATTERY_LOW, BATTERY_LOWMIDDLE, BATTERY_MIDDLE, BATTERY_MIDDLEFULL, BATTERY_FULL, BATTERY_UNKNOWN
    }


    private ItemNodeStatus statusCode;
    private BatteryResponseState bCode;
    //private User studentCommand;

    public void setBatteryStatus(BatteryResponseState batteryStatus) {
        this.bCode = batteryStatus;
    }

    public StudentNodeCmd(ItemNodeStatus enumName) {
       // this.studentCommand = MainApp.getCurrentUser();
        this.statusCode = enumName;
    }

    public StudentNodeCmd() {

    }

    @Override
    public OperationResult execute(final Context applicationContext) {
        Log.e("MY_DEBUG", "COMMAND OKKKKKKKK");


        if (MainApp.isConnected()) {

            switch (statusCode) {
                case LOCK_SCREEN:
                    Lock.lockScreen = true;
                    applicationContext.startService(new Intent(applicationContext, LockScreenService.class));
                    break;
                case UNLOCK_SCREEN:
                    Lock.lockScreen = false;
                    applicationContext.stopService(new Intent(applicationContext, LockScreenService.class));
                    if (Lock.hand_raiced) {
                        Lock.keep_hand_down = true;
                    }
                    applicationContext.sendBroadcast(new Intent("LOCK_HAND_HOME_FRAGMENT"));
                    break;
                case LOCK_HAND:
                    Log.e("MY_DEBUG", "Hand Locked");
                    Lock.lockHand = true;
                    MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String title = applicationContext.getResources().getString(R.string.raice_hand_title);
                            String msg = applicationContext.getResources().getString(R.string.raice_hand_lock);
                            ATcneaUtil.showLockHandDialog(title, msg, false);
                            //MainApp.showMessageConfirmation(MainApp.getCurrentActivity().getResources().getString(R.string.msg_not_disconnect));
                            //   ((MainActivity) MainApp.getCurrentActivity()).showLockHandDialog(title, msg, false);
                        }
                    });
                    break;
                case UNLOCK_HAND:
                    Lock.lockHand = false;
                    applicationContext.sendBroadcast(new Intent("LOCK_HAND_LOCK_SCREEN"));
                    applicationContext.sendBroadcast(new Intent("LOCK_HAND_HOME_FRAGMENT"));
                    Log.e("MY_DEBUG", "Hand Unlocked");
                    break;
                case RAISE_HAND:
                    break;
                case ALLOW_HAND:
                    MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String title = applicationContext.getResources().getString(R.string.raice_hand_title);
                            String msg = applicationContext.getResources().getString(R.string.raice_hand_allow);
                            ATcneaUtil.ShowAllowDenyHandDialog(title, msg, true);
                            // ((MainActivity) MainApp.getCurrentActivity()).showAllowDenyHandDialog(title, msg, true);
                        }
                    });
                    Lock.lockHand = false;
                    applicationContext.sendBroadcast(new Intent("LOCK_HAND_LOCK_SCREEN"));
                    applicationContext.sendBroadcast(new Intent("LOCK_HAND_HOME_FRAGMENT"));
                    break;
                case DENY_HAND:
                    MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String title = applicationContext.getResources().getString(R.string.raice_hand_title);
                            String msg = applicationContext.getResources().getString(R.string.raice_hand_deny);

                            ATcneaUtil.ShowAllowDenyHandDialog(title, msg, false);
                            //  ((MainActivity) MainApp.getCurrentActivity()).showAllowDenyHandDialog(title, msg, false);
                        }
                    });
                    Lock.lockHand = false;
                    applicationContext.sendBroadcast(new Intent("LOCK_HAND_LOCK_SCREEN"));
                    applicationContext.sendBroadcast(new Intent("LOCK_HAND_HOME_FRAGMENT"));
                    break;
                case LOCK_CHAT:
                    MainApp.setPermitChat(false);
                    if (MainApp.getCurrentActivity() instanceof MainActivity)
                        if (MainApp.getCurrentActivity() instanceof MainActivity)
                            ((MainActivity) MainApp.getCurrentActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    ((MainActivity) MainApp.getCurrentActivity()).updateViewPermitChat();

                                }
                            });

                    break;
                case UNLOCK_CHAT:
                    MainApp.setPermitChat(true);
                    if (MainApp.getCurrentActivity() instanceof MainActivity)
                        if (MainApp.getCurrentActivity() instanceof MainActivity)
                            ((MainActivity) MainApp.getCurrentActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    ((MainActivity) MainApp.getCurrentActivity()).updateViewPermitChat();

                                }
                            });
                    Log.e(TAG, "UNLOCK_CHAT");
                    break;
                case LOCK_DISCONNECT:
                    MainApp.setPermitDisconnection(false);
                    if (MainApp.getCurrentActivity() instanceof MainActivity)
                        if (MainApp.getCurrentActivity() instanceof MainActivity)
                            ((MainActivity) MainApp.getCurrentActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    ((MainActivity) MainApp.getCurrentActivity()).updateViewPermitDisconnection();
                                    ((MainActivity) MainApp.getCurrentActivity()).invalidateOptionsMenu();

                                    String title = applicationContext.getResources().getString(R.string.disconect_title);
                                    String msg = applicationContext.getResources().getString(R.string.disconect_text);
                                    ATcneaUtil.showLockHandDialog(title, msg, false);

                                }
                            });
                    break;
                case UNLOCK_DISCONNECT:
                    MainApp.setPermitDisconnection(true);
                    if (MainApp.getCurrentActivity() instanceof MainActivity)
                        ((MainActivity) MainApp.getCurrentActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ((MainActivity) MainApp.getCurrentActivity()).updateViewPermitDisconnection();
                                ((MainActivity) MainApp.getCurrentActivity()).invalidateOptionsMenu();

                            }
                        });

                    break;
            }
        }
        return new OperationResult(OperationResult.ResultCode.OK);
    }
}
