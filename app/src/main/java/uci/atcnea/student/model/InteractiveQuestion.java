package uci.atcnea.student.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by koyi on 16/05/16.
 */
public class InteractiveQuestion {

    public enum TypeQuestion {
        MULTIPLE_CHOICE,
        SIMPLE_CHOICE,
        TRUE_FALSE,
        VERBAL_QUESTION,
        SIMPLE_NUMERIC,
        COLUMN_LINK
    }

    public enum ModeQuestion {
        DIRECT_QUESTION,
        FIRST_TO_ANSWER,
        SHOW_ON_SCREEN,
    }

    private int timeRead;
    private int timeAnsw;
    private TypeQuestion type;
    private ModeQuestion mode;
    private String description;
    private int student_server_id;
    private ArrayList<String> items;

    public InteractiveQuestion(JSONObject data) {
        try {
            timeRead = data.getInt("time.read");
            timeAnsw = data.getInt("time.answ");
//            typology":"int.ques.multiple.choice"
            String typology = data.getString("typology");
            initType(typology);
            String answedMode = data.getString("answer.mode");
            initMode(answedMode);
            description = data.getString("description");
        } catch (JSONException e) {
            Log.e("MY_DEBUG", "Error Parsing JSON data");
        }
    }

    private void initType(String typology){
        switch (typology){
            case "int.ques.multiple.choice":
                type = TypeQuestion.MULTIPLE_CHOICE;
                break;
            case "int.ques.simple.choice":
                type = TypeQuestion.SIMPLE_CHOICE;
                break;
            case "int.ques.open.question.verbal":
                type = TypeQuestion.VERBAL_QUESTION;
                break;
            case "int.ques.true.or.false":
                type = TypeQuestion.TRUE_FALSE;
                addTrueFalseItems();
                break;
        }
    }

    private void addTrueFalseItems(){
        items = new ArrayList<>();
        items.add("True");
        items.add("False");
    }

    private void initMode(String answerMode){
        switch (answerMode){
            case "Pregunta dirigida":
                mode = ModeQuestion.DIRECT_QUESTION;
                break;
            case "Primero en responder":
                mode = ModeQuestion.FIRST_TO_ANSWER;
                break;
            case "Desmostracion en pantalla":
                mode = ModeQuestion.SHOW_ON_SCREEN;
                break;
        }
    }

    public boolean hasItems(){

        return (!type.equals(TypeQuestion.VERBAL_QUESTION) && !type.equals(TypeQuestion.TRUE_FALSE));
    }

    public void setItems(JSONArray array){
        items = new ArrayList<>();
        for (int i = 0; i < array.length(); i++){
            try {
                JSONObject item = array.getJSONObject(i);
                String name = item.getString("name");
                items.add(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public int getStudent_server_id() {
        return student_server_id;
    }

    public void setStudent_server_id(int student_server_id) {
        this.student_server_id = student_server_id;
    }

    public int getTotalTime(){
        return timeAnsw + timeRead;
    }

    public int getTimeRead() {
        return timeRead;
    }

    public void setTimeRead(int timeRead) {
        this.timeRead = timeRead;
    }

    public int getTimeAnsw() {
        return timeAnsw;
    }

    public void setTimeAnsw(int timeAnsw) {
        this.timeAnsw = timeAnsw;
    }

    public TypeQuestion getType() {
        return type;
    }

    public void setType(TypeQuestion type) {
        this.type = type;
    }

    public ModeQuestion getMode() {
        return mode;
    }

    public void setMode(ModeQuestion mode) {
        this.mode = mode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }
}
