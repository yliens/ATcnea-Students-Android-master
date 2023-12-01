package uci.atcnea.core.networking;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.interfaces.RemoteExecute;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;

/**
 * Created by odenys on 1/11/16.
 */
public class RemoteExecuteClient implements RemoteExecute {
    public RemoteExecuteClient() {
    }

    @Override
    public OperationResult execute(CommandInterface commandInterface) {
        return commandInterface.execute(MainApp.getAppContext());
    }
}
