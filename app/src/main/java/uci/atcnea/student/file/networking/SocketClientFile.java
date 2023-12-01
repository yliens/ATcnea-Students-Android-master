package uci.atcnea.student.file.networking;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.Date;

import main.model.Archivo;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.dao.User;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.utils.ConfigPreferences;

/**
 * @class   SocketClientFile
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public class SocketClientFile extends AsyncTask<Void,Void,Void> {
    public String file;
    public long file_size;
    public long file_id;
    public boolean fromFile;

    //private NotificationManager mNotificationManager;
    //private android.support.v4.app.NotificationCompat.Builder mNotifyBuilder;
    //private int numMessages;
    private Context context;
    public SocketClientFile(String file,Context context) {
        this.context=context;
        this.file = file;

    }

    public SocketClientFile(String file, long file_size,long file_id, Context context, boolean fromFile) {
        this.file = file;
        this.file_size = file_size;
        this.context = context;
        this.file_id=file_id;
        this.fromFile = fromFile;
    }

    public SocketClientFile(String file, long file_size, Context context) {
        this.file = file;
        this.file_size = file_size;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        /*mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);*/

        //Calcular tamaño del archivo
        /*String tamaño="";
        long t=0;
        if(file_size>(1024*1024))
            tamaño="\nTamaño: "+(file_size/(1024*1024))+" MB.";
        else if(file_size<(1024*1024) && file_size>(1024))
            tamaño="\nTamaño: "+(file_size/(1024))+" KB.";
        else if(file_size<(1024))
            tamaño="\nTamaño: "+(file_size/(1024))+" B.";*/

        /*mNotifyBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.title_copy))
                .setContentText("Archivo: "+file+tamaño)
                .setSmallIcon(R.drawable.ic_atcnea);

        numMessages=(int)(new Date()).getTime();
        mNotificationManager.notify(numMessages, mNotifyBuilder.build());*/

        final File localFile = new File(file);

        Archivo archivo = new Archivo(localFile.getName(),localFile.getPath(),false);

        GlobalBus.getBus().post( new Events.EventFile(
                archivo,
                fromFile?Events.FileAction.NOTIFY_FILE_COPY_FROM_TASK: Events.FileAction.NOTIFY_FILE_COPY,
                ((User)MainApp.getCurrentUserDao()).getId()
        ) );

        //GlobalBus.getBus().post(new Events.EventSummary(Events.SummaryType.CLOSE_SUMMARY_INVISIBLE));

    }

    @Override
    protected Void doInBackground(Void... params) {

        DataInputStream input;
        BufferedInputStream bis;
        BufferedOutputStream bos;
        int in;
        byte[] byteArray;
        //Fichero a transferir
        final String filename = file;

        try {
            final File localFile = new File(filename);
            Log.i(SocketClientFile.class.getName(), localFile.getAbsolutePath() + "  " + localFile.exists());

            Socket client = new Socket(MainApp.getCurrentServer().getHost(), ConfigPreferences.getInstance().getServerFilePort());
            bis = new BufferedInputStream(new FileInputStream(localFile));
            bos = new BufferedOutputStream(client.getOutputStream());
            //Enviamos el nombre del fichero
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
            dos.writeUTF(String.valueOf(file_id));
            //Enviamos el fichero
            byteArray = new byte[1024];
            int progreso = 0;
            while ((in = bis.read(byteArray)) != -1) {
                bos.write(byteArray, 0, in);
                progreso += in;

                GlobalBus.getBus().post( new Events.EventFile(new Archivo(localFile.getName(),localFile.getPath(),file_size,false), Events.FileAction.UPDATE_PERCENT).setPercent((double)progreso/(double)file_size) );

                /*mNotifyBuilder.setProgress((int) file_size, progreso, false);
                mNotificationManager.notify(numMessages, mNotifyBuilder.build());*/
            }

            bis.close();
            bos.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        System.out.println( "SESESESE: END" );

        final File localFile = new File(file);
        GlobalBus.getBus().post( new Events.EventFile(new Archivo(localFile.getName(),localFile.getPath(),file_size,false), Events.FileAction.UPDATE_PERCENT).setPercent( 1.0 ) );

    }
}
