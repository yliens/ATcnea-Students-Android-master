package uci.atcnea.student.utils;

import android.Manifest;
import android.app.Activity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;

import uci.atcnea.student.MainApp;

/**
 * Created by adrian on 9/02/17.
 */
public class GetPermissions {
    private static GetPermissions ourInstance = new GetPermissions();

    public static GetPermissions getInstance() {
        return ourInstance;
    }

    private GetPermissions() {
    }


    /**
     * For ask file permissions in android >=6
     *
     * @param permissionsCallBack
     */
    public static void askForFilePermissions(final PermissionsCallBack permissionsCallBack) {

        Dexter.withActivity(MainApp.getCurrentActivity())
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */
                if (report.areAllPermissionsGranted()) {
                    permissionsCallBack.onSuccess();
                } else
                    permissionsCallBack.onError();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                token.continuePermissionRequest();
                    /* ... */
            }


        }).check();
    }

    /**
     * For ask Internet and network permissions in android >=6
     *
     * @param permissionsCallBack
     */

    public static void askForInternetPermissions(final PermissionsCallBack permissionsCallBack) {

        Dexter.withActivity(MainApp.getCurrentActivity())
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */
                if (report.areAllPermissionsGranted()) {
                    permissionsCallBack.onSuccess();
                } else
                    permissionsCallBack.onError();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                token.continuePermissionRequest();
                    /* ... */
            }


        }).check();
    }

    /**
     * For ask Tasks permissions in android >=6
     *
     * @param permissionsCallBack
     */
    public static void askForTasksPermissions(final PermissionsCallBack permissionsCallBack) {

        Dexter.withActivity(MainApp.getCurrentActivity())
                .withPermissions(
                        Manifest.permission.GET_TASKS,
                        Manifest.permission.REORDER_TASKS,
                        Manifest.permission.KILL_BACKGROUND_PROCESSES
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */
                if (report.areAllPermissionsGranted()) {
                    permissionsCallBack.onSuccess();
                } else
                    permissionsCallBack.onError();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                token.continuePermissionRequest();
                    /* ... */
            }


        }).check();
    }

    /**
     * For ask Other General permissions in android >=6
     *
     * @param permissionsCallBack
     */
    public static void askForGeneralPermissions(final PermissionsCallBack permissionsCallBack) {

        Dexter.withActivity(MainApp.getCurrentActivity())
                .withPermission(Manifest.permission.SYSTEM_ALERT_WINDOW).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                permissionsCallBack.onSuccess();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                permissionsCallBack.onError();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
               /* .withPermissions(
                        Manifest.permission.RECEIVE_BOOT_COMPLETED*//*,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.EXPAND_STATUS_BAR,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.DISABLE_KEYGUARD,
                        Manifest.permission.WRITE_SETTINGS*//*

                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {*//* ... *//*
                if (report.areAllPermissionsGranted()) {
                     permissionsCallBack.onSuccess();
                }
                else
                    permissionsCallBack.onError();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                token.continuePermissionRequest();
                    *//* ... *//*
            }


        }).check();*/
    }

    public interface PermissionsCallBack {
        public void onSuccess();

        public void onError();

    }

}
