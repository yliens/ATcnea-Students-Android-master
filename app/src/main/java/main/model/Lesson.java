package main.model;

import org.json.JSONObject;

import java.io.Serializable;

import uci.atcnea.student.MainApp;

/**
 * @class   Lesson
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf
 */
public class Lesson  extends com.guo.duoduo.randomtextview.Lesson implements Serializable{
    private static final long serialVersionUID = 89676;
    private String ipDireccion;
    private Long idLesson;
    private Long id;
    private String macDireccion;
    private String lessonColor;
    private String lessonName;
    private String capacityState;
    private String teacherName;
    private String lessonDescription;
    private String authenticationType;
    private JSONObject jsonObject;

    private byte[] imageTeacher;

    public Lesson(String ipDireccion, String lessonName, String capacityState, String teacherName, String authenticationType, byte[] imageTeacher) {

        super( ipDireccion,  lessonName,  capacityState,  teacherName,  authenticationType,  imageTeacher);
        this.ipDireccion = ipDireccion;
        this.lessonName = lessonName;
        this.capacityState = capacityState;
        this.teacherName = teacherName;
        this.authenticationType = authenticationType;
        this.imageTeacher = imageTeacher;

        id = null;
    }

    public void saveInDatabase(){
        // Creando el objeto DAO para guardar en la BD
        uci.atcnea.student.dao.Lesson lesson = getLessonDao();
        // Guardando en la BD la clase
        lesson = MainApp.getDatabaseManager().insertLesson(lesson);
        id = lesson.getId();
    }

    public uci.atcnea.student.dao.Lesson getLessonDao(){
        // Creando el objeto DAO para guardar en la BD
        return new uci.atcnea.student.dao.Lesson(
                id,
                idLesson,
                ipDireccion,
                macDireccion,
                lessonName,
                capacityState,
                teacherName,
                authenticationType,
                imageTeacher
        );
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getLessonDescription() {
        return lessonDescription;
    }

    public void setLessonDescription(String lessonDescription) {
        this.lessonDescription = lessonDescription;
    }

    public String getLessonColor() {
        return lessonColor;
    }

    public void setLessonColor(String lessonColor) {
        super.setLessonColor(lessonColor);
        this.lessonColor = lessonColor;
    }

    public Long getId() {
        return id;
    }

    public String getLessonName() {
        return lessonName;
    }

    public String getIpDireccion() {
        return ipDireccion;
    }

    public void setIpDireccion(String ipDireccion) {
        this.ipDireccion = ipDireccion;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getCapacityState() {
        return capacityState;
    }

    public void setCapacityState(String capacityState) {
        this.capacityState = capacityState;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public byte[] getImageTeacher() {
        return imageTeacher;
    }

    public void setImageTeacher(byte[] imageTeacher) {
        this.imageTeacher = imageTeacher;
    }

    @Override
    public String toString() {
        return lessonName;
    }

    public String getMacDireccion() {
        return macDireccion;
    }

    public void setMacDireccion(String macDireccion) {
        this.macDireccion = macDireccion;
    }

    public long getIdLesson() {
        return idLesson;
    }

    public void setIdLesson(long idLesson) {
        this.idLesson = idLesson;
    }
/*
    case "authentication.automatic":{
        break;}
    case"authentication.automatic.permission":{ break;}
    case"authentication.user.password":{ break;}
    case"authentication.lms":{ break;}*/
}