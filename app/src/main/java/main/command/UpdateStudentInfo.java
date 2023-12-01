package main.command;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;

import java.io.Serializable;

import main.model.User;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.utils.ATcneaUtil;
import uci.atcnea.student.utils.DbBitmapUtility;
import uci.atcnea.student.utils.PhotoUtils;
import uci.atcnea.student.utils.TaskHelper;

/**
 * @class   UpdateStudentInfo
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public class UpdateStudentInfo implements CommandInterface, Serializable {
    private static final long serialVersionUID = 9313L;
    private static final String TAG = "UpdateStudentInfo";

    private String imageIcon;
    private String profileJson;
    private String password;

    public UpdateStudentInfo() {
        try {
            User studentCommand = MainApp.getCurrentUser();
            this.imageIcon = Base64.encodeToString(DbBitmapUtility.getBytes(new BitmapDrawable(studentCommand.getImagePath())), Base64.DEFAULT); //DbBitmapUtility.getBytes(MainApp.getCurrentUser().getIcon().getIconDrawable());
            this.profileJson = studentCommand.getProfileJson();
            this.password = studentCommand.getPassword();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public OperationResult execute(Context applicationContext) {

        MainApp.getCurrentUserDao().setProfileJson(profileJson);
        MainApp.getCurrentUserDao().setUsername(MainApp.getCurrentUser().getUsername());
        MainApp.getCurrentUserDao().setPassword(password);

        //Revisar porque cuando mando el perfil si no viene la imagen
        //se queda sin imagen
        if( !imageIcon.equals("")) {
            PhotoUtils photoUtils = new PhotoUtils(applicationContext);

            byte[] decode = Base64.decode(imageIcon, Base64.DEFAULT);
            String imagePath = photoUtils.saveImageProfile(DbBitmapUtility.getImage(decode));

            MainApp.getCurrentUserDao().setImagePath(imagePath);
        }
        MainApp.getDatabaseManager().updateStudent(MainApp.getCurrentUserDao());
        ((MainActivity) MainApp.getCurrentActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) MainApp.getCurrentActivity()).updateProfile(MainApp.getCurrentUser());
            }
        });



        return new OperationResult(OperationResult.ResultCode.OK);
    }
}
