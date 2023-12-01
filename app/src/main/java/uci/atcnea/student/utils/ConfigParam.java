package uci.atcnea.student.utils;

import uci.atcnea.core.networking.OperationResult;

/**
 * @class   ConfigParam
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public class ConfigParam {

    // Config relate to WS
    public static final int TIMEOUT_HTTP_REQUEST = 5000;
    public static final int TIME_CHECK_OPERATION = 3000;

    public static final String HTTP_POST = "POST";
    public static final String HTTP_GET = "GET";

    public static final String WS_SOAP_PROTOCOL = "SOAP";
    public static final String WS_REST_PROTOCOL = "REST";

    public static final int WS_ERROR_RESULT_400 = 400; // Error en la sintaxis del mensaje

    public static final int WRONG_CONNECTION = 500;
    public static final int IO_EXCEPTION = 501;


    // ERRORS WS
    public static final String TAG_ERROR = "ERROR";
    public static final int TAG_INT_ERROR = 1;
    public static final String TAG_SUCCESS = "SUCCESS";
    public static final int TAG_INT_SUCCESS = 2;

    public static final String MSG_ERROR = "MSG_ERROR";
    public static final String MSG_SUCCES = "MSG_SUCCES";

    public static final String JSON_RESULT_OK = "OK";
    public static final String JSON_RESULT_ERROR = "ERROR";
    public static final String JSON_RESULT_PROCESS = "PROCESO";


    public static final int HTTP_HOST_CONNECT_EXCEPTION = 7;

    public static final int TAG_ACTIVITY_OPEN_1 = 1;
    public static final int TAG_ACTIVITY_OPEN_2 = 2;
    public static final int TAG_ACTIVITY_OPEN_3 = 3;
    public static final int TAG_ACTIVITY_OPEN_4 = 4;


}
