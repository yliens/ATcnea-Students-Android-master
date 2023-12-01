package main.command;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.Serializable;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.activity.ShareScreenActivity;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.sharescreen.ShareScreen;
import uci.atcnea.student.utils.DbBitmapUtility;
import uci.atcnea.student.utils.TaskHelper;

/**
 * Created by adrian on 21/11/16.
 */
public class ShareScreenCommand implements CommandInterface, Serializable {

    private static final long serialVersionUID = 9320;

    private byte[] frame;

    public enum ShareScreenStatus {

        PLAY, STOP, SERVER_PLAY, SERVER_STOP, CANCELED, NOT_SUPPORT

    }

    private ShareScreenStatus status;

    public ShareScreenCommand() {
    }

    public ShareScreenCommand(byte[] frame, ShareScreenStatus status) {
        this.frame = frame;
        this.status = status;
    }

    @Override
    public OperationResult execute(Context applicationContext) {
        Log.e("ShareScreenCommand", status.name());

        switch (status) {
            case PLAY:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    TaskHelper.execute(new AsyncTask<Object, Object, Object>() {
                        @Override
                        protected Object doInBackground(Object... params) {
                            ShareScreen screen = new ShareScreen(MainApp.getCurrentActivity());
                            MainApp.getIntance().setMyShareScreen(screen);
                            MainApp.getIntance().getMyShareScreen().init();
                            MainApp.getIntance().getMyShareScreen().startProjection();
                            return null;
                        }
                    });
                } else {
                    ShareScreenCommand m = new ShareScreenCommand(new byte[0], ShareScreenStatus.NOT_SUPPORT);
                    SendMessageService msg = new SendMessageService(MainApp.getCurrentServer());
                    msg.setWaitForResponse(false);
                    msg.setCommand(m);
                    TaskHelper.execute(msg);
                }


                break;
            case STOP:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    TaskHelper.execute(new AsyncTask<Object, Object, Object>() {
                        @Override
                        protected Object doInBackground(Object... params) {
                            if (MainApp.getIntance().getMyShareScreen() != null)
                                MainApp.getIntance().getMyShareScreen().stopProjection();
                            return null;
                        }
                    });
                }

                break;
            case SERVER_PLAY:
                if (MainApp.getIntance().getShareScreenActivity() == null) {
                    MainApp.getIntance().startActivity(new Intent(applicationContext, ShareScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    Log.e("SERVER_PLAY", "SERVER_PLAY");
                } else {

                    MainApp.getIntance().getShareScreenActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap image = DbBitmapUtility.getImage(frame);
                            try {

                                MainApp.getIntance().getShareScreenActivity().getShareScreen().setImageBitmap(image);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                break;
            case SERVER_STOP:
                if (MainApp.getIntance().getShareScreenActivity() != null) {
                    GlobalBus.getBus().post(new Events.EventStopShareScreem());

                    Intent openMainActivity = new Intent(MainApp.getAppContext(), MainActivity.class);
                    openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    MainApp.getAppContext().startActivity(openMainActivity);

                    MainApp.getIntance().setShareScreenActivity(null);

                    Log.e("SERVER_STOP", "SERVER_STOP");

                }
                Log.e("SERVER_STOP", "SERVER_STOP");

                break;
        }

        return null;
    }
}
