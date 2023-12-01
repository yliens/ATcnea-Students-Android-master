package uci.atcnea.student.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import io.vov.vitamio.utils.Log;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.dao.TaskDao;
import uci.atcnea.student.dao.User;

/**
 * Created by guillermo on 6/12/16.
 */
public class Task {

    private Long id;// Id en la BD del estudiante

    private int taskId;//Id de la tarea

    private String name;//Nombre de la tarea
    private String description;//Descripcion de la tarea
    private LinkedList<String> resources;//Recursos de la tarea
    private long beginDate;//Fecha de inicio de la tarea
    private long endDate;//Fecha de fin de la tarea

    private boolean allowText;//Permitir texto en la respuesta
    private String responseText;//Texto de respuesta

    private boolean allowResources;//Permitir recursos de respuesta
    private int filesLimit;//Cantidad de archivos maxima
    private int sizeLimit;//tamanno maximo de los archivos
    private LinkedList<String> responseResources;//Lista de archivos de respuesta

    private TaskStatus taskStatus;//Estado de la tarea
    private TaskMode taskMode;//Tipo de tarea: individual o en grupo

    private String note;//Nota de la tarea

    public Task(String jsonObject) {

        resources = new LinkedList<>();
        responseResources = new LinkedList<>();

        try {
            JSONObject task = new JSONObject(jsonObject);

            id = null;// valor inicial de la tarea a crearse

            taskId = task.getInt("id");
            name = task.getString("name");
            description = task.getString("description");

            JSONArray jsonResources = task.getJSONArray("resource");
            for (int i=0;i<jsonResources.length();i++){
                resources.add( jsonResources.getString(i) );
                Log.d("resourceName",jsonResources.getString(i));
            }

            beginDate = task.has("begin_date")?task.getLong("begin_date"):Calendar.getInstance().getTimeInMillis();
            endDate = task.has("end_date")?task.getLong("end_date"):Calendar.getInstance().getTimeInMillis();

            allowText = task.has("inline_text")?task.getBoolean("inline_text"):false;
            responseText = "";

            allowResources = task.has("send_files")?task.getBoolean("send_files"):false;
            filesLimit = task.has("files_limit")?task.getInt("files_limit"):10000000;
            sizeLimit = task.has("size_limit")?task.getInt("size_limit"):10000000;

            if(task.getBoolean("delivery_group"))
                taskMode = TaskMode.GROUP;
            else
                taskMode = TaskMode.SINGLE;

            taskStatus = TaskStatus.UNSOLVED;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Task(uci.atcnea.student.dao.Task task) {
        id = task.getId();
        taskId = Integer.parseInt(task.getTaskIdClass()+"");
        name = task.getName();
        description = task.getDescription();
        beginDate = task.getBeginDate();
        endDate = task.getEndDate();
        allowText = task.getAllowText();
        responseText = task.getResponseText();
        allowResources = task.getAllowResources();
        filesLimit = task.getFilesLimit();
        sizeLimit = task.getSizeLimit();
        note = task.getNote();

        resources = new LinkedList<>();
        responseResources = new LinkedList<>();

        if(task.getTaskStatus() == TaskStatus.DELIVERED_WITH_NOTE.ordinal()){
            taskStatus = TaskStatus.DELIVERED_WITH_NOTE;
        }else if(task.getTaskStatus() == TaskStatus.DELIVERED_WITHOUT_NOTE.ordinal()){
            taskStatus = TaskStatus.DELIVERED_WITHOUT_NOTE;
        }else if(task.getTaskStatus() == TaskStatus.UNSOLVED.ordinal()){
            taskStatus = TaskStatus.UNSOLVED;
        }

        if(task.getTaskMode() == TaskMode.GROUP.ordinal()){
            taskMode = TaskMode.GROUP;
        }else if(task.getTaskMode() == TaskMode.SINGLE.ordinal()){
            taskMode = TaskMode.SINGLE;
        }
    }

    /**
     * Guardar task en la BD
     */
    public void saveInDatabase(){
        //Creando tarea par guardar en la BD
        uci.atcnea.student.dao.Task task = getTaskDao();

        //Guardar en la BD
        uci.atcnea.student.dao.Task tmpTask = MainApp.getDatabaseManager().insertTask( task );
        this.id = tmpTask.getId();
    }

    public boolean hasTask(){
        return MainApp.getDatabaseManager().hasTask( getTaskDao() ) > 0;
    }

    /**
     * Actualizar task en la BD
     */
    public void updateInDatabase(){
        UpdateIdFromDatabase();

        //Creando tarea par guardar en la BD (optener id)
        uci.atcnea.student.dao.Task task = getTaskDao();

        //Guardar en la BD
        MainApp.getDatabaseManager().updateTask( task );
    }

    /**
     * Eliminar task en la BD
     */
    public void deleteInDatabase(){
        UpdateIdFromDatabase();

        //Creando tarea par eliminar en la BD
        uci.atcnea.student.dao.Task task = getTaskDao();

        //Eliminar en la BD
        MainApp.getDatabaseManager().deleteTask( task );
    }

    public void UpdateIdFromDatabase(){
        if(id != null)return;

        //Creando tarea par guardar en la BD (optener id)
        uci.atcnea.student.dao.Task task = getTaskDao();

        //Creando tarea par guardar en la BD
        id = task.getId();
    }

    public uci.atcnea.student.dao.Task getTaskDao(){
        uci.atcnea.student.dao.Task task = new uci.atcnea.student.dao.Task(
                id,
                Long.parseLong(taskId+""),
                name,
                description,
                beginDate,
                endDate,
                allowText,
                responseText,
                allowResources,
                filesLimit,
                sizeLimit,
                note,
                taskStatus.ordinal(),
                taskMode.ordinal(),
                0,
                MainApp.getCurrentLesson().getIdLesson());

        //Pasando el usuario a la tarea
        task.setUser((User) MainApp.getCurrentUserDao());

        if(id == null){
            uci.atcnea.student.dao.Task taskTmp = MainApp.getDatabaseManager().refreshTaskFromClassId(task);
            if(taskTmp != null)
                task = taskTmp;
        }

        return task;
    }

    /**
     * Retorna un objeto con el resultado e la tarea, listo para ser enviado a la clase
     * @return Objeto creado a partir de la respuesta de la tarea.
     */
    public JSONObject generateResponse(){
        JSONObject response = new JSONObject();

        try {
            response.put("id",taskId);
            response.put("name",name);
            response.put("text",responseText);
            JSONArray listOfResources = new JSONArray();
            for (String item : responseResources){
                listOfResources.put(item);
            }
            response.put("resources",listOfResources);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LinkedList<String> getResources() {
        return resources;
    }

    public long getBeginDate() {
        return beginDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public boolean isAllowText() {
        return allowText;
    }

    public boolean isAllowResources() {
        return allowResources;
    }

    public int getFilesLimit() {
        return filesLimit;
    }

    public int getSizeLimit() {
        return sizeLimit;
    }

    public String getResponseText() {
        return responseText;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskMode getTaskMode() {
        return taskMode;
    }

    public long getId() {
        return id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void addResponseResources(String responseResources) {
        this.responseResources.add( responseResources );
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    //Datos enum
    public static enum TaskStatus{
        DELIVERED_WITH_NOTE,DELIVERED_WITHOUT_NOTE,UNSOLVED
    }
    public enum TaskMode{
        GROUP,SINGLE
    }
}
