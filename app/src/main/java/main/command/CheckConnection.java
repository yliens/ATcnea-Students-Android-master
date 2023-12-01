package main.command;

import android.content.Context;

import java.io.Serializable;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;

/**
 * Created by guillermo on 23/11/16.
 */
public class CheckConnection implements CommandInterface, Serializable {

    private static final long serialVersionUID = 1239875L;

    @Override
    public OperationResult execute(Context applicationContext) {
        return new OperationResult(OperationResult.ResultCode.OK);
    }
}
