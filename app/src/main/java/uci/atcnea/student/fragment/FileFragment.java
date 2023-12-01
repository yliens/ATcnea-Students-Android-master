
package uci.atcnea.student.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;

import main.command.NotifyFile;
import main.model.Archivo;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.interfaces.SyncTaskResultListenerInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;

import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.dao.FileRecord;
import uci.atcnea.student.dao.Resource;
import uci.atcnea.student.dao.TaskRecord;
import uci.atcnea.student.dao.User;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.fragment.Controllers.FileController;
import uci.atcnea.student.utils.ATcneaUtil;
import uci.atcnea.student.utils.FileAdapter;
import uci.atcnea.student.utils.GetFileRealPath;

/**
 * @author Adrian Arencibia Herrera
 *         Copyright (c) 2016-2017 FORTES, UCI.
 * @version 1.0
 * @class FileFragment
 * @date 7/07/16
 * @description
 * @rf Archivos
 */
public class FileFragment extends Fragment {
    private static final int FILE_SELECT_CODE = 1;
    private static final int RESULT_OK = -1;
    FloatingActionButton fab_send;
    //private ListViewCompat listViewFiles;
    private RecyclerView listViewFiles;
    private FileAdapter fileAdapter;

    public FileFragment() {
        fileAdapter = new FileAdapter(getContext());
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_file, null);
        fab_send = (FloatingActionButton) rootView.findViewById(R.id.fab_send_file);
        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });


        //listViewFiles = (ListViewCompat) rootView.findViewById(R.id.list_file);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainApp.getCurrentActivity());
        listViewFiles = (RecyclerView) rootView.findViewById(R.id.list_file);
        if (fileAdapter == null)
            fileAdapter = new FileAdapter(getContext());
        listViewFiles.setAdapter(fileAdapter);
        listViewFiles.setLayoutManager(linearLayoutManager);

        listViewFiles.setLayoutManager( new LinearLayoutManager(getActivity()) );

        FileController.getIntance().fragment = this;

        //listViewFiles.setHasFixedSize( true );
        return rootView;
    }

    /**
     * Funcion para actualizar el listado de archivos transferidos
     */
    public void updateListOfFiles(){
        MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fileAdapter.updateListOfFiles();
                fileAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Mostrar el selector de archivos
     * @param
     * @return String ...
     * @rf ...
     */
    private void showFileChooser() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        try {
            // Para internacionalizar
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Send"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getContext(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showFileChooser2() {
        //   Intent intent = new Intent(getContext(), ExplorerActivity.class);
        //   startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {

                    try {

                        String path = GetFileRealPath.getPath(getContext(), data.getData());

                        final File file = new File(path);

                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle(R.string.dialogFileTitle);
                        dialog.setMessage(getResources().getString(R.string.dialogFileMessage) + "\n" + file.getName());
                        dialog.setNegativeButton(R.string.negativeButton, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        final String finalPath = path;
                        dialog.setPositiveButton(R.string.positiveButtonSendFile, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                System.out.println("FILEE " + file.getAbsolutePath());
                                FileController.getIntance().sendFile(file, false);

                            }
                        });

                        dialog.create().show();
                    }catch (Exception ex){
                        Toast mje = Toast.makeText(getActivity(), R.string.task_open_file_error, Toast.LENGTH_LONG);
                        mje.show();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* For extract the path of any uri directions */
    /*public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.KITKAT;
        Log.i("URI",uri+"");
        String result = uri+"";
        // DocumentProvider
        //  if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        if (isKitKat && (result.contains("media.documents"))) {

            String[] ary = result.split("/");
            int length = ary.length;
            String imgary = ary[length-1];
            final String[] dat = imgary.split("%3A");

            final String docId = dat[1];
            final String type = dat[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {

            } else if ("audio".equals(type)) {
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[] {
                    dat[1]
            };

            return getDataColumn(context, contentUri, selection, selectionArgs);
        }
        else
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }*/

    /**
     * Agreagar archivo al listado visual
     * @param file archivo a ser agregado
     */
    public synchronized void addFileToAdapter(Archivo file) {

        if(listViewFiles != null) {

            ((FileAdapter) listViewFiles.getAdapter()).addFile(file);
            MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((FileAdapter) listViewFiles.getAdapter()).notifyDataSetChanged();
                }
            });

        }
    }

    public static int positionGlobal;
    public static double percentGlobal;

    /**
     * Actualizar el porciento de copia de un archivo
     * @param file Archivo a actualizar
     * @param percent Porciento de copia
     */
    public synchronized void updatePercent(Archivo file, double percent){
        for(int i = 0; i < fileAdapter.getArchivos().size();i++){
            if(fileAdapter.getArchivos().get(i).getName().equals( file.getName() )){
                positionGlobal = i;
                percentGlobal = percent;

                MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileAdapter.getArchivos().get(positionGlobal).setPercent( percentGlobal );
                        //Esto debe ser mejorado algun dia
                        fileAdapter.notifyItemChanged(positionGlobal);
                    }
                });

                return;
            }
        }
    }

    public FileAdapter getFileAdapter() {
        return fileAdapter;
    }

    /**
     * Actualizar el porciento de copia del archivo q se esta copiando
     * @param eventFile
     */
    @Subscribe
    public void eventFile(Events.EventFile eventFile){
        switch ( eventFile.fileAction ) {
            case UPDATE_PERCENT://Actualizar porciento
                Log.i("Percent",eventFile.percent+"");
                updatePercent(eventFile.file,eventFile.percent);
                break;
            case UPDATE_LIST:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileAdapter.notifyDataSetChanged();
                    }
                });
                break;
        }
    }

    /**
     * Registrar clase en el bus de eventos
     */
    @Override
    public void onStart() {
        super.onStart();
        GlobalBus.getBus().register( this );
    }

    /**
     * Unregister clase en el bus de eventos
     */
    @Override
    public void onStop() {
        super.onStop();
        GlobalBus.getBus().unregister( this );
    }
}
