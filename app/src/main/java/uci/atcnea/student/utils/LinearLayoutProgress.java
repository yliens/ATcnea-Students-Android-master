package uci.atcnea.student.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import uci.atcnea.student.R;

/**
 * Created by guillermo on 9/12/16.
 */
public class LinearLayoutProgress extends LinearLayout {

    //Progreso de terminado
    private double progress;

    public LinearLayoutProgress(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Progreso inicial
        progress = 0.0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        Paint paint;

        //Pintar progreso
        paint = new Paint();
        paint.setStyle( Paint.Style.FILL );
        paint.setColor( Color.parseColor("#378e43") );

        canvas.drawRect(0, 0, canvas.getWidth() * (float)progress, canvas.getHeight(), paint);

        //Pintar bordes
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE );
        paint.setStrokeWidth( 4 );
        paint.setColor( Color.parseColor("#E8DDC1") );

        canvas.drawLine( 0, 0, canvas.getWidth(), 0, paint);
        canvas.drawLine( canvas.getWidth(), canvas.getHeight(), canvas.getWidth(), 0, paint);
        canvas.drawLine( canvas.getWidth(), canvas.getHeight(), 0, canvas.getHeight(), paint);
        canvas.drawLine( 0, 0, 0, canvas.getHeight(), paint);
    }

    /**
     * Repintar el canvas
     */
    public void Draw(){
        invalidate();
    }

    /**
     * Actualizar el progreso de la barra
     * @param progress progreso de la barra en porciento (0, 100)
     */
    public void updateProgressInPorcent(double progress){
        this.progress = progress/100.0;
    }

    /**
     * Actualizar el progreso de la barra
     * @param progress progreso de la barra (0, 1)
     */
    public void updateProgress(double progress){
        this.progress = progress;
        Draw();
    }
}
