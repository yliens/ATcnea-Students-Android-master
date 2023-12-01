package uci.atcnea.student.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.icons.MaterialDrawerFont;

import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;


/**
 *
 */
public class DialogBuilder {

    public static AlertDialog.Builder dialogInformation(Context context, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));

        builder.setMessage(message);
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder;

    }

    public static AlertDialog.Builder dialogProfile(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        return builder;
    }

    public static AlertDialog.Builder showDialogProgres(Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View view = MainApp.getCurrentActivity().getLayoutInflater().inflate(R.layout.dialog_progres, null);
        dialog.setView(view);
        dialog.setCancelable(false);

        return dialog;
    }


    public static AlertDialog.Builder dialogEditProfile(final MainActivity context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View rootview = MainApp.getCurrentActivity().getLayoutInflater().inflate(R.layout.user_profile_edit, null);

        final TextInputLayout inputLayoutName = (TextInputLayout) rootview.findViewById(R.id.input_layout_name);
        final TextInputLayout inputLayoutEmail = (TextInputLayout) rootview.findViewById(R.id.input_layout_email);
        final TextInputLayout inputLayoutApllid = (TextInputLayout) rootview.findViewById(R.id.input_layout_apell);


        final EditText et_name = (EditText) rootview.findViewById(R.id.et_name);
        final EditText et_apellidos = (EditText) rootview.findViewById(R.id.et_apellidos);
        final EditText et_email = (EditText) rootview.findViewById(R.id.et_email);


        et_name.addTextChangedListener(new MyTextWatcher(et_name,inputLayoutName));

        et_apellidos.addTextChangedListener(new MyTextWatcher(et_apellidos,inputLayoutApllid));


        et_email.addTextChangedListener(new MyTextWatcher(et_email,inputLayoutEmail));

        builder.setView(rootview);
        builder.setTitle(R.string.title_dialog_edit_details);
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(validateName(et_name,inputLayoutName)&& validateApelli(et_apellidos,inputLayoutApllid) &&
                validateEmail(et_email,inputLayoutEmail)){

                    dialog.cancel();

                }
                //dialog.dismiss();
            }
        });
        return builder;

    }

    private static class MyTextWatcher implements TextWatcher {

        private View view;
        private TextInputLayout inputLayout;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public MyTextWatcher(View view, TextInputLayout inputLayout) {
            this.view = view;
            this.inputLayout = inputLayout;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.et_name:
                    validateName((EditText) view,inputLayout);
                    break;
                case R.id.et_apellidos:
                    validateApelli((EditText) view,inputLayout);
                    break;
                case R.id.et_email:
                    validateEmail((EditText) view,inputLayout);
                    break;
            }
        }
    }

    private static boolean validateName(EditText inputName, TextInputLayout inputLayoutName) {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(MainApp.getCurrentActivity().getResources().getString(R.string.error_e_name));
          ///  requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean validateApelli(EditText inputName, TextInputLayout inputLayoutName) {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(MainApp.getCurrentActivity().getResources().getString(R.string.error_e_apell));
            ///  requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }
    private static boolean validateEmail(EditText inputEmail, TextInputLayout inputLayoutEmail) {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(MainApp.getCurrentActivity().getResources().getString(R.string.error_e_email));
            //requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}