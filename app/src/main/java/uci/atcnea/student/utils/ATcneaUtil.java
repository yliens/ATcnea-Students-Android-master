package uci.atcnea.student.utils;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.List;
import java.util.Random;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.model.InteractiveQuestion;
import uci.atcnea.student.model.Quiz;

/**
 * @class   ATcneaUtil
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf
 */
public class ATcneaUtil {

    public static final int PROFILE_SETTING = 1;
    public static final int FRAGMENT_SELECT_STUDENT = 7;
    public static final int PROFILE_SETTING_IMAGE = 6;
    public static final int DRAWER_HOME_IDENTIFIER = 2;
    public static final int DRAWER_FILE_IDENTIFIER = 3;
    public static final int DRAWER_CHAT_IDENTIFIER = 4;
    public static final int DRAWER_RA_IDENTIFIER = 5;
    public static final int DRAWER_BOARD_IDENTIFIER = 100;
    public static final int DRAWER_TASK_IDENTIFIER = 10000;
    public static final int PROFILE_SETTING_CHANGE_USER = 8;
    public static final int PROFILE_SETTING_CREATE = 9;

    public static List<CommandInterface>[] onWaiting = new List[1];

    public static final int FRAGMENT_DISCOVERY = 11;


    public static final int DRAWER_HEADER_PROFILE = 13;

    public static final int FRAGMENT_BLANK_DEFAULT = 12;

    //Banderas para los hilos de times en cuestionario
    public static boolean FLAG_INTERACTIVE_QUESTION_RUN = true;

    public static InteractiveQuestion question;

    public static Quiz quiz;


    public static int getScreenOrientation() {
        return Resources.getSystem().getConfiguration().orientation;
    }

    /**
     * small helper method to reuse the logic to build the AccountHeader
     * this will be used to replace the header of the drawer with a compact/normal header
     *
     * @param compact
     * @param savedInstanceState
     */
    public static AccountHeaderBuilder buildDrawerHeader(boolean compact, Bundle savedInstanceState) {
        // Create the AccountHeader
        // Create a few sample userProfile

        //ArrayList<main.model.User> students = new ArrayList<>();
        //students.add(MainApp.getCurrentUser());


//        if (MainApp.getDatabaseManager().listStudent() != null) {
//            students.addAll(MainApp.getDatabaseManager().listStudent());
//        }





       /*
        if (MainApp.getCurrentUser().getImage() != null) {
            userProfile.withIcon(Uri.parse(MainApp.getCurrentUser().getImage()));
        }
        */
        //.withHeaderBackground(R.drawable.header_yellow)
        AccountHeaderBuilder accountHeaderResult = new AccountHeaderBuilder()
                .withActivity(MainApp.getCurrentActivity())
                .withHeaderBackground(R.drawable.wallpp)
                .withTranslucentStatusBar(false);

//        for (main.model.User student : students) {
//            accountHeaderResult.addProfiles(student);
//        }
        accountHeaderResult.addProfiles(
                MainApp.getCurrentUser(),
                new ProfileSettingDrawerItem().withName(MainApp.getCurrentActivity().getResources().getString(R.string.profile_setting_details))
                        .withIcon(R.drawable.ic_edit)
                        .withIdentifier(PROFILE_SETTING),
                new ProfileSettingDrawerItem().withName(MainApp.getCurrentActivity().getResources().getString(R.string.profile_setting_change_user))
                        .withIcon(R.drawable.app_android_general_logout)
                        .withIdentifier(PROFILE_SETTING_CHANGE_USER)


        )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        //false if you have not consumed the event and it should close the drawer
                        switch ((int) profile.getIdentifier()) {
                            case PROFILE_SETTING:
                                DialogBuilder.dialogEditProfile((MainActivity) MainApp.getCurrentActivity()).show();

                                break;
                            case PROFILE_SETTING_IMAGE:
                                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                galleryIntent.setType("image/*");
                                MainApp.getCurrentActivity().startActivityForResult(galleryIntent, MainActivity.ACTIVITY_SELECT_IMAGE);
                                break;

                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState);

        return accountHeaderResult;
    }

    /**
     * Listado de items del drawer
     *
     * @return IDrawerItem[]
     */
    public static IDrawerItem[] buildDrawersItems() {
        IDrawerItem[] listDrawer = new IDrawerItem[5];

        PrimaryDrawerItem itemHome = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_menu_home)
                .withIcon(R.drawable.app_android_menu_home)
                //.withBadgeStyle(new BadgeStyle(Color.RED, Color.RED))
                .withIdentifier(DRAWER_HOME_IDENTIFIER)
                .withSelectedIcon(R.drawable.ic_app_android_menu_home)
                //.withSelectedColor(Color.parseColor("#FFFFFF"))


                .withSelectable(true);

        PrimaryDrawerItem itemFiles = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_menu_files)
                .withIcon(R.drawable.app_android_menu_file)
                .withSelectedIcon(R.drawable.ic_app_android_menu_file)
                .withIdentifier(DRAWER_FILE_IDENTIFIER);

        PrimaryDrawerItem itemChat = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_menu_chat)
                .withIcon(R.drawable.app_android_menu_chat)
                .withSelectedIcon(R.drawable.ic_app_android_menu_chat)
                .withIdentifier(DRAWER_CHAT_IDENTIFIER);

        PrimaryDrawerItem itemRelityAum = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_menu_ra)
                .withIcon(R.drawable.app_android_menu_ar)
                .withSelectedIcon(R.drawable.ic_app_android_menu_ar)
                .withIdentifier(DRAWER_RA_IDENTIFIER);

        PrimaryDrawerItem itemBoard = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_menu_board)
                .withIcon( R.drawable.app_android_menu_board )
                .withSelectedIcon(R.drawable.ic_app_android_menu_board)
                .withIdentifier(DRAWER_BOARD_IDENTIFIER);

        PrimaryDrawerItem itemTask = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_menu_task)
                .withIcon( R.drawable.app_android_menu_homework)
                .withSelectedIcon(R.drawable.ic_app_android_menu_homework)
                .withIdentifier(DRAWER_TASK_IDENTIFIER);

        listDrawer[0] = itemHome;
        listDrawer[3] = itemFiles;
        listDrawer[1] = itemChat;
        //listDrawer[3] = itemRelityAum;
        listDrawer[2] = itemBoard;
        listDrawer[4] = itemTask;

        return listDrawer;
    }


    public static IDrawerItem[] buildOffLineDrawersItems() {
        IDrawerItem[] listDrawer = new IDrawerItem[1];

        PrimaryDrawerItem itemHome = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_menu_home)
                .withIcon(R.drawable.app_android_menu_home)
                //.withBadgeStyle(new BadgeStyle(Color.RED, Color.RED))
                .withIdentifier(DRAWER_HOME_IDENTIFIER)
                //.withBadge("0")
                .withSelectable(true);


        listDrawer[0] = itemHome;


        return listDrawer;
    }

    public static IDrawerItem[] buildDrawersFixedItems() {

        return null;
    }

    public static void showLockHandDialog(String title, String msg, Boolean good) {
        if (Lock.lockScreen) {
            Intent intent = new Intent("LOCK_HAND_LOCK_SCREEN");
            intent.putExtra("GOOD_HAND", good);
            intent.putExtra("MSG_HAND", msg);

            MainApp.getCurrentActivity().sendBroadcast(intent);
        } else {
            MainApp.getCurrentActivity().sendBroadcast(new Intent("LOCK_HAND_HOME_FRAGMENT"));
            ATcneaUtil.showInformationDialog(title, msg, good);
        }
    }

    public static AlertDialog builderMessage;

    public static void HideMessage(){
        if(builderMessage != null){
            MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builderMessage.dismiss();
                }
            });
        }
    }

    public static void showInformationDialog(String title, String msg, Boolean good) {

        HideMessage();

        builderMessage = new AlertDialog.Builder(
                MainApp.getCurrentActivity())
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        builderMessage.setTitle(title);
        builderMessage.setMessage(msg);
        if (good) {
            builderMessage.setIcon(R.drawable.ic_accept);
        } else {
            builderMessage.setIcon(R.drawable.ic_deny);
        }
//        builder.setMessage(msg);

        builderMessage.show();

    }

    public static void ShowMessageConfirmation(final String Message){

        HideMessage();

        MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builderMessage = DialogBuilder.dialogInformation(MainApp.getCurrentActivity(), Message).create();
                builderMessage.show();
            }
        });
    }

    public static void ShowAllowDenyHandDialog(String title, String msg, boolean good) {

        if (Lock.lockScreen) {
            Intent intent = new Intent("MSG_HAND_LOCK_SCREEN");
            intent.putExtra("GOOD_HAND", good);
            intent.putExtra("MSG_HAND", msg);

            MainApp.getCurrentActivity().sendBroadcast(intent);
        } else {
            ATcneaUtil.showInformationDialog(title, msg, good);
        }
    }

    /**
     * Generate random mac
     * @return
     */
    public static String randomMACAddress(){
        Random rand = new Random();
        byte[] macAddr = new byte[6];
        rand.nextBytes(macAddr);

        macAddr[0] = (byte)(macAddr[0] & (byte)254);  //zeroing last 2 bytes to make it unicast and locally adminstrated

        StringBuilder sb = new StringBuilder(18);
        for(byte b : macAddr){

            if(sb.length() > 0)
                sb.append(":");

            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
