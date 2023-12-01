package uci.atcnea.core.networking;

import android.content.Intent;
import android.util.Log;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

import main.command.LessonConnection;
import main.command.LessonDisconnection;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.interfaces.SyncTaskResultListenerInterface;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.utils.TaskHelper;

/**
 * Created by yerandy on 22/07/16.
 */
public class KryonetClientListener extends Listener {


    private static final String TAG = "KryonetClientListener";

    private Intent serviceIntent = new Intent(MainApp.CONNECT_STUDUDENT_RECEIVER);

    public KryonetClientListener() {
    }

    @Override
    public void connected(Connection connection) {

       /* LessonConnection sConnect = new LessonConnection(MainApp.getCurrentLesson().getAuthenticationType());
       // connection.sendTCP(studentConnect);


        new ObjectSpace(connection).register(2,new RemoteExecuteClient());



        SendMessageService s=new SendMessageService();
        s.setCommand(sConnect);
        s.setWaitForResponse(true);
        s.setCallback(new SyncTaskResultListenerInterface() {
            @Override
            public void onSyncTaskEventCompleted(OperationResult result, String ip) {
                MainApp.getIntance().autenticationProces(result);


            }
        });


        TaskHelper.execute(s);*/
       //int y=MainApp.client.sendTCP(sConnect);


        //Log.i(TAG, "connected "+y);
    }

    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof CommandInterface) {
            OperationResult result = ((CommandInterface) object).execute(MainApp.getAppContext());
            Log.i(TAG, "Connection Connection Connection Connection "+object.getClass());
            //connection.sendTCP(result);
        }



    }

    @Override
    public void disconnected(Connection connection) {

        if (MainApp.isConnected()) {
            LessonDisconnection disc = new LessonDisconnection();
            disc.setDisconnectStatus(LessonDisconnection.DisconnectType.FORCE);
            disc.setTypeDisconection(LessonDisconnection.Type.WRONG_CONNECTION);
            disc.execute(MainApp.getAppContext());
        }


        Log.i(TAG, "disconnected");

    }

    @Override
    public void idle(Connection connection) {
        Log.i(TAG, "idle ");
    }






}
