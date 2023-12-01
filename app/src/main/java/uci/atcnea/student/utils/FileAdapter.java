package uci.atcnea.student.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import main.model.Archivo;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;

/**
 * @class   FileAdapter
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf Archivos
 */
//public class FileAdapter extends BaseAdapter {
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileItem> {

    private List<Archivo> archivos;
    private Context context;

    public FileAdapter(Context context) {
        this.context=context;

        archivos = new LinkedList<>();

        //Leer de la BD el listado de archivos
        if(MainApp.isConnected()) {
            ArrayList<Archivo> listOfDB = MainApp.getDatabaseManager().listFiles();

            if (listOfDB != null) {
                archivos.addAll(listOfDB);
            }
        }

    }

    public void updateListOfFiles(){
        archivos.clear();
        ArrayList<Archivo> listOfDB = MainApp.getDatabaseManager().listFiles();
        if(listOfDB != null) {
            archivos.addAll( listOfDB );
        }
    }

    public List<Archivo> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<Archivo> archivos) {
        this.archivos = archivos;
    }

    public void addFile(Archivo archivo){
        archivos.add(0, archivo);
    }

    @Override
    public int getItemViewType(int position) {
        return archivos.get(position).isSent()?0:1;//super.getItemViewType(position);
    }

    @Override
    public FileItem onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0)
            return new FileItem( View.inflate(parent.getContext(),R.layout.file_user,null) );
        return new FileItem( View.inflate(parent.getContext(),R.layout.file_teacher,null) );
    }

    @Override
    public synchronized void onBindViewHolder(FileItem holder, int position) {
        holder.setArchivo( archivos.get(position) );
        Log.e("Update",archivos.get(position).getPercent()+"");
        holder.updatePercent();
    }

    @Override
    public void onBindViewHolder(FileItem holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        onBindViewHolder(holder,position);
    }

    @Override
    public int getItemCount() {
        return archivos==null?0:archivos.size();
    }

    /**
     * Items para cada elemento en el adaptador de los archivos
     */
    public class FileItem extends RecyclerView.ViewHolder{

        private Archivo archivo;

        public FileItem(View itemView) {
            super(itemView);
        }

        private LinearLayoutProgress fileContainer;

        /**
         * Actualizar porciento
         */
        public void updatePercent(){
            //Porciento
            fileContainer.updateProgress( archivo.getPercent() );
            Log.d("Update percent", archivo.getPercent() + "");
            fileContainer.Draw();
        }

        public void setArchivo(final Archivo archivo) {
            this.archivo = archivo;
            //Captura de elementos visuales
            CircleImageView fileUser = (CircleImageView) itemView.findViewById( R.id.file_user );
            TextView fileName = (TextView) itemView.findViewById( R.id.file_name );
            TextView fileDir = (TextView) itemView.findViewById( R.id.file_dir );
            CircleImageView fileOpen = (CircleImageView) itemView.findViewById( R.id.file_open );
            fileContainer = (LinearLayoutProgress) itemView.findViewById( R.id.file_container );

            //Orientacion a la derecha del dialogo
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            if( archivo.isSent() ) {
                params.gravity = Gravity.RIGHT;

                // Foto del profesor
                if (MainApp.getCurrentLesson().getImageTeacher().length > 2)
                    fileUser.setImageBitmap(DbBitmapUtility.getImage(MainApp.getCurrentLesson().getImageTeacher()));

            }else{
                params.gravity = Gravity.LEFT;

                // Foto del estudiante
                fileUser.setImageURI( Uri.parse(MainApp.getCurrentUser().getImagePath()) );

            }
            itemView.setLayoutParams( params );

            //Datos de los elementos
            fileName.setText( archivo.getName() );
            fileDir.setText( archivo.getPath() );

            //Porciento
            fileContainer.updateProgress( archivo.getPercent() );

            //Evento de abrir archivo
            fileOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalBus.getBus().post( new Events.EventFile(archivo, Events.FileAction.EXECUTE_FILE));
                }
            });
        }

    }
}