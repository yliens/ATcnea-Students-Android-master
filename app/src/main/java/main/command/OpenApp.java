package main.command;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import java.io.Serializable;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;

/**
 * @class   OpenApp
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf Iniciar aplicación remota por el profesor

 */
public class OpenApp implements CommandInterface,Serializable {

    private static final long serialVersionUID = 2126L;

    private static final String TAG = "OpenApp";

    private String nameApp;

    private boolean isApp;

    public OpenApp() {
    }

    /**
     *
     * @param applicationContext
     * @return
     */
    @Override
    public OperationResult execute(Context applicationContext) {

        MainApp.setOpenOther(true);
        if (isApp) {

            MainApp.openApp(applicationContext, nameApp);

        } else {

            System.out.println("Lance jueego");

            Log.e(TAG, nameApp);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(nameApp));
            MainApp.getCurrentActivity().startActivity(intent);

        }
        return null;
    }
}
