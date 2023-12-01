package uci.atcnea.core.interfaces;

import uci.atcnea.core.networking.OperationResult;

/**
 * @class   SyncTaskResultListenerInterface
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public interface SyncTaskResultListenerInterface {

    void onSyncTaskEventCompleted(OperationResult result,String ip);
}
