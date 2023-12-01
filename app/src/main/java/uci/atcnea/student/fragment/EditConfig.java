package uci.atcnea.student.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import de.hdodenhof.circleimageview.CircleImageView;
import main.model.User;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.utils.ConfigPreferences;


/**
 * Created by guillermo on 15/11/16.
 */
public class EditConfig {


    private Context activity;

    private Dialog customDialog;

    private EditText npDiscover;
    private EditText ipServidor;
    private EditText npServer;
    private EditText npFile;
    private CircleImageView editIv;


    public void ShowView(Context act, User user) {


        activity = act;

        customDialog = new Dialog(act);

        customDialog.setCancelable(false);
        //View loggin_view = this.getActivity().getLayoutInflater().inflate(R.layout.student_login, null);

        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//        customDialog.setContentView(R.layout.preferent_edit);
        //customDialog.setView(loggin_view);
        View view= LayoutInflater.from(act).inflate(R.layout.preferent_edit,null);

        customDialog.setContentView(view);
        npDiscover=(EditText)view.findViewById(R.id.np_discovery);
        ipServidor=(EditText)view.findViewById(R.id.ip_address);




//        npFile=(EditText)view.findViewById(R.id.np_file);

        npDiscover.setText(ConfigPreferences.getInstance().getDiscoveryPort()+"");
//        npServer.setText(MainApp.getIntance().getSERVER_PORT()+"");
//        npFile.setText(MainApp.getIntance().getSERVER_FILE_PORT()+"");

        editIv = (CircleImageView) view.findViewById(R.id.edit_iv);
        //editIv.setImageDrawable( new IconicsDrawable(MainApp.getCurrentActivity().getApplicationContext(), GoogleMaterial.Icon.gmd_settings ).color(Color.WHITE).actionBar() );

        CircleImageView btn_deny = (CircleImageView) customDialog.findViewById(R.id.btn_edit_deny);
        btn_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customDialog.dismiss();
            }
        });


        CircleImageView btn_acept = (CircleImageView) customDialog.findViewById(R.id.btn_edit_acept);

        btn_acept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(npDiscover.getText().toString())>1025/*&&Integer.parseInt(npServer.getText().toString())>1025
                        && Integer.parseInt(npFile.getText().toString())>1027*/){
                    ConfigPreferences.getInstance().setDiscoveryPort(Integer.parseInt(npDiscover.getText().toString()));

                    customDialog.dismiss();
                }
                else {


                }

            }
        });
        customDialog.show();
        //showDialog1();
    }


}
