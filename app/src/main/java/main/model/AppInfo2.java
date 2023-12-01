package main.model;

import java.io.Serializable;

/**
 * Created by guillermo on 24/10/16.
 *
 *
 *
 * @class   GetInstalledApps
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf
 */
public class AppInfo2 implements Serializable {
    private static final long serialVersionUID = 80L;
    private String packageName;
    private byte[] icon;
    private String name;

    public AppInfo2() {
    }

    public AppInfo2(String packageName, byte[] icon) {
        this.packageName = packageName;
        this.icon = icon;
    }

    public AppInfo2(String packageName, byte[] icon, String name) {
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
}
