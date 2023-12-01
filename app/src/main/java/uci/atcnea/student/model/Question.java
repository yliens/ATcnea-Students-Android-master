package uci.atcnea.student.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by koyi on 20/05/16.
 */
public abstract class Question {

    protected String feedback;
    protected int id;
    protected String complexity;
    protected String orientation;
    protected InteractiveQuestion.TypeQuestion type;
    protected String title;
    protected int score;

    public Question() {
    }

    public Question(String feedback, String complexity, String orientation, InteractiveQuestion.TypeQuestion type, String title) {
        this.feedback = feedback;
        this.complexity = complexity;
        this.orientation = orientation;
        this.type = type;
        this.title = title;
    }

    public Question(JSONObject data, InteractiveQuestion.TypeQuestion type) {
        try {
            feedback = data.getString("feedback");
            complexity = data.getString("complexity");
            orientation = data.getString("statement");
            this.type = type;
            title = data.getString("tittle");
            score = data.getInt("score");
            id = data.getInt("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public abstract JSONArray getUserResponseJson() throws JSONException;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getComplexity() {
        return complexity;
    }

    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public InteractiveQuestion.TypeQuestion getType() {
        return type;
    }

    public void setType(InteractiveQuestion.TypeQuestion type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
