package uci.atcnea.core.interfaces;

import android.content.Context;

import uci.atcnea.core.networking.OperationResult;
/**
 * @class   CommandInterface
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public interface CommandInterface {


    public OperationResult execute(Context applicationContext);

}
