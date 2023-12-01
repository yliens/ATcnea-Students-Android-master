package uci.atcnea.core.sockedserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.utils.ConfigPreferences;

/**
 * @class   SockedServerService
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public class SockedServerService extends Service {

    private boolean isStarted = false;


    public SockedServerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("service", "listeningService started");

        if (!isStarted)
            new ServiceSocketThread().start();

        return START_STICKY;
    }

    private class ServiceSocketThread extends Thread {
        ServerSocket mServerSocket = null;

        HandleSocketClient handleSocketClient;

        @Override
        public void run() {
            super.run();


            //SECTION TEST-----------------------------------------


//            new LockScreenThread_TEST(getApplicationContext()).start();
            //---------------------------------------------------------



            int localPort = ConfigPreferences.getInstance().getClienPort();
                try {

                mServerSocket = new ServerSocket(localPort);

            } catch (IOException e) {
                isStarted = false;
                //   e.printStackTrace();
                //  throw new RuntimeException(e);
            }

            Log.d("socket", "server socket started at: " + localPort);
            //  CommonHelper.registerNSDService(SockedServerService.this, localPort, registrationListener);
            //!Thread.currentThread().isInterrupted()
            while (true) {
                isStarted = true;
                Socket socketClient = null;
                String clientSentence;
                String capitalizedSentence;

                try {
                    socketClient = mServerSocket.accept();

                    handleSocketClient = new HandleSocketClient(socketClient);
                    handleSocketClient.start();

                    Log.d("socket", "Se conecto alguien");

                } catch (IOException e) {
                    isStarted = false;
                    //e.printStackTrace();
                    //throw new RuntimeException(e);

                } catch (Exception e) {
                    isStarted = false;
                    //e.printStackTrace();
                    //throw new RuntimeException(e);
                }
            }
            //CommonHelper.unregisterNSDService(ListeningService.this, registrationListener);
        }
    }

    public class HandleSocketClient extends Thread {

        Socket socketClient;

        public HandleSocketClient(Socket socketClient) {
            this.socketClient = socketClient;
        }

        @Override
        public void run() {

            ObjectInputStream ois = null;
            ObjectOutputStream oos = null;
            Object aux = null;

            try {
                ois = new ObjectInputStream(socketClient.getInputStream());
                oos = new ObjectOutputStream(socketClient.getOutputStream());

                aux = ois.readObject();

                if (aux instanceof CommandInterface) {

                    OperationResult result = ((CommandInterface) aux).execute(getApplicationContext());

                    oos.writeObject(result);
                } else{
                    Log.e("ERROR","ALGOOOOOOO");
                }

                socketClient.close();

            } catch (Exception e) {
                // System.out.println("Error en el servidor de entrada 1");
                 e.printStackTrace();

            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
