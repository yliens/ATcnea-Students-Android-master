package uci.atcnea.student.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by koyi on 20/05/16.
 */
public class SimpleChoiceQuestion extends Question {

    private ArrayList<String> items;
    private ArrayList<Integer> response;
    private ArrayList<Boolean> userResponse;
    private ArrayList<String> item_feedback;

    public SimpleChoiceQuestion(JSONObject data, JSONArray array) {
        super(data, InteractiveQuestion.TypeQuestion.SIMPLE_CHOICE);

        //Inicializando listas
        items = new ArrayList<String>();
        response = new ArrayList<Integer>();
        userResponse = new ArrayList<Boolean>();
        item_feedback = new ArrayList<String>();

        try {
            //Cargando valores
            for (int i = 0; i < array.length(); i++) {
                JSONObject resp = array.getJSONObject(i);
                items.add(resp.getString("item"));
                response.add(resp.getInt("calification"));
                item_feedback.add( resp.getString("feedback") );
                userResponse.add(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Boolean> getUserResponse() {
        return userResponse;
    }

    public void setUserResponse(ArrayList<Boolean> userResponse) {
        this.userResponse = userResponse;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public ArrayList<Integer> getResponse() {
        return response;
    }

    public void setResponse(ArrayList<Integer> response) {
        this.response = response;
    }

    @Override
    public JSONArray getUserResponseJson() throws JSONException {
        JSONArray array = new JSONArray();

        for(int i=0;i<userResponse.size();i++){
            //Añadiendo valor de cada elemento en la pregunta
            JSONObject obj = new JSONObject();
            obj.put("response", userResponse.get(i));
            array.put(obj);
        }

        return array;
    }
}
