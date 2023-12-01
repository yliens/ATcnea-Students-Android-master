package uci.atcnea.core.networking;

import android.os.AsyncTask;
import android.util.Log;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.interfaces.SyncTaskResultListenerInterface;
import uci.atcnea.core.interfaces.CommunicationInterface;
import uci.atcnea.student.MainApp;

/**
 * @class   SendMessageService
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public class SendMessageService extends AsyncTask<Void, Void, OperationResult> {

    private static final String TAG ="SendMessageService" ;
    private ServerObject     serverObject;
    private OperationResult  result;
    private CommandInterface command;
    private boolean          waitForResponse;


    private SyncTaskResultListenerInterface postExecuteListener;


    public SendMessageService() {
        this.serverObject = null;
        this.command = null;
        this.waitForResponse = true;
        this.result = new OperationResult(OperationResult.ResultCode.ERROR);
    }

    public SendMessageService(ServerObject serverObject) {
        this.serverObject = serverObject;
        this.command = null;
        this.waitForResponse = true;
        this.result = new OperationResult(OperationResult.ResultCode.ERROR);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected synchronized OperationResult doInBackground(Void... params) {

        OperationResult result = new OperationResult();
        result.setSuccess(false);

        if (command != null) {
            CommunicationInterface client = buildNetworkClient();

            if (waitForResponse) {
                result = client.sendCommandAndWaitResponse(command);
                Log.i(TAG, "ENVIANDO");

            } else {
                result = client.sendCommand(command);
                Log.i(TAG, "ENVIANDO");
            }

        }

        setResult(result);

        return result;

    }

    @Override
    protected void onPostExecute(OperationResult operationResult) {

        super.onPostExecute(operationResult);

        if (postExecuteListener != null) {
            //if (serverObject != null) {
                postExecuteListener.onSyncTaskEventCompleted(operationResult, "");
           // }


        }
    }

    public ServerObject getServerObject() {
        return serverObject;
    }

    public void setServerObject(ServerObject serverObject) {
        this.serverObject = serverObject;
    }

    public OperationResult getResult() {
        return result;
    }

    public void setResult(OperationResult result) {
        this.result = result;
    }

    public CommandInterface getCommand() {
        return command;
    }

    public void setCommand(CommandInterface command) {
        this.command = command;
    }

    public boolean isWaitForResponse() {
        return waitForResponse;
    }

    public void setWaitForResponse(boolean waitForResponse) {
        this.waitForResponse = waitForResponse;
    }

    private CommunicationInterface buildNetworkClient() {

        //return new SocketServer(getServerObject());
        return new SocketServer(MainApp.getCurrentServer());
    }

    public void setCallback(SyncTaskResultListenerInterface syncTaskResultListenerInterface) {

        this.postExecuteListener = syncTaskResultListenerInterface;
    }
}
