package main.blibrary;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by guillermo on 2/11/16.
 */
public class BFree_Pain extends BGraphic implements Serializable {

    private static final long serialVersionUID = 2666625L;

    private double Anchor;//Para grozor de la linea
    private BColor Color_Line;//Color de linea de la figura
    //private LinkedList<BPoint> list_of_points;//Lista de puntos del dibujado
    private List<BPoint> list_of_points;//Lista de puntos del dibujado

    public BFree_Pain(double x, double y, int index, boolean withContainer, double anchor, BColor color_Line) {
        super(x, y, index,withContainer,anchor, FigureType.FREE_LINE);
        Anchor = anchor;
        this.Color_Line = color_Line;

        X = Y = 0;

        //Iniciar lista de puntos
        list_of_points = Collections.synchronizedList( new LinkedList<BPoint>() );//new LinkedList<BPoint>();

    }

    public synchronized void add(BPoint point){
        list_of_points.add(point);
    }

    @Override
    public synchronized void Draw(Canvas canvas) {
        double minX = 90000000,minY = 90000000;
        double maxX = -90000000,maxY = -90000000;
        for (int i = 0; i < list_of_points.size()-1; i++){
            // Calculando posiciones y actualizando los puntos
            BPoint p1 = list_of_points.get(i);
            BPoint p2 = list_of_points.get(i + 1);
            p1 = new BPoint(p1.getX() + X, p1.getY() + Y);
            p2 = new BPoint(p2.getX() + X, p2.getY() + Y);
            list_of_points.set(i, p1);

            Paint paint = new Paint();

            paint.setStrokeWidth( Math.max((float)(Anchor * ( (canvas.getWidth()/canvas.getHeight()) / (boardWidth/boardHeight) ) ), 1 ) );

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor( Color_Line.getRGBColor() );
            //paint.setStrokeWidth( (float)(Anchor * ( (boardWidth/boardHeight) / (canvas.getWidth()/canvas.getHeight()) ) )  );
            canvas.drawLine((float) ((p1.getX())*scaleX(canvas)) ,
                    (float)((p1.getY())*scaleY(canvas)) ,
                    (float)((p2.getX())*scaleX(canvas)) ,
                    (float)((p2.getY())*scaleY(canvas)) ,
                    paint);

            if(isContainer) {
                minX = Math.min(minX, Math.min(p1.getX(), p2.getX()));
                minY = Math.min(minY, Math.min(p1.getY(), p2.getY()));
                maxX = Math.max(maxX, Math.max(p1.getX(), p2.getX()));
                maxY = Math.max(maxY, Math.max(p1.getY(), p2.getY()));
            }
        }
        // Actualizar ultimo punto
        if(!list_of_points.isEmpty()) {
            list_of_points.set(list_of_points.size() - 1, new BPoint(
                    list_of_points.get(list_of_points.size() - 1).getX() + X,
                    list_of_points.get(list_of_points.size() - 1).getY() + Y
            ));
        }
        // Actualizar posicion del container de existir
        if(isContainer){
            container.setX( minX );
            container.setY( minY );
            container.setWidth(maxX - minX);
            container.setHeight(maxY - minY);
        }
        X = Y = 0;
        super.Draw(canvas);
    }

    @Override
    public boolean In_Position(double X, double Y) {
        return false;
    }

    public double getAnchor() {
        return Anchor;
    }

    public void setAnchor(double anchor) {
        Anchor = anchor;
    }

    public List<BPoint> getList_of_points() {
        return list_of_points;
    }

    public void setList_of_points(LinkedList<BPoint> list_of_points) {
        this.list_of_points = list_of_points;
    }

}
