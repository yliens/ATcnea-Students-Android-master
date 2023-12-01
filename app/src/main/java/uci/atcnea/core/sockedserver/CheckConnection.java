package uci.atcnea.core.sockedserver;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;

import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.utils.ConfigPreferences;

/**
 * Created by guillermo on 3/02/17.
 */

public class CheckConnection  extends Thread {

    private DatagramSocket datagramSocket;

    private int socketTimeOut;

    private String TAG = "CheckConnection";

    private String serverMessageRequest = "DISCOVER_ATCNEA_SERVER_REQUEST";
    private String serverMessageResponse = "DISCOVER_ATCNEA_SERVER_RESPONSE";

    private String serverIp;

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
            /*try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), port);
                datagramSocket.send(sendPacket);
                Log.d(TAG, " Request packet sent to: 255.255.255.255 (DEFAULT)");

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }*/

            while(true){
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(serverIp), port);
                datagramSocket.send(sendPacket);

                while(true){

                }
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }


            // Broadcast del mensaje sobre todas las interfaces de red
           /* Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
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
            }

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

                if (message.equals(serverMessageResponse)) {
                    hostName = receivePacket.getAddress().getHostAddress();

                    ArrayList<Object> tmp = new ArrayList<Object>();
                    tmp.add(hostName);

                    final OperationResult result = new OperationResult(OperationResult.ResultCode.OK);
                    result.setData(tmp);

                    //serviceIntent.putExtra(MainApp.PREFERENCE_SERVER_ADDRESS, result);
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
        }*/
    }
}
