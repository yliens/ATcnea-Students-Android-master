package main.command;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.Serializable;

import main.model.User;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.service.LockScreenService;
import uci.atcnea.student.utils.ATcneaUtil;
import uci.atcnea.student.utils.DialogBuilder;
import uci.atcnea.student.utils.Lock;

/**
 * @class   LessonDisconnection
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf Terminar sesión
 */
public class LessonDisconnection implements CommandInterface,Serializable {

    private static final long serialVersionUID = 23666762L;

    private static final String TAG = "LessonDisconnection";

    public enum Type{
        CLOSE_APP,CLOSE_LESSON,ESTUDENT_DISCONET,WRONG_CONNECTION
    }
    public enum DisconnectType{
        FORCE, AUTHORIZE,NOT_AUTHORIZE,DELETED
    }

    private Type typeDisconection;
    private DisconnectType disconnectStatus;

    public LessonDisconnection() {
    }


    /**
     *
     * @param applicationContext
     * @return
     */
    @Override
    public OperationResult execute(Context applicationContext) {

        Log.e(TAG, "LessonDisconnection");

        if (!getDisconnectStatus().equals(DisconnectType.NOT_AUTHORIZE)){
            Lock.hand_raiced = false;
            Lock.lockScreen = false;
            Lock.keep_hand_down = false;
            Lock.lockHand = false;
        }

        switch (getDisconnectStatus()) {
            case AUTHORIZE:
                //Unlock Screen
                if (Lock.lockScreen) {
                    applicationContext.stopService(new Intent(applicationContext, LockScreenService.class));
                }

                GlobalBus.getBus().post( new Events.EventDisconect() );

                Log.e(TAG, "TRUE");
            break;
            case DELETED:
                ATcneaUtil.ShowMessageConfirmation(MainApp.getCurrentActivity().getResources().getString(R.string.msg_not_disconnect_delete));
                GlobalBus.getBus().post( new Events.EventDisconect(true) );
                break;
            case NOT_AUTHORIZE:

                MainApp.setWaitingConfirmation(null);
                MainApp.getDialogProgress().cancel();
                MainApp.setDialogProgress(null);

                ATcneaUtil.ShowMessageConfirmation(MainApp.getCurrentActivity().getResources().getString(R.string.msg_not_disconnect));

                Log.e(TAG, "FALSE");
                break;
            case FORCE:
                boolean startWithDiscover = true;
                    switch (getTypeDisconection()) {
                        case CLOSE_APP:

                            ATcneaUtil.ShowMessageConfirmation(MainApp.getAppContext().getString(R.string.msg_app_close));

                            startWithDiscover = false;
                            break;
                        case WRONG_CONNECTION:

                            ATcneaUtil.ShowMessageConfirmation(MainApp.getAppContext().getString(R.string.msg_wrong_conection));

                            startWithDiscover = false;
                            break;
                        case CLOSE_LESSON:

                            ATcneaUtil.ShowMessageConfirmation(MainApp.getAppContext().getString(R.string.msg_lesson_close));

                            startWithDiscover = false;
                            break;
                        case ESTUDENT_DISCONET:

                            ATcneaUtil.ShowMessageConfirmation(MainApp.getAppContext().getString(R.string.msg_estudent_close));

                            startWithDiscover = false;
                            break;

                    }


                GlobalBus.getBus().post( new Events.EventDisconect(true) );

                break;
        }

        return null;
    }

    public Type getTypeDisconection() {
        return typeDisconection;
    }

    public void setTypeDisconection(Type typeDisconection) {
        this.typeDisconection = typeDisconection;
    }

    public DisconnectType getDisconnectStatus() {
        return disconnectStatus;
    }

    public void setDisconnectStatus(DisconnectType disconnectStatus) {
        this.disconnectStatus = disconnectStatus;
    }
}
