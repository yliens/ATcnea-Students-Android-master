package main.command;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import java.io.Serializable;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.model.Quiz;

/**
 * Created by guillermo on 9/11/16.
 */
public class PresentationConnection implements CommandInterface,Serializable{

    private static final long serialVersionUID = 9316L;

    public enum TypePresentation {
        OPEN_PRESENTATION,
        SHOW_SLIDE,
        CLOSE_PRESENTATION
    }

    private TypePresentation type;

    private byte[] imgInBytes;

    public PresentationConnection() {
    }

    public PresentationConnection(TypePresentation type, byte[] imgInBytes) {
        this.type = type;
        this.imgInBytes = imgInBytes;
    }

    @Override
    public OperationResult execute(Context applicationContext) {
        Log.d("PresentationConnection",type.name());
        switch ( type ){
            case OPEN_PRESENTATION://Para mostrar el activity presentation
                Log.d("PresCommand","OPEN_PRESENTATION");
                showPress();
                break;
            case SHOW_SLIDE://Para mostrar las imagenes de las presentaciones
                //Enviar por broadcast la imagen nueva a mostrarse
                Log.d("PresCommand","SHOW_SLIDE");
                Intent intent = new Intent("PRESENTATION");
                intent.putExtra("IMG",imgInBytes);
                applicationContext.sendBroadcast(intent);
                break;
            case CLOSE_PRESENTATION://Para cerrar el activity presentation
                Log.d("PresCommand","CLOSE_PRESENTATION");
                //Enviar por Broadcast el evento de cerrar el activity
                intent = new Intent("PRESENTATION");
                intent.putExtra("CLOSE", true);
                applicationContext.sendBroadcast(intent);
                break;
            default:
                break;
        }

        return null;
    }

    private void showPress(){
        MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainApp.showPresentation(imgInBytes);
            }
        });
    }

}
