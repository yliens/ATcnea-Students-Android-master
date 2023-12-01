package uci.atcnea.core.sockedserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Este receiver es para escuchar el inicio del sistema e iniciar el servicio
 */
/**
 * @class   SockedServerBroadCastReceived
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public class SockedServerBroadCastReceived extends BroadcastReceiver {
    public SockedServerBroadCastReceived() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

       // Intent myIntent = new Intent(context, SocketClient.class);
       // context.startService(myIntent);

    }
}
