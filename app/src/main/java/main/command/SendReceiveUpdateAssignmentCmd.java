package main.command;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.LinkedList;

import io.vov.vitamio.utils.Log;
import main.model.Archivo;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.model.Task;

/**
 * Created by guillermo on 8/12/16.
 */
public class SendReceiveUpdateAssignmentCmd implements CommandInterface,Serializable {
    private static final long serialVersionUID = 1288L;

    private String assignmentJson;

    public enum AssignmentType {
        ASSIGNMENT, EVALUATION, UPDATE, NOTIFY, DELETE, FILE
    }

    private AssignmentType assignmentType;

    public String getAssignmentJson() {
        return assignmentJson;
    }

    public void setAssignmentJson(String assignmentJson) {
        this.assignmentJson = assignmentJson;
    }

    public SendReceiveUpdateAssignmentCmd(String assignmentJson, AssignmentType assignmentType) {
        this.assignmentJson = assignmentJson;
        this.assignmentType = assignmentType;
    }

    @Override
    public OperationResult execute(Context applicationContext) {

        switch ( assignmentType ){
            case ASSIGNMENT:
            case UPDATE:
            case DELETE:
                //Log.d("SRUA", "ASSIGNMENT");

                //GlobalBus.getBus().post( new Events.EventTask(assignmentJson, Events.TaskAction.NEW_TASK) );

                // Code para registrar ficheros en listado global que van a transferirse en la tarea
                try {
                    JSONArray jsonList = new JSONArray( assignmentJson );

                    for (int i = 0; i < jsonList.length(); i++) {
                        Task newTask = new Task(jsonList.getString(i));

                        // Chequear que la tarea no exista ya si es nueva
                        if( assignmentType == AssignmentType.ASSIGNMENT && newTask.hasTask() ){
                            Log.d("SRUC","Task repetida");
                            // Revisar que no debe ser asi cuando hay una lista de tareas
                            return new OperationResult(OperationResult.ResultCode.ERROR);
                            //continue;
                        }

                        // Accion a realizar
                        switch (assignmentType){
                            case ASSIGNMENT:
                                //Guardar en BD
                                newTask.saveInDatabase();
                                //Elemento de la BD
                                uci.atcnea.student.dao.Task taskDao = newTask.getTaskDao();
                                //Guardar archivos de la nueva tarea en la lista de espera de copias
                                for (int j = newTask.getResources().size() - 1; j >= 0; j--) {
                                    MainApp.filesFromTask.add(new Archivo(newTask.getResources().get(j)));
                                    //Esto debe cambiarse por id automatico y el id del servidor debe ser guardado en la BD
                                    MainApp.filesIdFromTask.add(Long.parseLong(taskDao.getId() + ""));
                                }

                                sendSmsFiles(newTask.getTaskId()+"");

                                // Lanzar evento con accion
                                GlobalBus.getBus().post( new Events.EventTask(newTask, Events.TaskAction.NEW_TASK) );
                                break;
                            case UPDATE:
                                //Eliminar los archivos de la tarea, para copiar los nuevos
                                MainApp.getDatabaseManager().deleteTaskFiles( newTask.getTaskDao() );
                                //Elemento de la BD
                                taskDao = newTask.getTaskDao();
                                //Guardar archivos de la nueva tarea en la lista de espera de copias
                                for (int j = newTask.getResources().size() - 1; j >= 0; j--) {
                                    MainApp.filesFromTask.add(new Archivo(newTask.getResources().get(j)));
                                    //Esto debe cambiarse por id automatico y el id del servidor debe ser guardado en la BD
                                    MainApp.filesIdFromTask.add(Long.parseLong(taskDao.getId() + ""));
                                }

                                sendSmsFiles(newTask.getTaskId()+"");

                                // Lanzar evento con accion
                                GlobalBus.getBus().post( new Events.EventTask(newTask, Events.TaskAction.UPDATE) );
                                break;
                            case DELETE:
                                //Eliminar los archivos de la tarea, para copiar los nuevos
                                MainApp.getDatabaseManager().deleteTaskFiles( newTask.getTaskDao() );
                                // Lanzar evento con accion
                                GlobalBus.getBus().post( new Events.EventTask(newTask, Events.TaskAction.DELETE) );
                                break;
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return new OperationResult(OperationResult.ResultCode.ERROR);
                }
                break;
            case EVALUATION://Lanzar evento con resultado de la tarea
                Log.d("SRUA", "EVALUATION");
                GlobalBus.getBus().post( new Events.EventTask(assignmentJson, Events.TaskAction.EVALUATION) );
                break;
        }
        return new OperationResult(OperationResult.ResultCode.OK);
    }

    private void sendSmsFiles(String idTask){
        SendMessageService sms = new SendMessageService();
        sms.setCommand(new SendReceiveUpdateAssignmentCmd(idTask, AssignmentType.FILE));
        sms.setWaitForResponse(false);
        sms.execute();
    }
}
