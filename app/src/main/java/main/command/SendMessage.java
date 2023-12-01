package main.command;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.Date;

import main.model.Message;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;

/**
 * Created by adrian on 6/12/16.
 */
public class SendMessage implements CommandInterface, Serializable {
    private static final long serialVersionUID = 9875L;

  //  private Message message;
    private Integer idGroup;
    private String nameGroup;

    private String username;
    private String message;
    private Date createdAt;

    public SendMessage() {
    }

    public SendMessage(Integer idGroup, String nameGroup, String username, String message, Date createdAt) {
        this.idGroup = idGroup;
        this.nameGroup = nameGroup;
        this.username = username;
        this.message = message;
        this.createdAt = createdAt;
    }

    @Override
    public OperationResult execute(Context applicationContext) {
        Message m=new Message();
        m.setIdGroup(idGroup);
        m.setNameGroup(nameGroup);
        m.setUsername(username);
        m.setCreatedAt(createdAt);
        m.setMessage(message);

        GlobalBus.getBus().post(new Events.EventChatMSG(m));

        Log.e("CHAT CHAT CHAT",message);
        return null;
    }

    public Integer getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(Integer idGroup) {
        this.idGroup = idGroup;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
