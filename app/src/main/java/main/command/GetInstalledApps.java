package main.command;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import main.model.AppInfo2;
import main.model.User;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.utils.DbBitmapUtility;

/**
 * @class   GetInstalledApps
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf Ver listado de aplicaciones remotas

 */
public class GetInstalledApps implements CommandInterface,Serializable {

    private static final long serialVersionUID = 12347 ;

    private static final String TAG = "GetInstalledApps";

    /**
     *
     */
    public GetInstalledApps() {

    }

    /**
     *
     * @param applicationContext
     * @return
     */
    @Override
    public OperationResult execute(Context applicationContext) {
        OperationResult result = new OperationResult();
        PackageManager pm = applicationContext.getPackageManager();


        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<Object> tmp = new ArrayList<>();
        for (ApplicationInfo packageInfo : packages) {
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            AppInfo2 appInfo=new AppInfo2(packageInfo.packageName, DbBitmapUtility.getBytes(pm.getApplicationIcon(packageInfo)),pm.getApplicationLabel(packageInfo).toString());

            tmp.add(appInfo);

            //Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
            //Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
        }

        result.setData(tmp);

        //return null;
        return result;
    }

    /**
     * @class   GetInstalledApps
     * @version 1.0
     * @date 7/07/16
     * @author Adrian Arencibia Herrera
     * Copyright (c) 2016-2017 FORTES, UCI.
     *
     * @description
     * @rf
     */
    /*public class AppInfo implements Serializable {
        private static final long serialVersionUID = 78L;
        private String packageName;
        private byte[] icon;
        private String name;


        public AppInfo(String packageName, byte[] icon) {
            this.packageName = packageName;
            this.icon = icon;
        }

        public AppInfo(String packageName, byte[] icon, String name) {
            this.packageName = packageName;
            this.icon = icon;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public byte[] getIcon() {
            return icon;
        }

        public void setIcon(byte[] icon) {
            this.icon = icon;
        }
    }*/

}
