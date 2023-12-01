package uci.atcnea.student.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by koyi on 20/05/16.
 */
public class TrueFalseQuestion extends Question{

    private Boolean response;
    private String userResponse;
    private ArrayList<String> items;
    private String feedback_true;
    private String feedback_false;

    public TrueFalseQuestion(String feedback, String complexity, String orientation, InteractiveQuestion.TypeQuestion type, String title, Boolean response) {
        super(feedback, complexity, orientation, type, title);
        this.response = response;
        items = new ArrayList<>();
        items.add("True");
        items.add("False");
        userResponse = "null";
    }

    public TrueFalseQuestion(JSONObject data, JSONArray array){
        super(data, InteractiveQuestion.TypeQuestion.TRUE_FALSE);
        try {
            JSONObject resp = array.getJSONObject(0);
            response = resp.getBoolean("response");
            feedback_true = resp.getString("feedbackTrue");
            feedback_false = resp.getString("feedbackFalse");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        items = new ArrayList<>();
        items.add("True");
        items.add("False");
        userResponse = "null";
    }

    public String getUserResponse() {
        return userResponse;
    }

    public void setUserResponse(String userResponse) {
        this.userResponse = userResponse;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }

    public String getFeedback_true() {
        return feedback_true;
    }

    public void setFeedback_true(String feedback_true) {
        this.feedback_true = feedback_true;
    }

    public String getFeedback_false() {
        return feedback_false;
    }

    public void setFeedback_false(String feedback_false) {
        this.feedback_false = feedback_false;
    }

    @Override
    public JSONArray getUserResponseJson() throws JSONException {
        JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();

        obj.put("response", userResponse);
        array.put(obj);

        return array;
    }
}
