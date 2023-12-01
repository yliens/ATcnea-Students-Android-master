package uci.atcnea.student.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import uci.atcnea.student.MainApp;
/**
 * @class   PhotoUtils
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf
 */
public class PhotoUtils {
    private static Context mContext;
    private BitmapFactory.Options generalOptions;

    public PhotoUtils(Context context) {
        mContext = context;
    }

    /**
     * Crea un fichero temporal
     * @param part
     * @param ext
     * @param myContext
     * @return File
     * @throws Exception
     */
    public static File createTemporaryFile(String part, String ext,
                                           Context myContext) throws Exception {
        String path = myContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + "/atcnea/";
        File tempDir = new File(path);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }

    /**
     * Construye un bitmap dado una URI
     * @param uri
     * @return bitmap
     */
    public Bitmap getImage(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream is = null;
        try {
            is = mContext.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options);
            is.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.generalOptions = options;
        return scaleImage(options, uri, 300);
    }

    public static int nearest2pow(int value) {
        return value == 0 ? 0
                : (32 - Integer.numberOfLeadingZeros(value - 1)) / 2;
    }

    /**
     * Escala una imagen
     * @param options
     * @param uri
     * @param targetWidth
     * @return bitmap
     */
    public Bitmap scaleImage(BitmapFactory.Options options, Uri uri,
                             int targetWidth) {
        if (options == null)
            options = generalOptions;
        Bitmap bitmap = null;
        double ratioWidth = ((float) targetWidth) / (float) options.outWidth;
        double ratioHeight = ((float) targetWidth) / (float) options.outHeight;
        double ratio = Math.min(ratioWidth, ratioHeight);
        int dstWidth = (int) Math.round(ratio * options.outWidth);
        int dstHeight = (int) Math.round(ratio * options.outHeight);
        ratio = Math.floor(1.0 / ratio);
        int sample = nearest2pow((int) ratio);

        options.inJustDecodeBounds = false;
        if (sample <= 0) {
            sample = 1;
        }
        options.inSampleSize = (int) sample;
        options.inPurgeable = true;
        try {
            InputStream is;
            is = mContext.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is, null, options);
            if (sample > 1)
                bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight,
                        true);
            is.close();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bitmap;
    }

    /**
     * Guarda una imagen en una ruta
     * @param path
     * @return AbsolutePath of the image
     */
    public String saveImageProfile(String path) {

        try {
            File img = this.createTemporaryFile(String.valueOf((new Date()).getTime()), "png", MainApp.getCurrentActivity());

            Bitmap bitmap = getImage(Uri.parse(path));

            bitmap= Bitmap.createScaledBitmap(bitmap, 200, 200,
                    true);

            FileOutputStream out = null;
            try {
                out =  new FileOutputStream(img);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    return img.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    /**
     * Guarda un bitmap como archivo
     * @param bitmap
     * @return AbsolutePath of the image
     */
    public String saveImageProfile(Bitmap bitmap) {

        try {
            File img = this.createTemporaryFile(String.valueOf((new Date()).getTime()), "png", MainApp.getCurrentActivity());



            bitmap= Bitmap.createScaledBitmap(bitmap, 200, 200,
                    true);

            FileOutputStream out = null;
            try {
                out =  new FileOutputStream(img);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    return img.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }
}


