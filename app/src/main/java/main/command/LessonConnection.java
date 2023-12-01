package main.command;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import java.io.Serializable;

import main.model.User;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import main.model.User;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.utils.DbBitmapUtility;

/**
 * @class   LessonConnection
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf Iniciar sesión
 */
public class LessonConnection implements CommandInterface, Serializable {

    private static final long serialVersionUID = 9911L;

    private String authenticationType;
    private byte[] imageIcon;
    private String lessonPassword;
    private String username;
    private String password;
    private String profile_json;
    private String imagepath;
    private String mac;
    private String ip;
    //private User userCommand;


    public LessonConnection() {
    }



    public LessonConnection(String authenticationType,String lessonPassword) {
        this.authenticationType = authenticationType;
        //this.userCommand = MainApp.getCurrentUser();
        this.imageIcon = DbBitmapUtility.getBytes(new BitmapDrawable(MainApp.getCurrentUser().getImagePath()));
        username=MainApp.getCurrentUser().getUsername();
        password=MainApp.getCurrentUser().getPassword();
        profile_json=MainApp.getCurrentUser().getProfileJson();
        imagepath=MainApp.getCurrentUser().getImagePath();
        mac=MainApp.getCurrentUser().getMac();
        ip = MainApp.getCurrentUser().getIp();
        this.lessonPassword = lessonPassword;
    }


//    public byte[] getImageIcon() {
//
//        return imageIcon;
//    }
//
//    public void setImageIcon(byte[] imageIcon) {
//        this.imageIcon = imageIcon;
//    }

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

    public String getProfile_json() {
        return profile_json;
    }

    public void setProfile_json(String profile_json) {
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

    public String getLessonPassword() {
        return lessonPassword;
    }

    public void setLessonPassword(String lessonPassword) {
        this.lessonPassword = lessonPassword;
    }

    @Override
    public OperationResult execute(Context applicationContext) {
        return new OperationResult(OperationResult.ResultCode.OK);
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }


}
