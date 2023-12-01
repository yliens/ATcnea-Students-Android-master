package main.blibrary;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @class  BErase.java
 * @version 1.0
 * @date 15/11/16
 * @author Odenys Almora Rodriguez
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  La clase BErase permite
 * @rf
 */
public class BErase extends BGraphic implements Serializable {

    private static final long serialVersionUID = 2666624L;

    private double Width;//Ancho
    private double Height;//Alto
    private List<BPoint> list_of_points;//Lista de puntos del dibujado

    public BErase() {
    }

    public BErase(double x, double y, Integer width, Integer height) {
        super(x, y, 10000, FigureType.ERASE);
        Width = width;
        Height = height;
        //Iniciar lista de puntos
        list_of_points = Collections.synchronizedList(new LinkedList<BPoint>());//new LinkedList<BPoint>();
    }

    public void add(BPoint point){
        list_of_points.add(point);
    }

    @Override
    public void Draw(Canvas canvas) {
   /*     for (int i = 0; i < list_of_points.size()-1; i++){
            BPoint p1 = list_of_points.get(i);
            canvas.clearRect(p1.getX() ,p1.getY(), getWidth(), getHeight());
        }*/

        Paint paint = new Paint();
        //paint.setXfermode( new PorterDuffXfermode(PorterDuff.Mode.CLEAR ));
        paint.setStyle(Paint.Style.FILL);
        paint.setColor( Color.WHITE );

        for (int i = 0; i < list_of_points.size(); i++){
            double PX = list_of_points.get( i ).getX();
            double PY = list_of_points.get( i ).getY();

            canvas.drawRect((float)((PX - Width * 0.5) * scaleX(canvas)),
                    (float)((PY - Height * 0.5) * scaleX(canvas)),
                    (float)((PX + Width * 0.5) * scaleX(canvas)),
                    (float)((PY + Height * 0.5) * scaleX(canvas)),
                    paint );

        }

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
        Width = width;
    }

    public double getHeight() {
        return Height;
    }

    public void setHeight(double height) {
        Height = height;
    }
}

