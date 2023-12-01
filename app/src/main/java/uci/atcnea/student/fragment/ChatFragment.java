package uci.atcnea.student.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import main.command.SendMessage;
import main.model.GroupSend;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.chat.Msg_user;
import uci.atcnea.student.chat.RV_Chat_Adapter;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.fragment.Controllers.ChatController;
import uci.atcnea.student.utils.TaskHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * @author Adrian Arencibia Herrera
 *         Copyright (c) 2016-2017 FORTES, UCI.
 * @version 1.0
 * @class ChatFragment
 * @date 11/07/16
 * @description
 * @rf Chat
 */
public class ChatFragment extends android.support.v4.app.Fragment implements TabLayout.OnTabSelectedListener, Serializable {


    private static final String TAG = "ChatFragment";
    RecyclerView rv_chat;

    EditText et_msg;
    Button btn_send;


    String salaName;


    private TabLayout tabLayout;
    private boolean tabSelected;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.chat_content_room, null);




        //initChatConnection();
        rv_chat = (RecyclerView) viewRoot.findViewById(R.id.recycler_view_chat);
//        if(rv_chat_adapter_sala==null)
//        rv_chat_adapter_sala=new RV_Chat_Adapter(getActivity().getApplicationContext());
//
//        if(rv_chat_adapter_profesor==null)
//            rv_chat_adapter_profesor=new RV_Chat_Adapter(getActivity().getApplicationContext());

        rv_chat.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rv_chat.setItemAnimator(new DefaultItemAnimator());

//        rv_chat.setAdapter();

        et_msg = (EditText) viewRoot.findViewById(R.id.message);
        btn_send = (Button) viewRoot.findViewById(R.id.btn_send);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // EvaluationResult evaluationResult=new EvaluationResult(4);

                // socketClient.btn_send(evaluationResult);


                try {//Para evitar q se cierre la app al enviar un mensaje

                    if (MainApp.isPermitChat()) {
                        String message = et_msg.getText().toString();
                        if (message.length() > 0) {
                            SendMessage m = new SendMessage(ChatController.getIntance().getChatObject().getaMapTabId().get(ChatController.getIntance().getChatObject().getCurrentTab()), "", MainApp.getCurrentUser().getUsername(), message, new Date());
                            SendMessageService msg = new SendMessageService(MainApp.getCurrentServer());
                            msg.setWaitForResponse(false);
                            msg.setCommand(m);
                            TaskHelper.execute(msg);

                            main.model.Message messageSave = new main.model.Message();
                            messageSave.setMessage(message);
                            messageSave.setUsername(MainApp.getCurrentUser().getUsername());
                            messageSave.setCreatedAt(new Date());
                            //messageSave.setNameGroup(ChatController.getIntance().getChatObject().getaMapTabId().get(ChatController.getIntance().getChatObject().getCurrentTab()));

//                    MainApp.getDatabaseManager().insertMessage(messageSave);
                            ChatController.getIntance().getChatObject().getaMapChat().get(ChatController.getIntance().getChatObject().getaMapTabId().get(ChatController.getIntance().getChatObject().getCurrentTab())).add(new Msg_user(m.getMessage()));
                            if (rv_chat != null) {
                                rv_chat.setAdapter(ChatController.getIntance().getChatObject().getaMapChat().get(ChatController.getIntance().getChatObject().getaMapTabId().get(ChatController.getIntance().getChatObject().getCurrentTab())));
                                //rv_chat.notify();
                            }
                            rv_chat.getLayoutManager().scrollToPosition(ChatController.getIntance().getChatObject().getaMapChat().get(ChatController.getIntance().getChatObject().getaMapTabId().get(ChatController.getIntance().getChatObject().getCurrentTab())).getItemCount() - 1);


                            // aMapChat.get(aMapTabId.get(currentTab)).notifyDataSetChanged();
                            et_msg.setText("");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });


        tabLayout = (TabLayout) viewRoot.findViewById(R.id.tabs);
//        tabLayout.addTab(tabLayout.newTab().setText("Sala"));
        //tabLayout.addTab(tabLayout.newTab().setText(salaName));
        tabLayout.setOnTabSelectedListener(this);
        tabSelected = false;


        updateViewPermitChat();

        //Registrar fragmnet actual
        ChatController.getIntance().chatFragment = this;

        //   initChatConnection();

        // ChatController.getIntance().getChatObject().setNewMsgCount(0);

        return viewRoot;
    }

    @Override
    public void onResume() {
        super.onResume();

        initChatConnection();

    }

    public void initChatConnection() {
        if (ChatController.getIntance().getChatGroups() != null) {
            for (GroupSend g : ChatController.getIntance().getChatGroups()) {
                if (ChatController.getIntance().getChatObject().getaMapChat().get(g.getId()) == null)
                    ChatController.getIntance().getChatObject().getaMapChat().put(g.getId(), new RV_Chat_Adapter(MainApp.getAppContext()));
//            tabLayout.addTab(tabLayout.newTab().setText(g.getName()),g.getId());
            }
        }
        int i = 0;
        if (ChatController.getIntance().getChatGroups() != null && tabLayout != null) {
            tabLayout.removeAllTabs();
            int currentTab = ChatController.getIntance().getChatObject().getCurrentTab();
            for (GroupSend g : ChatController.getIntance().getChatGroups()) {
//            aMapChat.put(g.getId(),new RV_Chat_Adapter(MainApp.getAppContext()));

                tabLayout.addTab(tabLayout.newTab().setText(g.getName()));
                ChatController.getIntance().getChatObject().getaMapTabId().put(i, g.getId());
                ChatController.getIntance().getChatObject().getaMapGroupId().put(g.getId(), i++);
            }
            //ChatController.getIntance().getChatObject().setCurrentTab(0);
            rv_chat.setAdapter(ChatController.getIntance().getChatObject().getaMapChat().get(ChatController.getIntance().getChatObject().getaMapTabId().get(ChatController.getIntance().getChatObject().getCurrentTab())));

            TabLayout.Tab tab = tabLayout.getTabAt(currentTab);
            tab.select();


/*        MainApp.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for (GroupSend g : MainApp.getIntance().getChatGroups()) {
                    ChatController.getIntance().getChatObject().getaMapChat().put(g.getId(), new RV_Chat_Adapter(MainApp.getAppContext()));
                    List<Message> messageList = MainApp.getDatabaseManager().getMessagesByGroup(g.getName());

                    if(messageList!=null)
                    for(Message m: messageList){

                        if(!m.getUserName().equals(MainApp.getCurrentUser().getUsername())){
                            Msg_other msg_other = new Msg_other(m.getUserName(), m.getMessage());
                            if (!ChatController.getIntance().getChatObject().getaMapChat().get(g.getId()).msg.contains(msg_other))
                                ChatController.getIntance().getChatObject().getaMapChat().get(g.getId()).add(msg_other);
                            if (rv_chat != null) {
                                rv_chat.setAdapter(ChatController.getIntance().getChatObject().getaMapChat().get(g.getId()));
                                //rv_chat.notify();
                            }


                            ChatController.getIntance().getChatObject().getaMapChat().get(g.getId()).notifyDataSetChanged();
                            //((MainActivity) MainApp.getCurrentActivity()).updateChatItemNewMSG(1);
                            rv_chat.getLayoutManager().scrollToPosition(ChatController.getIntance().getChatObject().getaMapChat().get(g.getId()).getItemCount() - 1);

                        }
                        else {
                            Msg_user msg_user = new Msg_user(m.getMessage());
                            if (!ChatController.getIntance().getChatObject().getaMapChat().get(g.getId()).msg.contains(msg_user))
                            ChatController.getIntance().getChatObject().getaMapChat().get(g.getId()).add(msg_user);
                            if (rv_chat != null) {
                                rv_chat.setAdapter(ChatController.getIntance().getChatObject().getaMapChat().get(g.getId()));
                                //rv_chat.notify();
                            }
                            rv_chat.getLayoutManager().scrollToPosition(ChatController.getIntance().getChatObject().getaMapChat().get(g.getId()).getItemCount() - 1);


                        }

                    }


                }



            }
        });*/


        }
        if (tabLayout != null) {
            try {
                TabLayout.Tab tab = tabLayout.getTabAt(ChatController.getIntance().getChatObject().getCurrentTab());
                tab.select();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

        }


    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        rv_chat.setAdapter(ChatController.getIntance().getChatObject().getaMapChat().get(ChatController.getIntance().getChatObject().getaMapTabId().get(tab.getPosition())));

        ChatController.getIntance().getChatObject().setCurrentTab(tab.getPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void updateViewPermitChat() {
        if (et_msg != null && btn_send != null) {
            et_msg.setEnabled(MainApp.isPermitChat());
            btn_send.setEnabled(MainApp.isPermitChat());
            if (MainApp.isPermitChat())
                et_msg.setHint(R.string.chat_hint);
            else
                et_msg.setHint(R.string.lock_chat);
        }
    }

    public void setSalaName(String type) {
        salaName = type;
        if (tabLayout != null && tabLayout.getTabAt(1) != null) {
            tabLayout.getTabAt(1).setText(salaName);
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (salaName == null)
            salaName = "Profesor";
    }


    @Override
    public void onStart() {
        super.onStart();
        //  GlobalBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // GlobalBus.getBus().unregister(this);
    }


    public void updateNewMSG(Events.EventChatMSG eventNewChatMSG) {
        if (rv_chat != null) {
            rv_chat.setAdapter(ChatController.getIntance().getChatObject().getaMapChat().get(eventNewChatMSG.getMessage().getIdGroup()));
            //rv_chat.notify();
            rv_chat.getLayoutManager().scrollToPosition(ChatController.getIntance().getChatObject().getaMapChat().get(eventNewChatMSG.getMessage().getIdGroup()).getItemCount() - 1);
        }
        if (tabLayout != null) {
            TabLayout.Tab tab = tabLayout.getTabAt(ChatController.getIntance().getChatObject().getaMapGroupId().get(eventNewChatMSG.getMessage().getIdGroup()));
            tab.select();

        }


    }
}
