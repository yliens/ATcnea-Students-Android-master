package main.command;

import android.content.Context;
import android.util.Base64;

import java.io.Serializable;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;

/**
 * Created by guillermo on 15/02/17.
 */

public class UpdateTeacherInfo implements CommandInterface, Serializable {

    private static final long serialVersionUID = 931993L;

    private String imageIcon;

    @Override
    public OperationResult execute(Context applicationContext) {

        // Cambiar imagen del profesor
        byte[] imageT = new byte[0];
        try {
            imageT = Base64.decode(imageIcon, Base64.DEFAULT);
            MainApp.getCurrentLesson().setImageTeacher(imageT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GlobalBus.getBus().post(new Events.EventTeacherChange());
        GlobalBus.getBus().post(new Events.EventFile(null, Events.FileAction.UPDATE_LIST));

        return null;
    }
}
