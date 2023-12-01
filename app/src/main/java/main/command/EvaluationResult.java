package main.command;

/**
 * Created by adrian on 7/04/16.
 */
import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

import main.model.User;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;


public class EvaluationResult implements CommandInterface, Serializable{

    private static final long serialVersionUID = 12345 ;

    private User studentCommand;
    private int eval ;

    /**
     *
     * @param eval
     */
    public EvaluationResult(int eval) {
        this.eval = eval;
        studentCommand = MainApp.getCurrentUser();
    }

    public EvaluationResult() {
    }

    /**
     *
     * @return
     */
    public User getStudentCommand() {
        return studentCommand;
    }

    /**
     *
     * @return
     */
    public int getEval() {
        return eval ;
    }

    /**
     *
     * @param eval
     */
    public void setEval(int eval) {
        this.eval = eval;
    }

    /**
     *
     * @param applicationContext
     * @return
     */
    @Override
    public OperationResult execute(final Context applicationContext) {

        Log.i("EvaluationResult::","Evaluación"+eval);

        OperationResult o=new OperationResult();
        ArrayList<Object> arrayList= new ArrayList<>();
        arrayList.add("Hola Mundo");
        o.setData(arrayList);
        return o;
    }

}
