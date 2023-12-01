package uci.atcnea.student.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import de.hdodenhof.circleimageview.CircleImageView;
import main.model.User;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;

import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.utils.DialogBuilder;
import uci.atcnea.student.utils.StudentAdapter;

/**
 * @class   StudentListFragment
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf
 */
public class StudentListFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static GridView studentGridView = null;
    public static StudentAdapter studentAdapter = null;
    //FloatingActionButton floatingActionButton;

    // Bandera de contrasenna guardada
    private static boolean savePassword = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.student_list, null);
        studentGridView = (GridView) viewRoot.findViewById(R.id.student_list);
        //floatingActionButton=(FloatingActionButton) viewRoot.findViewById(R.id.student_list_add_student);

        studentAdapter = new StudentAdapter();
        studentAdapter.setStudentList(MainApp.getDatabaseManager().listStudent());
        studentGridView.setAdapter(studentAdapter);

        studentGridView.setOnItemClickListener(this);
        studentGridView.setOnItemLongClickListener(this);
        return viewRoot;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        showLoginAuth((uci.atcnea.student.dao.User) studentAdapter.getItem(position));
    }

    public void showLoginAuth(final uci.atcnea.student.dao.User user) {
        // Dialogo para loggin del usuario
        final Dialog customDialog = new Dialog(getContext());

        // Desactivando opcion de cancelar del dialogo
        customDialog.setCancelable(false);

        // Fondo transparente para el estilo
        customDialog.getWindow().setBackgroundDrawable( new ColorDrawable(Color.TRANSPARENT));

        // Asignando vista del mensaje
        customDialog.setContentView(R.layout.student_login);

        // Imagen del usuario
        CircleImageView imageView=(CircleImageView)customDialog.findViewById(R.id.iv);
        imageView.setImageURI(Uri.parse(user.getImagePath()));

        // Edit para nombre de usuario
        final TextView userName = (TextView) customDialog.findViewById(R.id.userName);
        userName.setText(user.getName().getText());

        // Edit para el password
        final EditText pswTextView = (EditText) customDialog.findViewById(R.id.password);

        // Contenedor de recirdar password
        final CheckBox loginRememberPassword = (CheckBox) customDialog.findViewById(R.id.login_remember_password);
        loginRememberPassword.setChecked(false);

        // Chequear que el password este guardado
        /*for (int i=0;i<MainApp.rememberPassID.size();i++){
            if( user.getId().equals(MainApp.rememberPassID.get(i)) ){
                MainApp.rememberPassID.remove(i);
                savePassword = true;
                pswTextView.setText("asdas1231asda5454");
                loginRememberPassword.setChecked(true);
                break;
            }
        }*/
        savePassword = user.getSavePassword();
        if(savePassword){
            pswTextView.setText("asdas1231asda5454");
            loginRememberPassword.setChecked(true);
        }

        // Boton de cancelar el loggin
        CircleImageView btn_deny = (CircleImageView) customDialog.findViewById( R.id.btn_login_deny );
        btn_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });

        // Texto de notificacion de password incorrecto
        final TextView text_incorect_password = (TextView) customDialog.findViewById( R.id.login_incorect_password );

        // Boton de logearse
        CircleImageView btn_acept = (CircleImageView) customDialog.findViewById( R.id.btn_login_acept );
        btn_acept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psw = pswTextView.getText().toString();
                if(savePassword || psw.equals(user.getPassword())) {

                    // Salvar password en caso de estar activa la opcion
                    if(loginRememberPassword.isChecked()){
                        //MainApp.rememberPassID.add( user.getId() );
                        user.setSavePassword( true );
                    }else{
                        user.setSavePassword( false );
                    }

                    MainApp.getDatabaseManager().updateStudent( user );

                    // Actualizar el usuario actual
                    MainApp.setCurrentUser(user);

                    // Iniciar vistas
                    ((MainActivity) getActivity()).initView(true);
                    // sendMSGLogin();

                    //Iniciar componentes visuales (Fragments)
                    ((MainActivity)getActivity()).initFragments();

                    customDialog.dismiss();
                }else{
                    // Problemas en el loggin
                    //DialogBuilder.dialogInformation(getContext(),getString(R.string.error_incorrect_password)).show();
                    /*text_incorect_password.setVisibility( View.VISIBLE );
                    Animation shake = AnimationUtils.loadAnimation( MainApp.getCurrentActivity(), R.anim.shake );
                    text_incorect_password.startAnimation(shake);*/
                    pswTextView.setError(getString(R.string.error_incorrect_password));
                }
            }
        });

        // Para desactivar pasword salvado en caso de modificar el campo
        pswTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(savePassword){
                    savePassword = false;
                    pswTextView.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Mostrar dialogo
        customDialog.show();

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = DialogBuilder.dialogInformation(getContext(), getString(R.string.msg_delete_student));
        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainApp.getDatabaseManager().deleteStudentById(((uci.atcnea.student.dao.User)studentAdapter.getItem(position)).getId());
                studentAdapter.setStudentList(MainApp.getDatabaseManager().listStudent());
                studentAdapter.notifyDataSetChanged();
            }
        });

        builder.show();

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        GlobalBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void eventUpdateChatMSG(final Events.EventChangeFragment event) {
        ((MainActivity) getActivity()).initView(true);
    }
}
