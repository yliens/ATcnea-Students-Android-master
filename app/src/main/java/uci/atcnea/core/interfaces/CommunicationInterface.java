package uci.atcnea.core.interfaces;

import uci.atcnea.core.networking.OperationResult;

/**
 * @class   CommunicationInterface
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public interface CommunicationInterface {

    OperationResult sendCommandAndWaitResponse(CommandInterface command);

    OperationResult sendCommand(CommandInterface command);
}
