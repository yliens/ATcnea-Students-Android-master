package uci.atcnea.student.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.dao.async.AsyncOperationListener;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import main.model.*;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.activity.MainActivity;

/**
 * @author Octa
 */
public class DatabaseManager implements AsyncOperationListener {

    /**
     * Class tag. Used for debug.
     */
    private static final String TAG = DatabaseManager.class.getCanonicalName();
    /**
     * Instance of DatabaseManager
     */
    private static DatabaseManager instance;
    /**
     * The Android Activity reference for access to DatabaseManager.
     */
    private Context context;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase database;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private AsyncSession asyncSession;
    private List<AsyncOperation> completedOperations;

    /**
     * Constructs a new DatabaseManager with the specified arguments.
     *
     * @param context The Android {@link android.content.Context}.
     */
    public DatabaseManager(final Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(this.context, "d", null);
        completedOperations = new CopyOnWriteArrayList<AsyncOperation>();
    }

    /**
     * @param context The Android {@link android.content.Context}.
     * @return this.instance
     */
    public static DatabaseManager getInstance(Context context) {

        if (instance == null) {
            instance = new DatabaseManager(context);
        }

        return instance;
    }

    @Override
    public void onAsyncOperationCompleted(AsyncOperation operation) {
        completedOperations.add(operation);
    }

    private void assertWaitForCompletion1Sec() {
        asyncSession.waitForCompletion(1000);
        asyncSession.isCompleted();
    }

    /**
     * Query for readable DB
     */
    public void openReadableDb() throws SQLiteException {
        database = mHelper.getReadableDatabase();
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        asyncSession = daoSession.startAsyncSession();
        asyncSession.setListener(this);
    }

    /**
     * Query for writable DB
     */
    public void openWritableDb() throws SQLiteException {
        database = mHelper.getWritableDatabase();
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        asyncSession = daoSession.startAsyncSession();
        asyncSession.setListener(this);
    }

    public void closeDbConnections() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
        if (database != null && database.isOpen()) {
            database.close();
        }
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
        if (instance != null) {
            instance = null;
        }
    }

    public synchronized void dropDatabase() {
        try {
            openWritableDb();
            DaoMaster.dropAllTables(database, true); // drops all tables
            mHelper.onCreate(database);              // creates the tables
            asyncSession.deleteAll(User.class);    // clear all elements from a table
            asyncSession.deleteAll(Lesson.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Insertar usuario nueva a la BD.
     * @param user Usuario a insertarse.
     * @return retorna el usuario insertado.
     */
    public synchronized User insertStudent(User user) {
        try {
            if (user != null) {
                openWritableDb();
                UserDao userDao = daoSession.getUserDao();
                userDao.insert(user);
                //Log.d(TAG, "Inserted user: " + user.getEmail() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
    /**
     * Insertar grupo nuevo a la BD.
     * @param group Grupo a insertarse.
     *
     */
    public synchronized void insertGroup(Group group) {
        try {
            if (group != null) {
                openWritableDb();
                GroupDao groupDao = daoSession.getGroupDao();
                List<Group> list = groupDao.queryBuilder().where(new WhereCondition.StringCondition("ID_LESSON=" + MainApp.getCurrentLesson().getIdLesson()/*+" and macDireccion='" + MainApp.getCurrentLesson().getMacDireccion()+"'"*/)).list();
                boolean esta=false;
                for (Group g:list) {
                    if(g.getName().equals(group.getName())) {
                        esta = true;
                        break;
                    }
                }

                if(!esta)
                    groupDao.insert(group);


                //Log.d(TAG, "Inserted user: " + user.getEmail() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Obtener grupo de la BD.
     * @param group Grupo a insertarse.
     *
     */
    public synchronized Group getGroupByName(String group) {
        try {
            if (group != null) {
                openWritableDb();
                GroupDao groupDao = daoSession.getGroupDao();
                List<Group> list = groupDao.queryBuilder().where(new WhereCondition.StringCondition("ID_LESSON=" + MainApp.getCurrentLesson().getIdLesson()/*+" and macDireccion='" + MainApp.getCurrentLesson().getMacDireccion()+"'"*/)).list();
                boolean esta=false;
                for (Group g:list) {
                    if(g.getName().equals(group)) {
                        return g;
                    }
                }

                //Log.d(TAG, "Inserted user: " + user.getEmail() + " to the schema.");
                daoSession.clear();
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Insertar message nuevo a la BD.
     * @param message Message a insertarse.
     *
     */
    public synchronized void insertMessage(main.model.Message message) {
        try {
            Message msg=new Message();
            msg.setMessage(message.getMessage());

            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a dd/MM/yyyy");
            String time= format.format(new Date());
            msg.setCreatedAt(time);

            msg.setUserName(message.getUsername());

            msg.setGroup(getGroupByName(message.getNameGroup()));

            openWritableDb();
            MessageDao messageDao=daoSession.getMessageDao();
            messageDao.insert(msg);

            daoSession.clear();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Listar messages de la BD.
     * @param groupName
     * @return lista de mensages
     */
    public synchronized List<Message> getMessagesByGroup(String groupName) {
        try {


            openWritableDb();
            Group groupByName = getGroupByName(groupName);
            MessageDao messageDao=daoSession.getMessageDao();
            List<Message> list = messageDao.queryBuilder().where(new WhereCondition.StringCondition("GROUP_ID=" + groupByName.getId())).list();

            daoSession.clear();

            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cheaquear la ocurrencia de la tarea en la BD
     * @param task tarea a buscar
     * @return cantidad de ocurrencias de la tarea
     */
    public synchronized long hasTask(Task task){
        try {
            if (task != null) {
                openWritableDb();
                TaskDao taskDao = daoSession.getTaskDao();
                long count = taskDao.queryBuilder().where(
                        TaskDao.Properties.TaskIdClass.eq( task.getTaskIdClass() ),
                        TaskDao.Properties.UserId.eq( task.getUserId() )
                ).count();
                //Log.d(TAG, "Inserted user: " + user.getEmail() + " to the schema.");
                daoSession.clear();
                return count;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Insertar tarea nueva a la BD.
     * @param task Tarea a insertarse.
     * @return retorna la tarea insertada.
     */
    public synchronized Task insertTask(Task task){
        try {
            if (task != null) {
                openWritableDb();
                TaskDao userDao = daoSession.getTaskDao();
                userDao.insert(task);
                //Log.d(TAG, "Inserted user: " + user.getEmail() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return task;
    }

    /**
     * Insertar recurs nuevo en la BD.
     * @param resource Recurso a insertarse.
     * @return retorna el recurso insertada.
     */
    public synchronized Resource insertResource(Resource resource){
        try {
            if (resource != null) {
                openWritableDb();
                ResourceDao resourceDao = daoSession.getResourceDao();
                resourceDao.insert(resource);
                //Log.d(TAG, "Inserted user: " + user.getEmail() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resource;
    }

    /**
     * Insertar Relacion nueva en la BD
     * @param fileRecord Recurso a insertarse.
     * @return retorna la relacion creada.
     */
    public synchronized FileRecord insertFileRecord(FileRecord fileRecord){
        try {
            if (fileRecord != null) {
                openWritableDb();
                FileRecordDao fileRecordDao = daoSession.getFileRecordDao();
                fileRecordDao.insert(fileRecord);
                //Log.d(TAG, "Inserted user: " + user.getEmail() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileRecord;
    }

    /**
     * Insertar Relacion nueva en la BD
     * @param taskRecord Recurso a insertarse.
     * @return retorna la relacion creada.
     */
    public synchronized TaskRecord insertTaskRecord(TaskRecord taskRecord){
        try {
            if (taskRecord != null) {
                openWritableDb();
                TaskRecordDao taskRecordDao = daoSession.getTaskRecordDao();
                taskRecordDao.insert(taskRecord);
                //Log.d(TAG, "Inserted user: " + user.getEmail() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskRecord;
    }

    public synchronized Nomenclator getNomenclator(String name) {
        try {
            //if (user != null) {
                openWritableDb();
                NomenclatorDao nomenclatorDao = daoSession.getNomenclatorDao();
            Nomenclator unique = nomenclatorDao.queryBuilder().where(new WhereCondition.StringCondition(" name like '" + name + "'")).unique();
            //Log.d(TAG, "Inserted user: " + user.getEmail() + " to the schema.");
                daoSession.clear();
           // }
            return unique;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized ArrayList<User> listStudent() {
        List<User> users = null;
        ArrayList<User> usersFinal = new ArrayList<>();
        try {
            openReadableDb();
            UserDao userDao = daoSession.getUserDao();
            users = userDao.loadAll();

            for (User u:users) {
                if(u.getNomenclator().getName().equals("Student"))
                    usersFinal.add(u);

            }
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (usersFinal != null) {
            return usersFinal;
        }
        return null;
    }

    public synchronized ArrayList<User> filterListStudent(String pattern) {

        if(pattern.equals("")){//When pattern is empty
            return listStudent();
        }

        List<User> users = null;
        ArrayList<User> usersFinal = new ArrayList<>();
        try {
            openReadableDb();
            UserDao userDao = daoSession.getUserDao();
            users = userDao.loadAll();

            for (User u:users) {
                if(u.getNomenclator().getName().equals("Student")) {

                    JSONObject profile = new JSONObject( u.getProfileJson() );

                    if(u.getUsername().toLowerCase().matches(".*"+pattern.toLowerCase()+".*") ||
                            profile.getString("name").toLowerCase().matches(".*"+pattern.toLowerCase()+".*") ||
                            profile.getString("lastname").toLowerCase().matches(".*"+pattern.toLowerCase()+".*") ||
                            profile.getString("email").toLowerCase().matches(".*"+pattern.toLowerCase()+".*")) {//Agrega si cumple
                        usersFinal.add(u);
                    }
                }
            }
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (users != null) {
            return usersFinal;
        }
        return null;
    }

    /**
     * Actualizar estudiante
     * @param user Usuario a ser actualizado
     */
    public synchronized void updateStudent(main.model.User user) {
        try {
            if (user != null) {
                openWritableDb();
                daoSession.update(user);
                //Log.d(TAG, "Updated user: " + user.getEmail() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualizar Tarea
     * @param task Tarea a ser actualizada
     */
    public synchronized void updateTask(Task task) {
        try {
            if (task != null) {
                openWritableDb();
                daoSession.update(task);
                //Log.d(TAG, "Updated user: " + user.getEmail() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Eliminar los recursos asociados a una tarea
     * @param task
     */
    public void deleteTaskFiles(Task task){
        try {
            if (task != null) {
                openWritableDb();
                TaskRecordDao taskRecordDao = daoSession.getTaskRecordDao();
                List<TaskRecord> list = taskRecordDao.queryBuilder().where( TaskRecordDao.Properties.TaskId.eq( task.getId() ) ).list();
                daoSession.clear();
                for (TaskRecord item: list){
                    deleteResourceById(item.getResourceId());
                    deleteTaskRecordById(item.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Eliminar un recurso por el id
     * @param id
     */
    public void deleteResourceById(long id){
        try {
            openWritableDb();
            ResourceDao resourceDao = daoSession.getResourceDao();
            resourceDao.deleteByKey(id);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Eliminar un taskRecord por el id
     * @param id
     */
    public void deleteTaskRecordById(long id){
        try {
            openWritableDb();
            TaskRecordDao taskRecord = daoSession.getTaskRecordDao();
            taskRecord.deleteByKey( id );
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Refresca Task
     * @param task Tarea a ser refrescada
     */
    public synchronized Task refreshTask(Task task) {
        try {
            if (task != null) {
                openWritableDb();
                daoSession.refresh(task);


                //Log.d(TAG, "Updated user: " + user.getEmail() + " from the schema.");
                daoSession.clear();
                return task;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Refresca Task
     * @param task Tarea a ser refrescada
     */
    public synchronized Task refreshTaskFromClassId(Task task) {
        try {
            if (task != null) {
                openWritableDb();
                TaskDao taskDao = daoSession.getTaskDao();
                task = taskDao.queryBuilder().where(
                        TaskDao.Properties.LessonId.eq( task.getLessonId() ),
                        TaskDao.Properties.UserId.eq( task.getUserId() ),
                        TaskDao.Properties.TaskIdClass.eq( task.getTaskIdClass() )
                ).unique();

                //Log.d(TAG, "Updated user: " + user.getEmail() + " from the schema.");
                daoSession.clear();
                return task;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized boolean deleteTask(Task task) {
        try {
            openWritableDb();
            daoSession.delete(task);
            daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public synchronized boolean deleteStudentById(Long userId) {
        try {
            openWritableDb();
            UserDao userDao = daoSession.getUserDao();
            userDao.deleteByKey(userId);
            daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//    public synchronized Student getStudentById(Long userId) {
//        Student user = null;
//        try {
//            openReadableDb();
//            StudentDao userDao = daoSession.getStudentDao();
//            user = userDao.load(userId);
//            daoSession.clear();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return user;
//    }


//    public synchronized void deleteUsers() {
//        try {
//            openWritableDb();
//            StudentDao userDao = daoSession.getStudentDao();
//            userDao.deleteAll();
//            daoSession.clear();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public synchronized Lesson insertLesson(Lesson lesson) {
        try {
            if (lesson != null) {
                openWritableDb();

                LessonDao lessonDao=daoSession.getLessonDao();
                int count = lessonDao.queryBuilder().where(
                        LessonDao.Properties.IdLesson.eq(lesson.getIdLesson()),
                        LessonDao.Properties.MacDireccion.eq(lesson.getMacDireccion())
                ).list().size();
                if(count == 0){
                    lessonDao.insert(lesson);
                }else{
                    lesson = QueryLesson(lesson.getIdLesson(), lesson.getMacDireccion());
                }
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lesson;
    }

    public synchronized Lesson QueryLesson(Long idLesson, String Mac) {
        Lesson lesson = null;
        try {
            openReadableDb();
            LessonDao lessonDao = daoSession.getLessonDao();
            lesson = lessonDao.queryBuilder().where(
                    LessonDao.Properties.IdLesson.eq( idLesson ),
                    LessonDao.Properties.MacDireccion.eq( Mac )
            ).unique();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lesson;
    }

    public synchronized ArrayList<Lesson> listLessons() {
        List<Lesson> users = null;
        try {
            openReadableDb();
            LessonDao userDao = daoSession.getLessonDao();
            users = userDao.loadAll();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (users != null) {
            return new ArrayList<>(users);
        }
        return null;
    }

    /**
     * Listado de tareas
     * @return
     */
    public synchronized ArrayList<Task> listTask() {
        List<Task> tasks = null;
        try {
            openReadableDb();
            TaskDao taskDao = daoSession.getTaskDao();
            Lesson lesson = MainApp.getCurrentLesson().getLessonDao();
            tasks = taskDao.queryBuilder()
                    .where( TaskDao.Properties.UserId.eq( ((User)MainApp.getCurrentUserDao()).getId() ),
                            TaskDao.Properties.LessonId.eq( lesson.getIdLesson() ) )
                    .list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tasks != null) {
            return new ArrayList<>(tasks);
        }
        return null;
    }

    /**
     * Listado de recursos de tarea
     * @return
     */
    public synchronized ArrayList<Resource> listTaskResources(Long taskId, boolean statusResource) {
        List<Resource> resources = null;
        try {
            openReadableDb();

            TaskRecordDao taskRecordDao = daoSession.getTaskRecordDao();
            List<TaskRecord> taskRecordList = taskRecordDao.queryBuilder()
                    .where( TaskRecordDao.Properties.TaskId.eq( taskId ), TaskRecordDao.Properties.ResourceStatus.eq( statusResource ) )
                    .list();

            resources = new ArrayList<>();

            for(TaskRecord it: taskRecordList) {
                ResourceDao resourceDao = daoSession.getResourceDao();

                Resource resource = resourceDao.queryBuilder()
                        .where( ResourceDao.Properties.Id.eq( it.getResourceId() ) )
                        .unique();

                resources.add( resource );

            }
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (resources != null) {
            return new ArrayList<>(resources);
        }
        return null;
    }

    /**
     * Listado de archivos
     * @return
     */
    public synchronized ArrayList<Archivo> listFiles() {
        ArrayList<Archivo> files = null;
        try {

            User currentUser = ((User)MainApp.getCurrentUserDao());

            openReadableDb();
            FileRecordDao fileRecordDao = daoSession.getFileRecordDao();
            List<FileRecord> fileRecord = fileRecordDao.queryBuilder()
                    .where( FileRecordDao.Properties.UserId.eq( currentUser.getId() ))
                    // Para ordenar por la fecha de creado (cuando se haga)
                    // .orderDesc( FileRecordDao.Properties.CreatedAt )
                    .list();

            if(fileRecord != null) {

                // Para voltear la lista de archivos
                Collections.reverse( fileRecord );

                files = new ArrayList<>();

                Lesson lesson = MainApp.getCurrentLesson().getLessonDao();

                for (FileRecord res : fileRecord) {
                    ResourceDao resourceDao = daoSession.getResourceDao();
                    Resource resource = resourceDao.queryBuilder()
                            .where( ResourceDao.Properties.Id.eq( res.getResourceId() ),
                                    ResourceDao.Properties.LessonId.eq( lesson.getId() ) )
                            .unique();

                    if(resource != null) {
                        Archivo tmp = new Archivo(resource);
                        tmp.setSent(resource.getUserId() != currentUser.getId());
                        files.add(tmp);
                    }
                }

                return files;
            }

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void updateLesson(Lesson lesson) {
        try {
            if (lesson != null) {
                openWritableDb();
                daoSession.update(lesson);
                //Log.d(TAG, "Updated user: " + user.getEmail() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean deleteLessonById(Long userId) {
        try {
            openWritableDb();
            LessonDao userDao = daoSession.getLessonDao();
            userDao.deleteByKey(userId);
            daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized Lesson getLessonById(Long userId) {
        Lesson lesson = null;
        try {
            openReadableDb();
            LessonDao lessonDao = daoSession.getLessonDao();
            lesson = lessonDao.load(userId);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lesson;
    }


    public synchronized void deleteLessons() {
        try {
            openWritableDb();
            LessonDao lessonDao = daoSession.getLessonDao();
            lessonDao.deleteAll();
            daoSession.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
