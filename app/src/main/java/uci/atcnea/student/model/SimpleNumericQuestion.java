package uci.atcnea.student.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by koyi on 20/05/16.
 */
public class SimpleNumericQuestion extends Question {

    private ArrayList<Integer> response;
    private int userResponse;

    private ArrayList<String> feedback_responce;
    private ArrayList<Integer> calification;


    public SimpleNumericQuestion(String feedback, String complexity, String orientation, InteractiveQuestion.TypeQuestion type, String title, ArrayList<Integer>  response) {
        super(feedback, complexity, orientation, type, title);
        this.response = response;
    }

    public SimpleNumericQuestion(JSONObject data, JSONArray array) {
        super(data, InteractiveQuestion.TypeQuestion.SIMPLE_NUMERIC);
        try {
            JSONObject resp = array.getJSONObject(0);

            response = new ArrayList<Integer>();
            feedback_responce = new ArrayList<String>();
            calification = new ArrayList<Integer>();

            for(int i=0;i<array.length();i++) {
                JSONObject tmp_obj = array.getJSONObject(i);
                response.add( tmp_obj.getInt("item") );
                feedback_responce.add( resp.getString("feedback") );
                calification.add( resp.getInt("calification") );
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getUserResponse() {
        return userResponse;
    }

    public void setUserResponse(int userResponse) {
        this.userResponse = userResponse;
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
