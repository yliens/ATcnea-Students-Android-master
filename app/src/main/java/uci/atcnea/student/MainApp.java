package uci.atcnea.student;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;


import com.esotericsoftware.kryonet.Client;
import com.nullwire.trace.ExceptionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import main.command.LessonDisconnection;
import main.command.StudentNodeCmd;
import main.model.Archivo;
import main.model.GroupSend;
import uci.atcnea.core.networking.KryonetClientListener;
import uci.atcnea.core.networking.Network;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.core.networking.ServerObject;
import uci.atcnea.core.sockedserver.SockedServerService;
import main.model.Lesson;
import uci.atcnea.student.activity.InteractiveQuestionActivity;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.activity.PresentationActivity;
import uci.atcnea.student.activity.QuizActivity;
import uci.atcnea.student.activity.ShareScreenActivity;
import uci.atcnea.student.activity.VideoStreamingActivity;
import uci.atcnea.student.dao.DatabaseManager;

import uci.atcnea.student.model.Quiz;
import uci.atcnea.student.model.Summary;
import uci.atcnea.student.sharescreen.ShareScreen;
import uci.atcnea.student.utils.ATcneaUtil;
import uci.atcnea.student.utils.ConfigParam;
import uci.atcnea.student.utils.ConfigPreferences;
import uci.atcnea.student.utils.DialogBuilder;
import uci.atcnea.student.utils.TaskHelper;
import uci.atcnea.student.utils.TimeLineAdapter;


/**
 * @class   MainApp
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public class MainApp extends Application {

    private static final String TAG = "MainApp";
    public static final String MY_PREFS_NAME ="atcnea_config" ;



    private static MainApp intance;

    private static Context  myContext;
    private static Activity currentActivity;

    private static main.model.User currentUser;
    private static Lesson       currentLesson;
    private static ServerObject currentServer;
    private static boolean[] connected = new boolean[1];
    private static boolean openOther;

    private static Lesson waitingConfirmation;
    private  static AlertDialog dialogProgress;
    private  static AlertDialog dialogRadar;

    public static SharedPreferences myPreferences;
    public static final String PREFERENCE_SERVER_ADDRESS = "server_address";

    public static final String CONNECT_STUDUDENT_RECEIVER = "CONNECT_STUDUDENT_RECEIVER";
    public static String MAC_ADDRESS;
    public static String IP_ADDRESS;

    public static boolean hand_broadcast_register = false;

    private ServerSocket serverSocketFile;

    /*true: Permite desconectar    false: No permite*/
    private static boolean[] permitDisconnection={true};
    private static boolean[] permitChat={true};

    private  static boolean sendCommand=true;
    public static Client client;


    //FOR TASK
    public static LinkedList<Archivo> filesFromTask = new LinkedList<>();
    public static LinkedList<Long> filesIdFromTask = new LinkedList<>();
    //TASK END

    //Para lenguaje de la aplicacion
    public static Configuration config = new Configuration();
    //end lenguaje

    // Listado global de id de los usuarios con contraseñas salvadas
    public static List<Long> rememberPassID = Collections.synchronizedList(new LinkedList<Long>());

    //Para sumario de la clase
    public static ArrayList<Summary> SumaryList = new ArrayList<>();
    public static String sumaryName = "";
    public static TimeLineAdapter intanceTimeLineAdapter;
    public static boolean menuOpen = false;
    //end sumario

    private ShareScreenActivity shareScreenActivity;

    //ShareScreen
    ShareScreen myShareScreen;

   // private Dao
    private static DatabaseManager databaseManager;



/*  private int DISCOVERY_PORT;
    private int SERVER_PORT;
    private int CLIENT_PORT;
    private int SERVER_FILE_PORT;
    private int CLIENT_FILE_PORT;*/

    public static Lesson getCurrentLesson() {
        return currentLesson;
    }

    public static void setCurrentLesson(Lesson currentLesson) {
        MainApp.currentLesson = currentLesson;

        // Guardar lesson en la BD
        if(currentLesson != null) {
            MainApp.currentLesson.saveInDatabase();
        }
    }

    public static ServerObject getCurrentServer() {
        return currentServer;
    }

    public static void setCurrentServer(ServerObject currentServer) {
        MainApp.currentServer = currentServer;
    }

    public static boolean isOpenOther() {
        return openOther;
    }

    public static void setOpenOther(boolean openOther) {
        MainApp.openOther = openOther;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        /*
        * Para hacer los reportes de errores
        * la dirección url es la de la maquina donde esta montado
        * el servidor.. cualquier duda preguntarle a Adrian Arencibia Herrera.
         *
        * */
        ExceptionHandler.register(this, "http://10.55.10.241:8000/server.php");


        MainApp.setIntance(this);



        setOpenOther(false);

        databaseManager=new DatabaseManager(this);



        MainApp.myContext = getApplicationContext();
        MainApp.myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        MainApp.setCurrentActivity(null);

        MainApp.setCurrentLesson(null);
        MainApp.setCurrentServer(null);
        MainApp.setWaitingConfirmation(null);

        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo    info    = manager.getConnectionInfo();

        MainApp.MAC_ADDRESS = ConfigPreferences.getInstance().getMacDevice();//info.getMacAddress();
        MainApp.IP_ADDRESS = Formatter.formatIpAddress(manager.getConnectionInfo().getIpAddress());
        MainApp.setCurrentUser(null);
        MainApp.setDialogRadar(null);

        setUpCLientConection();
        startService(new Intent(this, SockedServerService.class));
        intance =this;


    }


    private static void setUpCLientConection() {

        MainApp.client = new Client(9000000,9000000);
        Network.register(MainApp.client);
        MainApp.client.addListener(new KryonetClientListener());

        //MainApp.client.start();

         new Thread(MainApp.client).start();

        //  MainApp.client.start();

    }


    public static MainApp getIntance() {
        return intance;
    }

    public static void setIntance(MainApp intance) {
        MainApp.intance = intance;
    }

    public static AlertDialog getDialogProgress() {
        return dialogProgress;
    }

    public static void setDialogProgress(AlertDialog dialogProgress) {
        MainApp.dialogProgress = dialogProgress;
    }

    public static Lesson getWaitingConfirmation() {
        return waitingConfirmation;
    }

    public static void setWaitingConfirmation(Lesson waitingConfirmation) {
        MainApp.waitingConfirmation = waitingConfirmation;
    }

    public  ServerSocket getServerSocketFile() {
        return serverSocketFile;
    }

    public  void setServerSocketFile(ServerSocket serverSocketFile) {
        this.serverSocketFile = serverSocketFile;
    }

    public static Context getAppContext() {
        return MainApp.myContext;
    }

    /*
     * Actividad activa
     *
     */
    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity current_activity) {
        MainApp.currentActivity = current_activity;
        if (current_activity != null) {
            MainApp.myContext = current_activity.getApplicationContext();
        }
    }


    /**
     * BroadCast to send battery level
     */
    public static void sendBatteryLevel() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;
                Log.e("MY_DEBUG", level + "%");

                SendMessageService msg = new SendMessageService(MainApp.getCurrentServer());
                StudentNodeCmd snc = new StudentNodeCmd(StudentNodeCmd.ItemNodeStatus.BATTERY_RESPONSE);

                if (isCharging) {
                    snc.setBatteryStatus(StudentNodeCmd.BatteryResponseState.BATTERY_CONNECTED);
                } else if (level < 10) {
                    snc.setBatteryStatus(StudentNodeCmd.BatteryResponseState.BATTERY_LOW);
                } else if (level >= 10 && level < 50) {
                    snc.setBatteryStatus(StudentNodeCmd.BatteryResponseState.BATTERY_LOWMIDDLE);
                } else if (level >= 50 && level < 75) {
                    snc.setBatteryStatus(StudentNodeCmd.BatteryResponseState.BATTERY_MIDDLE);
                } else if (level >= 75 && level < 100) {
                    snc.setBatteryStatus(StudentNodeCmd.BatteryResponseState.BATTERY_MIDDLEFULL);
                } else if (level >= 100) {
                    snc.setBatteryStatus(StudentNodeCmd.BatteryResponseState.BATTERY_FULL);
                } else {
                    snc.setBatteryStatus(StudentNodeCmd.BatteryResponseState.BATTERY_UNKNOWN);
                }

                msg.setWaitForResponse(false);
                msg.setCommand(snc);
                TaskHelper.execute(msg);

                Log.e("MY_DEBUG", String.valueOf(level) + "%");
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        MainApp.getAppContext().registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }

    /*
     * Manejador para mostrar mensajes TOAST al usuario
     */
    public static Handler myHandler = new Handler() {

        // Create handleMessage function

        @Override
        public void handleMessage(Message msg) {

            // busco el mensaje de error
            int errorResponse = msg.getData().getInt(ConfigParam.TAG_ERROR);

            // si es distinto de cero es que devolvio algo
            if (errorResponse != 0) {
                String errorMessage = "";

                switch (errorResponse) {
                    case ConfigParam.WS_ERROR_RESULT_400:
                        break;
                    case ConfigParam.WRONG_CONNECTION:
                    case ConfigParam.IO_EXCEPTION:

                        try {
                            if (MainApp.isConnected()) {
                                LessonDisconnection disc = new LessonDisconnection();
                                disc.setDisconnectStatus(LessonDisconnection.DisconnectType.FORCE);
                                disc.setTypeDisconection(LessonDisconnection.Type.WRONG_CONNECTION);
                                disc.execute(MainApp.getAppContext());
                            }
                        } catch (Exception e) {

                        }

                        break;
                    default:
                        errorMessage = msg.getData().getString(ConfigParam.MSG_ERROR);
                        break;
                }
                if (!errorMessage.isEmpty()) {
                    Toast.makeText(MainApp.getAppContext(), errorMessage, Toast.LENGTH_LONG)
                            .show();
                }
            }

            int succesResponse = msg.getData().getInt(ConfigParam.TAG_SUCCESS);

            if (succesResponse != 0) {
                String succesMessage = msg.getData().getString(ConfigParam.MSG_SUCCES);
                Toast.makeText(MainApp.getAppContext(), succesMessage, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };

    public static main.model.User getCurrentUser() {
        WifiManager manager = (WifiManager) getAppContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();


        MainApp.IP_ADDRESS = Formatter.formatIpAddress(manager.getConnectionInfo().getIpAddress());


        if(IP_ADDRESS.equals("0.0.0.0")){

            MainApp.MAC_ADDRESS = ConfigPreferences.getInstance().getMacDevice(); //info.getMacAddress();
          /*  WifiManager manager = (WifiManager) getAppContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo    info    = manager.getConnectionInfo();

            MainApp.MAC_ADDRESS = info.getMacAddress();
            MainApp.IP_ADDRESS = Formatter.formatIpAddress(manager.getConnectionInfo().getIpAddress()); */
//            currentUser=new User(MainApp.currentUser);
//            currentUser = currentUser!=null?new User(MainApp.currentUser):null;
        }

        return currentUser!=null?new main.model.User(MainApp.currentUser):null;
    }
    public static main.model.User getCurrentUserDao() {
        return currentUser;
    }

    public static void setCurrentUser(main.model.User currentUser) {
        MainApp.currentUser = currentUser;
    }

    /*
     * Verifica que exista conectividad
     */
    public static boolean checkConnectivity() {
        boolean enabled = true;

        ConnectivityManager connectivityManager = (ConnectivityManager) MainApp.getCurrentActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

      /*  Boolean is3g = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

        Boolean isWifi = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
*/
        if ((info == null || !info.isConnected() || !info.isAvailable())) {
            enabled = false;
        }
        return enabled;
    }

    public static void showDialogWifi() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainApp.getCurrentActivity());

        // set title
        alertDialogBuilder.setTitle(MainApp.getCurrentActivity().getString(R.string.not_network_connection_title));

        // set dialog message
        alertDialogBuilder
                .setMessage(MainApp.getCurrentActivity().getString(R.string.not_network_connection))
                .setCancelable(false)
                .setPositiveButton(MainApp.getCurrentActivity().getString(R.string.not_network_connection_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainApp.getCurrentActivity().startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));

                    }
                })
                .setNegativeButton(MainApp.getCurrentActivity().getString(R.string.not_network_connection_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public static boolean openApp(Context context, String packageName) {

        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
            }

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            MainApp.setOpenOther(true);
            context.startActivity(i);

            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static Client getClient() {
        return client;
    }

    public static void setClient(Client client) {
        MainApp.client = client;
    }

    /**
     * Funcion para ejecutar la respuesta de conectarse a una clase
     * @param result Resultado de la conexion
     */
    public void autenticationProces(OperationResult result) {

        // Terminar si ya el estudiante esta conectado a otra clase
        if(MainApp.isConnected())return;

        switch (result.getCode()) {
            case OK:
                //MainApp.setCurrentServer(new ServerObject(selectedLesson.getIpDireccion(), Integer.parseInt(getResources().getString(R.string.server_port))));
                //MainApp.setCurrentLesson(selectedLesson);
                ((MainActivity) getCurrentActivity()).connectToLesson();//setUpDrawerConnectConfig();
                Log.e(TAG, "Result OK");

                break;
            case WAITING_CONFIRMATION:

                MainApp.setWaitingConfirmation(getCurrentLesson());
                //showDialogProgres();

                // Mostrar mensaje de espera de confirmacion
                AlertDialog.Builder builder = DialogBuilder.showDialogProgres(getCurrentActivity());
                AlertDialog alertDialog = builder.create();
                MainApp.setDialogProgress(alertDialog);
                alertDialog.show();

                Log.e(TAG, "Result WAITING_CONFIRMATION");
                break;
            case CANCELLED:
                DialogBuilder.dialogInformation(getCurrentActivity(), getString(R.string.cancelled)).show();
                Log.e(TAG, "Result CANCELLED");
                break;
            case FULL_ERROR:
                Log.e(TAG, "Result FULL_ERROR");
                DialogBuilder.dialogInformation(getCurrentActivity(), getString(R.string.full_error)).show();
                break;
            case AUTHENTICATION_ERROR:
                //Aca no debe mostrar un mensaje sino debe cambiar la forma de entrar
                Log.e(TAG, "Result AUTHENTICATION_ERROR");
                DialogBuilder.dialogInformation(getCurrentActivity(), getString(R.string.authentication_error)).show();
                break;
            case UNAUTHORIZED:
                Log.e(TAG, "Result UNAUTHORIZED");
                DialogBuilder.dialogInformation(getCurrentActivity(), getString(R.string.bad_pass)).show();

                break;
            case CONFLICT:
                Log.e(TAG, "Result CONFLICT");
                DialogBuilder.dialogInformation(getCurrentActivity(), getString(R.string.conflict)).show();

                break;
            default:
                Log.e(TAG, "Result " + result.getCode());
                break;

        }


        MainApp.setSendCommand(true);

    }

    /*
    public static Account getCurrentAccount() {
        Account defaultAccount = AccountUtils.getCurrentEtecsaNautaAccount(MainApp.getAppContext());
        if (defaultAccount != null) {

            return defaultAccount;
        } else {

            MainApp.openAuthenticationActivity(false);
        }
        return defaultAccount;
    }

   /*

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static void setDaoSession(DaoSession dao_session) {
        MainApp.daoSession = dao_session;
    }

    public static String getBDName() {
        return getAppContext().getResources().getString(R.string.db_name);
    }

    /*
     * Inicialización de la BD de la app
     */

    /*
    public static void settingDatabase() {
        DaoMaster.DevOpenHelper dop = new DaoMaster.DevOpenHelper(MainApp.myContext, MainApp.getBDName(), null);
        SQLiteDatabase db = dop.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        MainApp.daoSession = daoMaster.newSession();
    }
    */

    public static boolean isConnected() {
        return MainApp.connected[0];
    }

    public static void setConnected(boolean connected) {
        MainApp.connected[0] = connected;
    }


    public static boolean isPermitDisconnection() {
        return permitDisconnection[0];
    }

    public static void setPermitDisconnection(boolean permit) {
        permitDisconnection[0] = permit;
    }


    public static boolean isPermitChat() {
        return permitChat[0];
    }

    public static void setPermitChat(boolean permitChat) {
        MainApp.permitChat[0] = permitChat;
    }


    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static void setDatabaseManager(DatabaseManager databaseManager) {
        databaseManager = databaseManager;
    }

    public static void reOpen(){
        getCurrentActivity().finish();
        //MainApp.myContext = getApplicationContext();
        //MainApp.myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        MainApp.setCurrentActivity(null);

        MainApp.setCurrentLesson(null);
        MainApp.setCurrentServer(null);
        MainApp.setWaitingConfirmation(null);

        //WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
       // WifiInfo    info    = manager.getConnectionInfo();

        //MainApp.MAC_ADDRESS = info.getMacAddress();
       // MainApp.IP_ADDRESS = Formatter.formatIpAddress(manager.getConnectionInfo().getIpAddress());
        MainApp.setCurrentUser(null);
        Intent  i=new Intent(getAppContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getAppContext().startActivity(i);
    }

    public static boolean isSendCommand() {
        return sendCommand;
    }

    public static void setSendCommand(boolean sendCommand) {
        MainApp.sendCommand = sendCommand;
    }

    public static AlertDialog getDialogRadar() {
        return dialogRadar;
    }

    public static void setDialogRadar(AlertDialog dialogRadar) {
        MainApp.dialogRadar = dialogRadar;
    }

    public static void showNotification(String msg) {


        DialogBuilder.dialogInformation(MainApp.getCurrentActivity(), msg).show();
    }

    public static void showInteractiveQuestion() {
        Intent intent = new Intent(MainApp.getCurrentActivity(), InteractiveQuestionActivity.class);
        MainApp.getCurrentActivity().startActivity(intent);
    }

    /**
     * Para mostar el activity Presentation
     */
    public static void showPresentation(byte[]img){

        Intent intent = new Intent(MainApp.getCurrentActivity(), PresentationActivity.class);

        intent.putExtra("IMG",img);

        MainApp.getCurrentActivity().startActivity(intent);

    }

    public static void showQuiz(Quiz quiz) {
        ATcneaUtil.quiz = quiz;
        Intent intent = new Intent(MainApp.getCurrentActivity(), QuizActivity.class);
        MainApp.getCurrentActivity().startActivity(intent);

    }

    public static void showVideoPlayer(String streamingPath) {

        Intent i = new Intent(MainApp.getCurrentActivity(), VideoStreamingActivity.class);
        i.putExtra("path", streamingPath);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainApp.getCurrentActivity().startActivity(i);

        Log.e("VIDEO", streamingPath);
    }

    public ShareScreen getMyShareScreen() {
        return myShareScreen;
    }

    public void setMyShareScreen(ShareScreen myShareScreen) {
        this.myShareScreen = myShareScreen;
    }

    public ShareScreenActivity getShareScreenActivity() {
        return shareScreenActivity;
    }

    public void setShareScreenActivity(ShareScreenActivity shareScreenActivity) {
        this.shareScreenActivity = shareScreenActivity;
    }


}
