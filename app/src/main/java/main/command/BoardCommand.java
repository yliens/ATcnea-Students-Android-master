package main.command;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import main.blibrary.BGraphic;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;

/**
 * @author Odenys Almora Rodriguez
 *         Copyright (c) 2016-2017 FORTES, UCI.
 * @version 1.0
 * @class main.command
 * @date 15/11/16
 */

public class BoardCommand implements CommandInterface,Serializable {

    private static final long serialVersionUID = 266662L;

    private BGraphic board;

    private int connectionID;

    public enum BoardType {
        BOARD_INITIALIZE, BOARD_EDITING, BOARD_EDITING_END, BOARD_END, BOARD_CLEAR
    }

    private BoardType boardType;

    private boolean writable;

    public BoardCommand() {
    }

    public BoardCommand(BGraphic board, BoardType boardType) {
        this.board = board;
        this.boardType = boardType;
    }

    @Override
    public OperationResult execute(Context applicationContext) {
        board.setContainer(null);
        board.setContainer(false);
        Events.EventBoard eventData=null;
        switch (boardType) {
            case BOARD_CLEAR:
                eventData = new Events.EventBoard(Events.BoardType.BOARD_CLEAR,board,connectionID);
                break;
            case BOARD_INITIALIZE:
                if(writable){
                    eventData = new Events.EventBoard(Events.BoardType.BOARD_INITIALIZE_WRITABLE, board, connectionID);
                }else {
                    eventData = new Events.EventBoard(Events.BoardType.BOARD_INITIALIZE, board, connectionID);
                }
                break;
            case BOARD_EDITING:
                eventData = new Events.EventBoard(Events.BoardType.BOARD_EDITING,board,connectionID);
                break;
            case BOARD_EDITING_END:
                eventData = new Events.EventBoard(Events.BoardType.BOARD_EDITING_END,board,connectionID);
                break;
            case BOARD_END:
                eventData = new Events.EventBoard(Events.BoardType.BOARD_END);
                break;
            default:
                Log.e("BoardCommand", "Accion no identificada");
        }

        //Invocar evento
        GlobalBus.getBus().post( eventData );

        return new OperationResult(OperationResult.ResultCode.OK);
    }

    public BGraphic getBoard() {
        return board;
    }

    public void setBoard(BGraphic board) {
        this.board = board;
    }

    public BoardType getBoardType() {
        return boardType;
    }

    public void setBoardType(BoardType boardType) {
        this.boardType = boardType;
    }
}

