package main.command;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

import main.model.User;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.model.Quiz;

/**
 * @class   QuizConnection
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf Pregunta interactiva
 */
public class QuizConnection implements CommandInterface,Serializable {

    private static final long serialVersionUID = 9317;

    public enum QuizType {
        QUIZ,RESPONSE,EVALUATION
    }
    private String test;
    private QuizType quizType;


    public QuizConnection(String test,QuizType quizType) {
        this.test = test;
        this.quizType = quizType;
    }

    public QuizConnection() {
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    /**
     *
     * @param applicationContext
     * @return
     */
    @Override
    public OperationResult execute(Context applicationContext) {

        Log.d("Prueba",test);

        if (MainApp.isConnected()) {
            if (quizType.equals(QuizType.QUIZ)) {

                try{
                    //new code
                    JSONObject data = new JSONObject(test);

                    Quiz quiz = new Quiz(data);

                    showQuiz(quiz);
                    //end new code

                } catch (Exception e) {
                    Log.e("MY_DEBUG", "Error into JSON: -> " + e.getMessage());
                }
            }
            if (quizType.equals(QuizType.EVALUATION)){
                Intent intent = new Intent("QUIZ_RESULT");
                intent.putExtra("RESULT", test);
                applicationContext.sendBroadcast(intent);
                Log.e("MY_DEBUG", "Result  ->  " + test );
            }
        }
        return new OperationResult();
    }

    private void showQuiz(final Quiz quiz){
        MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainApp.showQuiz(quiz);
            }
        });
    }
}
