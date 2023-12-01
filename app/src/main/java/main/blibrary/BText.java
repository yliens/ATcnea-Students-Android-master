package main.blibrary;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.io.Serializable;

/**
 * Created by guillermo on 2/11/16.
 */
public class BText extends BGraphic implements Serializable {

    private static final long serialVersionUID = 26666211L;

    private double Size_Text;
    private BColor Color_Text;
    private String Style_Text;
    private String Text;

    public BText(double x, double y, int index, boolean isContainer, double size_Text, BColor color_Text, String style_Text, String text) {
        super(x, y, index, FigureType.TEXT);
        Size_Text = size_Text;
        Color_Text = color_Text;
        Style_Text = style_Text;
        Text = text;

        this.isContainer = isContainer;
        container = new BContainer(x, y, 0, 0);
    }

    @Override
    public void Draw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setTextSize( (int)Size_Text );
        paint.setColor( Color_Text.getRGBColor() );
        paint.setStyle(Paint.Style.FILL);

        // Mostrar textro en el Canvas
        canvas.drawText(Text, (float)(X * scaleX(canvas)), (float)(Y * scaleY(canvas)), paint);

        if(isContainer) {
            // Para calcular los extremos del texto para el container
            Rect border = new Rect();
            paint.getTextBounds(Text, 0, Text.length(), border);

            // Calculando extremos del container
            container.setY(Y - border.height() * 0.5 );
            container.setWidth(border.width());
            container.setHeight(border.height());
        }

        super.Draw(canvas);
    }

    @Override
    public boolean In_Position(double X, double Y) {
        return false;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getStyle_Text() {
        return Style_Text;
    }

    public void setStyle_Text(String style_Text) {
        Style_Text = style_Text;
    }

    public BColor getColor_Text() {
        return Color_Text;
    }

    public void setColor_Text(BColor color_Text) {
        Color_Text = color_Text;
    }

    public double getSize_Text() {
        return Size_Text;
    }

    public void setSize_Text(double size_Text) {
        Size_Text = size_Text;
    }
}
