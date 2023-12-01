package main.blibrary;

import android.graphics.Canvas;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by guillermo on 2/11/16.
 */
public class BGraphic implements Comparable,Serializable{

    private static final long serialVersionUID = 2666626L;

    protected double X;//Posicion X en el Board
    protected double Y;//Posicion Y en el Board
    protected double boardWidth;
    protected double boardHeight;
    protected int Index;//Order in the
    protected BContainer container;
    protected boolean isContainer=false;
    public enum BGraphicStatus{
        CREATE,EDITING
    }
    private BGraphicStatus status;
    public enum FigureType{ FREE_LINE, CIRCLE, SQUARE, LINE, IMAGE, TEXT, ERASE };

    private FigureType Type;

    //Siguiendo el patron Compositive
    protected List<BGraphic> My_Graphics;//Listado de hijos

    public BGraphic() {
        My_Graphics = new LinkedList<BGraphic>();
    }

    public BGraphic(double x, double y, int index, FigureType type) {
        X = x;
        Y = y;
        Index = index;
        Type = type;
        isContainer = false;
        My_Graphics = Collections.synchronizedList(new LinkedList<BGraphic>());//new LinkedList<BGraphic>();
        status= BGraphicStatus.CREATE;
    }

    public BGraphic(double x, double y, int index,boolean isContainer,double anchor, FigureType type) {
        X = x;
        Y = y;
        Index = index;
        Type = type;
        this.isContainer=isContainer;
        if (isContainer) {
            BContainer container = new BContainer(X, Y, 0, 0);
            container.setBoardHeight(getBoardHeight());
            container.setBoardWidth(getBoardWidth());
            setContainer(container);
        }
        My_Graphics = new LinkedList<BGraphic>();
        status = BGraphicStatus.CREATE;
        this.setBoardWidth(1);
        this.setBoardHeight(1);
    }

    public double scaleX(Canvas canvas){
        return (double)canvas.getWidth()/boardWidth;
    }

    public double scaleY(Canvas canvas){
        return (double)canvas.getHeight()/boardHeight;
    }

    /**
     * Para limpiar el canva.
     */
    public void clearRect(Canvas canvas){
      //  canvas.clearRect(0, 0, canvas.getCanvas().getWidth(), canvas.getCanvas().getHeight());
    }

    /**
     * Para dibujar el objeto actual y luego los hijos contenidos.
     */
    public void Draw(Canvas canvas){
        if (isContainer() && status.equals(BGraphicStatus.EDITING)) getContainer().Draw(canvas);
        for (int i = 0; i < My_Graphics.size(); i++){
            My_Graphics.get(i).Draw(canvas);
        }
    }

    public boolean In_Position(double X, double Y){
        return false;
    }

    /**
     * Enviar objeto al fondo del listado
     * @param graphic Graphic a enviar al fondo
     */
    public void Send_To_End(BGraphic graphic){
        int index = My_Graphics.indexOf( graphic );
        BGraphic tem_graphic = My_Graphics.remove( index );
        ((LinkedList)My_Graphics).addFirst( tem_graphic );
    }

    /**
     * Enviar objeto al principio del listado
     * @param graphic Graphic a enviar al principio
     */
    public void Send_To_Begin(BGraphic graphic){
        int index = My_Graphics.indexOf( graphic );
        BGraphic tem_graphic = My_Graphics.remove( index );
        ((LinkedList)My_Graphics).addLast( tem_graphic );
    }

    /**
     * Enviar objeto una posicion por delante
     * @param graphic Graphic a enviar una posicion por delante
     */
    public void Send_One_Front(BGraphic graphic){
        int index = My_Graphics.indexOf( graphic );
        BGraphic tem_graphic = My_Graphics.remove( index );
        My_Graphics.add(Math.min( index + 1, My_Graphics.size()), tem_graphic );
    }

    /**
     * Enviar objeto una posicion por detras
     * @param graphic Graphic a enviar una posicion por detras
     */
    public void Send_One_Back(BGraphic graphic){
        int index = My_Graphics.indexOf( graphic );
        BGraphic tem_graphic = My_Graphics.remove( index );
        My_Graphics.add(Math.max( index - 1, 0), tem_graphic );
    }

    /**
     * Ordenar el listado de hijos, segun el valor Index.
     */
    public void Sort_My_Graphics(){
        Collections.sort( My_Graphics );
    }

    /**
     * Adicionar grafico al listado de hijos.
     * @param graphic Graphic que se adicionara al listado.
     */
    public void Add_Graphic(BGraphic graphic){
        if(graphic.isContainer()){//Terminar de edicion de los objetos
            graphic.setContainer(false);
            graphic.setContainer(null);
        }
        My_Graphics.add(graphic);
    }

    /**
     * Eliminar grafico del listado de hijos.
     * @param graphic Graphic que se eliminara del listado.
     */
    public boolean Delete_Graphic(BGraphic graphic){
        return My_Graphics.remove(graphic);
    }

    @Override
    public int compareTo(Object another) {
        return this.Index - ((BGraphic)another).getIndex();
    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        if(isContainer)container.setX(x);
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        if(isContainer)container.setY(y);
        Y = y;
    }

    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }

    public BContainer getContainer() {
        return container;
    }

    public void setContainer(BContainer container) {
        this.container = container;
    }

    public boolean isContainer() {
        return isContainer;
    }

    public void setContainer(boolean container) {
        isContainer = container;
    }

    public BGraphicStatus getStatus() {
        return status;
    }

    public void setStatus(BGraphicStatus status) {
        this.status = status;
    }

    public LinkedList<BGraphic> getMy_Graphics() {
        return ((LinkedList)My_Graphics);
    }

    public double getBoardWidth() {
        return boardWidth;
    }

    public void setBoardWidth(double boardWidth) {
        if(isContainer)container.setBoardWidth(boardWidth);
        this.boardWidth = boardWidth;
    }

    public double getBoardHeight() {
        return boardHeight;
    }

    public void setBoardHeight(double boardHeigth) {
        if(isContainer)container.setBoardHeight(boardHeigth);
        this.boardHeight = boardHeigth;
    }
}
