package uci.atcnea.student.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konifar.fab_transformation.FabTransformation;
import com.mikepenz.crossfader.Crossfader;
import com.mikepenz.crossfader.util.UIUtils;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import main.command.CheckConnection;
import main.command.LessonDisconnection;
import main.command.SendGroupUser;
import main.command.ShareScreenCommand;
import main.model.GroupSend;
import main.model.Lesson;
import main.model.User;
import uci.atcnea.core.interfaces.SyncTaskResultListenerInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.dao.Group;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.fragment.BoardFragment;
import uci.atcnea.student.fragment.ChatFragment;
import uci.atcnea.student.fragment.Controllers.BoardController;
import uci.atcnea.student.fragment.Controllers.ChatController;
import uci.atcnea.student.fragment.Controllers.FileController;
import uci.atcnea.student.fragment.Controllers.TaskController;
import uci.atcnea.student.fragment.EditConfig;
import uci.atcnea.student.fragment.EditProfile;
import uci.atcnea.student.fragment.EditProfileFragment;
import uci.atcnea.student.fragment.FileFragment;
import uci.atcnea.student.fragment.FragmentMenu;
import uci.atcnea.student.fragment.HomeFragment;
import uci.atcnea.student.fragment.LessonDiscoveryFragment;
import uci.atcnea.student.fragment.StudentListFragment;
import uci.atcnea.student.fragment.TaskFragment;
import uci.atcnea.student.service.LockScreenService;
import uci.atcnea.student.sharescreen.ShareScreen;
import uci.atcnea.student.utils.ATcneaUtil;
import uci.atcnea.student.utils.CrossfadeWrapper;
import uci.atcnea.student.utils.DialogBuilder;
import uci.atcnea.student.utils.GetPermissions;
import uci.atcnea.student.utils.LessonAdapter;
import uci.atcnea.student.utils.PhotoUtils;
import uci.atcnea.student.utils.TaskHelper;
import uci.atcnea.student.utils.TimeLineAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/*import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import io.github.rockerhieu.emojiconize.Emojiconize;*/

/*import com.konifar.fab_transformation.FabTransformation;*/
//import uci.atcnea.student.utils.TimeLineAdapter;

/**
 * @author Adrian Arencibia Herrera
 *         Copyright (c) 2016-2017 FORTES, UCI.
 * @version 1.0
 * @class MainActivity
 * @date 11/07/16
 * @description
 * @rf
 */
public class MainActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener, FastAdapter.OnClickListener<IDrawerItem>, AccountHeader.OnAccountHeaderListener {


    private static final String TAG = "MainActivity";
    public static final int ACTIVITY_SELECT_IMAGE = 2;
    //save our header or drawerResult
    private AccountHeader headerResult = null;
    private Drawer drawerResult = null;
    private MiniDrawer miniResult = null;
    private Crossfader crossFader;

    private static final int FILE_SELECT_CODE = 1;
    public String ip_server;
    public String file_to_send;
    public int file_size;
    public LessonAdapter lessonAdapter;


    //For search student in the list
    public SearchView searchEdit;

    // Para titulo de la clase en el menu superior
    public TextView menuClassName;

    private LessonDiscoveryFragment lessonDiscoveryFragment;

    public static Lesson[] selectedLesson = new Lesson[1];

    private Toolbar toolbar;

    //Fragments de la aplicacion
    private Bundle savedInstanceState;
    private ChatFragment chatFragment;
    private FileFragment fileFragment;
    private HomeFragment homeFragment;
    private EditProfileFragment editProfileFragment;
    private StudentListFragment studentListFragment;
    public BoardFragment boardFragment;
    private TaskFragment taskFragment;

    //for timeline
    public static FloatingActionButton btnSummaryList;
    private LinearLayout contener;
    private TextView summaryName;
    //end timeline

    private int currentFragment = 0;

    private boolean drawerInit;
    private ShareScreen y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       /* Emojiconize.activity(this).go();*/

        super.onCreate(savedInstanceState);
/*        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/icomoon.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );*/



        setContentView(R.layout.activity_main);

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MainApp.setCurrentActivity(this);

        //para el lenguaje del activity
        getResources().updateConfiguration(MainApp.config, null);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        this.savedInstanceState = savedInstanceState;

        drawerInit = false;

        //TEST CHAT
        LinkedList<GroupSend> groupSends = new LinkedList<>();
        groupSends.add(new GroupSend(0, "Sala"));
        groupSends.add(new GroupSend(2, "Sala 2"));
        SendGroupUser gu = new SendGroupUser(groupSends);
        //gu.execute(getApplication());

        // Intancia del titulo de la clase
        menuClassName = (TextView) findViewById(R.id.menu_class_title);

        //TimeLine
        MainApp.intanceTimeLineAdapter = new TimeLineAdapter();
        btnSummaryList = (FloatingActionButton) findViewById(R.id.btnSummaryList);
        contener = (LinearLayout) findViewById(R.id.sumary_layout);
        //btnSummaryList.setVisibility(View.INVISIBLE);
        btnSummaryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalBus.getBus().post(new Events.EventSummary(Events.SummaryType.OPEN_SUMMARY));

            }
        });
        summaryName = (TextView) findViewById(R.id.summary_name);
        ImageView summary_unshow = (ImageView) findViewById(R.id.summary_unshow);
        summary_unshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalBus.getBus().post(new Events.EventSummary(Events.SummaryType.CLOSE_SUMMARY_VISIBLE));
            }
        });
        //end timeline

        //Para menu
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        //earchEdit = (SearchView) findViewById(R.id.search_edit);//para edit de buscar
        //searchEdit.setVisibility(View.GONE);

        //Iniciando fragment por defecto

        //Configuracion inicial
        setUpInitialConfig();

        //Actualizar menu
        invalidateOptionsMenu();


       GetPermissions.askForFilePermissions(new GetPermissions.PermissionsCallBack() {
            @Override
            public void onSuccess() {


                Log.e("onSuccess","onSuccess");
            }

            @Override
            public void onError() {
                Log.e("onError","onError");

            }
        });

/*        GetPermissions.askForGeneralPermissions(new GetPermissions.PermissionsCallBack() {
            @Override
            public void onSuccess() {
                Log.e("onSuccess","onSuccess");
            }

            @Override
            public void onError() {
                Log.e("onError","onError");

            }
        });*/






    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void initView(boolean fromFragment) {

        setUpDrawerInitialConfig(savedInstanceState);
        setDrawerInit(true);

        if (this.savedInstanceState != null) {
            int tmp = this.savedInstanceState.getInt("currentFragment");
          /*  try {
                ChatController.setIntance((ChatController) this.savedInstanceState.getSerializable("chat_controller"));
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }*/



            // esto es pq este metodo lo llaman desde un fragment, y eos esta mal
            // invente esto para diferenciarlo
            if (fromFragment) {
                tmp = ATcneaUtil.DRAWER_HOME_IDENTIFIER;
            }
            generalItemClick(tmp);

            if (MainApp.isConnected()) {
                setUpDrawerConnectConfig();
            }
        } else {
            generalItemClick(ATcneaUtil.DRAWER_HOME_IDENTIFIER);

            if (MainApp.isConnected()) {
                setUpDrawerConnectConfig();
            }
        }

    }

    private void setUpInitialConfig() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

//        User st = MainApp.getCurrentUser();
        if (MainApp.getCurrentUser() == null) {

            studentListFragment = new StudentListFragment();
            ft.replace(R.id.fragment_content, studentListFragment);

            ft.commit();

            currentFragment = ATcneaUtil.FRAGMENT_SELECT_STUDENT;
        } else {


            // loadHomeFragment();
            initView(false);

        }
    }

    private void setUpDrawerInitialConfig(Bundle savedInstanceState) {

        //Actualizar menu
        invalidateOptionsMenu();

        AccountHeaderBuilder accountHeaderBuilder = ATcneaUtil.buildDrawerHeader(true, savedInstanceState);

        accountHeaderBuilder.withOnAccountHeaderListener(this);

        headerResult = accountHeaderBuilder.build();

        IDrawerItem[] drawerItems = ATcneaUtil.buildOffLineDrawersItems();


        drawerResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(drawerItems) // add the items we want to use with our Drawer
                .withGenerateMiniDrawer(true)
                .withSavedInstance(savedInstanceState)
                // build only the view of the Drawer (don't inflate it automatically in our layout which is done with .build())
                .buildView();


        //the MiniDrawer is managed by the Drawer and we just get it to hook it into the Crossfader
        miniResult = drawerResult.getMiniDrawer();

        //get the widths in px for the first and second panel
        int firstWidth = (int) UIUtils.convertDpToPixel(300, this);
        int secondWidth = (int) UIUtils.convertDpToPixel(72, this);

        //create and build our crossfader (see the MiniDrawer is also builded in here, as the build method returns the view to be used in the crossfader)
        //the crossfader library can be found here: https://github.com/mikepenz/Crossfader
        crossFader = new Crossfader()
                .withContent(findViewById(R.id.fragment_content))
                .withFirst(drawerResult.getSlider(), firstWidth)
                .withSecond(miniResult.build(this), secondWidth)
                .withSavedInstance(savedInstanceState)
                .build();

        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(new CrossfadeWrapper(crossFader));


        //define a shadow (this is only for normal LTR layouts if you have a RTL app you need to define the other one
        crossFader.getCrossFadeSlidingPaneLayout().setShadowResourceLeft(R.drawable.material_drawer_shadow_left);

        crossFader.withPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelOpened(View panel) {

            }

            @Override
            public void onPanelClosed(View panel) {

            }
        });
        if (MainApp.getCurrentUser() == null) {
            headerResult.removeProfile(0);
            //dialogAddProfile();
            //drawerResult.removeAllItems();
            //drawerResult.removeHeader();
        }

        drawerResult.setOnDrawerItemClickListener(this);
    }

    public void setUpDrawerConnectConfig() {

        drawerResult.removeAllItems();
        drawerResult.addItems(ATcneaUtil.buildDrawersItems());
        miniResult.getItemAdapter().removeRange(1, miniResult.getItemAdapter().getAdapterItemCount());
        miniResult.getItemAdapter().add(Arrays.asList(ATcneaUtil.buildDrawersItems()));
        for (IDrawerItem d : drawerResult.getDrawerItems()) {
            miniResult.updateItem(d.getIdentifier());
        }
        // miniResult.withOnMiniDrawerItemClickListener(this);
        //drawerResult.setOnDrawerItemClickListener(this);


    }

    public void setUpDrawerDisconnectConfig() {
        drawerResult.removeAllItems();

        drawerResult.addItems(ATcneaUtil.buildOffLineDrawersItems());

        miniResult.getItemAdapter().removeRange(1, miniResult.getItemAdapter().getAdapterItemCount());
        miniResult.getItemAdapter().add(Arrays.asList(ATcneaUtil.buildOffLineDrawersItems()));
        for (IDrawerItem d : drawerResult.getDrawerItems()) {
            miniResult.updateItem(d.getIdentifier());
        }
        // miniResult.withOnMiniDrawerItemClickListener(this);
        //drawerResult.setOnDrawerItemClickListener(this);
    }


    private void dialogAddProfile() {
        final ProfileDrawerItem profile = new ProfileDrawerItem();
        profile.withIcon(R.drawable.face1);

        AlertDialog.Builder builder = DialogBuilder.dialogProfile(this);
        View view = getLayoutInflater().inflate(R.layout.add_profile, null);
        final EditText editText = (EditText) view.findViewById(R.id.et_p_name);
        builder.setView(view);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (editText.getText().length() > 0) {
                    profile.withName(editText.getText().toString());
                    profile.withEmail(editText.getText().toString().toLowerCase() + "@uci.cu");
                    dialog.dismiss();
                    addProfile(profile);
                }

            }
        });

        builder.setCancelable(false);
        builder.show();


    }

    public void addProfile(ProfileDrawerItem profile) {

        headerResult.updateProfile(profile);
    }


    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {
                Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (isDrawerInit()) {
            //add the values which need to be saved from the drawer to the bundle
            outState = drawerResult.saveInstanceState(outState);
            //add the values which need to be saved from the accountHeader to the bundle
            outState = headerResult.saveInstanceState(outState);
            //add the values which need to be saved from the crossFader to the bundle
            outState = crossFader.saveInstanceState(outState);

        }

        outState.putInt("currentFragment", currentFragment);
     /*   try {
            outState.putSerializable("chat_controller", ChatController.getIntance());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }*/


        this.savedInstanceState = outState;

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //if (ATcneaUtil.getScreenOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
        inflater.inflate(R.menu.embedded, menu);
        //menu.findItem(R.id.menu_1).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_sort).color(Color.WHITE).actionBar());
       // menu.findItem(R.id.menu_config).setIcon(new IconicsDrawable(getApplicationContext(), GoogleMaterial.Icon.gmd_settings).color(Color.WHITE).actionBar());
        menu.findItem(R.id.menu_search_user).setIcon(R.drawable.menu_search);

        searchEdit = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search_user));
        searchEdit.setQueryHint(getResources().getString(R.string.search));
        searchEdit.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (StudentListFragment.studentAdapter != null) {
                    StudentListFragment.studentAdapter.setStudentList(MainApp.getDatabaseManager().filterListStudent(query));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            StudentListFragment.studentAdapter.notifyDataSetChanged();
                        }
                    });
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (StudentListFragment.studentAdapter != null) {
                    StudentListFragment.studentAdapter.setStudentList(MainApp.getDatabaseManager().filterListStudent(newText));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            StudentListFragment.studentAdapter.notifyDataSetChanged();
                        }
                    });
                }
                return false;
            }
        });

        menu.findItem(R.id.menu_new_user).setIcon(R.drawable.menu_add);
        menu.findItem(R.id.menu_help).setIcon(R.drawable.menu_help);
        //menu.findItem(R.id.menu_user_disconnect).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_close).color(Color.WHITE).actionBar());

        //}
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        update_menu_icons(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Actualizar el estado de los iconos del menu en el activity principal.
     *
     * @param menu menu a modificarse.
     */
    private void update_menu_icons(Menu menu) {

        if (currentFragment == ATcneaUtil.FRAGMENT_SELECT_STUDENT) {// Listado de estudiantes
            searchEdit.setVisibility(View.VISIBLE);//Mostrar buscador
            menu.findItem(R.id.menu_search_user).setVisible(true);
            menu.findItem(R.id.menu_help).setVisible(true);
            menu.findItem(R.id.menu_new_user).setVisible(true);
            menu.findItem(R.id.menu_1).setVisible(false);
            menu.findItem(R.id.menu_user_disconnect).setVisible(false);
            menuClassName.setVisibility(View.GONE);
        } else if (currentFragment == ATcneaUtil.PROFILE_SETTING_CREATE) {//Por quitarse
            searchEdit.setVisibility(View.GONE);//Ocultar buscador
            menu.findItem(R.id.menu_search_user).setVisible(false);
            menu.findItem(R.id.menu_help).setVisible(false);
            menu.findItem(R.id.menu_new_user).setVisible(false);
            menu.findItem(R.id.menu_1).setVisible(false);
            menu.findItem(R.id.menu_user_disconnect).setVisible(false);
            menuClassName.setVisibility(View.VISIBLE);
        } else {// Dentro de la clase
            searchEdit.setVisibility(View.GONE);//Ocultar buscador
            menu.findItem(R.id.menu_search_user).setVisible(false);
            menu.findItem(R.id.menu_help).setVisible(false);
            menu.findItem(R.id.menu_new_user).setVisible(false);
            menu.findItem(R.id.menu_1).setVisible(true);
            //Para ocultar el boton de desconectarse
            if (MainApp.isConnected()) {
                // Mostrar la opcion de desconectarse de la clase
                MenuItem item = menu.findItem(R.id.menu_user_disconnect);
                item.setVisible(true);
                // Activar o desactivar en caso de ser necesario
                if (MainApp.isPermitDisconnection()) {
                    item.setIcon(R.drawable.app_android_general_logout);
                } else {
                    item.setIcon(R.drawable.app_android_general_logout_off);
                }
                menuClassName.setVisibility(View.VISIBLE);
            } else {
                menu.findItem(R.id.menu_user_disconnect).setVisible(false);
                menuClassName.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (drawerResult != null && drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else if (currentFragment != 0 && currentFragment == ATcneaUtil.PROFILE_SETTING_CREATE) {
            loadStudentListFragment();

        } else if (currentFragment != 0 &&
                currentFragment != ATcneaUtil.DRAWER_HOME_IDENTIFIER &&
                currentFragment != ATcneaUtil.FRAGMENT_SELECT_STUDENT &&
                currentFragment != ATcneaUtil.FRAGMENT_DISCOVERY) {
            loadHomeFragment();

        } else if (currentFragment != 0 && currentFragment == ATcneaUtil.DRAWER_HOME_IDENTIFIER) {

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case R.id.menu_1://open/close menu lateral
                crossFader.crossFade();
                return true;
            case R.id.menu_search_user://filtrar lista de estudiantes
                if (StudentListFragment.studentAdapter != null) {
                    StudentListFragment.studentAdapter.setStudentList(MainApp.getDatabaseManager().filterListStudent(searchEdit.getQuery().toString()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            StudentListFragment.studentAdapter.notifyDataSetChanged();
                        }
                    });
                }
                return true;
            case R.id.menu_help://Por hacer
                return true;
            case R.id.menu_new_user://Crear estudiante nuevo
                /*EditProfileFragment.setAction(EditProfileFragment.ActionButton.SAVE);
                generalItemClick(ATcneaUtil.PROFILE_SETTING_CREATE);
*/
                create_user_profile(null);

                return true;
            case R.id.menu_user_disconnect:
//                desconectar user
                if (MainApp.isPermitDisconnection()) {
                    disconnectToLesson();

                    //limpiar listado del timelist
                    MainApp.SumaryList.clear();
                    //end timeline

                } else {
                    updateViewPermitDisconnection();
                    DialogBuilder.dialogInformation(this, getResources().getString(R.string.msg_not_disconnect));
                }

                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_config:

                createEditConfigDialog();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createEditConfigDialog() {

        (new EditConfig()).ShowView(this, null);

    }

    public void updateBadge(int count) {
        try {

            if (count == 0) {
                PrimaryDrawerItem itemMenu = new PrimaryDrawerItem()
                        .withName(R.string.drawer_item_menu_home)
                        .withIcon(R.drawable.app_android_menu_home)
                        .withIdentifier(ATcneaUtil.DRAWER_HOME_IDENTIFIER)
                        .withSelectable(true)
                        .withSetSelected(true);


                drawerResult.updateItem(itemMenu);

            } else{

                PrimaryDrawerItem itemMenu = new PrimaryDrawerItem()
                        .withName(R.string.drawer_item_menu_home)
                        .withIcon(R.drawable.app_android_menu_home)
                        .withBadgeStyle(new BadgeStyle(Color.RED, Color.RED))
                        .withBadge("" + count)
                        .withIdentifier(ATcneaUtil.DRAWER_HOME_IDENTIFIER)
                        .withIconColorRes(R.color.md_red_700);
                drawerResult.updateItem(itemMenu);

            }

            miniResult.updateItem(ATcneaUtil.DRAWER_HOME_IDENTIFIER);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }


    }


    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

        int id = (int) drawerItem.getIdentifier();
        generalItemClick(id);

        return false;
    }

    @Override
    public boolean onClick(View v, IAdapter<IDrawerItem> adapter, IDrawerItem item, int position) {
        int id = (int) item.getIdentifier();
        generalItemClick(id);
        return false;
    }

    public void generalItemClick(int id) {

        //Ocultar el boton de timeline
        if (MainApp.menuOpen) {
            GlobalBus.getBus().post(new Events.EventSummary(Events.SummaryType.CLOSE_SUMMARY_VISIBLE));
        } else {
            btnSummaryList.setVisibility(View.INVISIBLE);
        }
        //end ocultar

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        crossFader.withCanSlide(true);

        //Ocultando menus del board
//        BoardFragment.getColor1Menu().getActivityContentView().setVisibility( View.INVISIBLE );
//        BoardFragment.getColor2Menu().getActivityContentView().setVisibility( View.INVISIBLE );
//        BoardFragment.getToolsMenu().getActivityContentView().setVisibility( View.INVISIBLE );


        switch (id) {
            case ATcneaUtil.DRAWER_HOME_IDENTIFIER:

                loadHomeFragment();

                break;
            case ATcneaUtil.DRAWER_FILE_IDENTIFIER:

                if (fileFragment == null)
                    fileFragment = new FileFragment();

                if (!MainApp.menuOpen) {//mostrar si el menu de sumario no esta abierto
                    btnSummaryList.setVisibility(View.VISIBLE);
                }

                updateItem(
                        0,
                        true,
                        ATcneaUtil.DRAWER_FILE_IDENTIFIER,
                        R.drawable.app_android_menu_file,
                        R.drawable.ic_app_android_menu_file,
                        R.string.drawer_item_menu_files
                );
                ft.replace(R.id.fragment_content, fileFragment);
                ft.commit();

                // Actualizar objetos no visibles antes de crearse la intancia
                fileFragment.updateListOfFiles();

                currentFragment = ATcneaUtil.DRAWER_FILE_IDENTIFIER;

                break;
            case ATcneaUtil.DRAWER_CHAT_IDENTIFIER:

                if (chatFragment == null) {
                    chatFragment = new ChatFragment();
                    ChatController.getIntance().setChatFragment(chatFragment);

                }

                if (!MainApp.menuOpen) {//mostrar si el menu de sumario no esta abierto
                    btnSummaryList.setVisibility(View.VISIBLE);
                }

                //updateChatItem();
                updateItem(
                        0,
                        true,
                        ATcneaUtil.DRAWER_CHAT_IDENTIFIER,
                        R.drawable.app_android_menu_chat,
                        R.drawable.ic_app_android_menu_chat,
                        R.string.drawer_item_menu_chat
                );
                ft.replace(R.id.fragment_content, chatFragment);
                ft.commit();

                // Limpiar conteo del badge
                ChatController.getIntance().getChatObject().setNewMsgCount(0);

                currentFragment = ATcneaUtil.DRAWER_CHAT_IDENTIFIER;

                break;

            case ATcneaUtil.DRAWER_RA_IDENTIFIER:

                MainApp.openApp(this, "com.joakin.diaz");

                if (!MainApp.menuOpen) {//mostrar si el menu de sumario no esta abierto
                    btnSummaryList.setVisibility(View.VISIBLE);
                }

                //currentFragment = ATcneaUtil.DRAWER_RA_IDENTIFIER;

                break;

            case ATcneaUtil.DRAWER_BOARD_IDENTIFIER:

                crossFader.withCanSlide(false);

                //Para el fragment de pizarra
                Log.d("BOARD", "pintar pizarra");

                //Mostrar los menus del board
//                BoardFragment.getColor1Menu().getActivityContentView().setVisibility( View.VISIBLE );
//                BoardFragment.getColor2Menu().getActivityContentView().setVisibility( View.VISIBLE );
//                BoardFragment.getToolsMenu().getActivityContentView().setVisibility( View.VISIBLE );

                if (boardFragment == null) {
                    boardFragment = new BoardFragment();
                }

                //mostrar si el menu de sumario no esta abierto
                if (!MainApp.menuOpen) {
                    btnSummaryList.setVisibility(View.VISIBLE);
                }

                ft.replace(R.id.fragment_content, boardFragment);
                ft.commit();

                currentFragment = ATcneaUtil.DRAWER_BOARD_IDENTIFIER;

                break;

            case ATcneaUtil.DRAWER_TASK_IDENTIFIER:
                //Para el fragment de tareas

                if (taskFragment == null) {
                    taskFragment = new TaskFragment();
                }

                if (!MainApp.menuOpen) {//mostrar si el menu de sumario no esta abierto
                    btnSummaryList.setVisibility(View.VISIBLE);
                }

                updateItem(
                        0,
                        true,
                        ATcneaUtil.DRAWER_TASK_IDENTIFIER,
                        R.drawable.app_android_menu_homework,
                        R.drawable.ic_app_android_menu_homework,
                        R.string.drawer_item_menu_task
                );
                ft.replace(R.id.fragment_content, taskFragment);
                ft.commit();

                currentFragment = ATcneaUtil.DRAWER_TASK_IDENTIFIER;

                break;

            case ATcneaUtil.PROFILE_SETTING:

                GlobalBus.getBus().post(new Events.EventSummary(Events.SummaryType.CLOSE_SUMMARY_INVISIBLE));//Ocultar sumario

                if (isDrawerInit()) {
                    drawerResult.setSelection(ATcneaUtil.PROFILE_SETTING);
                    miniResult.updateItem(ATcneaUtil.PROFILE_SETTING);
                }

                //(new EditProfile()).ShowView(this, MainApp.getCurrentUser());

                (new EditProfile(this, MainApp.getCurrentUser())).show( getFragmentManager(), "tag" );

                /*//if (editProfileFragment == null) {
                EditProfileFragment.setAction(EditProfileFragment.ActionButton.EDIT);
                editProfileFragment = new EditProfileFragment();
                // }


                ft.replace(R.id.fragment_content, editProfileFragment);
                ft.commit();

                currentFragment = ATcneaUtil.PROFILE_SETTING;*/

                break;
            case ATcneaUtil.PROFILE_SETTING_CREATE:

                if (isDrawerInit()) {
                    drawerResult.setSelection(ATcneaUtil.PROFILE_SETTING);
                    miniResult.updateItem(ATcneaUtil.PROFILE_SETTING);
                }
                //if (editProfileFragment == null) {
                //EditProfileFragment.setAction(EditProfileFragment.ActionButton.EDIT);
                editProfileFragment = new EditProfileFragment();
                // }


                ft.replace(R.id.fragment_content, editProfileFragment);
                ft.commit();

                currentFragment = ATcneaUtil.PROFILE_SETTING_CREATE;

                break;
            case ATcneaUtil.FRAGMENT_SELECT_STUDENT:

                loadStudentListFragment();


                currentFragment = ATcneaUtil.FRAGMENT_SELECT_STUDENT;

                break;
            case ATcneaUtil.PROFILE_SETTING_CHANGE_USER:
                if (!MainApp.isConnected())
                    MainApp.reOpen();
                else {
                    DialogBuilder.dialogInformation(this, getString(R.string.msg_not_change_user)).create().show();
                }

                currentFragment = ATcneaUtil.PROFILE_SETTING_CHANGE_USER;
                break;
            case ATcneaUtil.FRAGMENT_DISCOVERY:
                loadHomeFragment();
                break;
            default:

                ft.replace(R.id.fragment_content, new FragmentMenu());

                ft.commit();

                currentFragment = ATcneaUtil.FRAGMENT_BLANK_DEFAULT;

                break;
        }


        //Actualizar los elementos del menu
        invalidateOptionsMenu();

    }

    private void loadStudentListFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (studentListFragment == null) {
            studentListFragment = new StudentListFragment();
        }


        ft.replace(R.id.fragment_content, studentListFragment);
        ft.commit();

        currentFragment = ATcneaUtil.FRAGMENT_SELECT_STUDENT;
    }

    private void loadHomeFragment() {


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (!MainApp.isConnected()) {

            //BoardController.getIntance().initIntance();

            if (lessonAdapter == null)
                lessonAdapter = new LessonAdapter(MainActivity.this);
            if (lessonDiscoveryFragment == null) {
                lessonDiscoveryFragment = new LessonDiscoveryFragment();
                lessonDiscoveryFragment.setStartWithDiscover(false);
            }


            ft.replace(R.id.fragment_content, lessonDiscoveryFragment);

            currentFragment = ATcneaUtil.FRAGMENT_DISCOVERY;
        } else {

            //Mostrar boton de timeline si esta el user conectado
            if (!MainApp.menuOpen) {//mostrar si el menu de sumario no esta abierto
                btnSummaryList.setVisibility(View.VISIBLE);
            }
            //end Mostrar

            // Mostrar nombre de la clase
            menuClassName.setText(MainApp.getCurrentLesson().getLessonName());

            if (homeFragment == null) {
                homeFragment = new HomeFragment();
            }

            if (lessonDiscoveryFragment == null) {
                lessonDiscoveryFragment = new LessonDiscoveryFragment();
                lessonDiscoveryFragment.setStartWithDiscover(false);
            }

            ft.replace(R.id.fragment_content, homeFragment);

            currentFragment = ATcneaUtil.DRAWER_HOME_IDENTIFIER;
        }

        ft.commit();

    }

    /**
     * Conectarse a la clase
     */
    public void connectToLesson() {

        MainApp.setConnected(true);
        setUpDrawerConnectConfig();
        chatFragment=null;
        initFragments();
        drawerResult.setSelection(ATcneaUtil.DRAWER_HOME_IDENTIFIER);
        miniResult.setSelection(ATcneaUtil.DRAWER_HOME_IDENTIFIER);

        //Hilo para evento de desconectarse
        /*new Thread() {
            @Override
            public void run() {
                while (MainApp.isConnected()) {
                    SendMessageService s = new SendMessageService();
                    s.setCommand(new CheckConnection());
                    s.setWaitForResponse(true);
                    s.setCallback(new SyncTaskResultListenerInterface() {
                        @Override
                        public void onSyncTaskEventCompleted(OperationResult result, String ip) {
                            if(result != null && result.getCode() == OperationResult.ResultCode.OK){
                                // conexion bien
                            }else{
                                // conexion mal
                                GlobalBus.getBus().post(new Events.EventDisconect());
                            }
                        }
                    });
                    TaskHelper.execute(s);
                    Log.d(TAG, "CheckConnection");
                    try {
                        Thread.sleep(Integer.parseInt(MainApp.getCurrentActivity().getString(R.string.check_connection_time)));
                        //Thread.sleep(MainApp.getIntance().getInte);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();*/

    }

    public void initFragments() {
        boardFragment = new BoardFragment();//Iniciar fragment de pizarra
        taskFragment = new TaskFragment();//Iniciar fragment de tarea
        //if (chatFragment == null) {
            chatFragment = new ChatFragment();
            //  chatFragment.initChatConnection();
            //ChatController.getIntance().setChatFragment(chatFragment);
        //}
        //if (homeFragment == null) {
            homeFragment = new HomeFragment();
        //}
        //if (fileFragment == null) {
            fileFragment = new FileFragment();
        //}
        //if (editProfileFragment == null) {
            editProfileFragment = new EditProfileFragment();
        //}
        //if (studentListFragment == null) {
            studentListFragment = new StudentListFragment();
        //}

        BoardController.getIntance();
        TaskController.getIntance();
        FileController.getIntance();
        ChatController.getIntance();

    }

    public void disconnectToLesson() {
        if (MainApp.getWaitingConfirmation() == null) {
            SendMessageService service = new SendMessageService(MainApp.getCurrentServer());
            LessonDisconnection disconnection = new LessonDisconnection();
            service.setCommand(disconnection);
            service.setWaitForResponse(true);
            service.setCallback(new SyncTaskResultListenerInterface() {
                @Override
                public void onSyncTaskEventCompleted(OperationResult result, String ip) {
                    Log.e(TAG, "disconnectToLesson " + result.getCode());

                    switch (result.getCode()) {
                        case WAITING_CONFIRMATION:
                            AlertDialog.Builder builder = DialogBuilder.showDialogProgres(MainActivity.this);
                            AlertDialog alertDialog = builder.create();
                            MainApp.setDialogProgress(alertDialog);
                            MainApp.getDialogProgress().show();
                            MainApp.setWaitingConfirmation(MainApp.getCurrentLesson());

                            break;
                        case OK:
                        default:

                            MainApp.client.stop();
                            GlobalBus.getBus().post(new Events.EventDisconect());

                            break;

                    }
                }
            });
            TaskHelper.execute(service);
        }

    }


    public void updateViewDisconnect(boolean swd) {
        //lessonDiscoveryFragment = new LessonDiscoveryFragment();
        try {

            lessonDiscoveryFragment.setStartWithDiscover(swd);
            setUpDrawerDisconnectConfig();
            drawerResult.setSelection(ATcneaUtil.DRAWER_HOME_IDENTIFIER);
        } catch (Exception e) {
            getLessonDiscoveryFragment();
        }
    }

    public HomeFragment getHomeFragment() {
        return homeFragment;
    }

    public void setHomeFragment(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    public LessonDiscoveryFragment getLessonDiscoveryFragment() {
        if (lessonDiscoveryFragment == null) {
            lessonDiscoveryFragment = new LessonDiscoveryFragment();
            lessonDiscoveryFragment.setStartWithDiscover(false);
        }
        return lessonDiscoveryFragment;
    }

    public void setLessonDiscoveryFragment(LessonDiscoveryFragment lessonDiscoveryFragment) {
        this.lessonDiscoveryFragment = lessonDiscoveryFragment;
    }

    public void showNotifyChat(String msg, String type) {
        chatFragment.setSalaName(msg);
        DialogBuilder.dialogInformation(this, getString(R.string.msg_chat_notify) + " " + msg).show();
    }

  /*  public void showInteractiveQuestion() {
        Intent intent = new Intent(this, InteractiveQuestionActivity.class);
        startActivity(intent);
    }*/

   /* public void showQuiz(Quiz quiz) {
        ATcneaUtil.quiz = quiz;
        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        startActivity(intent);

    }*/

  /*  public void showAllowDenyHandDialog(String title, String msg, Boolean good) {
        if (Lock.lockScreen){
            Intent intent = new Intent("MSG_HAND_LOCK_SCREEN");
            intent.putExtra("GOOD_HAND", good);
            intent.putExtra("MSG_HAND", msg);

            sendBroadcast(intent);
        }else{
            showInformationDialog(title, msg, good);
        }
    }*/

   /* public void showLockHandDialog(String title, String msg, Boolean good) {
        if (Lock.lockScreen){
            Intent intent = new Intent("LOCK_HAND_LOCK_SCREEN");
            intent.putExtra("GOOD_HAND", good);
            intent.putExtra("MSG_HAND", msg);

            sendBroadcast(intent);
        }else{
            sendBroadcast(new Intent("LOCK_HAND_HOME_FRAGMENT"));
            showInformationDialog(title, msg, good);
        }
    }*/

  /*  public void showInformationDialog(String title, String msg, Boolean good) {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        if (good) {
            builder.setIcon(R.drawable.ic_accept);
        } else {
            builder.setIcon(R.drawable.ic_deny);
        }
//        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();

    }*/

    /* private void sendInteractiveQuestionCmd(String json) {
         SendMessageService service = new SendMessageService(MainApp.getCurrentServer());
         SendInteractiveQuestion siq = new SendInteractiveQuestion(json, SendInteractiveQuestion.TypeInteractiveQuestion.IQ_RESPONSE);
         service.setCommand(siq);
         service.setWaitForResponse(false);
         TaskHelper.execute(service);
     }
 */
    public void noConnectToLesson() {
        DialogBuilder.dialogInformation(this, getResources().getString(R.string.msgCancelConection)).show();
    }

    public void updateItem(int n, boolean selected, int identifier,int iconUp, int iconDown, int textId) {

        PrimaryDrawerItem item = new PrimaryDrawerItem()
                .withName(textId)
                .withIdentifier(identifier)
                .withIcon(iconUp)
                .withSelectable(true)
                .withSelectedIcon(iconDown)
                .withSetSelected(selected);

        if(n > 0) {
            item = item.withBadgeStyle(new BadgeStyle(Color.RED, Color.RED))
                    .withBadge(n + "")
                    .withIconColorRes(R.color.md_red_700);
        }

        drawerResult.updateItem(item);
        //drawerResult.updateIcon(ATcneaUtil.DRAWER_CHAT_IDENTIFIER, new ImageHolder(FontAwesome.Icon.faw_commenting));
        miniResult.updateItem(identifier);
    }


    public FileFragment getFileFragment() {
        return fileFragment;
    }

    public void setFileFragment(FileFragment fileFragment) {
        this.fileFragment = fileFragment;
    }


    public void updateViewPermitDisconnection() {
        if (homeFragment != null)
            homeFragment.updateViewPermitDisconnection();
    }

    public void updateViewPermitChat() {
        if (chatFragment != null)
            chatFragment.updateViewPermitChat();

    }


    @Override
    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
        generalItemClick((int) profile.getIdentifier());
        return false;
    }

    public void updateProfile(User currentUser) {

        headerResult.removeProfile(0);
        headerResult.addProfile(currentUser, 0);
        headerResult.setActiveProfile(currentUser);
        miniResult.updateProfile();
    }

    public void selectMenuItem(int drawerHomeIdentifier) {

        drawerResult.setSelection(drawerHomeIdentifier);
        miniResult.updateItem(drawerHomeIdentifier);
        generalItemClick(drawerHomeIdentifier);
    }

    public boolean isDrawerInit() {
        return drawerInit;
    }

    public void setDrawerInit(boolean drawerInit) {
        this.drawerInit = drawerInit;
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "Pause");
        super.onPause();
        // onResume();
    }

    @Override
    protected void onDestroy() {


      /* if (MainApp.isConnected() && !MainApp.isOpenOther()) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtras(savedInstanceState);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }*/
        MainApp.setOpenOther(false);
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        // Toast.makeText(this, "RESUME", Toast.LENGTH_SHORT).show();
        MainApp.setCurrentActivity(this);
        super.onResume();
        //para actualizar el RecyclerView del sumario (llamar evento)
        GlobalBus.getBus().post(new Events.EventSummary(Events.SummaryType.UPDATE_SUMMARY));
    }


    @Override
    public void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
/*        GlobalBus.getBus().register(BoardController.getIntance());
        GlobalBus.getBus().register(TaskController.getIntance());
        GlobalBus.getBus().register(FileController.getIntance());
        GlobalBus.getBus().register(ChatController.getIntance());*/
    }

    @Override
    public void onStop() {
        super.onStop();
        try {

            GlobalBus.getBus().unregister(this);

        }catch (Exception e){
            e.printStackTrace();
        }
        /*ChatController.destroitIntance();
        BoardController.destroitIntance();
        FileController.destroitIntance();
        TaskController.destroitIntance();*/
    }

    /**
     * Mostrar el mensaje de crear o editar estudiante
     *
     * @param user null si es un usuario nuevo, en caso contrario se modifica el usuario existente.
     */
    private void create_user_profile(User user) {
        //(new EditProfile()).ShowView(this, null);
        EditProfile editProfile = new EditProfile(this, null);
        editProfile.setCancelable(false);
        editProfile.show( getFragmentManager(), "tag" );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ShareScreen.REQUEST_CODE) {
            MainApp.getIntance().getMyShareScreen().handleActivityResult(this, requestCode, resultCode, data);
        }

        if (requestCode == MainActivity.ACTIVITY_SELECT_IMAGE && resultCode == MainActivity.RESULT_OK) {

            PhotoUtils photoUtils = new PhotoUtils(this);
            String imagePath = photoUtils.saveImageProfile(((Uri) data.getData()).toString());
            GlobalBus.getBus().post(new Events.EventUserImageEdit(imagePath));

            //MainApp.getCurrentUser().setImagePath(path);
            //MainApp.getCurrentUser().withIcon(Uri.parse(path));
            //imageView.setImageURI(Uri.parse(imagePath));
            //((MainActivity) MainApp.getCurrentActivity()).updateProfile(MainApp.getCurrentUser());
//            miniResult.updateProfile();
        }
    }

    //----------------------------------------------------------//
    //                Eventos del bus de eventos                //
    //----------------------------------------------------------//
    @Subscribe
    public void eventSumary(final Events.EventSummary event) {//Para eventos del sumario
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (event.sumaryType) {
                    case OPEN_SUMMARY://Abrir sumario
                        FabTransformation.with(btnSummaryList)
                                .duration(500)
                                .setListener(new FabTransformation.OnTransformListener() {
                                    @Override
                                    public void onStartTransform() {
                                        MainApp.menuOpen = true;//marcar como q menu esta abierto

                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);

                                        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

                                        mRecyclerView.setLayoutManager(linearLayoutManager);
                                        mRecyclerView.setHasFixedSize(true);

                                        mRecyclerView.setAdapter(MainApp.intanceTimeLineAdapter);
                                    }

                                    @Override
                                    public void onEndTransform() {

                                    }
                                })
                                .transformTo(contener);
                        break;
                    case CLOSE_SUMMARY_VISIBLE://Ocultar sumario
                        FabTransformation.with(btnSummaryList)
                                .duration(500)
                                .setListener(new FabTransformation.OnTransformListener() {
                                    @Override
                                    public void onStartTransform() {
                                        MainApp.menuOpen = false;//marcar como q menu esta cerrado
                                    }

                                    @Override
                                    public void onEndTransform() {
                                    }
                                })
                                .transformFrom(contener);
                        break;
                    case CLOSE_SUMMARY_INVISIBLE://Ocultar sumario y boton
                        FabTransformation.with(btnSummaryList)
                                .duration(500)
                                .setListener(new FabTransformation.OnTransformListener() {
                                    @Override
                                    public void onStartTransform() {
                                        MainApp.menuOpen = false;//marcar como q menu esta cerrado
                                    }

                                    @Override
                                    public void onEndTransform() {
                                        btnSummaryList.setVisibility(View.INVISIBLE);
                                    }
                                })
                                .transformFrom(contener);
                        break;
                    case UPDATE_SUMMARY://Actualizar listado del sumario
                        if (MainApp.sumaryName.equals("")) {//Clase sin sumario
                            summaryName.setText(getResources().getString(R.string.summary_title_empty));
                        } else {//Clase con sumario
                            // Por terminar
                            summaryName.setText("Nombre del sumario");
                        }
                        MainApp.intanceTimeLineAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });
    }

    @Subscribe
    public void eventDisconnect(final Events.EventDisconect event) {

        // Desconectar los socket

        // Ejecutar acciones
        if (MainApp.isConnected()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    /*if (Lock.lockScreen) {
                        stopService(new Intent(MainActivity.this, LockScreenService.class));
                    }*/

                    // Parar la pantalla compartida
                    (new ShareScreenCommand(new byte[0] , ShareScreenCommand.ShareScreenStatus.STOP)).execute(MainActivity.this);

                    // Variable global de estado de conexion
                    MainApp.setConnected(false);

                    // Limpiando intacia de lesson actual
                    MainApp.setCurrentLesson(null);

                    // Limpiando intancia del server actual
                    MainApp.setCurrentServer(null);
                    MainApp.setWaitingConfirmation(null);

                    // Cerrar dialogos de proceso abiertos
                    if (MainApp.getDialogProgress() != null) {
                        MainApp.getDialogProgress().cancel();
                        MainApp.setDialogProgress(null);
                    }

                    // Destruir las instancias de los controladores
                    ChatController.destroitIntance();
                    BoardController.destroitIntance();
                    FileController.destroitIntance();
                    TaskController.destroitIntance();

                    // Ocultar mensajes existentes
                    if(!event.showMessage) {
                        ATcneaUtil.HideMessage();
                    }

                    // Por si no se a parado e server de krionet
                    if(MainApp.client != null  && MainApp.client.isConnected()){
                        MainApp.client.stop();
                    }

                    // Tumbar servicio de pantalla bloqueada
                    MainApp.getCurrentActivity().stopService(new Intent(MainApp.getAppContext(), LockScreenService.class));

                    // Llevar la vista a vista de lessonDiscovery
                    ((MainActivity) MainApp.getCurrentActivity()).updateViewDisconnect(true);

                    // Ocultar sumario (llamar evento)
                    GlobalBus.getBus().post(new Events.EventSummary(Events.SummaryType.CLOSE_SUMMARY_INVISIBLE));

                    // Limpiar sumario
                    MainApp.SumaryList = new ArrayList<>();
                }
            });
        }
    }

    //Eventos de chat
    @Subscribe
    public void eventUpdateChat(Events.EventChat eventNewChat) {


        //Para guardar los grupos en labase de datos
        for (GroupSend g : eventNewChat.getGroupList()
                ) {
            Group group = new Group();
            group.setName(g.getName());
            group.setCreatedAt(new Date());
            group.setUpdatedAt(new Date());
            group.setIdLesson(MainApp.getCurrentLesson().getIdLesson());
            MainApp.getDatabaseManager().insertGroup(group);
        }

        initFragments();
        ChatController.getIntance().eventUpdateChat(eventNewChat);

        android.util.Log.e("eventUpdateChat", "eventUpdateChat");
    }

    // Evento para los badges
    @Subscribe
    public void eventBadge(final Events.EventBadge event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (event.badgeType) {
                    case CHAT:
                        if (currentFragment != ATcneaUtil.DRAWER_CHAT_IDENTIFIER) {
                            updateItem(
                                    event.count,
                                    false,
                                    ATcneaUtil.DRAWER_CHAT_IDENTIFIER,
                                    R.drawable.app_android_menu_chat,
                                    R.drawable.ic_app_android_menu_chat,
                                    R.string.drawer_item_menu_chat
                            );
                        }else{
                            ChatController.getIntance().getChatObject().setNewMsgCount(0);
                        }
                        break;
                    case FILE:
                        if(currentFragment != ATcneaUtil.DRAWER_FILE_IDENTIFIER) {
                            updateItem(
                                    event.count,
                                    false,
                                    ATcneaUtil.DRAWER_FILE_IDENTIFIER,
                                    R.drawable.app_android_menu_file,
                                    R.drawable.ic_app_android_menu_file,
                                    R.string.drawer_item_menu_files
                            );
                        }else{
                            FileController.getIntance().newFiles = 0;
                        }
                        break;
                    case TASK:
                        if(currentFragment != ATcneaUtil.DRAWER_TASK_IDENTIFIER){
                            updateItem(
                                    event.count,
                                    false,
                                    ATcneaUtil.DRAWER_TASK_IDENTIFIER,
                                    R.drawable.app_android_menu_homework,
                                    R.drawable.ic_app_android_menu_homework,
                                    R.string.drawer_item_menu_task
                            );
                        }else{
                            TaskController.getIntance().newTasks = 0;
                        }
                        break;
                }
            }
        });
    }

    //Evento de chat
    @Subscribe
    public void eventUpdateChatMSG(final Events.EventChatMSG eventNewChatMSG) {

        // aMapChat.get(eventNewChatMSG.getMessage().getIdGroup()).add(new Msg_other(eventNewChatMSG.getMessage().getUsername(),eventNewChatMSG.getMessage().getMessage()));
        // rv_chat.setAdapter(aMapChat.get(eventNewChatMSG.getMessage().getIdGroup()));

        //Insertar en labase de datos
        //MainApp.getDatabaseManager().insertMessage(eventNewChatMSG.getMessage());


        MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Msg_other msg_other = new Msg_other(eventNewChatMSG.getMessage().getUsername(), eventNewChatMSG.getMessage().getMessage());
//                MainApp.getIntance().getChatObject().getaMapChat().get(eventNewChatMSG.getMessage().getIdGroup()).add(msg_other);
//               // if (rv_chat != null) {
                //    rv_chat.setAdapter(MainApp.getIntance().getChatObject().getaMapChat().get(eventNewChatMSG.getMessage().getIdGroup()));
                //rv_chat.notify();
                // }
                //if (tabLayout != null) {
                //   TabLayout.Tab tab = tabLayout.getTabAt(MainApp.getIntance().getChatObject().getaMapGroupId().get(eventNewChatMSG.getMessage().getIdGroup()));
                //    tab.select();
                //MainApp.getIntance().getChatObject().setCurrentTab(MainApp.getIntance().getChatObject().getaMapGroupId().get(eventNewChatMSG.getMessage().getIdGroup()));
                // }

                // MainApp.getIntance().getChatObject().getaMapChat().get(eventNewChatMSG.getMessage().getIdGroup()).notifyDataSetChanged();
                //((MainActivity) MainApp.getCurrentActivity()).updateChatItemNewMSG();
                //rv_chat.getLayoutManager().scrollToPosition(MainApp.getIntance().getChatObject().getaMapChat().get(eventNewChatMSG.getMessage().getIdGroup()).getItemCount()-1);
            }
        });
        //chatFragment.eventUpdateChatMSG(eventNewChatMSG);
        android.util.Log.e("eventUpdateChatMSG", "eventUpdateChatMSG");
    }

/*    @Override
    public void onEmojiconBackspaceClicked(View v) {

    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {

    }*/


    public int getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(int currentFragment) {
        this.currentFragment = currentFragment;
    }
}
