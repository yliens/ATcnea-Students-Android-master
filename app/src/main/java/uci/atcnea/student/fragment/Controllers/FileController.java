package uci.atcnea.student.fragment.Controllers;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import main.command.NotifyFile;
import main.model.Archivo;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.interfaces.SyncTaskResultListenerInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.dao.FileRecord;
import uci.atcnea.student.dao.Resource;
import uci.atcnea.student.dao.TaskRecord;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.fragment.FileFragment;
import uci.atcnea.student.utils.ATcneaUtil;
import uci.atcnea.student.utils.GetFileRealPath;

/**
 * Created by guillermo on 24/01/17.
 */
public class FileController {
    private static FileController intance;

    public static FileController getIntance() {
        if (intance == null) {
            intance = new FileController();
            GlobalBus.getBus().register(intance);

            intance.newFiles = 0;
        }
        return intance;
    }

    public FileController() {
    }

    /**
     * Destroit intance of controller
     */
    public static void destroitIntance() {
        if (intance != null)
            GlobalBus.getBus().unregister(intance);
        intance = null;
    }

    // Intancia del fragment File
    public FileFragment fragment;

    // Conteo de nuevos archivos transferidos (para badge)
    public int newFiles;

    /**
     * Enviar archivo
     *
     * @param file Archivo a enviarse
     */
    public synchronized void sendFile(File file, boolean FromTask) {

        String finalPath = file.getPath();

        SendMessageService msg = new SendMessageService(MainApp.getCurrentServer());
        final NotifyFile notifyf = new NotifyFile();
        notifyf.setFileName(file.getName());
        notifyf.setFileSize(file.length());
        notifyf.setFromTask(FromTask);
        notifyf.setFileDir(finalPath);
        //notifyf

        msg.setWaitForResponse(true);
        msg.setCommand(notifyf);

        msg.setCallback(new SyncTaskResultListenerInterface() {
            @Override
            public void onSyncTaskEventCompleted(OperationResult result, String ip) {

                if (result.getCode() == OperationResult.ResultCode.OK) {
                    if (ATcneaUtil.onWaiting[0] == null)
                        ATcneaUtil.onWaiting[0] = Collections.synchronizedList( new LinkedList<CommandInterface>() );//new LinkedList<CommandInterface>();
                    ATcneaUtil.onWaiting[0].add(notifyf);


                } else if (result.getCode() == OperationResult.ResultCode.CANCELLED) {
                    ATcneaUtil.showInformationDialog(
                            MainApp.getCurrentActivity().getResources().getString(R.string.title_activity_file_cancelled),
                            MainApp.getCurrentActivity().getResources().getString(R.string.dialog_file_message_cancelled),
                            false
                    );
                } else if (result.getCode() == OperationResult.ResultCode.ERROR) {

                    int maxSize = 0;

                    if (result.getData() != null && !result.getData().isEmpty()) {
                        maxSize = (Integer) result.getData().get(0);
                    }

                    ATcneaUtil.showInformationDialog(
                            MainApp.getCurrentActivity().getResources().getString(R.string.title_activity_file_cancelled),
                            MainApp.getCurrentActivity().getResources().getString(R.string.dialog_file_message_error) + " " + maxSize +
                                    " " + MainApp.getCurrentActivity().getResources().getString(R.string.dialog_file_message_error2),
                            false
                    );
                }
            }
        });

        msg.execute();
    }

    /**
     * Abrir archivo
     *
     * @param file Archivo a enviarse
     */
    public synchronized void openFile(Archivo file) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        Uri uri = Uri.parse(file.getPath());
//        intent.setDataAndType(uri, getMimeType(file.getPath()));
//        MainApp.getCurrentActivity().startActivity(Intent.createChooser(intent, file.getName()));

        try {
            GetFileRealPath.openFile(MainApp.getCurrentActivity(), new File(file.getPath()) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
            Intent newIntent = new Intent(Intent.ACTION_VIEW);
            String mimeType = GetFileRealPath.MimeType(file.getPath());
            Log.d("FILE_PATH", file.getPath());
            //Log.d("MIME_TYPE", mimeType);
            newIntent.setDataAndType(Uri.parse(file.getPath()), mimeType);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainApp.getCurrentActivity().startActivity(newIntent);
        } catch (Exception e) {
            String title = MainApp.getIntance().getResources().getString(R.string.dialogFileTitle);
            String text = MainApp.getIntance().getResources().getString(R.string.task_open_file_error);
            ATcneaUtil.showInformationDialog(title, text, true);
        }*/

    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);

        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        // Log.e("TIPE",type);
        return type;
    }


    //----------------------------------------------------------//
    //                Eventos del bus de eventos                //
    //----------------------------------------------------------//
    //Evento de file
    @Subscribe
    public void eventFile(Events.EventFile eventFile) {
        switch (eventFile.fileAction) {
            case NOTIFY_FILE_COPY://Notificar nueva copia
                if (fragment != null)
                    fragment.addFileToAdapter(eventFile.file);

                Log.d("FILE_COPY", "copy");

                //Registrar en la BD
                Resource resource = MainApp.getDatabaseManager().insertResource(new Resource(
                        null,
                        eventFile.file.getName(),
                        eventFile.file.getSize(),
                        eventFile.file.getPath(),
                        (eventFile.userId == null) ? -1 : eventFile.userId,//Hay q ver de q forma capturar el id del que crea el recurso
                        MainApp.getCurrentLesson().getId()
                ));
                MainApp.getDatabaseManager().insertFileRecord((new FileRecord(
                        null,
                        "",
                        0L,
                        ((uci.atcnea.student.dao.User) MainApp.getCurrentUserDao()).getId(),
                        resource.getId()
                )));

                // Para actualizar el badge de archivos
                newFiles++;
                GlobalBus.getBus().post(new Events.EventBadge(Events.BadgeType.FILE, newFiles));

                break;
            case NOTIFY_FILE_COPY_TO_TASK:

                Log.d("FILE_COPY_TO_TASK", "copy");

                //Notificar archivo a la tarea
                GlobalBus.getBus().post(new Events.EventTask(Events.TaskAction.RESOURCE_CHANGE));

                //Registrar en la BD
                resource = MainApp.getDatabaseManager().insertResource(new Resource(
                        null,
                        eventFile.file.getName(),
                        eventFile.file.getSize(),
                        eventFile.file.getPath(),
                        (eventFile.userId == null) ? -1 : eventFile.userId, //Hay q ver de q forma capturar el id del que crea el recurso
                        MainApp.getCurrentLesson().getId()
                ));
                MainApp.getDatabaseManager().insertTaskRecord(new TaskRecord(
                        null,
                        true,
                        eventFile.taskId,
                        resource.getId()
                ));

                break;
            case NOTIFY_FILE_COPY_FROM_TASK:
                if (fragment != null)
                    fragment.addFileToAdapter(eventFile.file);
                break;
            case SEND_FILE_FROM_TASK://Enviar archivo
                File file = new File(eventFile.file.getPath());
                FileController.getIntance().sendFile(file, true);
                break;
            case EXECUTE_FILE://Ejecutar archivo
                FileController.getIntance().openFile(eventFile.file);
                break;
        }
    }
}
