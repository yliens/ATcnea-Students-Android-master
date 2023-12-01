package uci.atcnea.student.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;

/**
 * Created by guillermo on 6/02/17.
 */

public class ConfigPreferences{
    private static final String MY_PREFS_DISCOVERY = "atcnea_config_discovery";
    private static final String MY_PREFS_SERVER = "atcnea_config_server";
    private static final String MY_PREFS_SERVER_FILE = "atcnea_config_server_file";
    private static final String MY_PREFS_CLIENT = "atcnea_config_client" ;
    private static final String MY_PREFS_FILE_CLIENT = "atcnea_config_client_file";

    private static ConfigPreferences instance;
    private int discoveryPort;
    private int serverPort;
    private int serverFilePort;
    private int clienPort;
    private int clienFilePort;

    public static ConfigPreferences getInstance() {
        if(instance == null){
            instance = new ConfigPreferences();

            instance.InitPreferences();
        }
        return instance;
    }

    private SharedPreferences preferences;

    private void InitPreferences(){

        preferences = MainApp.getIntance().getSharedPreferences("ATCNEA_APP", 0);

        macDevice = preferences.getString("MAC_DEVICE", null);
        if(macDevice == null){
            /*WifiManager manager = (WifiManager) MainApp.getIntance().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();

            macDevice = info.getMacAddress();

            if(macDevice == null || macDevice.equals("")){
                macDevice = ATcneaUtil.randomMACAddress();
            }*/

            // Creandose siempre las MAC aleatoriamente
            macDevice = ATcneaUtil.randomMACAddress();

            SavePreference("MAC_DEVICE", macDevice);
        }

        discoveryPort=preferences.getInt(MY_PREFS_DISCOVERY,-1);

        serverPort=preferences.getInt(MY_PREFS_SERVER,-1);
        clienPort=preferences.getInt(MY_PREFS_CLIENT,-1);

        serverFilePort =preferences.getInt(MY_PREFS_SERVER_FILE,-1);
        clienFilePort =preferences.getInt(MY_PREFS_FILE_CLIENT,-1);



        if(discoveryPort==-1||serverPort==-1|| serverFilePort ==-1 ||clienFilePort==-1||clienPort==-1){

            SavePreference(MY_PREFS_DISCOVERY,Integer.parseInt(MainApp.getIntance().getString(R.string.server_discovery_port)));

            SavePreference(MY_PREFS_SERVER,Integer.parseInt(MainApp.getIntance().getString(R.string.server_port)));
            SavePreference(MY_PREFS_CLIENT,Integer.parseInt(MainApp.getIntance().getString(R.string.client_port)));

            SavePreference(MY_PREFS_SERVER_FILE,Integer.parseInt(MainApp.getIntance().getString(R.string.server_file_port)));
            SavePreference(MY_PREFS_FILE_CLIENT,Integer.parseInt(MainApp.getIntance().getString(R.string.client_file_port)));

            discoveryPort=preferences.getInt(MY_PREFS_DISCOVERY,-1);

            serverPort=preferences.getInt(MY_PREFS_SERVER,-1);
            clienPort=preferences.getInt(MY_PREFS_CLIENT,-1);

            serverFilePort =preferences.getInt(MY_PREFS_SERVER_FILE,-1);
            clienFilePort =preferences.getInt(MY_PREFS_FILE_CLIENT,-1);

        }


    }

    /**
     *
     * @param key Identificador de la preferencia
     * @param value Valor de la preferencia
     */
    private void SavePreference(String key, String value){
        SharedPreferences.Editor preferencesEdit = preferences.edit();
        preferencesEdit.putString(key, value);
        preferencesEdit.commit();
    }
    /**
     *
     * @param key Identificador de la preferencia
     * @param value Valor de la preferencia
     */
    private void SavePreference(String key, int value){
        SharedPreferences.Editor preferencesEdit = preferences.edit();
        preferencesEdit.putInt(key, value);
        preferencesEdit.commit();
    }

    /**
     * Mac del dispositivo almacenada en la primera ejecucion
     */
    private String macDevice;

    public String getMacDevice() {
        return macDevice;
    }

    public void setMacDevice(String macDevice) {
        this.macDevice = macDevice;
        SavePreference("MAC_DEVICE", macDevice);
    }

    /**
     * Puerto de discovery
    * */
    public int getDiscoveryPort() {
        return discoveryPort;
    }

    public void setDiscoveryPort(int discoveryPort) {
        this.discoveryPort = discoveryPort;
        SavePreference(MY_PREFS_DISCOVERY,discoveryPort);
    }

    /**
     * Puerto del Servidor socket
     * */
    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
        SavePreference(MY_PREFS_SERVER,serverPort);
    }

    /**
     * Puerto del servidor de archivo
     * */
    public int getServerFilePort() {
        return serverFilePort;
    }

    public void setServerFilePort(int serverFilePort) {
        this.serverFilePort = serverFilePort;
        SavePreference(MY_PREFS_SERVER_FILE,serverFilePort);
    }

    /**Puerto del socket cliente
     *
     * */

    public int getClienPort() {
        return clienPort;
    }

    public void setClienPort(int clienPort) {
        this.clienPort = clienPort;
        SavePreference(MY_PREFS_CLIENT,clienPort);
    }

    /**
     * Puerto del socket cliente de archivos
     * */
    public int getClienFilePort() {
        return clienFilePort;
    }

    public void setClienFilePort(int clienFilePort) {
        this.clienFilePort = clienFilePort;
        SavePreference(MY_PREFS_FILE_CLIENT,clienFilePort);
    }
}
