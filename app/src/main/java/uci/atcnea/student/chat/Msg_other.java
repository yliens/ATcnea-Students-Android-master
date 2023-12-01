package uci.atcnea.student.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Adrian Arencibia Herrera
 *         Copyright (c) 2016-2017 FORTES, UCI.
 * @version 1.0
 * @class Msg_other
 * @date 11/07/16
 * @description
 * @rf Chat
 */
public class Msg_other implements Msg {

    String name;
    String time;
    String msg;

    public Msg_other(String msg) {
        this.msg = msg;
    }

    public Msg_other(String name, String msg) {
        this.name = name;
        this.msg = msg;
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a dd/MM/yyyy");
        time= format.format(new Date());
    }

    @Override
    public String getMSG() {
        return name + " : " + msg;
    }

    @Override
    public void setMSG(String msg) {
        this.msg = msg;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public void setTime(String time) {
        time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}