package uci.atcnea.core.networking;

import android.nfc.Tag;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.interfaces.CommunicationInterface;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;

/**
 * @class   SocketServer
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public class SocketServer implements CommunicationInterface {

    private static final String TAG ="SocketServer" ;
    private Socket myServer;
    private String serverIP;
    private int serverPort;

    public SocketServer(String serverIP, int port) {
        this.serverIP = serverIP.replace("/","");
        this.serverPort = port;
    }

    public SocketServer(ServerObject serverObject) {
        try {


        if(serverObject != null) {
            this.serverIP = serverObject.getHost();
            this.serverPort = serverObject.getPort();
        }else{
            this.serverIP = MainApp.getCurrentServer().getHost();
            this.serverPort = MainApp.getCurrentServer().getPort();
        }
        }catch (Exception e){

        }

    }

    @Override
    public synchronized OperationResult sendCommandAndWaitResponse(CommandInterface command) {

        try {
            myServer = new Socket(this.serverIP, this.serverPort);

            Log.i(TAG,myServer.toString());
            ObjectOutputStream oos = new ObjectOutputStream(myServer.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(myServer.getInputStream());

            oos.writeObject(command);

            Object response = ois.readObject();
            OperationResult responseResult = (OperationResult) response;
            myServer.close();
            Log.e("CommandAndWaitResponse",command.getClass().getName());
            return responseResult;
        }catch (Exception e) {
            e.printStackTrace();
            GlobalBus.getBus().post( new Events.EventDisconect() );
            return new OperationResult(e);
        }
       /* catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new OperationResult(e);
        }
        catch (IOException e) {
            e.printStackTrace();
            return new OperationResult(e);
        }*/
    }

    @Override
    public synchronized OperationResult sendCommand(CommandInterface command) {

        try {
            myServer = new Socket(this.serverIP, this.serverPort);

            ObjectOutputStream oos = new ObjectOutputStream(myServer.getOutputStream());

            oos.writeObject(command);
            Log.e("sendCommand",command.getClass().getName());
            return new OperationResult(OperationResult.ResultCode.OK);
        }
        catch (IOException e) {
            GlobalBus.getBus().post( new Events.EventDisconect() );
            e.printStackTrace();
            return new OperationResult(e);
        }
    }
}
