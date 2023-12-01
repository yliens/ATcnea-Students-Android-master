package main.blibrary;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.io.Serializable;

/**
 * Created by guillermo on 2/11/16.
 */
public class BCircle extends BGraphic implements Serializable {

    private static final long serialVersionUID = 2666621L;

    private double Width;//Ancho
    private double Height;//Alto
    private double Anchor;//Para grozor de la linea
    private BColor Color_Fill;//Color de relleno del objeto (si es null no rellena)
    private BColor Color_Line;//Color de linea de la figura

    public BCircle() {
    }

    public BCircle(double x, double y, int index, boolean whitContainer, double width, double height, double anchor, BColor color_Fill, BColor color_Line) {
        super(x, y, index,whitContainer,anchor, FigureType.CIRCLE);
        Width = width;
        Height = height;
        Anchor = anchor;
        Color_Fill = color_Fill;
        Color_Line = color_Line;
    }

    @Override
    public void Draw(Canvas canvas) {

        //Para relleno
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color_Fill.getRGBColor());
        canvas.drawOval(new RectF( (float) (X * scaleX(canvas)),
                (float) (Y * scaleY(canvas)),
                (float) ((X + Width) * scaleX(canvas)),
                (float) ((Y + Height) * scaleY(canvas))),
                paint);

        //Para bordes
        paint.setStrokeWidth( (float)(Anchor * ( (canvas.getWidth()/canvas.getHeight()) / (boardWidth/boardHeight) ) ) );
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color_Line.getRGBColor());
        //paint.setStrokeWidth( (int)Anchor );
        canvas.drawOval(new RectF( (float) (X * scaleX(canvas)),
                (float) (Y * scaleY(canvas)),
                (float) ((X + Width) * scaleX(canvas)),
                (float) ((Y + Height) * scaleY(canvas))),
                paint);

        super.Draw(canvas);
    }

    @Override
    public boolean In_Position(double X, double Y) {
        return false;
    }

    public double getWidth() {
        return Width;
    }

    public void setWidth(double width) {
        if (isContainer) container.setWidth(width);
        Width = width;
    }

    public double getHeight() {
        return Height;
    }

    public void setHeight(double height) {
        if (isContainer) container.setHeight(height);
        Height = height;
    }

    public double getAnchor() {
        return Anchor;
    }

    public void setAnchor(double anchor) {
        Anchor = anchor;
    }

    public BColor getColor_Fill() {
        return Color_Fill;
    }

    public void setColor_Fill(BColor color_Fill) {
        Color_Fill = color_Fill;
    }

    public BColor getColor_Line() {
        return Color_Line;
    }

    public void setColor_Line(BColor color_Line) {
        Color_Line = color_Line;
    }
}
