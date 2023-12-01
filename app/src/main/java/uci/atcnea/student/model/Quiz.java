package uci.atcnea.student.model;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by koyi on 20/05/16.
 */
public class Quiz {

    private int attempts;
    private String name;
    private int time;
    private String description;
    private String calification;
    private String evaluationType;
    private ArrayList< ArrayList<Question> > questions;
    private int id;
    private int student_id_server;

    public Quiz(JSONObject data) {
        try {
            //new code
            id = data.getInt("id");
            name = data.getString("name");
            description = data.getString("statement");
            time = data.getInt("time");
            attempts = data.getInt("attemps");

            questions = new ArrayList<>();
            // Iniciar los intentos
            for (int i=0;i<attempts;i++){
                questions.add( new ArrayList<Question>() );
            }

            evaluationType = data.getString("evaluation_type");
            student_id_server = data.getInt("student");

            //Interpretar preguntas
            setQuestions(data.getJSONArray("exercises"));
            //end new code

            /*evaluationType = data.getString("Evaluation");
            name = data.getString("Name");
            attempts = data.getInt("Attemps");
            description = data.getString("Description");
            time = data.getInt("Duration");
            calification = data.getString("Calification");*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Interpretar preguntas a partir del JSON
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setQuestions(JSONArray jq) {
        try {
            //new code

            Log.d("Quiz/Test",jq.toString());

            for (int j=0;j<attempts;j++) {
                for (int i = 0; i < jq.length(); i++) {
                    //Arreglo con la pregunta
                    JSONArray question_complete = jq.getJSONArray(i);

                    //desglose de la pregunta
                    JSONObject question_head = question_complete.getJSONObject(0);
                    JSONArray question_body = question_complete.getJSONArray(1);

                    //Tipo de pregunta
                    String type = question_head.getString("type");

                    switch (type) {
                        case "quiz.exercise.sn":
                            SimpleNumericQuestion snq = new SimpleNumericQuestion(question_head, question_body);
                            questions.get(j).add(snq);
                            Log.e("MY_DEBUG/RSN", "1");
                            break;
                        case "quiz.exercise.tof":
                            TrueFalseQuestion tf = new TrueFalseQuestion(question_head, question_body);
                            questions.get(j).add(tf);
                            Log.e("MY_DEBUG/VF", "2");
                            break;
                        case "quiz.exercise.ms":
                            MultipleChoiceQuestion mcq = new MultipleChoiceQuestion(question_head, question_body);
                            questions.get(j).add(mcq);
                            Log.e("MY_DEBUG/SM", "3");
                            break;
                        case "quiz.exercise.ss":
                            SimpleChoiceQuestion scq = new SimpleChoiceQuestion(question_head, question_body);
                            questions.get(j).add(scq);
                            Log.e("MY_DEBUG/SS", "4");
                            break;
                        case "quiz.exercise.link":
                            ColumnLinkQuestion clq = new ColumnLinkQuestion(question_head, question_body);
                            questions.get(j).add(clq);
                            Log.e("MY_DEBUG/EC", "5");
                            break;
                    }
                }
            }
            //end new code

            /*for (int i = 0; i < jq.length(); i++) {
                JSONArray qArray = jq.getJSONArray(i);
                JSONObject qData= qArray.getJSONObject(0);
                String type = qData.getString("type");
                switch (type){
                    case "Respuesta simple numerica":
                        SimpleNumericQuestion snq = new SimpleNumericQuestion(qData, qArray.getJSONArray(1));
                        questions.add(snq);
                        Log.e("MY_DEBUG", "1");
                        break;
                    case "Verdadero o falso":
                        TrueFalseQuestion tf = new TrueFalseQuestion(qData, qArray.getJSONArray(1));
                        questions.add(tf);
                        Log.e("MY_DEBUG", "2");
                        break;
                    case "Seleccion multiple":
                        MultipleChoiceQuestion mcq = new MultipleChoiceQuestion(qData, qArray.getJSONArray(1));
                        questions.add(mcq);
                        Log.e("MY_DEBUG", "3");
                        break;
                    case "Seleccion simple":
                        SimpleChoiceQuestion scq = new SimpleChoiceQuestion(qData, qArray.getJSONArray(1));
                        questions.add(scq);
                        Log.e("MY_DEBUG", "4");
                        break;
                    case "Enlazar columnas":
                        ColumnLinkQuestion clq = new ColumnLinkQuestion(qData, qArray.getJSONArray(1));
                        questions.add(clq);
                        Log.e("MY_DEBUG", "5");
                        break;
                }
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setId(JSONObject idTest) {
        try {
            id = idTest.getInt("idTest");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getEvaluationType() {
        return evaluationType;
    }

    public int getId() {
        return id;
    }

    public int getAttempts() {
        return attempts;
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<ArrayList<Question>> getQuestions() {
        return questions;
    }

    public int getStudent_id_server() {
        return student_id_server;
    }
}
