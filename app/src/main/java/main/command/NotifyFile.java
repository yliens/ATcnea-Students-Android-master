package main.command;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import main.model.User;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.file.networking.SocketClientFile;
import uci.atcnea.student.file.networking.SocketServerFile;
import uci.atcnea.student.utils.ATcneaUtil;

/**
 * @class   NotifyFile
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf Enviar archivo a los estudiantes



 */
public class NotifyFile implements CommandInterface,Serializable {

    private static final long serialVersionUID = 2125L;

    private long id;
    private String fileName;
    private long fileSize;
    private boolean isStdToTcher;
    private String fileDir;
    private boolean isCancel;
    private boolean isFromTask;


    public NotifyFile() {
        id = (new java.util.Date()).getTime();
        isCancel = false;
        isStdToTcher = true;
    }

    @Override
    public OperationResult execute(Context applicationContext) {

        if (isStdToTcher == true) {
            if (isCancel) {

                MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ATcneaUtil.showInformationDialog(MainApp.getCurrentActivity().getResources().getString(R.string.title_activity_file_cancelled), MainApp.getCurrentActivity().getResources().getString(R.string.dialog_file_message_cancelled), false);
                        //   ((MainActivity) MainApp.getCurrentActivity()).showInformationDialog(MainApp.getCurrentActivity().getResources().getString(R.string.title_activity_file_cancelled), MainApp.getCurrentActivity().getResources().getString(R.string.dialog_file_message_cancelled), false);
                    }
                });
            } else {
                for (CommandInterface nf : ATcneaUtil.onWaiting[0]) {

                    if (id == ((NotifyFile) nf).id) {
                        SocketClientFile clientFile = new SocketClientFile(((NotifyFile) nf).getFileDir(), fileSize, id, applicationContext, isFromTask);
                        clientFile.execute();
                        break;
                    }
                }
            }
        } else if (isStdToTcher == false) {
            SocketServerFile serverFile = new SocketServerFile(fileName, fileSize, applicationContext);
            serverFile.execute();
            return new OperationResult(OperationResult.ResultCode.OK);
        }
        return null;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFromTask(boolean fromTask) {
        isFromTask = fromTask;
    }
}
