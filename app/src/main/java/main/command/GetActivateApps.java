package main.command;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


import main.model.AppInfo2;
import main.model.User;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.utils.DbBitmapUtility;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.USAGE_STATS_SERVICE;

/**
 * Created by guillermo on 24/10/16.
 */
public class GetActivateApps implements CommandInterface, Serializable {

    private static final long serialVersionUID = 12346;

    private static final String TAG = "GetActivateApps";

    public GetActivateApps() {
    }

    @Override
    public OperationResult execute(Context applicationContext) {
        OperationResult result = new OperationResult();
        PackageManager pm = applicationContext.getPackageManager();

        //OPTIMIZAR LA FORMA DE USARLO


        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<Object> tmp = new ArrayList<>();

        boolean show = true;



        for (ApplicationInfo packageInfo : packages) {

            for (String item : getProcces()) {


                if (item.equals(packageInfo.processName) && !item.equals("uci.atcnea.student") /*&& (processState == 10 || processState == 2 || processState == 11)*/) {

                    AppInfo2 appInfo = new AppInfo2(packageInfo.packageName, DbBitmapUtility.getBytes(pm.getApplicationIcon(packageInfo)), pm.getApplicationLabel(packageInfo).toString());

                    tmp.add(appInfo);

                    break;

                }

            }

        }
        result.setData(tmp);

        return result;
    }


    public static List<String> getProcces() {
        List<String> proc=new ArrayList<>();
        try {
            // Run the command
            String[] cmd = {
                    "sh",
                    "-c",
                    "ps  | grep u0_"
            };
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            // Grab the results
            StringBuilder log = new StringBuilder();
            String line;
            String regex = "^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+[0-9a-z_]?$";
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split("\\s+");
                if (split.length > 8 && split[8].matches(regex)) {
                    log.append(split[8] + "\n");
                    proc.add(split[8]);
                }
            }
            Log.e("PRO", log.toString());

            log.toString().split(" ");
            return proc;
        } catch (IOException e) {
        }
        return proc;
    }
}
