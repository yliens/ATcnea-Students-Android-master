package main.command;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.model.Summary;

/**
 *
 * @class SendSummary
 * @version 1.0
 * @date 12/10/16
 * @author Guillermo Gonzalez Jimenez
 *
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf Recibir sumario de la clase.
 *
 */
public class SendSummary implements CommandInterface,Serializable {

    private static final long serialVersionUID = 9319;

    private String summaryJson;


    public SendSummary() {
    }

    @Override
    public OperationResult execute(Context applicationContext) {
        //datos recibidos en json del sumario actual de la clase
        MainApp.SumaryList = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(summaryJson);

            Log.d("JSONSummary",summaryJson);

            JSONArray subjects = reader.optJSONArray("subjects");
            JSONArray states = reader.optJSONArray("states");

            //Crear listado del sumario nuevo para ser mostrado
            for (int i=0;i<subjects.length();i++) {
                MainApp.SumaryList.add( new Summary(subjects.getString(i),states.getBoolean(i)) );
            }

            //para actualizar el RecyclerView del sumario (llamar evento)
            GlobalBus.getBus().post( new Events.EventSummary( Events.SummaryType.UPDATE_SUMMARY ) );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new OperationResult(OperationResult.ResultCode.OK);
    }
}