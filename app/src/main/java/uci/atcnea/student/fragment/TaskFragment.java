package uci.atcnea.student.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import main.command.SendReceiveUpdateAssignmentCmd;
import main.model.Archivo;
import uci.atcnea.core.interfaces.SyncTaskResultListenerInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.dao.Resource;
import uci.atcnea.student.dao.TaskRecord;
import uci.atcnea.student.dao.User;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.fragment.Controllers.FileController;
import uci.atcnea.student.model.Task;
import uci.atcnea.student.fragment.Controllers.TaskController;
import uci.atcnea.student.utils.ATcneaUtil;
import uci.atcnea.student.utils.GetFileRealPath;
import uci.atcnea.student.utils.TaskHelper;

/**
 * Created by guillermo on 6/12/16.
 */
public class TaskFragment extends Fragment{

    //For openable file
    private static final int FILE_SELECT_CODE = 1;
    private static final int RESULT_OK = -1;

    //Listado de tareas
    private RecyclerView listOfTask;

    //Titulo del listado de tareas/contenedor de datos
    private LinearLayout taskDetails;

    //Componentes vusuales para datos de la tarea
    private TextView taskMenuTitle;
    private TextView taskName;
    private WebView taskDescription;
    private TextView taskStatusSend;
    private TextView taskStatusEvaluation;
    private TextView taskType;
    private GridView taskAdjunctList;
    private GridView taskAdjunctListStudent;
    private ImageButton taskAddAdjunct;
    private ImageButton taskSend;
    private TextView taskTextAddAdjunct;
    private TextView taskTextResponse;
    private TextView taskAdjunctListTitle;
    private ScrollView taskResponseTextScroll;

    public Task selectedTask;

    //Respuestas del usuario
    private EditText taskResponseText;

    public TaskAdapter taskAdapter;

    //Adapters para los archivos adjuntos
    private AdjunctAdapter adjunctAdapterTeacher;
    private AdjunctAdapter adjunctAdapterStudent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task,null);

        //Captura de los objetos visuales
        listOfTask = (RecyclerView) view.findViewById(R.id.task_list);
        taskMenuTitle = (TextView) view.findViewById(R.id.task_menu_title);
        taskName = (TextView) view.findViewById(R.id.task_name);
        taskDescription = (WebView) view.findViewById(R.id.task_description);
        taskStatusSend = (TextView) view.findViewById(R.id.task_status_send);
        taskStatusEvaluation = (TextView) view.findViewById(R.id.task_status_evaluation);
        taskType = (TextView) view.findViewById(R.id.task_type);
        taskAdjunctList = (GridView) view.findViewById(R.id.task_adjunct_list);
        taskAdjunctListStudent = (GridView) view.findViewById(R.id.task_adjunct_list_student);
        taskAddAdjunct = (ImageButton) view.findViewById(R.id.task_add_adjunct);
        taskSend = (ImageButton) view.findViewById(R.id.task_send);
        taskResponseText = (EditText) view.findViewById(R.id.task_response_text);
        taskTextAddAdjunct = (TextView) view.findViewById(R.id.task_text_add_adjunct);
        taskTextResponse = (TextView) view.findViewById(R.id.task_text_response);
        taskAdjunctListTitle = (TextView) view.findViewById(R.id.task_adjunct_list_title);

        taskResponseTextScroll = (ScrollView) view.findViewById(R.id.task_response_text_scroll);

        //Captura del contenedor de datos del examen
        taskDetails = (LinearLayout) view.findViewById(R.id.task_details);

        //Para el listado de tareas (creando el Adapter)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainApp.getCurrentActivity());
        taskAdapter = new TaskAdapter();
        listOfTask.setAdapter( taskAdapter );
        listOfTask.setLayoutManager(linearLayoutManager);
        listOfTask.setHasFixedSize( true );

        // Para adjuntos del profesor
        adjunctAdapterTeacher = new AdjunctAdapter(new ArrayList<Resource>());
        // BORRAR(adjunctAdapterTeacher);
        taskAdjunctList.setAdapter(adjunctAdapterTeacher);

        // Para adjuntos del estudiante
        adjunctAdapterStudent = new AdjunctAdapter(new ArrayList<Resource>());
        //BORRAR(adjunctAdapterStudent);
        taskAdjunctListStudent.setAdapter(adjunctAdapterStudent);

        // Evento de agregar adjunto del estudiante
        taskAddAdjunct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Numero de adjuntos actuales
                int numberOfAdjunct = ((AdjunctAdapter) taskAdjunctListStudent.getAdapter()).getResourcesList().size();
                // Mostrar cuadro de busqueda de archivos
                if(selectedTask.getFilesLimit() > numberOfAdjunct) {
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    try {
                        startActivityForResult(
                                Intent.createChooser(intent, "Select a File to Send"),
                                FILE_SELECT_CODE);
                    } catch (android.content.ActivityNotFoundException ex) {
                        // Potentially direct the user to the Market with a Dialog
                        Toast.makeText(getContext(), "Please install a File Manager.",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{// Error al sobrepasar el limite de archivos
                    String title = MainApp.getCurrentActivity().getResources().getString(R.string.dialogFileTitle);
                    String msg = MainApp.getCurrentActivity().getResources().getString(R.string.dialog_adjunct_task_error_limit);
                    msg += " " + selectedTask.getFilesLimit();
                    msg += " " + MainApp.getCurrentActivity().getResources().getString(R.string.dialog_adjunct_task_error_limit2);
                    ATcneaUtil.ShowAllowDenyHandDialog(title, msg, false);
                }
            }
        });

        //Evento de enviar la tarea
        taskSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validaciones BEGIN

                if(selectedTask.isAllowText() &&
                        (taskResponseText.getText() + "").replace(" ","").equals("")){
                    String title = MainApp.getIntance().getResources().getString(R.string.dialogFileTitle);
                    String text = MainApp.getIntance().getResources().getString(R.string.task_responce_text_error);
                    ATcneaUtil.showInformationDialog(title, text, false);
                    return;
                }

                if(selectedTask.isAllowResources() && adjunctAdapterStudent.getCount() == 0){
                    String title = MainApp.getIntance().getResources().getString(R.string.dialogFileTitle);
                    String text = MainApp.getIntance().getResources().getString(R.string.task_responce_file_empty_error);
                    ATcneaUtil.showInformationDialog(title, text, false);
                    return;
                }

                // Validaciones END

                //Agreagar datos del estudiante a la tarea
                selectedTask.setResponseText( taskResponseText.getText().toString() );//text responce
                for( int i=adjunctAdapterStudent.getResourcesList().size()-1;i>=0;i--) {//files responces
                    selectedTask.addResponseResources( adjunctAdapterStudent.getResourcesList().get(i).getName() );
                }

                String jsonTask = selectedTask.generateResponse().toString();

                //Eviar comando de la tarea
                SendMessageService service = new SendMessageService(MainApp.getCurrentServer());
                SendReceiveUpdateAssignmentCmd qc = new SendReceiveUpdateAssignmentCmd(
                        jsonTask,
                        SendReceiveUpdateAssignmentCmd.AssignmentType.NOTIFY
                );

                service.setCommand(qc);
                service.setWaitForResponse(true);

                // Si se recibe confirmacion, enviar los archivos adjuntos de la tarea
                service.setCallback(new SyncTaskResultListenerInterface() {
                    @Override
                    public void onSyncTaskEventCompleted(OperationResult result, String ip) {
                        if(result == null)return;
                        if(result.getCode() == OperationResult.ResultCode.OK){
                            //Enviar archivos de la tarea subidos por el estudiante
                            for( int i=adjunctAdapterStudent.getResourcesList().size()-1;i>=0;i--) {
                                GlobalBus.getBus().post( new Events.EventFile(
                                        new Archivo( adjunctAdapterStudent.getResourcesList().get(i) ),
                                        Events.FileAction.SEND_FILE_FROM_TASK
                                ));
                            }
                            //Cambiar estado de tarea
                            selectedTask.setTaskStatus(Task.TaskStatus.DELIVERED_WITHOUT_NOTE);
                            selectedTask.updateInDatabase();

                            GlobalBus.getBus().post( new Events.EventTask(Events.TaskAction.RESOURCE_CHANGE) );
                        }
                    }
                });

                TaskHelper.execute(service);

            }
        });

        // Registrando vista en el controlador de tarea
        TaskController.getIntance().setFragment(this);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

         switch (requestCode) {
            case FILE_SELECT_CODE:
                try {
                    // Get the path
                    String path = GetFileRealPath.getPath(getContext(), data.getData());

                    Log.e("MY_DEBUG", "File Path: " + path);

                    final File file = new File(path);

                    if(file.length()/1024 > selectedTask.getSizeLimit()){
                        String text = MainApp.getIntance().getResources().getString(R.string.task_open_file_size_error);
                        text += " " + (selectedTask.getSizeLimit() / 1024);
                        text += MainApp.getIntance().getResources().getString(R.string.task_open_file_size_error_2);
                        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
                        break;
                    }

                    //Agreagar a la BD
                    Resource newResource = new Resource(
                            null,
                            file.getName(),
                            file.length(),//Arreglar
                            file.getPath(),
                            ((User) MainApp.getCurrentUserDao()).getId(),
                            MainApp.getCurrentLesson().getId()
                    );

                    newResource = MainApp.getDatabaseManager().insertResource(newResource);

                    MainApp.getDatabaseManager().insertTaskRecord(new TaskRecord(
                            null,
                            false,
                            selectedTask.getId(),
                            newResource.getId()
                    ));

                    //Agregar al componente visual
                    ((AdjunctAdapter) taskAdjunctListStudent.getAdapter()).getResourcesList().add(newResource);

                    //Actualizar componente visual
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((AdjunctAdapter) taskAdjunctListStudent.getAdapter()).notifyDataSetChanged();
                        }
                    });

                }catch (Exception ex){
                    Toast.makeText(getActivity(), R.string.task_open_file_error, Toast.LENGTH_LONG);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void HideTask(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Ocultar tarea seleccionada
                taskDetails.setVisibility( View.INVISIBLE );
            }
        });
    }

    public void LoadTask(final int taskId){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Mostrar tarea seleccionada
                taskDetails.setVisibility( View.VISIBLE );

                Task item = null;//Tarea seleccionada

                for(Task it: taskAdapter.getListTask()){
                    if(it.getId() == taskId){
                        item = it;
                        break;
                    }
                }

                if(item==null){//Terminar en caso de no encontrarse la tarea
                    taskDetails.setVisibility( View.INVISIBLE );
                    return;
                }

                selectedTask = item;

                //Cargar recursos adjuntos
                LoadTaskResource();

                //taskMenuTitle.setText( "Por verse que va aqui" );
                taskName.setText( item.getName() );

                taskDescription.loadData(item.getDescription(), "text/html", "utf-8");
                taskDescription.setBackgroundColor( Color.TRANSPARENT );
                //taskDescription.setText( item.getDescription() );

                // Acciones a realizar antes de mostrar elementos visuales
                taskSend.setVisibility( View.GONE );

                // Mostrar o ocultar archivos adjuntos en caso de estar activo o no
                if(!selectedTask.isAllowResources()) {
                    taskAddAdjunct.setVisibility(View.GONE);
                    taskTextAddAdjunct.setVisibility(View.GONE);
                }else{
                    taskAddAdjunct.setVisibility(View.VISIBLE);
                    taskTextAddAdjunct.setVisibility(View.VISIBLE);
                }

                // Texto guardado
                taskResponseText.setText( selectedTask.getResponseText() );

                // Desactivar por defecto la edicion
                taskResponseText.setEnabled( false );
                taskAddAdjunct.setEnabled( false );

                // Mostrar o ocultar para escribir texto en caso de estar activo o no
                if(!selectedTask.isAllowText()) {
                    taskTextResponse.setVisibility(View.GONE);
                    taskResponseText.setVisibility(View.GONE);
                    taskResponseTextScroll.setVisibility(View.GONE);
                }else{
                    taskResponseTextScroll.setVisibility(View.VISIBLE);
                    taskTextResponse.setVisibility(View.VISIBLE);
                    taskResponseText.setVisibility(View.VISIBLE);
                }

                switch (item.getTaskStatus()) {
                    case UNSOLVED:
                        taskStatusSend.setText( R.string.task_status_send_no_given );
                        // Permitir edicion de texto
                        if(selectedTask.isAllowText()) {
                            taskResponseText.setEnabled(true);
                        }
                        // Mostrar boton para subir archivos
                        taskAddAdjunct.setEnabled( true );
                        // Mostrar boton de enviar
                        taskSend.setVisibility( View.VISIBLE );
                        break;
                    case DELIVERED_WITH_NOTE:
                    case DELIVERED_WITHOUT_NOTE:
                        taskStatusSend.setText( R.string.task_status_send_given  );
                        break;
                }

                switch (item.getTaskStatus()){
                    case DELIVERED_WITH_NOTE:
                        taskStatusEvaluation.setText( selectedTask.getNote() );
                        break;
                    case UNSOLVED:
                    case DELIVERED_WITHOUT_NOTE:
                        taskStatusEvaluation.setText( R.string.task_status_evaluation_no_value );
                        break;
                }

                switch (item.getTaskMode()) {
                    case GROUP:
                        taskType.setText( R.string.task_type_group );
                        break;
                    case SINGLE:
                        taskType.setText( R.string.task_type_no_group );
                        break;
                }

                // Limpiar campos de entrega
                // taskResponseText.setText("");//text responce
            }
        });
    }

    /**
     * Cargar recursos de la tarea actual
     */
    private void LoadTaskResource(){
        if(selectedTask != null) {
            // Cargando recursos de la tarea
            List<Resource> resourcesList = MainApp.getDatabaseManager().listTaskResources(
                    Long.parseLong(selectedTask.getId() + ""),
                    true
            );
            adjunctAdapterTeacher.resourcesList = resourcesList;
            // Ocultar titulo cuando no existan archivos del profesor
            if(resourcesList.isEmpty()){
                taskAdjunctListTitle.setVisibility(View.GONE);
            }else{
                taskAdjunctListTitle.setVisibility(View.VISIBLE);
            }

            // Cargando recursos subidos por el estudiante
            resourcesList = MainApp.getDatabaseManager().listTaskResources(
                    Long.parseLong(selectedTask.getId() + ""),
                    false
            );
            adjunctAdapterStudent.resourcesList = resourcesList;

            // Actualizando componentes
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adjunctAdapterTeacher.notifyDataSetChanged();
                    adjunctAdapterStudent.notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * Adaptador para el listado de tareas
     */
    public class TaskAdapter extends RecyclerView.Adapter<TaskItem>{

        private LinkedList<Task> listTask;

        public TaskAdapter() {
            listTask = new LinkedList<>();

            UpdateListTask();
        }

        public void UpdateListTask(){
            listTask = new LinkedList<>();

            ArrayList<uci.atcnea.student.dao.Task> list = MainApp.getDatabaseManager().listTask();

            if(list != null) {
                //Cargando tareas de la BD
                for (uci.atcnea.student.dao.Task it : list) {
                    listTask.add(new Task(it));
                }
            }

            notifyDataSetChanged();
        }

        @Override
        public TaskItem onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(),R.layout.task_item,null);
            return new TaskItem(view);
        }

        @Override
        public void onBindViewHolder(TaskItem holder, int position) {
            Task task = listTask.get(position);
            holder.setTaskName( task.getName() );
            holder.setEnded( task.getTaskStatus() == Task.TaskStatus.DELIVERED_WITH_NOTE );
            holder.setTaskId( Integer.parseInt(task.getId()+"") );
        }

        @Override
        public int getItemCount() {
            return (listTask == null?0:listTask.size());
        }

        public void AddListTask(LinkedList<Task> listTask) {
            this.listTask.addAll( listTask );
        }

        public void AddTask(Task task){
            this.listTask.add( task );
        }

        public LinkedList<Task> getListTask() {
            return listTask;
        }
    }

    /**
     * Items para cada elemento en el adaptador de las tareas
     */
    private class TaskItem extends RecyclerView.ViewHolder{

        private CheckBox taskName;
        private int taskId;
        public TaskItem(View itemView) {
            super(itemView);

            //Capturando elementos visuales
            taskName = (CheckBox) itemView.findViewById(R.id.task_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadTask( taskId );
                }
            });
        }

        public void setTaskName(String taskName) {
            this.taskName.setText(taskName);
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }

        public void setEnded(boolean ended) {
            taskName.setChecked( ended );
        }
    }

    /**
     * Adaptador para la tabla de archivos adjuntos
     */
    public class AdjunctAdapter extends BaseAdapter{

        public List<Resource> resourcesList;

        public TaskFragment fragment;

        public AdjunctAdapter(List<Resource> resourcesList) {
            this.resourcesList = resourcesList;
        }

        @Override
        public int getCount() {
            return resourcesList==null?0:resourcesList.size();
        }

        @Override
        public Object getItem(int position) {
            return resourcesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return resourcesList.get(position).getId();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = View.inflate(parent.getContext(),R.layout.task_adjunct_item,null);

            //Capturar elementos
            TextView adjuntName = (TextView) view.findViewById(R.id.adjunt_name);
            LinearLayout adjunctContainer = (LinearLayout) view.findViewById(R.id.adjunct_container);

            //Nombre del adjunto
            adjuntName.setText( resourcesList.get(position).getName() );
            adjuntName.setPaintFlags( adjuntName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG );

            //Evento onclick del adjunto
            adjunctContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*String resourceName = resourcesList.get(position).getName();
                    GlobalBus.getBus().post(new Events.EventFile(new Archivo(resourceName), Events.FileAction.EXECUTE_FILE));*/
                    Resource file = resourcesList.get(position);
                    GlobalBus.getBus().post(new Events.EventFile(
                            new Archivo(file),
                            Events.FileAction.EXECUTE_FILE)
                    );
                    /*Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.parse(file.getUri());
                    intent.setDataAndType(uri, FileController.getMimeType(file.getUri()));
                    MainApp.getCurrentActivity().startActivity(Intent.createChooser(intent, file.getName()));*/
                }
            });

            return view;
        }

        public List<Resource> getResourcesList() {
            return resourcesList;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        GlobalBus.getBus().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe
    public void eventNewTask(final Events.EventTask eventNewTask){

        switch ( eventNewTask.taskAction ){
            case RESOURCE_CHANGE:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (selectedTask != null) {
                            LoadTask(Integer.parseInt(selectedTask.getId()+""));
                        }
                    }
                });
                break;
        }
    }
}
