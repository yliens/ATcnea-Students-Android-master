package com.guo.duoduo.randomtextview;

/**
 * Created by adrian on 21/04/16.
 */
public class Lesson {
    private String ipDireccion;
    private String lessonName;
    private String capacityState;
    private String teacherName;
    private String authenticationType;
    private String lessonColor;

    private byte[] imageTeacher;



    public Lesson(String ipDireccion, String lessonName, String capacityState, String teacherName, String authenticationType, byte[] imageTeacher) {
        this.ipDireccion = ipDireccion;
        this.lessonName = lessonName;
        this.capacityState = capacityState;
        this.teacherName = teacherName;
        this.authenticationType = authenticationType;
        this.imageTeacher = imageTeacher;
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
        return  lessonName ;
    }

    public String getLessonColor() {
        return lessonColor;
    }

    public void setLessonColor(String lessonColor) {
        this.lessonColor = lessonColor;
    }


    /*
    case "authentication.automatic":{
        break;}
    case"authentication.automatic.permission":{ break;}
    case"authentication.user.password":{ break;}
    case"authentication.lms":{ break;}*/
}