package main.blibrary;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.Serializable;

/**
 * Created by guillermo on 2/11/16.
 */
public class BLine extends BGraphic implements Serializable {

    private static final long serialVersionUID = 2666628L;

    private double stopX;
    private double stopY;
    private double Anchor;//Para grozor del dibujo
    private BColor Color_Line;//Color de linea de la figura

    public BLine() {
    }

    public BLine(double x, double y, int index, boolean whitContainer, double stopX, double stopY, double anchor, BColor color_Line) {
        super(x, y, index, whitContainer,anchor, FigureType.LINE);
        this.stopX = stopX;
        this.stopY = stopY;
        Anchor = anchor;
        Color_Line = color_Line;
    }

    @Override
    public void Draw(Canvas canvas) {

        Paint paint = new Paint();

        System.out.println(Color_Line);

        paint.setColor(Color_Line.getRGBColor());
        paint.setStrokeWidth( (float)(Anchor * ( (canvas.getWidth()/canvas.getHeight()) / (boardWidth/boardHeight) ) ) );
        paint.setStyle(Paint.Style.STROKE );

        canvas.drawLine((float) (X * scaleX(canvas)) ,
                (float) (Y * scaleY(canvas)),
                (float) (stopX * scaleX(canvas)),
                (float) (stopY * scaleY(canvas)),
                paint);

        super.Draw(canvas);
    }

    @Override
    public boolean In_Position(double X, double Y) {
        return false;
    }

    public double getStopX() {
        return stopX;
    }

    public void setStopX(double stopX) {
        if (isContainer) {
            container.setX(Math.min(getX(), stopX));
            container.setWidth(Math.abs(getX() - stopX));
        }
        this.stopX = stopX;
    }

    public double getStopY() {
        return stopY;
    }

    public void setStopY(double stopY) {
        if (isContainer){
            container.setY(Math.min(getY(), stopY));
            container.setHeight(Math.abs(getY() - stopY));
        }
        this.stopY = stopY;
    }

    public double getAnchor() {
        return Anchor;
    }

    public void setAnchor(Integer anchor) {
        Anchor = anchor;
    }

    public BColor getColor_Line() {
        return Color_Line;
    }

    public void setColor_Line(BColor color_Line) {
        Color_Line = color_Line;
    }
}
