package uci.atcnea.student.fragment.Controllers;

import com.squareup.otto.Subscribe;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.model.GroupSend;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.activity.MainActivity;
import uci.atcnea.student.chat.Msg_other;
import uci.atcnea.student.chat.RV_Chat_Adapter;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.fragment.ChatFragment;
import uci.atcnea.student.utils.ATcneaUtil;

/**
 * Created by guillermo on 24/01/17.
 */
public class ChatController implements Serializable {



    public static void setIntance(ChatController intance) {
        ChatController.intance[0] = intance;
    }

    private static ChatController[] intance=new ChatController[1];

    public ChatFragment chatFragment;
    private ChatObject chatObject;
    private List<GroupSend> chatGroups;

    public static ChatController getIntance() {
        if (intance[0] == null) {
            intance[0] = new ChatController();
            GlobalBus.getBus().register(intance[0]);

        }
        // valores iniciales BEGIN

        // valores iniciales END

        return intance[0];
    }

    public ChatController() {
        chatObject = new ChatObject();
    }

    /**
     * Destroit intance of controller
     */
    public static void destroitIntance() {
        if (intance[0] != null)
            GlobalBus.getBus().unregister(intance[0]);
        intance[0] = null;
    }


    public ChatFragment getChatFragment() {
        return chatFragment;
    }

    public void setChatFragment(ChatFragment chatFragment) {
        this.chatFragment = chatFragment;
    }

    //Eventos del fragment
    @Subscribe
    public void eventUpdateChat(Events.EventChat eventNewChat) {
        setChatGroups(eventNewChat.getGroupList());

        //if (ChatController.getIntance().getChatGroups() == null) {
        int i = 0;
        for (GroupSend g : ChatController.getIntance().getChatGroups()) {
            if (!chatObject.getaMapChat().containsKey(g.getId())) {
                chatObject.getaMapChat().put(g.getId(), new RV_Chat_Adapter(MainApp.getAppContext()));
                getChatObject().getaMapTabId().put(i, g.getId());
                getChatObject().getaMapGroupId().put(g.getId(), i++);
            }
        }
        if (getChatObject().getaMapChat() != null) {

            Iterator<Integer> iterator = getChatObject().getaMapChat().keySet().iterator();

            while (iterator.hasNext()) {
                Integer key = iterator.next();
                boolean esta = false;
                int p = 0;
                for (GroupSend g : ChatController.getIntance().getChatGroups()) {
                    if (key == g.getId()) {
                        esta = true;
                        break;
                    }
                    p++;
                }
                if (!esta) {
                    iterator.remove();
                    //getChatObject().getaMapChat().remove(key);
                    getChatObject().getaMapTabId().remove(p);
                    getChatObject().getaMapGroupId().remove(key);
                }
            }


        }


        // }
        getChatObject().setCurrentTab(0);

        if (chatFragment != null) {

            MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatFragment.initChatConnection();
                }
            });
        }
        android.util.Log.e("eventUpdateChat", "eventUpdateChatBueno");
    }

    @Subscribe
    public void eventUpdateChatMSG(final Events.EventChatMSG eventNewChatMSG) {
        MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Msg_other msg_other = new Msg_other(eventNewChatMSG.getMessage().getUsername(), eventNewChatMSG.getMessage().getMessage());
                if (!getChatObject().getaMapChat().get(eventNewChatMSG.getMessage().getIdGroup()).msg.contains(msg_other))
                    getChatObject().getaMapChat().get(eventNewChatMSG.getMessage().getIdGroup()).add(msg_other);
                if (chatFragment != null) {
                    chatFragment.updateNewMSG(eventNewChatMSG);

                }
                getChatObject().setCurrentTab(getChatObject().getaMapGroupId().get(eventNewChatMSG.getMessage().getIdGroup()));
                getChatObject().getaMapChat().get(eventNewChatMSG.getMessage().getIdGroup()).notifyDataSetChanged();
                if (((MainActivity) MainApp.getCurrentActivity()).getCurrentFragment() != ATcneaUtil.DRAWER_CHAT_IDENTIFIER) {
                    //((MainActivity) MainApp.getCurrentActivity()).updateChatItemNewMSG(chatObject.addNewMsg());
                    GlobalBus.getBus().post( new Events.EventBadge(Events.BadgeType.CHAT, chatObject.addNewMsg()) );
                }

            }
        });

        android.util.Log.e("eventUpdateChatMSG", "eventUpdateChatMSG");
    }

    public class ChatObject {

        //para saber la cantidad de msg sin ver
        int newMsgCount;
        //para saber el ultimo grupo q escribio
        int currentTab;
        //para vincular cada adapter con la sala a q pertenece
        Map<Integer, RV_Chat_Adapter> aMapChat = new HashMap<Integer, RV_Chat_Adapter>();
        //para la relacion tab position grupo id
        Map<Integer, Integer> aMapTabId = new HashMap<Integer, Integer>();
        //para la relacion grupo con tab
        Map<Integer, Integer> aMapGroupId = new HashMap<Integer, Integer>();

        public List<GroupSend> groups;

        public ChatObject() {
            newMsgCount = 0;

        }

        public Map<Integer, RV_Chat_Adapter> getaMapChat() {
            return aMapChat;
        }

        public void setaMapChat(Map<Integer, RV_Chat_Adapter> aMapChat) {
            this.aMapChat = aMapChat;
        }

        public int addNewMsg() {
            return ++newMsgCount;
        }

        public int getNewMsgCount() {
            return newMsgCount;
        }

        public void setNewMsgCount(int newMsgCount) {
            this.newMsgCount = newMsgCount;
        }

        public Map<Integer, Integer> getaMapTabId() {
            return aMapTabId;
        }

        public void setaMapTabId(Map<Integer, Integer> aMapTabId) {
            this.aMapTabId = aMapTabId;
        }

        public Map<Integer, Integer> getaMapGroupId() {
            return aMapGroupId;
        }

        public void setaMapGroupId(Map<Integer, Integer> aMapGroupId) {
            this.aMapGroupId = aMapGroupId;
        }

        public List<GroupSend> getGroups() {
            return groups;
        }

        public void setGroups(List<GroupSend> groups) {
            this.groups = groups;
        }

        public int getCurrentTab() {
            return currentTab;
        }

        public void setCurrentTab(int currentTab) {
            this.currentTab = currentTab;
        }
    }

    public ChatObject getChatObject() {
        return chatObject;
    }

    public void setChatObject(ChatObject chatObject) {
        this.chatObject = chatObject;
    }

    public List<GroupSend> getChatGroups() {
        return chatGroups;
    }

    public void setChatGroups(List<GroupSend> chatGroups) {
        this.chatGroups = chatGroups;
//        for (GroupSend g: chatGroups ) {
//
//        MainApp.getDatabaseManager().insertGroup(new Group());
//        }
    }

}
