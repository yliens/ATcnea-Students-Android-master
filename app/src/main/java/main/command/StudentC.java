package main.command;

import android.content.Context;


import java.io.Serializable;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;

/**
 * Created by yerandy on 22/07/16.
 */
public class StudentC implements CommandInterface,Serializable {

    private static final long serialVersionUID = 9321;

    private String username;
    private String password;
    private String profile_json;
    private String imagepath;
    private String mac;
    private String ip;


    //private Connection connection;

    public StudentC() {
    }

    @Override
    public OperationResult execute(Context applicationContext) {
        return null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileJson() {
        return profile_json;
    }

    public void setProfileJson(String profile_json) {
        this.profile_json = profile_json;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
