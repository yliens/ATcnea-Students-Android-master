package uci.atcnea.student.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.File;

import main.blibrary.BColor;
import main.blibrary.BGraphic;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import main.blibrary.BoardView;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.fragment.Controllers.BoardController;
import uci.atcnea.student.utils.GetFileRealPath;

/**
 * Created by guillermo on 17/11/16.
 */
public class BoardFragment extends Fragment {

    public static final int FILE_SELECT_CODE = 1;

    // Menu para las herramientas
    private static FloatingActionMenu toolsMenu;
    // Menu para color 1
    private static FloatingActionMenu color1Menu;
    // menu para colo
    private static FloatingActionMenu color2Menu;

    //Botones de los menus
    public static FloatingActionButton button1;
    public static FloatingActionButton button2;
    public static FloatingActionButton button3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_board, null);

        BoardView board = (BoardView) fragment_view.findViewById(R.id.board_main);
        // Registrar el componente de forma global
        BoardController.getIntance().boardView = board;

        //Registrar fragmnet actual
        BoardController.getIntance().fragment = this;

        // Colores por defecto de la pizarra
        board.setStroke_color( IntToBColor(Color.parseColor("#000000")) );
        board.setFill_color( IntToBColor(Color.TRANSPARENT) );

        getToolsMenu().setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
                getColor2Menu().close(true);
                getColor1Menu().close(true);
            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {

            }
        });
        getColor2Menu().setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
                getToolsMenu().close(true);
                getColor1Menu().close(true);
            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {

            }
        });
        getColor1Menu().setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
                getToolsMenu().close(true);
                getColor2Menu().close(true);
            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {

            }
        });

        // Iconos iniciales de los botones de menus
        button1.setBackgroundResource( R.drawable.ic_board_pencil );
        button2.setBackgroundResource( R.drawable.ic_board_color_alpha );
        button3.setBackgroundResource( R.drawable.ic_board_stroke_black );

        if(button1 != null) {
            button1.setVisibility(View.VISIBLE);
            button2.setVisibility(View.VISIBLE);
            button3.setVisibility(View.VISIBLE);
        }

        return fragment_view;
    }

    /**
     * Crear menu de las herramientas del board
     * @return retorna el menu de herramientas
     */
    public static FloatingActionMenu getToolsMenu() {
        if(toolsMenu == null) {
            final int []arr = {
                    R.drawable.ic_board_circle,
                    R.drawable.ic_board_clear,
                    -9999,//R.drawable.ic_board_clear_board,
                    R.drawable.ic_board_image,
                    -9999,//R.drawable.ic_board_image_from_gallery,
                    R.drawable.ic_board_line,
                    R.drawable.ic_board_pencil,
                    R.drawable.ic_board_rectangle,
                    R.drawable.ic_board_text
            };

            //Eventos del menu
            View.OnClickListener events [] = new View.OnClickListener[arr.length];
            events[0] = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BoardController.getIntance().boardView.setTool_in_use(BGraphic.FigureType.CIRCLE );
                    button1.setBackgroundResource(arr[0]);
                    CloseMenus();
                }
            };
            events[1] = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BoardController.getIntance().boardView.setTool_in_use(BGraphic.FigureType.ERASE );
                    button1.setBackgroundResource(arr[1]);
                    CloseMenus();
                }
            };
            events[2] = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //board.setTool_in_use(BGraphic.FigureType.CIRCLE );
                    button1.setBackgroundResource(arr[2]);
                    CloseMenus();
                }
            };
            events[3] = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BoardController.getIntance().boardView.setTool_in_use(BGraphic.FigureType.IMAGE );
                    button1.setBackgroundResource(arr[3]);
                    CloseMenus();
                }
            };
            events[4] = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //board.setTool_in_use(BGraphic.FigureType.I );
                    button1.setBackgroundResource(arr[4]);
                    CloseMenus();
                }
            };
            events[5] = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BoardController.getIntance().boardView.setTool_in_use(BGraphic.FigureType.LINE );
                    button1.setBackgroundResource(arr[5]);
                    CloseMenus();
                }
            };
            events[6] = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BoardController.getIntance().boardView.setTool_in_use(BGraphic.FigureType.FREE_LINE );
                    button1.setBackgroundResource(arr[6]);
                    CloseMenus();
                }
            };
            events[7] = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BoardController.getIntance().boardView.setTool_in_use(BGraphic.FigureType.SQUARE );
                    button1.setBackgroundResource(arr[7]);
                    CloseMenus();
                }
            };
            events[8] = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BoardController.getIntance().boardView.setTool_in_use(BGraphic.FigureType.TEXT );
                    button1.setBackgroundResource(arr[8]);
                    CloseMenus();
                }
            };

            if(button1 == null) {
                ImageView icon = new ImageView(MainApp.getCurrentActivity()); // Create an icon
                //icon.setImageResource(R.drawable.ic_board_pencil);
                button1 = new FloatingActionButton.Builder(MainApp.getCurrentActivity())
                        .setContentView(icon)
                        .build();
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getColor2Menu().close(true);
                        getColor1Menu().close(true);
                    }
                });
            }

            toolsMenu = buildBoardMenus(MainApp.getCurrentActivity(),
                    button1,
                    arr,
                    events,
                    300,
                    80);
        }
        return toolsMenu;
    }

    private static void CloseMenus(){
        getToolsMenu().close(true);
        getColor2Menu().close(true);
        getColor1Menu().close(true);
    }

    /**
     * Crear menu de los colores del board 1
     * @return retorna el menu de colores 1
     */
    public static FloatingActionMenu getColor1Menu() {
        if(color1Menu == null) {
            //Iconos del menu
            final int []arr = {
                    R.drawable.ic_board_color_black,
                    R.drawable.ic_board_color_blue,
                    R.drawable.ic_board_color_green,
                    R.drawable.ic_board_color_red,
                    R.drawable.ic_board_color_yellow,
                    R.drawable.ic_board_color_white,
                    R.drawable.ic_board_color_alpha
            };

            //Eventos del menu
            View.OnClickListener events [] = new View.OnClickListener[arr.length];
            final int color[] = {
                    Color.parseColor("#000000"),
                    Color.parseColor("#0063b1"),
                    Color.parseColor("#498205"),
                    Color.parseColor("#e81123"),
                    Color.parseColor("#ffb900"),
                    Color.parseColor("#ffffff"),
                    Color.TRANSPARENT
            };
            for(int i=0;i<arr.length;i++){
                final int finalI = i;
                events[i] = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BoardController.getIntance().boardView.setFill_color( IntToBColor(color[finalI]) );
                        button2.setBackgroundResource( arr[finalI] );
                        CloseMenus();
                    }
                };
            }

            if(button2 == null) {
                ImageView icon = new ImageView(MainApp.getCurrentActivity()); // Create an icon
                //icon.setImageResource(R.drawable.ic_board_color_alpha);
                button2 = new FloatingActionButton.Builder(MainApp.getCurrentActivity())
                        .setContentView(icon)
                        .build();

                FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(50,50, Gravity.RIGHT | Gravity.BOTTOM);

                layout.setMargins(16,16,16,85);

                button2.setLayoutParams( layout );

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getToolsMenu().close(true);
                        getColor2Menu().close(true);
                    }
                });
            }

            color1Menu = buildBoardMenus(MainApp.getCurrentActivity(),
                    button2,
                    arr,
                    events,
                    250,
                    60);
        }
        button2.setVisibility(View.VISIBLE);
        return color1Menu;
    }

    /**
     * Convertir a BColor
     * @param color Color a convertir
     * @return Valor convertido
     */
    public static BColor IntToBColor(int color){
        return new BColor(
                Color.red(color),
                Color.green(color),
                Color.blue(color),
                Color.alpha(color)
        );
    }

    /**
     * Crear menu de los colores del board 2
     * @return retorna el menu de colores 2
     */
    public static FloatingActionMenu getColor2Menu() {
        if(color2Menu == null) {
            final int []arr = {
                    R.drawable.ic_board_stroke_black,
                    R.drawable.ic_board_stroke_blue,
                    R.drawable.ic_board_stroke_green,
                    R.drawable.ic_board_stroke_red,
                    R.drawable.ic_board_stroke_yellow,
                    R.drawable.ic_board_stroke_white
            };

            //Eventos del menu
            View.OnClickListener events [] = new View.OnClickListener[arr.length];
            final int color[] = {
                    Color.parseColor("#000000"),
                    Color.parseColor("#0063b1"),
                    Color.parseColor("#498205"),
                    Color.parseColor("#e81123"),
                    Color.parseColor("#ffb900"),
                    Color.parseColor("#ffffff")
            };
            for(int i=0;i<arr.length;i++){
                final int finalI = i;
                events[i] = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BoardController.getIntance().boardView.setStroke_color( IntToBColor(color[finalI]) );
                        button3.setBackgroundResource( arr[finalI] );
                        CloseMenus();
                    }
                };
            }

            if(button3 == null) {
                ImageView icon = new ImageView(MainApp.getCurrentActivity()); // Create an icon
                //icon.setImageResource(R.drawable.ic_board_stroke_black);

                button3 = new FloatingActionButton.Builder(MainApp.getCurrentActivity())
                        .setContentView(icon)
                        .build();

                FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(50,50, Gravity.RIGHT | Gravity.BOTTOM);

                layout.setMargins(16,16,16,145);

                button3.setLayoutParams( layout );
            }

            color2Menu = buildBoardMenus(MainApp.getCurrentActivity(),
                    button3,
                    arr,
                    events,
                    250,
                    60);
        }
        button3.setVisibility(View.VISIBLE);
        return color2Menu;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        getColor2Menu().close(false);
        getToolsMenu().close(false);
        getColor1Menu().close(false);

        button1.setVisibility( View.INVISIBLE );
        button2.setVisibility( View.INVISIBLE );
        button3.setVisibility( View.INVISIBLE );

        // borra datos
        toolsMenu = null;
        color1Menu = null;
        color2Menu = null;
        button1 = null;
        button2 = null;
        button3 = null;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        /*if(button1 != null) {
            button1.setVisibility(View.VISIBLE);
            button2.setVisibility(View.VISIBLE);
            button3.setVisibility(View.VISIBLE);
        }*/

    }



    static private FloatingActionMenu buildBoardMenus(Context context,
                                                      FloatingActionButton actionButton,
                                                      int[]icons,
                                                      View.OnClickListener[] events,
                                                      int ratio,
                                                      int popupRatio){

        // Factory de los sub-botones
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder((Activity) context)
                .setLayoutParams(new FloatingActionButton.LayoutParams(popupRatio, popupRatio));

        //Elemento a retornar
        FloatingActionMenu.Builder ret = new FloatingActionMenu.Builder((Activity) context);

        // Creando botones del menu
        for (int i=0;i<icons.length;i++) {
            if(icons[i] == -9999) continue;
            // Creando icono del boton
            ImageView itemIcon = (new ImageView(context));
            // Agregando recurso visual al boton
            itemIcon.setImageResource(icons[i]);
            // Creando el boton
            SubActionButton button = itemBuilder.setContentView(itemIcon).build();
            //Evento de los botones
            button.setOnClickListener(events[i]);
            button.setBackgroundColor(Color.TRANSPARENT);
            // Agregando boton al menu
            ret = ret.addSubActionView(button);
        }

        return ret.attachTo(actionButton)
                .setRadius(ratio)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Registrar evento
        GlobalBus.getBus().unregister(BoardController.getIntance().boardView);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Repintar pizarra marcarse el fragment
        BoardController.getIntance().boardView.DrawBoard();

        if(button1 != null) {
            button1.setVisibility(View.VISIBLE);
            button2.setVisibility(View.VISIBLE);
            button3.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == -1) {
            switch (requestCode) {
                case FILE_SELECT_CODE:
                    // Get the path
                    String path = GetFileRealPath.getPath(getContext(), data.getData());

                    android.util.Log.e("MY_DEBUG", "File Path: " + path);

                    final File file = new File(path);

                    BoardController.getIntance().boardView.PathImageCreate(file.getPath());

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
