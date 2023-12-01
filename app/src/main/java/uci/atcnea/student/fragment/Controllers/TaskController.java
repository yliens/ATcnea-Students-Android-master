package uci.atcnea.student.fragment.Controllers;

import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import uci.atcnea.student.MainApp;
import uci.atcnea.student.dao.User;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.fragment.TaskFragment;
import uci.atcnea.student.model.Task;

/**
 * Created by guillermo on 20/01/17.
 */
public class TaskController {
    private static TaskController intance;

    public static TaskController getIntance() {
        if (intance == null) {
            intance = new TaskController();
            GlobalBus.getBus().register(intance);

            intance.newTasks = 0;
        }
        return intance;
    }

    public TaskController() {
    }

    /**
     * Destroit intance of controller
     */
    public static void destroitIntance() {
        if (intance != null)
            GlobalBus.getBus().unregister(intance);
        intance = null;
    }

    private TaskFragment fragment;

    // Conteo de nuevas tareas transferidas (para badge)
    public int newTasks;

    //Eventos de tarea
    @Subscribe
    public void eventNewTask(final Events.EventTask eventNewTask) {

        switch (eventNewTask.taskAction) {
            case NEW_TASK://Crear nueva tarea

                //Guardar tarea nueva en la BD hecho antes
                //eventNewTask.task.saveInDatabase();

                // Actualizar elemento visual en el fragment
                if (fragment != null) {
                    MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragment.taskAdapter.AddTask(eventNewTask.task);
                            fragment.taskAdapter.notifyDataSetChanged();
                        }
                    });
                }

                // Actualizar el badge de la tarea en el menu lateral
                newTasks++;
                GlobalBus.getBus().post(new Events.EventBadge(Events.BadgeType.TASK, newTasks));

                break;
            case UPDATE:
                //Guardar tarea nueva en la BD
                eventNewTask.task.updateInDatabase();

                // Actualizar elemento visual en el fragment
                if (fragment != null) {
                    MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragment.taskAdapter.UpdateListTask();
                            fragment.taskAdapter.notifyDataSetChanged();
                        }
                    });
                }
                break;
            case DELETE:
                eventNewTask.task.UpdateIdFromDatabase();

                // Actualizar vista en caso de borrarse la tarea activa
                if (fragment != null && fragment.selectedTask != null) {
                    if (fragment.selectedTask.getId() == eventNewTask.task.getId()) {
                        // Ocultar la tarea del bloque de datos
                        fragment.HideTask();
                        // Eliminando intancia de la tarea
                        fragment.selectedTask = null;
                    }
                }

                //Guardar tarea nueva en la BD
                eventNewTask.task.deleteInDatabase();

                // Actualizar elemento visual en el fragment
                if (fragment != null) {
                    MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragment.taskAdapter.UpdateListTask();
                            fragment.taskAdapter.notifyDataSetChanged();
                        }
                    });
                }

                break;
            case EVALUATION:
                try {
                    JSONArray jsonList = new JSONArray(eventNewTask.data);

                    for (int i = 0; i < jsonList.length(); i++) {
                        JSONObject obj = jsonList.getJSONObject(i);

                        long id = obj.getInt("id");
                        uci.atcnea.student.dao.Task task = new uci.atcnea.student.dao.Task();
                        task.setTaskIdClass(id);
                        task.setLessonId(MainApp.getCurrentLesson().getIdLesson());
                        task.setUser((User) MainApp.getCurrentUserDao());

                        task = MainApp.getDatabaseManager().refreshTaskFromClassId(task);
                        Task tasknew = new Task(task);
                        tasknew.setNote(obj.getString("evaluation"));
                        tasknew.setTaskStatus(Task.TaskStatus.DELIVERED_WITH_NOTE);
                        tasknew.updateInDatabase();

                    }

                    if (fragment != null) {
                        MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragment.taskAdapter.UpdateListTask();
                                fragment.taskAdapter.notifyDataSetChanged();
                                GlobalBus.getBus().post(new Events.EventTask(Events.TaskAction.RESOURCE_CHANGE));
                            }
                        });
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void setFragment(TaskFragment fragment) {
        this.fragment = fragment;
        fragment.taskAdapter.UpdateListTask();
    }
}
