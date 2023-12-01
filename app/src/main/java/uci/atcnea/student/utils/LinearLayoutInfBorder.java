package uci.atcnea.student.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import uci.atcnea.student.activity.QuizActivity;

/**
 * Created by guillermo on 25/11/16.
 */
public class LinearLayoutInfBorder extends LinearLayout {

    public LinearLayoutInfBorder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //Pintar elementos
        super.onDraw(canvas);

        //Pintar fondo
        Paint paint =new Paint();
        paint.setStrokeWidth(10);
        paint.setColor( Color.BLACK );
        canvas.drawLine( 0, canvas.getHeight()-5, canvas.getWidth(), canvas.getHeight()-10, paint );

        paint.setColor( Color.parseColor( "#f8971c" ) );
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight()-5,paint);

    }
}
