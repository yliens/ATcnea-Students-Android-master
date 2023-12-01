package main.command;

import android.content.Context;

import java.io.Serializable;

import main.model.User;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;

/**
 * @class   NotifyChat
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf Enviar notificaciones a los estudiantes


 */
public class NotifyChat implements CommandInterface,Serializable {

    private static final long serialVersionUID = 2122L;
    private String message;
    private String type;
    private User studentCommand;
    public NotifyChat() {
     this.studentCommand = MainApp.getCurrentUser();
    }

    public NotifyChat(String message, String type) {
        this.message = message;
        this.type = type;
    }

    /**
     *
     * @param applicationContext
     * @return
     */
    @Override
    public OperationResult execute(Context applicationContext) {

            MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainApp.showNotification(message);

                }
            });


        return null;
    }
}
