package main.command;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.Serializable;
import java.util.Locale;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.activity.MainActivity;

/**
 * Created by guillermo on 2/11/16.
 */
public class ChangeLanguage implements CommandInterface, Serializable {

    private static final long serialVersionUID = 951357L;

    private String language;

    public ChangeLanguage(String language) {
        this.language = language;
    }

    public ChangeLanguage() {
    }

    @Override
    public OperationResult execute(Context applicationContext) {
        switch (language) {
            case "en":
                MainApp.config.locale = new Locale("en");
                break;
            case "fr":
                MainApp.config.locale = new Locale("fr");
                break;
            case "es":
            default:
                MainApp.config.locale = new Locale("es");
        }

        MainApp.getIntance().getBaseContext().getResources().updateConfiguration(MainApp.config,
                MainApp.getIntance().getBaseContext().getResources().getDisplayMetrics());
        MainApp.getCurrentActivity().finish();

        MainApp.getAppContext().startActivity(new Intent(MainApp.getAppContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        Log.e("ChangeLanguage", language);
        return null;
    }
}
