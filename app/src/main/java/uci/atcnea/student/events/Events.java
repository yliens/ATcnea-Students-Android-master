package uci.atcnea.student.events;

import java.util.List;

import main.blibrary.BGraphic;
import main.model.Archivo;
import main.model.GroupSend;
import main.model.Message;
import uci.atcnea.student.model.Task;

/**
 * Created by guillermo on 22/11/16.
 */
public class Events {

    /**
     * Evento para mostrar fragment de user
     */
    public static class EventChangeFragment{
        public EventChangeFragment() {
        }
    }

    /**
     * Evento para cambios de datos del profesor
     */
    public static class EventTeacherChange{
        public EventTeacherChange() {
        }
    }

    /**
     * Evento para los badges del menu lateral
     */
    public static class EventBadge{
        public BadgeType badgeType;
        public int count;

        public EventBadge(BadgeType badgeType) {
            this.badgeType = badgeType;
            count = 0;
        }

        public EventBadge(BadgeType badgeType, int count) {
            this.badgeType = badgeType;
            this.count = count;
        }
    }

    /**
     * Evento para los archivos
     */
    public static class EventFile{
        public Archivo file;
        public FileAction fileAction;
        public double percent;
        public Long userId;
        public Long taskId;

        public EventFile(Archivo file, FileAction fileAction) {
            this.file = file;
            this.fileAction = fileAction;
        }

        public EventFile(Archivo file, FileAction fileAction, Long userId) {
            this.file = file;
            this.fileAction = fileAction;
            this.percent = percent;
            this.userId = userId;
        }

        public EventFile setPercent(double percent){
            this.percent = percent;
            return this;
        }

        public EventFile setTaskId(Long taskId) {
            this.taskId = taskId;
            return this;
        }
    }

    /**
     * Evento para el recibo de tareas
     */
    public static class EventTask{
        public String data;

        public TaskAction taskAction;
        public Task task;

        public EventTask(String data, TaskAction taskAction) {
            this.data = data;
            this.taskAction = taskAction;
        }

        public EventTask(Task task, TaskAction taskAction) {
            this.task = task;
            this.taskAction = taskAction;
        }

        public EventTask(TaskAction taskAction) {
            this.taskAction = taskAction;
        }
    }

    /**
     * Evento para disconect
     */
    public static class EventDisconect{
        public String mge;
        public boolean showMessage;

        public EventDisconect() {
            showMessage = false;
        }

        public EventDisconect(boolean showMessage) {
            this.showMessage = showMessage;
        }

        public EventDisconect(String mge) {
            this.mge = mge;
        }
    }

    /**
     * Eventos para Board
     */
    public static class EventBoard {

        public BoardType boardType;

        public BGraphic board;

        public int Id;

        public EventBoard(BoardType boardType, BGraphic board, int id) {
            this.boardType = boardType;
            this.board = board;
            Id = id;
        }

        public EventBoard(BoardType boardType) {
            this.boardType = boardType;
        }
    }

    /**
     * Eventos para sumario
     */
    public static class EventSummary{
        public SummaryType sumaryType;

        public EventSummary(SummaryType sumaryType) {
            this.sumaryType = sumaryType;
        }
    }

    /**
     * Eventos para grupos del chat
     */
    public static class EventChat{
        List<GroupSend> groupList;

        public List<GroupSend> getGroupList() {
            return groupList;
        }

        public void setGroupList(List<GroupSend> groupList) {
            this.groupList = groupList;
        }

    }

    /**
     * Eventos para chat msg
     */
    public static class EventChatMSG{
        Message message;

        public EventChatMSG(Message message) {
            this.message = message;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    /**
     * Eventos para user
     */
    public static class EventUserImageEdit{
        String imagePath;

        public EventUserImageEdit(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }
    }


    /**
     * Evento para deterner compartir pantalla
     */
    public static class EventStopShareScreem{
        public EventStopShareScreem() {
        }
    }
    /**
     * Evento para deterner streaming
     */
    public static class EventStopStreaming{
        public EventStopStreaming() {
        }
    }
    /**
     * Evento para deterner discover
     */
    public static class EventStopDiscover{
        public EventStopDiscover() {
        }
    }


    //Valores enums
    public enum BadgeType{// Tipo de badge q se va a actualizar
        TASK,CHAT,FILE
    }
    public enum FileAction{//Acciones para envio de archivos
        SEND_FILE_FROM_TASK,NOTIFY_FILE_COPY,NOTIFY_FILE_COPY_FROM_TASK,NOTIFY_FILE_COPY_TO_TASK,UPDATE_PERCENT,EXECUTE_FILE,UPDATE_LIST
    }
    public enum TaskAction{//Acciones para las tareas
        NEW_TASK,EVALUATION,UPDATE,DELETE,RESOURCE_CHANGE
    }
    public enum BoardType {//para pizarra
        BOARD_INITIALIZE, BOARD_INITIALIZE_WRITABLE, BOARD_EDITING, BOARD_EDITING_END, BOARD_END, BOARD_CLEAR
    }
    public enum SummaryType{//para sumario
        OPEN_SUMMARY, CLOSE_SUMMARY_VISIBLE, CLOSE_SUMMARY_INVISIBLE, UPDATE_SUMMARY
    }
}
