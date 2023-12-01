package uci.atcnea.student.utils;

import android.os.Bundle;
import android.os.Message;

import uci.atcnea.student.MainApp;

/**
 * @class   SendHandlerMessage
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public class SendHandlerMessage {

    static public void sendErrorMessage(int tagValue, String msg) {
        Bundle b = new Bundle();
        b.putInt(ConfigParam.TAG_ERROR, tagValue);
        b.putString(ConfigParam.MSG_ERROR, msg);

        Message msgObj = MainApp.myHandler.obtainMessage();
        msgObj.setData(b);
        MainApp.myHandler.sendMessage(msgObj);
    }

    static public void sendSuccesMessage(String msg) {
        Bundle b = new Bundle();
        b.putInt(ConfigParam.TAG_SUCCESS, 1);
        b.putString(ConfigParam.MSG_SUCCES, msg);

        Message msgObj = MainApp.myHandler.obtainMessage();
        msgObj.setData(b);
        MainApp.myHandler.sendMessage(msgObj);
    }
}
