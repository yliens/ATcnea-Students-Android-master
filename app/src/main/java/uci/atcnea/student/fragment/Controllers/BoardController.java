package uci.atcnea.student.fragment.Controllers;

import android.graphics.Bitmap;

import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import main.blibrary.BGraphic;
import main.blibrary.BoardView;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.fragment.BoardFragment;

/**
 * Created by guillermo on 19/01/17.
 */
public class BoardController {

    private static BoardController intance;

    public static BoardController getIntance(){
        if(intance == null) {
            intance = new BoardController();

            intance.initIntance();
            GlobalBus.getBus().register(intance);
        }

        return intance;
    }

    /**
     * Destroit intance of controller
     */
    public void initIntance(){
        //Objetos remotos
        remote_tools = new BGraphic();
        remoteID = Collections.synchronizedList(new LinkedList<Integer>());//new LinkedList<>();

        //Crear la pizarra
        board = new BGraphic();

        //inicializar la salva del board
        board_tmp = Bitmap.createBitmap(20,20, Bitmap.Config.ARGB_8888);

        board_share = isCreate = isEditing = false;

        editing = true;

        tool_in_use = BGraphic.FigureType.FREE_LINE;
    }

    /**
     * Destroit intance of controller
     */
    public static void destroitIntance() {
        if(intance!=null)
        GlobalBus.getBus().unregister(intance);

        intance = null;
    }

    public BoardController() {
    }

    // Estado de pizarra
    public boolean board_share = false;

    // Estado de edicion
    public boolean editing;

    //Objetos en edicion remota y datos adjuntos para tratamiento optimo
    public BGraphic remote_tools;
    public List<Integer> remoteID;

    //Tipo de herramienta en uso
    public BGraphic.FigureType tool_in_use;

    //Objeto de la pizarra
    public BGraphic board;

    //Objeto en edicion
    public BGraphic edit_tool;

    //Canvas temporal pa optimizacion de tiempo
    public Bitmap board_tmp;

    //Variable global de uso de la pizarra
    public boolean isCreate, isEditing;

    // Fragment de pizarra
    public BoardFragment fragment;

    // Registrar boardView
    public BoardView boardView;

    // Tamano del canvas de la pizarra
    public double canvasWidth, canvasHeight;

    public void updateVisualComponent(){
        if(boardView != null){
            boardView.DrawBoard();
        }
    }

    //----------------------------------------------------------//
    //                Eventos del bus de eventos                //
    //----------------------------------------------------------//
    @Subscribe
    public synchronized void eventBoard(final Events.EventBoard event){
        MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int pos;
                switch ( event.boardType ) {
                    case BOARD_INITIALIZE:
                        editing = false;
                    case BOARD_INITIALIZE_WRITABLE:
                        // Limpiar pizarra
                        clearBoard();

                        // Cargando nueva pizarra del servidor
                        board = event.board;

                        // Habilitar edicion
                        if(event.boardType == Events.BoardType.BOARD_INITIALIZE_WRITABLE)
                            editing = true;

                        // Habilitar el compartimiento de pizarra
                        board_share = true;
                        break;
                    case BOARD_EDITING:
                        pos = position(event.Id);
                        if(pos < 0 && pos != EMPTY_LIST){
                            pos = (pos*-1)-1;
                            remoteID.add(pos, event.Id);
                            remote_tools.getMy_Graphics().add(pos, event.board);
                        }else if(pos != EMPTY_LIST && pos < remoteID.size()){
                            //System.out.println("Test edit" + pos + " " + BoardController.getIntance().remote_tools.getMy_Graphics().size());
                            remote_tools.getMy_Graphics().set(pos, event.board);
                        }
                        break;
                    case BOARD_EDITING_END:

                        board.Add_Graphic(event.board);

                        pos = position(event.Id);
                        if(pos >= 0 && pos != EMPTY_LIST && pos < remoteID.size()){
                            remote_tools.getMy_Graphics().remove(pos);
                            remoteID.remove(pos);
                        }

                        break;
                    case BOARD_END:
                        // Limpiar pizarra
                        clearBoard();
                        // Activar la edicion nuevamente
                        editing = true;
                        // Deshabilitar el compartimiento de pizarra
                        board_share = false;
                        break;
                    case BOARD_CLEAR:
                        // Limpiar pizarra
                        clearBoard();
                        break;
                }
                if(boardView != null) {
                    boardView.DrawBoard();
                }
            }
        });
    }

    /**
     * Limpiar la pizarra
     */
    private void clearBoard(){
        // Borrar pizarra
        board = new BGraphic();
        // Borrar elemento q se estaban editando
        remote_tools.getMy_Graphics().clear();
        remoteID.clear();
        // Eliminar elemento actual en edicion (posible error)
        edit_tool = null;
        isCreate = false;
        isEditing = false;
        // Limpiar salva de la pantalla
        board_tmp = Bitmap.createBitmap(20,20, Bitmap.Config.ARGB_8888);
    }

    private int position( int id ){
        //if(remoteID.isEmpty())return EMPTY_LIST;
        return Collections.binarySearch(remoteID, id, new Comparator<Integer>() {
            @Override
            public int compare(Integer o, Integer p) {
                return o - p;
            }
        });
    }

    private static int EMPTY_LIST = -95959555;
}
