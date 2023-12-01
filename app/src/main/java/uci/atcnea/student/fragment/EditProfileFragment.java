package uci.atcnea.student.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import main.command.UpdateStudentInfo;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;

import uci.atcnea.student.dao.DatabaseManager;
import uci.atcnea.student.dao.User;
import uci.atcnea.student.utils.ATcneaUtil;
import uci.atcnea.student.utils.DbBitmapUtility;
import uci.atcnea.student.utils.PhotoUtils;
import uci.atcnea.student.utils.TaskHelper;

/**
 * @class   EditProfileFragment
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf Crear una cuenta
 */
public class EditProfileFragment extends Fragment implements Validator.ValidationListener {


    TextInputLayout inputLayoutName;
    TextInputLayout inputLayoutEmail;
    TextInputLayout inputLayoutApllid;

    TextInputLayout inputLayoutUser;
    TextInputLayout inputLayoutPass;
    TextInputLayout inputLayoutPassRepeat;

    @NotEmpty(messageResId = R.string.error_e_name)
    EditText et_name;
    @NotEmpty(messageResId = R.string.error_e_apell)
    EditText et_apellidos;

    @Email(messageResId = R.string.error_e_email)
    EditText et_email;


    @NotEmpty(messageResId = R.string.error_e_user, trim = true)
    EditText et_user;

   /* @Password(scheme = Password.Scheme.ANY, messageResId = R.string.error_e_pass)
    EditText et_pass;
    @ConfirmPassword(messageResId = R.string.error_e_pass_repeat)
    EditText et_pass_repeat;
  */

    CircleImageView imageView;

    FloatingActionButton fab;
    Validator validator;

    String imagePath;

    @Override
    public void onValidationSucceeded() {


        JSONObject jsonObject = new JSONObject();

        String pass = "";//et_pass.getText().toString();
        try {
            jsonObject.put("name", et_name.getText().toString());
            jsonObject.put("lastname", et_apellidos.getText().toString());
            jsonObject.put("email", et_email.getText().toString());

            if (getAction() == ActionButton.SAVE) {
                if (imagePath == null || imagePath == "") {
                    PhotoUtils photoUtils = new PhotoUtils(getContext());
                    imagePath = photoUtils.saveImageProfile(DbBitmapUtility.drawableToBitmap(imageView.getDrawable()));
                }

                WifiManager manager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = manager.getConnectionInfo();

                String mac = info.getMacAddress();
                User student = new User(true,imagePath,et_user.getText().toString(), pass, jsonObject.toString(),(new Date()).toString() ,(new Date()).toString(), MainApp.getDatabaseManager().getNomenclator("Student").getId());

                MainApp.getDatabaseManager().insertStudent(student);
                MainApp.setCurrentUser(student);
                ((MainActivity) getActivity()).initView(true);
               // ((MainActivity) MainApp.getCurrentActivity()).generalItemClick(ATcneaUtil.FRAGMENT_SELECT_STUDENT);

            } else {

                MainApp.getCurrentUserDao().setProfileJson(jsonObject.toString());
                MainApp.getCurrentUserDao().setUsername(et_user.getText().toString());
                MainApp.getCurrentUserDao().setPassword(pass);
                MainApp.getCurrentUserDao().setImagePath(imagePath);
                MainApp.getDatabaseManager().updateStudent( MainApp.getCurrentUserDao());

                ((MainActivity) MainApp.getCurrentActivity()).updateProfile(MainApp.getCurrentUser());

                if (MainApp.isConnected()) {
                    SendMessageService service = new SendMessageService(MainApp.getCurrentServer());
                    service.setCommand(new UpdateStudentInfo());
                    service.setWaitForResponse(true);
                    TaskHelper.execute(service);
                    ((MainActivity) MainApp.getCurrentActivity()).selectMenuItem(ATcneaUtil.DRAWER_HOME_IDENTIFIER);
                } else {
                    ((MainActivity) MainApp.getCurrentActivity()).selectMenuItem(ATcneaUtil.DRAWER_HOME_IDENTIFIER);

                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this.getContext());

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this.getContext(), message, Toast.LENGTH_LONG).show();
            }
        }

    }

    public enum ActionButton {
        SAVE, EDIT
    }


    private static ActionButton action ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.user_profile_edit, null);
        inputLayoutName = (TextInputLayout) rootview.findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) rootview.findViewById(R.id.input_layout_email);
        inputLayoutApllid = (TextInputLayout) rootview.findViewById(R.id.input_layout_apell);

        inputLayoutUser = (TextInputLayout) rootview.findViewById(R.id.input_layout_user);

        //  inputLayoutPass = (TextInputLayout) rootview.findViewById(R.id.input_layout_pass);
        //  inputLayoutPassRepeat = (TextInputLayout) rootview.findViewById(R.id.input_layout_pass_repeat);


        et_name = (EditText) rootview.findViewById(R.id.et_name);
        et_apellidos = (EditText) rootview.findViewById(R.id.et_apellidos);
        et_email = (EditText) rootview.findViewById(R.id.et_email);
        et_user = (EditText) rootview.findViewById(R.id.et_user);

//             et_pass = (EditText) rootview.findViewById(R.id.et_pass);
//             et_pass_repeat = (EditText) rootview.findViewById(R.id.et_pass_repeat);
//        Cambiar el valor del PASS

        validator = new Validator(this);
        validator.setValidationListener(this);

//        et_name.addTextChangedListener(new MyTextWatcher(et_name, inputLayoutName));
//        et_apellidos.addTextChangedListener(new MyTextWatcher(et_apellidos, inputLayoutApllid));
//        et_email.addTextChangedListener(new MyTextWatcher(et_email, inputLayoutEmail));
//
//        et_user.addTextChangedListener(new MyTextWatcher(et_user, inputLayoutUser));
//        et_pass.addTextChangedListener(new MyTextWatcher(et_pass, inputLayoutPass));
//        et_pass_repeat.addTextChangedListener(new MyTextWatcher(et_pass_repeat, inputLayoutPassRepeat));

        imageView = (CircleImageView) rootview.findViewById(R.id.image_profile);

        fab = (FloatingActionButton) rootview.findViewById(R.id.btn_edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, MainActivity.ACTIVITY_SELECT_IMAGE);
            }
        });

        if (getAction() == ActionButton.EDIT) {
            try {
                JSONObject jsonStudent = new JSONObject(MainApp.getCurrentUser().getProfileJson() != null ? MainApp.getCurrentUser().getProfileJson() :
                        "");
                et_name.setText(jsonStudent.getString("name"));
                et_apellidos.setText(jsonStudent.getString("lastname"));
                et_email.setText(jsonStudent.getString("email"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            et_user.setText(MainApp.getCurrentUser().getUsername());
            initViews();
            imagePath=MainApp.getCurrentUser().getImagePath();
        } else
            imagePath = "";

        return rootview;
    }

    private void initViews() {
        if (MainApp.getCurrentUser().getImagePath() != null && MainApp.getCurrentUser().getImagePath().length() > 1)
            imageView.setImageURI(Uri.parse(MainApp.getCurrentUser().getImagePath()));
//        else
//            imageView.setImageDrawable(MainApp.getCurrentUser().getIcon().getIconDrawable());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.ACTIVITY_SELECT_IMAGE && resultCode == MainActivity.RESULT_OK) {

            PhotoUtils photoUtils = new PhotoUtils(getContext());
            imagePath = photoUtils.saveImageProfile(((Uri) data.getData()).toString());
            //MainApp.getCurrentUser().setImagePath(path);
            //MainApp.getCurrentUser().withIcon(Uri.parse(path));
            imageView.setImageURI(Uri.parse(imagePath));
            //((MainActivity) MainApp.getCurrentActivity()).updateProfile(MainApp.getCurrentUser());
//            miniResult.updateProfile();
        }
    }


    public static ActionButton getAction() {
        return action;
    }

    public static void setAction(ActionButton ac) {
        action = ac;
    }


//    private static class MyTextWatcher implements TextWatcher {
//
//        private View view;
//        private TextInputLayout inputLayout;
//
//        private MyTextWatcher(View view) {
//            this.view = view;
//        }
//
//        public MyTextWatcher(View view, TextInputLayout inputLayout) {
//            this.view = view;
//            this.inputLayout = inputLayout;
//        }
//
//        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        }
//
//        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        }
//
//        public void afterTextChanged(Editable editable) {
//            switch (view.getId()) {
//                case R.id.et_name:
//                    validateName((EditText) view, inputLayout);
//                    break;
//                case R.id.et_apellidos:
//                    validateApelli((EditText) view, inputLayout);
//                    break;
//                case R.id.et_email:
//                    validateEmail((EditText) view, inputLayout);
//                    break;
//            }
//        }
//    }
//
//    private static boolean validateName(EditText inputName, TextInputLayout inputLayoutName) {
//        if (inputName.getText().toString().trim().isEmpty()) {
//            inputLayoutName.setError(MainApp.getCurrentActivity().getResources().getString(R.string.error_e_name));
//            //requestFocus(inputName);
//            return false;
//        } else {
//            inputLayoutName.setErrorEnabled(false);
//        }
//
//        return true;
//    }
//
//    private static boolean validateApelli(EditText inputName, TextInputLayout inputLayoutName) {
//        if (inputName.getText().toString().trim().isEmpty()) {
//            inputLayoutName.setError(MainApp.getCurrentActivity().getResources().getString(R.string.error_e_apell));
//            ///  requestFocus(inputName);
//            return false;
//        } else {
//            inputLayoutName.setErrorEnabled(false);
//        }
//
//        return true;
//    }
//
//    private static boolean validateEmail(EditText inputEmail, TextInputLayout inputLayoutEmail) {
//        String email = inputEmail.getText().toString().trim();
//
//        if (email.isEmpty() || !isValidEmail(email)) {
//            inputLayoutEmail.setError(MainApp.getCurrentActivity().getResources().getString(R.string.error_e_email));
//            //requestFocus(inputEmail);
//            return false;
//        } else {
//            inputLayoutEmail.setErrorEnabled(false);
//        }
//
//        return true;
//    }
//
//    private static boolean isValidEmail(String email) {
//        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
//    }

}
