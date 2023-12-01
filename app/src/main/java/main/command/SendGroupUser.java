package main.command;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

import main.model.GroupSend;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;

/**
 * Created by adrian on 7/12/16.
 */
public class SendGroupUser implements CommandInterface, Serializable {
    private static final long serialVersionUID = 811L;
    private List<GroupSend> groupList;


    public SendGroupUser() {
    }

    public SendGroupUser(List<GroupSend> groupList) {
        this.groupList = groupList;
    }

    public List<GroupSend> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<GroupSend> groupList) {
        this.groupList = groupList;
    }

    @Override
    public OperationResult execute(Context applicationContext) {
       // MainApp.getIntance().setChatGroups(groupList);

        Events.EventChat eventChat = new Events.EventChat();
        eventChat.setGroupList(groupList);
        GlobalBus.getBus().post(eventChat);

        Log.e("setChatGroups",groupList.size()+"");
        return null;
    }
}
