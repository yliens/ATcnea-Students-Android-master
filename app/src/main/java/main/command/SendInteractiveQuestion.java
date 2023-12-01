package main.command;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.model.InteractiveQuestion;
import uci.atcnea.student.utils.ATcneaUtil;

/**
 * @class   SendInteractiveQuestion
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf Enviar pregunta interactiva

 */
public class SendInteractiveQuestion implements CommandInterface,Serializable {

    private static final long serialVersionUID = 9318;

    //private String TAG = this.getClass().getName();

    public enum TypeInteractiveQuestion {
        INTERACTIVE_QUESTION,
        IQ_RESPONSE,
        IQ_EVALUATION,
        IQ_DEPRECATED
    }

    private String interactiveQuestionJson;

    private TypeInteractiveQuestion type;

    public SendInteractiveQuestion(String interactiveQuestionJson, TypeInteractiveQuestion type) {
        this.interactiveQuestionJson = interactiveQuestionJson;
        this.type = type;
    }

    public SendInteractiveQuestion() {
    }

    @Override
    public OperationResult execute(Context applicationContext) {
        if (MainApp.isConnected()) {
            if (type == TypeInteractiveQuestion.INTERACTIVE_QUESTION) {
                JSONObject json;
                try {
                    json = new JSONObject(interactiveQuestionJson);

                    InteractiveQuestion question = new InteractiveQuestion(json.getJSONObject("data"));

                    question.setStudent_server_id(json.getInt("student"));

                    if (question.hasItems()) {
                        question.setItems(json.getJSONArray("items"));
                    }

                    showDialog(question, applicationContext);

                } catch (JSONException e) {
                    Log.e("MY_DEBUG", "Error Parsing JSON");
                }

                Log.e("MY_DEBUG", interactiveQuestionJson);

            } else if (type == TypeInteractiveQuestion.IQ_EVALUATION) {
                // La evaluacion de la pregunta
                Intent intent = new Intent("INTERACTIVE_QUESTION_RESULT");
                intent.putExtra("IQ_EVALUATION", interactiveQuestionJson );
                applicationContext.sendBroadcast(intent);
                Log.e("MY_DEBUG", interactiveQuestionJson);
            }else if(type == TypeInteractiveQuestion.IQ_DEPRECATED){
                // Para cuando se rechasa lo enviado por el usuario
                Intent intent = new Intent("INTERACTIVE_QUESTION_RESULT");
                applicationContext.sendBroadcast(intent);
            }
        }
        return new OperationResult(OperationResult.ResultCode.OK);
    }

    private void showDialog(final InteractiveQuestion question, final Context context) {
        //if (MainApp.getCurrentActivity() instanceof MainActivity) {

        MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ATcneaUtil.question = question;
//                    Intent intent = new Intent(((MainActivity) MainApp.getCurrentActivity()), InteractiveQuestionActivity.class);
//                    ((MainActivity) MainApp.getCurrentActivity()).startActivity(intent);
                    MainApp.showInteractiveQuestion();

                }
            });
        // }

    }

    public String getInteractiveQuestionJson() {
        return interactiveQuestionJson;
    }

    public void setInteractiveQuestionJson(String interactiveQuestionJson) {
        this.interactiveQuestionJson = interactiveQuestionJson;
    }

    public TypeInteractiveQuestion getType() {
        return type;
    }

    public void setType(TypeInteractiveQuestion type) {
        this.type = type;
    }
}