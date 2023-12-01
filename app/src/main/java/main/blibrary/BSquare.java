package main.blibrary;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.io.Serializable;

/**
 * Created by guillermo on 2/11/16.
 */
public class BSquare extends BGraphic implements Serializable {

    private static final long serialVersionUID = 26666210L;

    protected double Width;//Ancho
    protected double Height;//Alto
    protected double Anchor;//Para grozor del dibujo
    protected BColor Color_Fill;//Color de relleno del objeto (si es null no rellena)
    protected BColor Color_Line;//Color de linea de la figura

    public BSquare() {
    }

    public BSquare(double x, double y, int index, boolean whitContainer, double width, double height, double anchor, BColor color_Fill, BColor color_Line) {
        super(x, y, index, whitContainer,anchor, FigureType.SQUARE);
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
        paint.setColor(Color_Fill.getRGBColor());
        paint.setStyle( Paint.Style.FILL );
        canvas.drawRect((float) (X * scaleX(canvas)),
                (float) (Y * scaleY(canvas)),
                (float) ((X + Width) * scaleX(canvas)),
                (float) ((Y + Height) * scaleY(canvas)),
                paint);

        //Para bordes
        paint.setStrokeWidth( (float)(Anchor * ( (canvas.getWidth()/canvas.getHeight()) / (boardWidth/boardHeight) ) ) );
        paint.setStyle( Paint.Style.STROKE );
        paint.setColor(Color_Line.getRGBColor());
        //paint.setStrokeWidth( (int)Anchor );
        canvas.drawRect((float) (X * scaleX(canvas)),
                (float) (Y * scaleY(canvas)),
                (float) ((X + Width) * scaleX(canvas)),
                (float) ((Y + Height) * scaleY(canvas)),
                paint);

        super.Draw(canvas);
    }

    @Override
    public boolean In_Position(double X, double Y) {
        double line1= getDistancePointLine(getX(),getY(),getX(),getHeight()+getY(),X,Y);
        double line2= getDistancePointLine(getX()+getWidth(),getY(),getX()+getWidth(),getHeight()+getY(),X,Y);

        double line3= getDistancePointLine(getX(),getY(),getX()+getWidth(),getY(),X,Y);
        double line4= getDistancePointLine(getX(),getHeight()+getY(),getX()+getWidth(),getHeight()+getY(),X,Y);
            if(isChangeSign(line1,line2) && isChangeSign(line3,line4))return true;
        return false;
    }

    private boolean isChangeSign(double a,double b ){
        if((a<0 && b<0) || (a>=0 && b>=0) ) return false; return true;
    }

    private double getDistancePointLine(double X1,double Y1,double X2,double Y2,double X,double Y){
        double A = ( X -  X1) *( Y2 -  Y1) ;
        double B = ( Y -  Y1) * (X2 - X1)  ;
        return A - B;
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
