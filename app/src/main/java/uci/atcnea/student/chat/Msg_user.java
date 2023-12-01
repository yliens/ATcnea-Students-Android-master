package uci.atcnea.student.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @class   Msg_user
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf Chat
 */
public class Msg_user  implements Msg{

    String msg;
    String time;
    public Msg_user(String msg) {
        this.msg=msg;
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a dd/MM/yyyy");
        time= format.format(new Date());

    }

    @Override
    public String getMSG() {
        return msg;
    }

    @Override
    public void setMSG(String msg) {
        this.msg=msg;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public void setTime(String time) {
        time=time;
    }
}