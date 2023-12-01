package main.model;

import android.net.Uri;

import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;

/**
 * @class   User
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf Crear una cuenta
 */
public class User extends ProfileDrawerItem implements Serializable {

    private static final long serialVersionUID = 865077;

    //String username, String userNick, String password, String mac

    private String username;
    private String password;
    private String profileJson;
    private String imagePath;

    private String mac = MainApp.MAC_ADDRESS;
    private String ip = MainApp.IP_ADDRESS;

    private Integer id;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.mac = MainApp.MAC_ADDRESS;
        this.ip = MainApp.IP_ADDRESS;

        id=(int)(new Date()).getTime();
        super.withIdentifier(id);
    }

    public User(String username) {
        this.username = username;
        this.password = "";
        this.mac = MainApp.MAC_ADDRESS;
        this.ip = MainApp.IP_ADDRESS;
        id=(int)(new Date()).getTime();
        super.withIdentifier(id);
    }

    public User(String username, String password, String profileJson, String imagePath) {
        this.mac = MainApp.MAC_ADDRESS;
        this.ip = MainApp.IP_ADDRESS;
        this.username = username;
        this.password = password;
        this.profileJson = profileJson;
        this.imagePath = imagePath;
        if(imagePath.length()>0)
        super.withIcon(Uri.parse(imagePath));
        else
        super.withIcon(R.drawable.face1);

        id=(int)(new Date()).getTime();
        super.withIdentifier(id);
    }

    public User(User currentUser) {
        this.mac = MainApp.MAC_ADDRESS;
        this.ip = MainApp.IP_ADDRESS;
        this.username = currentUser.getUsername();
        this.password = currentUser.getPassword();
        this.profileJson =currentUser.getProfileJson();
        this.imagePath =currentUser.getImagePath();
        if(imagePath.length()>0)
            super.withIcon(Uri.parse(imagePath));
        id=(int)(new Date()).getTime();
        super.withIdentifier(id);
    }

    public String getProfileJson() {
        return profileJson;
    }

    public void setProfileJson(String profileJson) {
        this.profileJson = profileJson;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getMac() {
        return mac;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public StringHolder getName() {
        String name="";
        try {
            JSONObject jsonObject=new JSONObject(profileJson);
            name=jsonObject.getString("name")+" "+jsonObject.getString("lastname");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new StringHolder(name);
    }

    @Override
    public ProfileDrawerItem withUser(String user) {
        return this;
    }

    @Override
    public StringHolder getUser() {

        return new StringHolder(username);
    }

    @Override
    public StringHolder getEmail() {
        String email="";
        try {
            JSONObject jsonObject=new JSONObject(profileJson);
            email=jsonObject.getString("email");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new StringHolder(email);
    }

    @Override
    public ImageHolder getIcon() {
        if(super.getIcon()==null)
        return new ImageHolder(MainApp.getCurrentActivity().getResources().getDrawable(R.drawable.face2));
        else return super.getIcon();
    }

}
