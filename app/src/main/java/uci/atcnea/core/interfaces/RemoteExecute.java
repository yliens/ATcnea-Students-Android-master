package uci.atcnea.core.interfaces;

import uci.atcnea.core.networking.OperationResult;

/**
 * Created by adrian on 1/11/16.
 */
public interface RemoteExecute {
    public OperationResult execute (CommandInterface commandInterface);
}