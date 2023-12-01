package main.command;

import android.content.Context;

import java.io.Serializable;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;

/**
 * Created by yerandy on 22/07/16.
 */
public class LesseonConnectRequest implements CommandInterface,Serializable {

    private static final long serialVersionUID = 12348 ;

    public LesseonConnectRequest() {

    }

    @Override
    public OperationResult execute(Context applicationContext) {
        return null;
    }

}
