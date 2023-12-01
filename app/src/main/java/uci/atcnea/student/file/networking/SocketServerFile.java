package uci.atcnea.student.file.networking;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import main.model.Archivo;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.utils.ConfigPreferences;

/**
 * @author Adrian Arencibia Herrera
 *         Copyright (c) 2016-2017 FORTES, UCI.
 * @version 1.0
 * @class SocketServerFile
 * @date 11/07/16
 * @description
 * @rf
 */
public class SocketServerFile extends AsyncTask<Void, Void, Void> {
    public String file;
    public long file_size;

    /*private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotifyBuilder;*/
    private int numMessages;
    private Context context;
    private String TAG = "SocketServerFile";

    public SocketServerFile(String file, Context context) {
        this.context = context;
        this.file = file;

    }

    public SocketServerFile(String file, long file_size, Context context) {
        this.file = file;
        this.file_size = file_size;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        /*mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Establece un ID para la notificación, para que se pueda actualizar

        mNotifyBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.title_copy))
                .setContentText("Archivo: "+file+"\nTamaño: "+(file_size/(1024*1024))+" MB.")
                .setSmallIcon(R.drawable.ic_atcnea);

        numMessages=(int)(new Date()).getTime();
        mNotificationManager.notify(numMessages, mNotifyBuilder.build());*/


        File root = new File(Environment.getExternalStorageDirectory(), "ATcnea");
        File localFile = new File(root, file);

        Log.d("fileName", file);

        boolean fileToTask = false;
        Long taskId = null;

        //Chequeando que el archivo recibido no pertenece a una tarea
        for (int i = MainApp.filesFromTask.size() - 1; i >= 0; i--) {
            if (file.equals(MainApp.filesFromTask.get(i).getName())) {
                fileToTask = true;

                taskId = MainApp.filesIdFromTask.get(i);

                //Eliminar archivo recibido
                MainApp.filesFromTask.remove(i);
                MainApp.filesIdFromTask.remove(i);
                break;
            }
        }

        Log.d("SizeToList", MainApp.filesFromTask.size() + "");
        Log.d("fileToTask", fileToTask + "");

        GlobalBus.getBus().post((new Events.EventFile(
                        new Archivo(localFile.getName(), localFile.getPath(), file_size, true),
                        (fileToTask ? Events.FileAction.NOTIFY_FILE_COPY_TO_TASK : Events.FileAction.NOTIFY_FILE_COPY))
                ).setTaskId(taskId)
        );

        Log.e(TAG, "BEFORE");


    }

    @Override
    protected Void doInBackground(Void... params) {


        //Fichero a transferir

        try {
//           Log.e(TAG, MainApp.getIntance().getServerSocketFile().toString());

            boolean executing = true;

            Socket client = null;


            if (MainApp.getIntance().getServerSocketFile() == null)
                MainApp.getIntance().setServerSocketFile(new ServerSocket(ConfigPreferences.getInstance().getClienFilePort()));


            client = MainApp.getIntance().getServerSocketFile().accept();
            executing = false;


            File root = new File(Environment.getExternalStorageDirectory(), "ATcnea");
            if (!root.exists()) {
                root.mkdirs();
            }

            File localFile = new File(root, file);


            BufferedInputStream bis;
            BufferedOutputStream bos;
            byte[] receivedData;
            int in;
//Buffer de 1024 bytes
            receivedData = new byte[1024];


            bis = new BufferedInputStream(client.getInputStream());


//Para guardar fichero recibido en Downloads
            bos = new BufferedOutputStream(new FileOutputStream(localFile));

            double progress = 0;

            while ((in = bis.read(receivedData)) != -1) {
                bos.write(receivedData, 0, in);
                progress += in;
                /*mNotifyBuilder.setProgress((int) file_size, (int) progress, false);
                mNotificationManager.notify(numMessages, mNotifyBuilder.build());*/

                GlobalBus.getBus().post((new Events.EventFile(new Archivo(localFile.getName(), localFile.getPath(), file_size, true), Events.FileAction.UPDATE_PERCENT)).setPercent(progress / (double) file_size));

                Log.e(TAG, "COPING " + progress + "/" + file_size + ", " + progress / (double) file_size);
            }

            bos.close();
            bis.close();

            client.close();

            Log.e(TAG, "CLOSED");

        } catch (IOException e) {

            e.printStackTrace();

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        File root = new File(Environment.getExternalStorageDirectory(), "ATcnea");
        File localFile = new File(root, file);
        GlobalBus.getBus().post(new Events.EventFile(new Archivo(localFile.getName(), localFile.getPath(), file_size, false), Events.FileAction.UPDATE_PERCENT).setPercent(1.0));

    }
}
