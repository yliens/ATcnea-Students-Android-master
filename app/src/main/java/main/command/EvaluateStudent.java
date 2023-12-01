package main.command;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.utils.DialogBuilder;

/**
 * @class   EvaluateStudent
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf
 */
public class EvaluateStudent implements CommandInterface,Serializable {

    private static final long serialVersionUID = 951359L;

    private String mensajeEvaluacion;

    /**
     *
     */
    public EvaluateStudent() {
    }

    /**
     *
     * @param applicationContext
     * @return
     */
    @Override
    public OperationResult execute(Context applicationContext) {

        if(mensajeEvaluacion.length()>0){
            MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    DialogBuilder.dialogInformation(MainApp.getCurrentActivity(),mensajeEvaluacion.replace("\"\"","")).show();


                }
            });
        }

        return null;
    }
}
