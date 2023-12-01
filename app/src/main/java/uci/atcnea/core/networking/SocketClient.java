package uci.atcnea.core.networking;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.RemoteObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import main.command.GetInstalledApps;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.interfaces.CommunicationInterface;
import uci.atcnea.core.interfaces.RemoteExecute;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.service.LockScreenService;
import uci.atcnea.student.utils.DialogBuilder;

/**
 * Created by yerandy on 18/07/16.
 */

public class SocketClient {

    private Socket myClient;
    private String serverIP;
    private int serverPort;

    public SocketClient(String serverIP, int port) {
        this.serverIP = serverIP;
        this.serverPort = port;
    }

    public void StartClient() {

        try {
            // puerto: 8081
            // servidor: 192.168.1.5
            myClient = new Socket(this.serverIP, this.serverPort);

            // DataOutputStream outToServer = new DataOutputStream(myClient.getOutputStream());
            // BufferedReader inFromServer = new BufferedReader(new InputStreamReader(myClient.getInputStream()));


            ObjectOutputStream oos = new ObjectOutputStream(myClient.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(myClient.getInputStream());

            CommandInterface command = new GetInstalledApps();
            oos.writeObject(command);

            Object response = ois.readObject();
            OperationResult resp = (OperationResult) response;

            ArrayList<Object> listApps = resp.getData();

            for (Object appName : listApps) {
                if (appName instanceof String) {
                    System.out.println(appName);
                }
            }

            //  outToServer.writeBytes(sentence + '\n');
            //  modifiedSentence = inFromServer.readLine();
            // System.out.println(modifiedSentence);
            myClient.close();

        } catch (IOException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}


/*
public class SocketClient implements CommunicationInterface {


    @Override
    public OperationResult sendCommandAndWaitResponse(CommandInterface command) {
        RemoteObject remoteObject = (RemoteObject) remoteExecute(command);
        byte responseID = remoteObject.getLastResponseID();
        OperationResult result = new OperationResult(OperationResult.ResultCode.QUOTA_EXCEEDED                                                          );
        try {
            result = (OperationResult) remoteObject.waitForResponse(responseID);
        } catch (Exception e) {
           e.printStackTrace();
        }
        return result;
    }

    private RemoteExecute remoteExecute(CommandInterface command) {
        RemoteExecute execute = ObjectSpace.getRemoteObject(MainApp.getClient(), 3, RemoteExecute.class);
        ((RemoteObject) execute).setNonBlocking(true);
        ((RemoteObject) execute).setResponseTimeout(9999999);
        execute.execute(command);
        return execute;
    }


    @Override
    public OperationResult sendCommand(CommandInterface command) {
        try {

            remoteExecute(command);
            return new OperationResult(OperationResult.ResultCode.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new OperationResult(e);
        }

    }
}
*/
