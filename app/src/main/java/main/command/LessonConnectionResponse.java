package main.command;

import android.content.Context;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.core.networking.ServerObject;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.utils.ConfigPreferences;

/**
 * @author Adrian Arencibia Herrera
 *         Copyright (c) 2016-2017 FORTES, UCI.
 * @version 1.0
 * @class LessonConnectionResponse
 * @date 7/07/16
 * @description
 * @rf Iniciar sesión
 */
public class LessonConnectionResponse implements CommandInterface, Serializable {

    private static final long serialVersionUID = 9912L;

    private boolean autorized;

    public LessonConnectionResponse() {
    }

    // private User studentCommand;

    public LessonConnectionResponse(boolean autorized) {
        this.autorized = autorized;
    }

//    public LessonConnectionResponse() {
//        this.studentCommand = MainApp.getCurrentUser();
//    }

    /**
     * @param applicationContext
     * @return
     */
    @Override
    public OperationResult execute(Context applicationContext) {
        // Log.e(TAG,"LessonConnectionResponse");
        if (autorized) {
            MainApp.setCurrentLesson(MainApp.getWaitingConfirmation());
            MainApp.setCurrentServer(new ServerObject(MainApp.getCurrentLesson().getIpDireccion(), ConfigPreferences.getInstance().getServerPort()));

            try {
                if (MainApp.getIntance().getServerSocketFile() != null)
                    MainApp.getIntance().getServerSocketFile().close();
                MainApp.getIntance().setServerSocketFile(new ServerSocket(ConfigPreferences.getInstance().getClienFilePort()));

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (MainApp.getCurrentActivity() instanceof MainActivity) {
                MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) MainApp.getCurrentActivity()).connectToLesson();
                        // Quitar dialogo de progreso
                        if (MainApp.getDialogProgress() != null) {
                            MainApp.getDialogProgress().cancel();
                        }
                    }
                });
            }
            MainApp.setWaitingConfirmation(null);
            //Log.e(TAG,"TRUE");
        } else {
            MainApp.setWaitingConfirmation(null);
            if (MainApp.getCurrentActivity() instanceof MainActivity) {
                MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) MainApp.getCurrentActivity()).noConnectToLesson();
                        // Quitar dialogo de progreso
                        if (MainApp.getDialogProgress() != null) {
                            MainApp.getDialogProgress().cancel();
                        }
                        // Mostrar dialogo de confirmacion
                        // ERROR solo para evitar los demas comentarios
                        MainApp.getIntance().autenticationProces(
                                new OperationResult(OperationResult.ResultCode.ERROR)
                        );
                    }
                });
            }
            // Log.e(TAG,"FALSE");
        }


        OperationResult execute = new GetInstalledApps().execute(applicationContext);
        return execute;
    }


}
