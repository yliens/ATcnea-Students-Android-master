package uci.atcnea.student.model;

import android.util.Log;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by koyi on 20/05/16.
 */
public class ColumnLinkQuestion extends Question{

    private ArrayList<String> itemsA;
    private ArrayList<String> itemsB;
    private ArrayList<String> response;
    private ArrayList<Integer> userResponse;
    private ArrayList<Integer> itemsBpos;

    public ColumnLinkQuestion(String feedback, String complexity, String orientation, InteractiveQuestion.TypeQuestion type, String title, ArrayList<String> itemsA, ArrayList<String> itemsB, ArrayList<String> response) {
        super(feedback, complexity, orientation, type, title);
        this.itemsA = itemsA;
        this.response = response;
        userResponse = new ArrayList<>();
    }

    public ColumnLinkQuestion(JSONObject data, JSONArray array){
        super(data, InteractiveQuestion.TypeQuestion.COLUMN_LINK);

        //Inicializando listas
        itemsA = new ArrayList<String>();
        itemsB = new ArrayList<String>();
        response = new ArrayList<String>();
        userResponse = new ArrayList<Integer>();
        itemsBpos = new ArrayList<Integer>();

        try {

            //Lista para posiciones iniciales (aplicar en distribucion aleatoria del enlazar)
            ArrayList<Integer> rand_nums = new ArrayList<Integer>();

            //operacion de lectura del JSON
            for (int i = 0; i < array.length(); i++) {
                JSONObject resp = array.getJSONObject(i);
                itemsA.add(resp.getString("item"));
                response.add(resp.getString("response"));
                userResponse.add(-1);

                //agregar valores a la lista de posiciones iniciles
                rand_nums.add(i);
            }

            //Para numeros aleatorios
            Random rand = new Random();

            //Hacer distribucion aleatoria
            for (int i=0,c_items=itemsA.size();i<c_items;i++){

                //numero aleatorio
                int pos = Math.abs( rand.nextInt() ) % rand_nums.size();

                //tomar pos aleatoria
                int rand_pos = rand_nums.get( pos );

                //crear listado B de items
                itemsB.add( response.get( rand_pos ) );

                //position unorder
                itemsBpos.add( rand_pos );

                //eliminar pos
                rand_nums.remove( pos );

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> getUserResponse() {
        return userResponse;
    }

    public void setUserResponse(ArrayList<Integer> userResponse) {
        this.userResponse = userResponse;
    }

    public ArrayList<String> getItemsA() {
        return itemsA;
    }

    public void setItemsA(ArrayList<String> itemsA) {
        this.itemsA = itemsA;
    }

    public ArrayList<String> getItemsB() {
        return itemsB;
    }

    public void setItemsB(ArrayList<String> itemsB) {
        this.itemsB = itemsB;
    }

    @Override
    public JSONArray getUserResponseJson() throws JSONException {
        JSONArray array = new JSONArray();

        for(int i=0;i<userResponse.size();i++){
            //Añadiendo valor de cada elemento en la pregunta
            JSONObject obj = new JSONObject();
            int pos = userResponse.get(i);
            obj.put("response", (pos == -1? -1 : itemsBpos.get(pos) == i) );
            array.put(obj);
        }

        return array;
    }
}
