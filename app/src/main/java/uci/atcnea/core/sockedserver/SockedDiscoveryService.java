package uci.atcnea.core.sockedserver;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.ClientDiscoveryHandler;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import uci.atcnea.core.networking.KryonetClientListener;
import uci.atcnea.core.networking.Network;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.utils.ConfigPreferences;
import uci.atcnea.student.utils.TaskHelper;

/**
 * @author Adrian Arencibia Herrera
 *         Copyright (c) 2016-2017 FORTES, UCI.
 * @version 1.0
 * @class SockedDiscoveryService
 * @date 11/07/16
 * @description
 * @rf
 */
public class SockedDiscoveryService extends Service {

    private boolean isStarted = false;

    private String serverMessageRequest = "DISCOVER_ATCNEA_SERVER_REQUEST";
    private String serverMessageResponse = "DISCOVER_ATCNEA_SERVER_RESPONSE";
    private String TAG = "SockedDiscoveryService";

    public static final String BROADCAST_DISCOVERY_ACTION = "uci.atcnea.core.SockedDiscoveryService";

    public static final String addServerToView = "uci.atcnea.student.activity.Main2Activity";
    public static final String addServerToView2 = "uci.atcnea.student.activity.MainActivity";

    private Intent serviceIntent;

    private String hostName;
    int socketTimeOut;
    private LocalBroadcastManager broadcaster;
    Client client;

    public SockedDiscoveryService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();


        GlobalBus.getBus().register(this);
        serviceIntent = new Intent(BROADCAST_DISCOVERY_ACTION);

        hostName = getString(R.string.server_not_found);
        socketTimeOut = Integer.parseInt(getString(R.string.socket_timeout_discovery));


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("service", "SockedDiscoveryService started");

        broadcaster = LocalBroadcastManager.getInstance(this);

        if (!isStarted) {
            new DiscoveryThread().start();
            //new DiscoverySocketThread().run();
        }



        return START_NOT_STICKY;
    }


    private class DiscoveryThread extends Thread {

        @Override
        public void run() {
            super.run();


            client = new Client(1000000, 100000);
            Network.register(client);
            client.addListener(new KryonetClientListener());

            //MainApp.client.start();

            TaskHelper.execute(new AsyncTask<Object, Object, Object>() {
                @Override
                protected Object doInBackground(Object... params) {

                    client.start();

                    return null;
                }
            });
            client.setDiscoveryHandler(new ATcneaDiscoveryHandler());

            // MainApp.client.setKeepAliveTCP(8000);

            int udpPort = ConfigPreferences.getInstance().getDiscoveryPort();
            Log.e("PORT", udpPort+"");

            hostName = "";
            HashMap<InetAddress, String> address = client.discoverHostsWithMessage(udpPort, socketTimeOut, serverMessageRequest);

            ArrayList<Object> tmp = new ArrayList<Object>();

            for (Map.Entry<InetAddress, String> entry : address.entrySet()) {
                InetAddress host = entry.getKey();
                String info = entry.getValue();

                try {
                    JSONObject jsonResult = new JSONObject(info);
                    jsonResult.put("host", host.getHostAddress());
                    tmp.add(jsonResult);
                    Log.d(TAG, jsonResult.toString());

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            final OperationResult result = new OperationResult(OperationResult.ResultCode.OK);

            // if (tmp.size() == 0) {
            result.setSuccess(false);
            // }
            result.setData(tmp);

            // serviceIntent.putExtra(MainApp.PREFERENCE_SERVER_ADDRESS, result);
            // broadcaster.sendBroadcast(serviceIntent);

            MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (MainApp.getCurrentActivity() instanceof MainActivity) {
                        ((MainActivity) MainApp.getCurrentActivity()).getLessonDiscoveryFragment().addLessonResult(result);

                    }

                }
            });


        }
    }

    private class ATcneaDiscoveryHandler implements ClientDiscoveryHandler {

        @Override
        public DatagramPacket onRequestNewDatagramPacket() {
            byte[] recvBuf = new byte[15000];

            DatagramPacket sendPacket = new DatagramPacket(recvBuf, recvBuf.length);

            return sendPacket;
        }

        @Override
        public void onDiscoveredHost(DatagramPacket datagramPacket, Kryo kryo) {

            String message = new String(datagramPacket.getData()).trim();
            //DatagramPacket d=datagramPacket;

            ArrayList<Object> tmp = new ArrayList<Object>();
            try {
                JSONObject jsonResult = new JSONObject(message);
                jsonResult.put("host", datagramPacket.getAddress());
                tmp.add(jsonResult);
                Log.d(TAG, jsonResult.toString());
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }


            final OperationResult result = new OperationResult(OperationResult.ResultCode.OK);

            // if (tmp.size() == 0) {
            result.setSuccess(true);
            // }
            result.setData(tmp);

            Log.d(TAG, "onDiscoveredHost");
            Log.d(TAG, message);

            MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (MainApp.getCurrentActivity() instanceof MainActivity) {
                        ((MainActivity) MainApp.getCurrentActivity()).getLessonDiscoveryFragment().addLessonResult(result);

                    }

                }
            });

//            serviceIntent.putExtra(MainApp.PREFERENCE_SERVER_ADDRESS, result);
//            broadcaster.sendBroadcast(serviceIntent);

        }

        @Override
        public void onFinally() {

            Log.d(TAG, "onFinally");

        }
    }


    //Eventos
    @Subscribe
    public void eventStopService(Events.EventStopDiscover stopDiscover) {

        SockedDiscoveryService.this.stopSelf();

    }

    private class DiscoverySocketThread extends Thread {
        DatagramSocket datagramSocket;

        @Override
        public void run() {
            super.run();

            // Encontrar el servidor usando UDP broadcast
            try {
                //Abrir un purto random para enviar el paquete
                datagramSocket = new DatagramSocket();
                datagramSocket.setBroadcast(true);
                // Tiempo que estará esperando por respuesta
                datagramSocket.setSoTimeout(socketTimeOut);

                byte[] sendData = serverMessageRequest.getBytes();

                //Enviando primeramente a 255.255.255.255
                int port = ConfigPreferences.getInstance().getDiscoveryPort();
                try {
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("10.55.11.35"), port);
                    datagramSocket.send(sendPacket);
                    Log.d(TAG, " Request packet sent to: 255.255.255.255 (DEFAULT)");

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

                // Broadcast del mensaje sobre todas las interfaces de red
/*                Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                    if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                        continue; // No enviar a una red virtual (loopback interface)
                    }

                    for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                        InetAddress broadcast = interfaceAddress.getBroadcast();
                        if (broadcast == null) {
                            continue;
                        }

                        // Enviando el paquete broadcast
                        try {
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, port);
                            datagramSocket.send(sendPacket);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                        Log.d(TAG, getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                    }
                }*/

                Log.d(TAG, " Done looping over all network interfaces. Now waiting for a reply!");

                // Esperando por la respuesta
                byte[] recvBuf = new byte[15000];

                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);

                while (datagramSocket.getReceiveBufferSize() > 0) {
                    datagramSocket.receive(receivePacket);
                    // Se obtiene una respuesta
                    Log.d(TAG, ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

                    // Se chequea que la respuesta sea la correcta
                    String message = new String(receivePacket.getData()).trim();

                    Log.e("DISCOVER",message);

                    if (message.equals(serverMessageResponse)) {
                        hostName = receivePacket.getAddress().getHostAddress();

                        ArrayList<Object> tmp = new ArrayList<Object>();
                        tmp.add(hostName);

                        final OperationResult result = new OperationResult(OperationResult.ResultCode.OK);
                        result.setData(tmp);

                        //serviceIntent.putExtra(MainApp.PREFERENCE_SERVER_ADDRESS, result);
                        // broadcaster.sendBroadcast(serviceIntent);

                       /* MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (MainApp.getCurrentActivity() instanceof MainActivity) {
                                    ((MainActivity) MainApp.getCurrentActivity()).getLessonDiscoveryFragment().addLessonResult(result);

                                }

                            }
                        });*/

                    }

                    Log.i(TAG, hostName);
                }
                datagramSocket.close();


                //this.stop();
                stopSelf();

            } catch (SocketTimeoutException e) {
//                OperationResult result = new OperationResult(e);
//
//                serviceIntent.putExtra(MainApp.PREFERENCE_SERVER_ADDRESS, result);
//                broadcaster.sendBroadcast(serviceIntent);

                updateView();
                datagramSocket.close();
                // this.stop();
                stopSelf();
            } catch (IOException e) {
//                OperationResult result = new OperationResult(e);
//
//                serviceIntent.putExtra(MainApp.PREFERENCE_SERVER_ADDRESS, result);
//                broadcaster.sendBroadcast(serviceIntent);
//


                updateView();
                datagramSocket.close();
                //this.stop();
                stopSelf();
            } catch (Exception e) {
//                OperationResult result = new OperationResult(e);
//
//
//                serviceIntent.putExtra(MainApp.PREFERENCE_SERVER_ADDRESS, result);
//                broadcaster.sendBroadcast(serviceIntent);

                updateView();
                datagramSocket.close();
                // this.stop();
                stopSelf();
            }
        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        GlobalBus.getBus().unregister(this);
        updateView();
        super.onDestroy();
    }


    public void updateView() {
        MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MainApp.getCurrentActivity() instanceof MainActivity) {
                    ((MainActivity) MainApp.getCurrentActivity()).getLessonDiscoveryFragment().endDiscover();

                }
            }
        });

    }

}
