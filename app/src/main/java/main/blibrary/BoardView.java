package main.blibrary;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import main.command.BoardCommand;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.fragment.BoardFragment;
import uci.atcnea.student.fragment.Controllers.BoardController;
import uci.atcnea.student.utils.TaskHelper;

/**
 * Created by guillermo on 3/11/16.
 */
public class BoardView extends View {

    //Colores de relleno y borde
    BColor fill_color;
    BColor stroke_color;

    public void setStroke_color(BColor stroke_color) {
        this.stroke_color = stroke_color;
    }

    public void setFill_color(BColor fill_color) {
        this.fill_color = fill_color;
    }

    //Variables para el uso de las figuras que lo necesiten (square, circle, etc)
    private double pivoteX;
    private double pivoteY;

    public BoardView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        //Definir la captura de eventos
        setOnTouchListener( eventCapture );

        // Pantalla editable
        // BoardController.getIntance().editing = true;

        //Herramienta por defecto al iniciar aplicacion
        //setTool_in_use(BGraphic.FigureType.FREE_LINE);

    }

    //Refrescar la imagen de la pizarra
    public synchronized void DrawBoard() {
        // Salvar en caso de existir valores en el board
        if(!BoardController.getIntance().board.getMy_Graphics().isEmpty()){
            saveCanvas();
        }
        //Reimprimir la pizarra
        invalidate();
    }

    private synchronized void shareBoard(BoardCommand command) {
        if(BoardController.getIntance().board_share) {
            SendMessageService service = new SendMessageService(MainApp.getCurrentServer());
            service.setCommand(command);
            service.setWaitForResponse(false);
            TaskHelper.execute(service);
        }
    }

    private boolean saveStateOfCanvas = false;

    /**
     * Para salvar el canvas actual
     */
    private void saveCanvas(){

        // Salir si la vista no esta creada aun
        if(this.getWidth() == 0)return;

        Bitmap board_tmp = Bitmap.createBitmap(
                Math.max(this.getWidth(),5),
                Math.max(this.getHeight(),5),
                Bitmap.Config.ARGB_8888);

        Canvas tmp = new Canvas(board_tmp);

        saveStateOfCanvas = true;
        this.draw(tmp);
        saveStateOfCanvas = false;

        BoardController.getIntance().board_tmp = board_tmp;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        BoardController.getIntance().canvasWidth = canvas.getWidth();
        BoardController.getIntance().canvasHeight = canvas.getHeight();

        //color de fondo
        canvas.drawColor(Color.WHITE);

        if( !saveStateOfCanvas ) {//Solo se ejecuta si no se esta salvando el estado del canvas
            //Dibujar la pizarra
            canvas.drawBitmap(BoardController.getIntance().board_tmp,0,0,new Paint());

            //Dibujar el remote tools
            if (BoardController.getIntance().remote_tools != null) {
                BoardController.getIntance().remote_tools.Draw(canvas);
            }

            //Dibujar el objeto en edicion si esta activo
            if ((BoardController.getIntance().isCreate || BoardController.getIntance().isEditing) && BoardController.getIntance().edit_tool != null) {
                BoardController.getIntance().edit_tool.Draw(canvas);
            }
        }else{
            //Dibujar la pizarra

            canvas.drawBitmap(BoardController.getIntance().board_tmp,0,0,new Paint());
            BoardController.getIntance().board.Draw(canvas);

            BoardController.getIntance().board.My_Graphics.clear();

            //canvas.drawBitmap(board_tmp,0,0,new Paint());
        }

    }


    public BGraphic.FigureType getTool_in_use() {
        return BoardController.getIntance().tool_in_use;
    }

    /**
     * Para seleccionar la herramienta que se desea utilizar
     *
     * @param tool_in_use herramienta a utilizarse
     */
    public void setTool_in_use(BGraphic.FigureType tool_in_use) {
        BoardController.getIntance().tool_in_use = tool_in_use;
        endActualEdition();
    }

    private void onActionUpHandle() {
        if (BoardController.getIntance().isCreate){
            BoardController.getIntance().edit_tool.setStatus(BGraphic.BGraphicStatus.EDITING);
            BoardController.getIntance().isCreate = false;
            BoardController.getIntance().isEditing = true;
        }
        DrawBoard();
    }

    private void onActionDownHandle(MotionEvent event) {
        pivoteX = event.getX();
        pivoteY = event.getY();

        //double anchor = sizeComponent.getValue();
        if (BoardController.getIntance().isCreate || BoardController.getIntance().isEditing) {
            BoardController.getIntance().isCreate = false;
            if (!(BoardController.getIntance().isEditing = (BoardController.getIntance().edit_tool.isContainer() && BoardController.getIntance().edit_tool.getContainer().In_Position(pivoteX, pivoteY)) ||
                    BoardController.getIntance().edit_tool.In_Position(pivoteX, pivoteY))) {
                shareBoard(new BoardCommand(BoardController.getIntance().edit_tool, BoardCommand.BoardType.BOARD_EDITING_END));
                BoardController.getIntance().board.Add_Graphic(BoardController.getIntance().edit_tool);

                DrawBoard();
            }
        }

    }

    public void PathImageCreate(String path){

        Bitmap bitmap = BitmapFactory.decodeFile(path);

        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, blob);
        byte[] array = blob.toByteArray();

        // Calculando el tamanno de la imagen
        double width = 0, height = 0;
        if(bitmap.getWidth() >= bitmap.getHeight()){
            width = 150;
            height = bitmap.getHeight() * 150 / bitmap.getWidth();
        }else{
            height = 150;
            height = bitmap.getWidth() * 150 / bitmap.getHeight();
        }

        BoardController.getIntance().edit_tool = new BImage(pivoteX, pivoteY, 10000, true, width, height, array);
        BoardController.getIntance().edit_tool.setBoardHeight( getCanvasHeight() );
        BoardController.getIntance().edit_tool.setBoardWidth( getCanvasWidth() );
        BoardController.getIntance().edit_tool.setStatus(BGraphic.BGraphicStatus.EDITING);
        BoardController.getIntance().isCreate = false;
        BoardController.getIntance().isEditing = true;

        DrawBoard();

    }

    //----------------------------------------------------------//
    //          Eventos segun el tipo de herramienta            //
    //----------------------------------------------------------//

    /**
     * Evento de OnActionDown del mouse sobre el componente
     * @param event Valores del evento onActionDown
     * @return Respuesta del evento
     */
    public boolean onActionDown(final MotionEvent event){//Evento de dar click
        if(!BoardController.getIntance().editing)return true;
        switch (BoardController.getIntance().tool_in_use){
            case FREE_LINE:
                onActionDownHandle(event);
                if (!BoardController.getIntance().isCreate && !BoardController.getIntance().isEditing) {
                    BoardController.getIntance().edit_tool = new BFree_Pain(0, 0, 10000, true, 5, stroke_color);
                    BoardController.getIntance().edit_tool.setBoardHeight(getCanvasHeight());
                    BoardController.getIntance().edit_tool.setBoardWidth(getCanvasWidth());
                    BoardController.getIntance().isCreate = true;
                }
                break;
            case CIRCLE:
                onActionDownHandle(event);
                if (!BoardController.getIntance().isCreate && !BoardController.getIntance().isEditing) {
                    BoardController.getIntance().edit_tool = new BCircle(pivoteX, pivoteY, 10000, true, 0, 0, 5, fill_color, stroke_color);
                    BoardController.getIntance().edit_tool.setBoardHeight( getCanvasHeight() );
                    BoardController.getIntance().edit_tool.setBoardWidth( getCanvasWidth() );
                    BoardController.getIntance().isCreate = true;
                }
                break;
            case ERASE:
                BoardController.getIntance().isCreate = false;
                onActionDownHandle(event);
                if (!BoardController.getIntance().isCreate) {
                    BoardController.getIntance().edit_tool = new BErase(event.getX(), event.getY(), 20, 20);
                    BoardController.getIntance().edit_tool.setBoardHeight(getCanvasHeight());
                    BoardController.getIntance().edit_tool.setBoardWidth(getCanvasWidth());
                    BoardController.getIntance().isCreate = true;
                }
                break;
            case IMAGE:
                onActionDownHandle(event);
                if (!BoardController.getIntance().isCreate && !BoardController.getIntance().isEditing) {
                    // Mostrar cuadro de busqueda de archivos para cargar imagen
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    try {
                        BoardController.getIntance().fragment.startActivityForResult(
                                Intent.createChooser(intent, "Select a File to Send"),
                                BoardFragment.FILE_SELECT_CODE);
                    } catch (android.content.ActivityNotFoundException ex) {
                        // Potentially direct the user to the Market with a Dialog
                        Toast.makeText(getContext(), "Please install a File Manager.",
                                Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case LINE:
                onActionDownHandle(event);
                if (!BoardController.getIntance().isCreate && !BoardController.getIntance().isEditing) {
                    BoardController.getIntance().edit_tool = new BLine(event.getX(), event.getY(), 10000, true, event.getX(), event.getY(), 5, stroke_color);
                    BoardController.getIntance().edit_tool.setBoardHeight( getCanvasHeight() );
                    BoardController.getIntance().edit_tool.setBoardWidth( getCanvasWidth() );
                    BoardController.getIntance().isCreate = true;
                }
                break;
            case SQUARE:
                onActionDownHandle(event);
                if (!BoardController.getIntance().isCreate && !BoardController.getIntance().isEditing) {
                    BoardController.getIntance().edit_tool = new BSquare(pivoteX, pivoteY, 10000, true, pivoteX, pivoteY, 5, fill_color, stroke_color);
                    BoardController.getIntance().edit_tool.setBoardHeight( getCanvasHeight() );
                    BoardController.getIntance().edit_tool.setBoardWidth( getCanvasWidth() );
                    BoardController.getIntance().isCreate = true;
                }
                break;
            case TEXT:
                onActionDownHandle(event);
                if(!BoardController.getIntance().isCreate && !BoardController.getIntance().isEditing){

                    BoardController.getIntance().edit_tool = new BText(
                            pivoteX,
                            pivoteY,
                            10000,
                            true,
                            24,
                            stroke_color,
                            "Arial",
                            ""
                    );
                    BoardController.getIntance().edit_tool.setBoardHeight( getCanvasHeight() );
                    BoardController.getIntance().edit_tool.setBoardWidth( getCanvasWidth() );

                    final Dialog inputText = new Dialog(BoardController.getIntance().fragment.getContext());
                    inputText.getWindow().setBackgroundDrawable( new ColorDrawable(Color.TRANSPARENT));

                    inputText.setContentView(R.layout.input_text);

                    final EditText inputTextValue = (EditText) inputText.findViewById(R.id.input_text);

                    CircleImageView btnInputAcept = (CircleImageView) inputText.findViewById(R.id.btn_input_acept);
                    btnInputAcept.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!inputTextValue.getText().equals("")){

                                ((BText)BoardController.getIntance().edit_tool).setText( inputTextValue.getText() + "" );

                                BoardController.getIntance().edit_tool.setStatus(BGraphic.BGraphicStatus.EDITING);
                                BoardController.getIntance().isEditing = true;

                                DrawBoard();

                                shareBoard(new BoardCommand(BoardController.getIntance().edit_tool, BoardCommand.BoardType.BOARD_EDITING));

                                inputText.dismiss();
                            }
                        }
                    });

                    CircleImageView btnInputCancel = (CircleImageView) inputText.findViewById(R.id.btn_input_cancel);
                    btnInputCancel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inputText.dismiss();
                        }
                    });

                    inputText.show();
                }
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Evento de OnActionMove del mouse sobre el componente
     * @param event Valores del evento onActionMove
     * @return Respuesta del evento
     */
    public boolean onActionMove(MotionEvent event){//Evento de mover el mouse
        if(!BoardController.getIntance().editing)return true;
        switch (BoardController.getIntance().tool_in_use){
            case FREE_LINE:
                if (BoardController.getIntance().isCreate) {
                    ((BFree_Pain) BoardController.getIntance().edit_tool).add(new BPoint(event.getX(), event.getY()));
                    DrawBoard();
                    shareBoard(new BoardCommand(BoardController.getIntance().edit_tool, BoardCommand.BoardType.BOARD_EDITING));
                } else if(BoardController.getIntance().isEditing){
                    double valX = event.getX() - pivoteX;
                    double valY = event.getY() - pivoteY;
                    pivoteX = event.getX();
                    pivoteY = event.getY();
                    BoardController.getIntance().edit_tool.setContainer(false);// Parche para no modificar el container
                    BoardController.getIntance().edit_tool.setX( valX );
                    BoardController.getIntance().edit_tool.setY( valY );
                    BoardController.getIntance().edit_tool.setContainer(true);// fin del parche
                    DrawBoard();
                    shareBoard(new BoardCommand(BoardController.getIntance().edit_tool, BoardCommand.BoardType.BOARD_EDITING));
                }
                break;
            case CIRCLE:
                if (BoardController.getIntance().isCreate) {
                    BoardController.getIntance().edit_tool.setX(Math.min(pivoteX, event.getX()));
                    BoardController.getIntance().edit_tool.setY(Math.min(pivoteY, event.getY()));
                    ((BCircle) BoardController.getIntance().edit_tool).setWidth(Math.abs(pivoteX - event.getX()));
                    ((BCircle) BoardController.getIntance().edit_tool).setHeight(Math.abs(pivoteY - event.getY()));
                    shareBoard(new BoardCommand(BoardController.getIntance().edit_tool, BoardCommand.BoardType.BOARD_EDITING));
                    DrawBoard();
                } else if (BoardController.getIntance().isEditing) {
                    UpdateEditingPosition(event);
                    shareBoard(new BoardCommand(BoardController.getIntance().edit_tool, BoardCommand.BoardType.BOARD_EDITING));
                }
                break;
            case ERASE:
                if (BoardController.getIntance().isCreate) {
                    ((BErase)BoardController.getIntance().edit_tool).add( new BPoint(event.getX(), event.getY()) );
                    DrawBoard();
                    shareBoard(new BoardCommand(BoardController.getIntance().edit_tool, BoardCommand.BoardType.BOARD_EDITING));
                }
                break;
            case IMAGE:
                //Por hacer
                if (BoardController.getIntance().isEditing) {
                    UpdateEditingPosition(event);
                    shareBoard(new BoardCommand(BoardController.getIntance().edit_tool, BoardCommand.BoardType.BOARD_EDITING));
                }
                break;
            case LINE:
                if (BoardController.getIntance().isCreate) {
                    ((BLine) BoardController.getIntance().edit_tool).setStopX(event.getX());
                    ((BLine) BoardController.getIntance().edit_tool).setStopY(event.getY());
                    DrawBoard();
                    shareBoard(new BoardCommand(BoardController.getIntance().edit_tool, BoardCommand.BoardType.BOARD_EDITING));
                } else if (BoardController.getIntance().isEditing) {
                    double valX = event.getX() - pivoteX;
                    double valY = event.getY() - pivoteY;
                    pivoteX = event.getX();
                    pivoteY = event.getY();
                    BoardController.getIntance().edit_tool.setContainer(false);// Parche para no modificar el container
                    BoardController.getIntance().edit_tool.setX( BoardController.getIntance().edit_tool.getX() + valX );
                    BoardController.getIntance().edit_tool.setY( BoardController.getIntance().edit_tool.getY() + valY );
                    BoardController.getIntance().edit_tool.setContainer(true);// fin del parche
                    ((BLine)BoardController.getIntance().edit_tool).setStopX( ((BLine)BoardController.getIntance().edit_tool).getStopX() + valX );
                    ((BLine)BoardController.getIntance().edit_tool).setStopY( ((BLine)BoardController.getIntance().edit_tool).getStopY() + valY );
                    DrawBoard();
                    shareBoard(new BoardCommand(BoardController.getIntance().edit_tool, BoardCommand.BoardType.BOARD_EDITING));
                }
                break;
            case SQUARE:
                if (BoardController.getIntance().isCreate) {
                    BoardController.getIntance().edit_tool.setX(Math.min(pivoteX, (int) event.getX()));
                    BoardController.getIntance().edit_tool.setY(Math.min(pivoteY, (int) event.getY()));
                    ((BSquare) BoardController.getIntance().edit_tool).setWidth(Math.abs(pivoteX - event.getX()));
                    ((BSquare) BoardController.getIntance().edit_tool).setHeight(Math.abs(pivoteY - event.getY()));
                    DrawBoard();
                    shareBoard(new BoardCommand(BoardController.getIntance().edit_tool, BoardCommand.BoardType.BOARD_EDITING));
                } else if (BoardController.getIntance().isEditing) {
                    UpdateEditingPosition(event);
                    shareBoard(new BoardCommand(BoardController.getIntance().edit_tool, BoardCommand.BoardType.BOARD_EDITING));
                }
                break;
            case TEXT:
                if (BoardController.getIntance().isEditing) {
                    UpdateEditingPosition(event);
                    shareBoard(new BoardCommand(BoardController.getIntance().edit_tool, BoardCommand.BoardType.BOARD_EDITING));
                }
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Evento de OnActionUp del mouse sobre el componente
     * @param event Valores del evento onActionUp
     * @return Respuesta del evento
     */
    public boolean onActionUp(MotionEvent event){//Evento de soltar el click
        if(!BoardController.getIntance().editing)return true;
        switch (BoardController.getIntance().tool_in_use){
            case FREE_LINE:
            case CIRCLE:
            case IMAGE:
            case LINE:
            case SQUARE:
                //saveCanvas();
                onActionUpHandle();
                break;
            case ERASE:
                shareBoard(new BoardCommand(BoardController.getIntance().edit_tool, BoardCommand.BoardType.BOARD_EDITING_END));
                BoardController.getIntance().board.Add_Graphic(BoardController.getIntance().edit_tool);

                BoardController.getIntance().isEditing = false;
                BoardController.getIntance().isCreate = false;

                DrawBoard();
                break;
            case TEXT:

                break;
            default:
                return false;
        }
        return true;
    }

    //----------------------------------------------------------//
    //       Eventos segun el tipo de herramienta (END)         //
    //----------------------------------------------------------//

    /**
     * Terminar edicion actual
     */
    public void endActualEdition(){
        if(BoardController.getIntance().isEditing && BoardController.getIntance().editing) {
            shareBoard(new BoardCommand(BoardController.getIntance().edit_tool, BoardCommand.BoardType.BOARD_EDITING_END));
            BoardController.getIntance().board.Add_Graphic(BoardController.getIntance().edit_tool);

            DrawBoard();
        }
        BoardController.getIntance().isCreate = false;
        BoardController.getIntance().isEditing = false;
    }

    /**
     * Actualizar elementos en edicion
     * @param event
     */
    private void UpdateEditingPosition(MotionEvent event){
        double valX = event.getX() - pivoteX;
        double valY = event.getY() - pivoteY;
        pivoteX = event.getX();
        pivoteY = event.getY();
        BoardController.getIntance().edit_tool.setX( BoardController.getIntance().edit_tool.getX() + valX );
        BoardController.getIntance().edit_tool.setY( BoardController.getIntance().edit_tool.getY() + valY );

        DrawBoard();
    }


    /**
     * Captura de los eventos
     */
    private OnTouchListener eventCapture = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onActionDown(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    onActionMove(event);
                    break;
                case MotionEvent.ACTION_UP:
                    onActionUp(event);
                    break;
            }
            return true;
        }
    };

    public BGraphic getBoard() {
        return BoardController.getIntance().board;
    }

    public double getCanvasWidth() {
        return BoardController.getIntance().canvasWidth;
    }

    public void setCanvasWidth(double canvasWidth) {
        BoardController.getIntance().canvasWidth = canvasWidth;
    }

    public double getCanvasHeight() {
        return BoardController.getIntance().canvasHeight;
    }

    public void setCanvasHeight(double canvasHeight) {
        BoardController.getIntance().canvasHeight = canvasHeight;
    }

}
