package main.blibrary;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import java.io.Serializable;

import io.vov.vitamio.utils.Log;

/**
 * @class  BContainer.java
 * @version 1.0
 * @date 15/11/16
 * @author Odenys Almora Rodriguez
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  La clase BContainer permite
 * @rf
 */
public class BContainer extends BSquare implements Serializable {

    private static final long serialVersionUID = 2666623L;

    public BContainer(double x, double y, double width, double height) {
        super(x , y, 1000, false, width , height , 2, new BColor(0,0,0,0), new BColor(0,0,0,255));
    }

    @Override
    public void Draw(Canvas canvas) {

        Log.d("Container", "entre");

        //Para borde
        Paint paint = new Paint();
        paint.setColor(Color_Line.getRGBColor());
        paint.setStyle( Paint.Style.STROKE );
        paint.setStrokeWidth( 2 );
        paint.setPathEffect(new DashPathEffect(new float[]{10,10},0));
        //Falta agregar el split a la funcion
        canvas.drawRect(
                (float) ((float)(X - 10) * scaleX(canvas)),
                (float)(Y - 10)*(float)scaleY(canvas),
                (float)( X + Width + 10)*(float)scaleX(canvas),
                (float)(Y + Height + 10)*(float)scaleY(canvas),
                paint
        );

    }

    public void setX(double x,double split) {
        super.setX(x);
    }

    public void setY(double y,double split) {
        super.setY(y);
    }

}
