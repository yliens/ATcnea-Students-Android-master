package main.command;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

import main.model.User;
import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.activity.VideoStreamingActivity;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.service.VideoStreamingService;

/**
 * @class   VideoStreamingAction
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf Reproducir archivo de video

 */
public class VideoStreamingAction implements CommandInterface,Serializable {

    private static final long serialVersionUID = 9317L;

    public enum ItemNodeStatus {
        STARTSTREAMING,
        STOPSTREAMING
    }

    public VideoStreamingAction() {
    }

    private ItemNodeStatus mCode;
    private String streamingPath;

    public VideoStreamingAction(String path) {
        streamingPath = path;
    }


    @Override
    public OperationResult execute(Context applicationContext) {

        MainApp.setOpenOther(true);

//        MainApp.showVideoPlayer(streamingPath);



        if (mCode != null && mCode == ItemNodeStatus.STARTSTREAMING) {
            Intent i = new Intent(MainApp.getCurrentActivity(), VideoStreamingActivity.class);
            i.putExtra("path", streamingPath);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainApp.getCurrentActivity().startActivity(i);
        }
        if(mCode != null && mCode == ItemNodeStatus.STOPSTREAMING){
            GlobalBus.getBus().post(new Events.EventStopStreaming());
        }

        return new OperationResult();
    }
}
