package main.command;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.Serializable;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;

/**
 * @class   OpenWebSite
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf Abrir sitio web remoto


 */
public class OpenWebSite implements CommandInterface, Serializable {
    private static final long serialVersionUID = 9312;
    private static final String TAG ="OpenWebSite" ;

    private String url;

    public OpenWebSite(String url) {
        this.url = url;
    }


    public OpenWebSite() {
    }

    /**
     *
     * @param applicationContext
     * @return
     */
    @Override
    public OperationResult execute(Context applicationContext) {

        Log.e(TAG,url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        MainApp.getCurrentActivity().startActivity(intent);

        return null;
    }
}
