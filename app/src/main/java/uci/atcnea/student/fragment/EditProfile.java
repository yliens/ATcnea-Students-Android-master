package uci.atcnea.student.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.squareup.otto.Subscribe;
import com.thebluealliance.spectrum.SpectrumDialog;
import com.thebluealliance.spectrum.SpectrumPalette;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import main.command.UpdateStudentInfo;
import main.model.User;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.utils.ATcneaUtil;
import uci.atcnea.student.utils.ConfigPreferences;
import uci.atcnea.student.utils.DbBitmapUtility;
import uci.atcnea.student.utils.PhotoUtils;
import uci.atcnea.student.utils.TaskHelper;


/**
 * Created by guillermo on 15/11/16.
 */
@SuppressLint("ValidFragment")
public class EditProfile extends android.app.DialogFragment implements Validator.ValidationListener, SpectrumPalette.OnColorSelectedListener {
    @NotEmpty(messageResId = R.string.error_not_null)
    @Pattern(regex = "[A-Z][a-zA-Z0-9áéíóú]+(\\s[A-Z][a-zA-Z0-9áéíóú]+)*", messageResId = R.string.error_e_name)
    private EditText nameTextView;

    @NotEmpty(messageResId = R.string.error_not_null)
    @Pattern(regex = "[A-Z][a-zA-Z0-9áéíóú]+(\\s[A-Z][a-zA-Z0-9áéíóú]+)*", messageResId = R.string.error_e_apell)
    private EditText lastnameTextView;

    @NotEmpty(messageResId = R.string.error_not_null)
    @Pattern(regex = "[a-z0-9@\\.]{1,16}", messageResId = R.string.error_e_user)
    private EditText usernameTextView;

    @NotEmpty(messageResId = R.string.error_not_null)
    @Email(messageResId = R.string.error_e_email)
    private EditText mailTextView;

    @NotEmpty(messageResId = R.string.error_not_null)
    @Pattern(regex = "[a-zA-Z0-9@\\._\\*]{1,32}", messageResId = R.string.error_e_pass)
    private EditText psw1TextView;

    @NotEmpty(messageResId = R.string.error_not_null)
    @Pattern(regex = "[a-zA-Z0-9@\\._\\*]{1,32}", messageResId = R.string.error_e_pass)
    private EditText psw2TextView;

    private CircleImageView imageView;

    private ImageView ivEditAction;

    private Context activity;

    private static Dialog customDialog;

    /**
     * Dismissing window handler
     */
    private final Handler handler = new Handler();

    @ColorInt
    int color;

    private static User user;
    private static Context act;

    public EditProfile() {

    }

    public EditProfile(Context act, User user) {
        this.act = act;
        this.user = user;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        if (savedInstanceState != null)
            imagePath = savedInstanceState.getString("imagePath");
        ShowView();

        return customDialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("imagePath", imagePath);

        super.onSaveInstanceState(outState);

    }

    public void ShowView() {
        GlobalBus.getBus().register(this);

        this.user = user;

        activity = act;

        customDialog = new Dialog(act);

        customDialog.setCancelable(false);
        //View loggin_view = this.getActivity().getLayoutInflater().inflate(R.layout.student_login, null);

        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        customDialog.setContentView(R.layout.student_edit);
        //customDialog.setView(loggin_view);

        imageView = (CircleImageView) customDialog.findViewById(R.id.edit_iv);

        SpectrumPalette spectrumPalette = (SpectrumPalette) customDialog.findViewById(R.id.palette);
        int[] colors = activity.getResources().getIntArray(R.array.demo_colors);

        spectrumPalette.setColors(colors);
        spectrumPalette.setOnColorSelectedListener(this);

        ivEditAction = (ImageView) customDialog.findViewById(R.id.iv_edit_action);

        nameTextView = (EditText) customDialog.findViewById(R.id.edit_name);
        lastnameTextView = (EditText) customDialog.findViewById(R.id.edit_lastname);
        usernameTextView = (EditText) customDialog.findViewById(R.id.edit_username);
        mailTextView = (EditText) customDialog.findViewById(R.id.edit_mail);
        psw1TextView = (EditText) customDialog.findViewById(R.id.edit_password1);
        psw2TextView = (EditText) customDialog.findViewById(R.id.edit_password2);

        CircleImageView btn_deny = (CircleImageView) customDialog.findViewById(R.id.btn_edit_deny);
        btn_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                GlobalBus.getBus().unregister(EditProfile.this);
                }catch (Exception e){
                    e.printStackTrace();
                }
                customDialog.dismiss();
            }
        });


        if (user != null) {//Para elementos al usarse el editar user
            try {
                JSONObject jsonStudent = new JSONObject(MainApp.getCurrentUser().getProfileJson() != null ? MainApp.getCurrentUser().getProfileJson() :
                        "");
                nameTextView.setText(jsonStudent.getString("name"));
                lastnameTextView.setText(jsonStudent.getString("lastname"));
                mailTextView.setText(jsonStudent.getString("email"));
                color = jsonStudent.getInt("color");
                spectrumPalette.setSelectedColor(color);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Ocultar elementos no visibles en la edicion
            usernameTextView.setVisibility(View.GONE);

            imagePath = MainApp.getCurrentUser().getImagePath();
        } else {
            if (imagePath == null)
                imagePath = "";
            spectrumPalette.setSelectedColor(colors[0]);
        }

        if (!imagePath.equals("")) {//Cargar imagen si la tiene
            imageView.setImageURI(Uri.parse(imagePath));
        }

        CircleImageView btn_acept = (CircleImageView) customDialog.findViewById(R.id.btn_edit_acept);

        btn_acept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (psw1TextView.getText().toString().equals(psw2TextView.getText().toString())) {//comprobar el password
                    //guardar profile
                    Validator validator = new Validator(EditProfile.this);
                    validator.setValidationListener(EditProfile.this);

                    validator.validate();

                } else {
                    //password no coinside
                    //psw1TextView.setError(MainApp.getCurrentActivity().getResources().getString(R.string.error_e_pass));
                    psw2TextView.setError(MainApp.getCurrentActivity().getResources().getString(R.string.error_e_pass_repeat));
                }

            }
        });

        ivEditAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                ((Activity) activity).startActivityForResult(galleryIntent, MainActivity.ACTIVITY_SELECT_IMAGE);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                ((Activity) activity).startActivityForResult(galleryIntent, MainActivity.ACTIVITY_SELECT_IMAGE);

            }
        });

        //customDialog.show();

        //showDialog1();
    }

    private String imagePath;

    @Override
    public void onValidationSucceeded() {
        JSONObject jsonObject = new JSONObject();

        String pass = psw1TextView.getText().toString();
        try {
            jsonObject.put("name", nameTextView.getText().toString());
            jsonObject.put("lastname", lastnameTextView.getText().toString());
            jsonObject.put("email", mailTextView.getText().toString());
            jsonObject.put("color", color);

            if (user == null) {//Usuario nuevo
                if (imagePath == null || imagePath == "") {
                    PhotoUtils photoUtils = new PhotoUtils(activity);
                    imagePath = photoUtils.saveImageProfile(DbBitmapUtility.drawableToBitmap(imageView.getDrawable()));
                }

                /*WifiManager manager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = manager.getConnectionInfo();

                String mac = info.getMacAddress();*/
                String mac = ConfigPreferences.getInstance().getMacDevice();


                uci.atcnea.student.dao.User student = new uci.atcnea.student.dao.User(
                        true,
                        imagePath,
                        usernameTextView.getText().toString(),
                        pass, jsonObject.toString(),
                        (new Date()).toString(),
                        (new Date()).toString(),
                        MainApp.getDatabaseManager().getNomenclator("Student").getId());
                student.setNomenclator(MainApp.getDatabaseManager().getNomenclator("Student"));

                MainApp.getDatabaseManager().insertStudent(student);
                MainApp.setCurrentUser(student);


                //((MainActivity) activity).initView(true);

                GlobalBus.getBus().post(new Events.EventChangeFragment());

                // ((MainActivity) MainApp.getCurrentActivity()).generalItemClick(ATcneaUtil.FRAGMENT_SELECT_STUDENT);

                customDialog.dismiss();

            } else {//Editando usuario

                MainApp.getCurrentUserDao().setProfileJson(jsonObject.toString());
//                MainApp.getCurrentUserDao().setUsername(psw1TextView.getText().toString());
                MainApp.getCurrentUserDao().setPassword(pass);
                MainApp.getCurrentUserDao().setImagePath(imagePath);
                MainApp.getDatabaseManager().updateStudent(MainApp.getCurrentUserDao());

                ((MainActivity) MainApp.getCurrentActivity()).updateProfile(MainApp.getCurrentUser());

                if (MainApp.isConnected()) {
                    SendMessageService service = new SendMessageService(MainApp.getCurrentServer());
                    service.setCommand(new UpdateStudentInfo());
                    service.setWaitForResponse(true);
                    TaskHelper.execute(service);
                }

                ((MainActivity) MainApp.getCurrentActivity()).selectMenuItem(ATcneaUtil.DRAWER_HOME_IDENTIFIER);

                customDialog.dismiss();

            }
            GlobalBus.getBus().unregister(EditProfile.this);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(activity);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                //Por cambiarse
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        }
    }


    private void showDialog1() {
        new SpectrumDialog.Builder(activity)
                .setColors(R.array.demo_colors)
                .setSelectedColorRes(R.color.md_blue_500)
                .setDismissOnColorSelected(true)
                .setOutlineWidth(2)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
                            Toast.makeText(activity, "Color selected: #" + Integer.toHexString(color).toUpperCase(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "Dialog cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).build().show(((AppCompatActivity) activity).getSupportFragmentManager(), "dialog_demo_1");
    }


    @Subscribe
    public void eventEditImage(Events.EventUserImageEdit eventUserImageEdit) {

        imagePath = eventUserImageEdit.getImagePath();
        ((Activity) activity).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageURI(Uri.parse(imagePath));

            }
        });
    }

    @Override
    public void onColorSelected(@ColorInt int i) {
        color = i;
    }
}
