package main.blibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.io.Serializable;

/**
 * Created by guillermo on 2/11/16.
 */
public class BImage extends BGraphic implements Serializable {

    private static final long serialVersionUID = 2666627L;

    private byte[] ImageDraw;//Imagen a dibujar
    private double width;//Ancho de la imagen
    private double height;//Alto de la imagen

    public BImage() {
    }

    public BImage(double x, double y, int index, boolean whitContainer, double width, double height, byte[] imageDraw) {
        super(x, y, index, whitContainer,1, FigureType.IMAGE);
        ImageDraw = imageDraw;
        this.width = width;
        this.height = height;
        container.setWidth(width);
        container.setHeight(height);
    }

    @Override
    public void Draw(Canvas canvas) {

        Bitmap image = BitmapFactory.decodeByteArray(ImageDraw, 0, ImageDraw.length);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                image,
                (int)(width * scaleX(canvas)),
                (int)(height * scaleY(canvas)),
                false
        );

        canvas.drawBitmap(resizedBitmap, (float) X,(float) Y, new Paint());

        image.recycle();
        resizedBitmap.recycle();

        super.Draw(canvas);

    }

    @Override
    public boolean In_Position(double X, double Y) {
        return false;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double witch) {
        if (isContainer) container.setWidth(width);
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        if (isContainer) container.setHeight(height);
        this.height = height;
    }

    public byte[] getImageDraw() {
        return ImageDraw;
    }

    public void setImageDraw(byte[] imageDraw) {
        ImageDraw = imageDraw;
    }
}
