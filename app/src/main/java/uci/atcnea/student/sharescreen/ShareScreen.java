package uci.atcnea.student.sharescreen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;

import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import main.command.SendMessage;
import main.command.ShareScreenCommand;
import main.command.StudentNodeCmd;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.fragment.Controllers.ChatController;
import uci.atcnea.student.utils.DbBitmapUtility;
import uci.atcnea.student.utils.TaskHelper;

/**
 * Created by adrian on 21/11/16.
 */
public class ShareScreen {
    Activity myActivity;


    private static final String TAG = ShareScreen.class.getName();
    public static final int REQUEST_CODE = 100;
    private static String STORE_DIRECTORY;
    private static int IMAGES_PRODUCED;
    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static MediaProjection sMediaProjection;

    private MediaProjectionManager mProjectionManager;
    private ImageReader mImageReader;
    private Handler mHandler;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    private OrientationChangeCallback mOrientationChangeCallback;
    ImageView imageView;
    private int mResultCode;
    private Intent mResultData;
    private Thread myHandlingThread;


    public ShareScreen(Activity myActivity) {
        this.myActivity = myActivity;
    }

    public void init() {
        // call for the projection manager
        mProjectionManager = (MediaProjectionManager) myActivity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);


        // start capture handling thread
        myHandlingThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                Looper.loop();
            }
        };

        myHandlingThread.start();

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {


            Image image = null;
            //FileOutputStream fos = null;
            Bitmap bitmap = null;


            image = reader.acquireLatestImage();
            if (image != null) {
                Image.Plane[] planes = image.getPlanes();
                ByteBuffer buffer = planes[0].getBuffer();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * mWidth;

                // create bitmap
                bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                image.close();

                // write bitmap to a file
                //fos = new FileOutputStream(STORE_DIRECTORY + "/myscreen_" + IMAGES_PRODUCED + ".png");
                // bitmap.compress(CompressFormat.JPEG, 100, fos);

                final Bitmap finalBitmap = Bitmap.createScaledBitmap(bitmap, (int) (mWidth / 2.2), (int) (mHeight / 2.2), true);//bitmap.copy(Bitmap.Config.RGB_565,true);

                ShareScreenCommand screenCommand = new ShareScreenCommand(DbBitmapUtility.getBytes(finalBitmap), ShareScreenCommand.ShareScreenStatus.PLAY);

                SendMessageService msg = new SendMessageService(MainApp.getCurrentServer());
                msg.setWaitForResponse(false);
                msg.setCommand(screenCommand);
                TaskHelper.execute(msg);


                IMAGES_PRODUCED++;
                Log.e(TAG, "captured image: " + IMAGES_PRODUCED);
                //Log.e(TAG, "captured image: " + STORE_DIRECTORY);
            }

        }



    };


private class OrientationChangeCallback extends OrientationEventListener {
    public OrientationChangeCallback(Context context) {
        super(context);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onOrientationChanged(int orientation) {
        synchronized (this) {
            final int rotation = mDisplay.getRotation();
            if (rotation != mRotation) {
                mRotation = rotation;
                try {
                    // clean up
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);

                    // re-create virtual display depending on device width / height
                    createVirtualDisplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private class MediaProjectionStopCallback extends MediaProjection.Callback {
    @Override
    public void onStop() {
        Log.e("ScreenCapture", "stopping projection.");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mVirtualDisplay != null) mVirtualDisplay.release();
                if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
                if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
                sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
            }
        });
    }

}


    /******************************************
     * UI Widget Callbacks
     *******************************/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startProjection() {
        myActivity.startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);

    }

    public void stopProjection() {
        mHandler.post(new Runnable() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if (sMediaProjection != null) {
                    sMediaProjection.stop();
                }
            }
        });
       // myHandlingThread.stop();
    }

    /******************************************
     * Factoring Virtual Display creation
     ****************/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createVirtualDisplay() {
        // get width and height
        Point size = new Point();
        mDisplay.getSize(size);
        mWidth = size.x;
        mHeight = size.y;

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean handleActivityResult(Activity activity, int requestCode, int resultCode,
                                        Intent data) {

        if (requestCode == REQUEST_CODE) {
            sMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);

            if (sMediaProjection != null) {
                File externalFilesDir = Environment.getExternalStorageDirectory();
                if (externalFilesDir != null) {
                    STORE_DIRECTORY = externalFilesDir.getAbsolutePath() + "/screenshots/";
                    File storeDirectory = new File(STORE_DIRECTORY);
                    if (!storeDirectory.exists()) {
                        boolean success = storeDirectory.mkdirs();
                        if (!success) {
                            Log.e(TAG, "failed to create file storage directory.");
                            return false;
                        }
                    }
                } else {
                    Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");
                    return false;
                }

                // display metrics
                DisplayMetrics metrics = myActivity.getResources().getDisplayMetrics();
                mDensity = metrics.densityDpi;
                mDisplay = myActivity.getWindowManager().getDefaultDisplay();

                // create virtual display depending on device width / height
                createVirtualDisplay();

                // register orientation change callback
                mOrientationChangeCallback = new OrientationChangeCallback(myActivity);
                if (mOrientationChangeCallback.canDetectOrientation()) {
                    mOrientationChangeCallback.enable();
                }

                // register media projection stop callback
                sMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
            }
            else{
                ShareScreenCommand m=new ShareScreenCommand(new byte[0], ShareScreenCommand.ShareScreenStatus.CANCELED);
                SendMessageService msg = new SendMessageService(MainApp.getCurrentServer());
                msg.setWaitForResponse(false);
                msg.setCommand(m);
                TaskHelper.execute(msg);


                Log.e("NOOOOOO","NOOOOOOO");
            }
        }


        return true;
    }


}
