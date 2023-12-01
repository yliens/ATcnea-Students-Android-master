package main.command;

import android.content.Context;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.utils.ConfigPreferences;

/**
 * Created by adrian on 6/02/17.
 */

public class UpdateLessonPort implements CommandInterface, Serializable {
    private static final long serialVersionUID = 326956;

    private String clientFilePort;
    private String serverFilePort;

    public UpdateLessonPort() {
    }

    public UpdateLessonPort(String clientFilePort, String serverFilePort) {
        this.clientFilePort = clientFilePort;
        this.serverFilePort = serverFilePort;
    }

    @Override
    public OperationResult execute(Context applicationContext) {


        ConfigPreferences.getInstance().setClienFilePort(Integer.parseInt(clientFilePort));
        ConfigPreferences.getInstance().setServerFilePort(Integer.parseInt(serverFilePort));

        try {
            if (MainApp.getIntance().getServerSocketFile() != null)
                MainApp.getIntance().getServerSocketFile().close();
            MainApp.getIntance().setServerSocketFile(new ServerSocket(ConfigPreferences.getInstance().getClienFilePort()));

        } catch (IOException e) {
            e.printStackTrace();
        }

    return new OperationResult(OperationResult.ResultCode.OK);

   }
}
