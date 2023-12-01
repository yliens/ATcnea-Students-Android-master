package uci.atcnea.core.networking;

import android.accounts.AccountsException;
import android.net.ParseException;
import android.util.Log;


import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;


import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.net.ssl.SSLException;

import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.utils.ConfigParam;
import uci.atcnea.student.utils.SendHandlerMessage;
/**
 * @class   OperationResult
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public class OperationResult implements Serializable{

    private static final long serialVersionUID = 11787L;

    private static final String TAG = "OperationResult";

    public enum ResultCode {
        ZERO, OK, UNHANDLED_HTTP_CODE, UNAUTHORIZED, FILE_NOT_FOUND,
        INSTANCE_NOT_CONFIGURED, UNKNOWN_ERROR, WRONG_CONNECTION,
        TIMEOUT, INCORRECT_ADDRESS, HOST_NOT_AVAILABLE,
        NO_NETWORK_CONNECTION, CANCELLED,
        INVALID_LOCAL_FILE_NAME, INVALID_OVERWRITE,
        CONFLICT, LOCAL_STORAGE_FULL, LOCAL_STORAGE_NOT_MOVED,
        LOCAL_STORAGE_NOT_COPIED, OAUTH2_ERROR_ACCESS_DENIED,
        IO_EXCEPTION, JSON_EXCEPTION, CLASS_NOT_FOUND_EXCEPTION, MOODLE_EXCEPTION, ZERA_EXCEPTION, RHODA_EXCEPTION,
        QUOTA_EXCEEDED, ACCOUNT_NOT_FOUND, ACCOUNT_EXCEPTION, ACCOUNT_NOT_NEW, ACCOUNT_NOT_THE_SAME, INVALID_CHARACTER_IN_NAME,
        UNSOPPORTED_ENCODING_EXCEPTION, CLIENT_PROTOCOL_EXCEPTION, PARSE_EXCEPTION, TOAST_SHOW, ERROR,
        AUTHENTICATION_ERROR,
        FULL_ERROR,WAITING_CONFIRMATION
    }

    private boolean mSuccess = false;
    private int mHttpCode = -1;
    private Exception mException = null;
    private ResultCode resultCode;//= ResultCode.UNKNOWN_ERROR;


    private ArrayList<Object> mData;

    public OperationResult() {
//        resultCode = ResultCode.OK;
//        mSuccess = true;
//        mData = null;
    }


    public OperationResult(ResultCode code) {
        resultCode = code;
        mSuccess = (code == ResultCode.OK);
        mData = null;
    }

    public OperationResult(boolean success, int httpCode) {
        mSuccess = success;
        mHttpCode = httpCode;

        if (success) {
            resultCode = ResultCode.OK;

        } else if (httpCode > 0) {
            switch (httpCode) {
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    resultCode = ResultCode.UNAUTHORIZED;
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    resultCode = ResultCode.FILE_NOT_FOUND;
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    resultCode = ResultCode.INSTANCE_NOT_CONFIGURED;
                    break;
                case HttpURLConnection.HTTP_CONFLICT:
                    resultCode = ResultCode.CONFLICT;
                    break;
                default:
                    resultCode = ResultCode.UNHANDLED_HTTP_CODE;
                    Log.d(TAG, "OperationResult has processed UNHANDLED_HTTP_CODE: " + httpCode);
            }
        }
    }

    public OperationResult(Exception e) {
        mException = e;
        int code = -1;

        if (e instanceof SocketException) {
            resultCode = ResultCode.WRONG_CONNECTION;
            code = ConfigParam.WRONG_CONNECTION;

        } else if (e instanceof SocketTimeoutException) {
            resultCode = ResultCode.TIMEOUT;

        } else if (e instanceof ConnectTimeoutException) {
            resultCode = ResultCode.TIMEOUT;

        } else if (e instanceof MalformedURLException) {
            resultCode = ResultCode.INCORRECT_ADDRESS;

        } else if (e instanceof UnknownHostException) {
            resultCode = ResultCode.HOST_NOT_AVAILABLE;

        } else if (e instanceof AccountsException) {
            resultCode = ResultCode.ACCOUNT_EXCEPTION;

        } else if (e instanceof UnsupportedEncodingException) {
            resultCode = ResultCode.UNSOPPORTED_ENCODING_EXCEPTION;

        } else if (e instanceof ParseException) {
            resultCode = ResultCode.PARSE_EXCEPTION;

        } else if (e instanceof IOException) {
            resultCode = ResultCode.IO_EXCEPTION;
            code = ConfigParam.IO_EXCEPTION;

        } else if (e instanceof ClassNotFoundException) {
            resultCode = ResultCode.CLASS_NOT_FOUND_EXCEPTION;

        } else {
            resultCode = ResultCode.UNKNOWN_ERROR;
        }

        SendHandlerMessage.sendErrorMessage(code, this.getLogMessage());
    }


    public void setData(ArrayList<Object> files) {
        mData = files;
    }

    public ArrayList<Object> getData() {
        return mData;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public void setSuccess(boolean succes) {
        mSuccess = succes;
    }

    public boolean isCancelled() {
        return resultCode == ResultCode.CANCELLED;
    }

    public int getHttpCode() {
        return mHttpCode;
    }

    public ResultCode getCode() {
        return resultCode;
    }

    public Exception getException() {
        return mException;
    }

    public String getLogMessage() {

        if (mException != null) {
            if (mException instanceof SocketException) {
                return "Socket Exception";//MainApp.getCurrentActivity().getString(R.string.error_socket_exception);

            } else if (mException instanceof SocketTimeoutException) {
                return MainApp.getCurrentActivity().getString(R.string.error_socket_time_out);

            } else if (mException instanceof ConnectTimeoutException) {
                return MainApp.getCurrentActivity().getString(R.string.error_socket_time_out);

            } else if (mException instanceof MalformedURLException) {
                return "Malformed URL exception";

            } else if (mException instanceof UnknownHostException) {
                return MainApp.getCurrentActivity().getString(R.string.error_know_host);

            } else if (mException instanceof SSLException) {
                return "SSL exception";

            } /*else if (mException instanceof HttpException) {
                return "HTTP violation";

			}*/ else if (mException instanceof IOException) {
                return "IOException";

            } else if (mException instanceof AccountsException) {
                return "Exception while using account";

            } else if (mException instanceof JSONException) {
                return MainApp.getCurrentActivity().getString(R.string.error_unknow);

            } else if (mException instanceof ClassNotFoundException) {
                return "El servidor no está implementado";

            } else {
                return MainApp.getCurrentActivity().getString(R.string.error_unknow);
            }
        }

        if (resultCode == ResultCode.INSTANCE_NOT_CONFIGURED) {
            return "The ownCloud server is not configured!";

        } else if (resultCode == ResultCode.NO_NETWORK_CONNECTION) {
            return "No network connection";

        } else if (resultCode == ResultCode.LOCAL_STORAGE_FULL) {
            return "Local storage full";

        } else if (resultCode == ResultCode.LOCAL_STORAGE_NOT_MOVED) {
            return "Error while moving file to final directory";

        } else if (resultCode == ResultCode.ACCOUNT_NOT_NEW) {
            return "Account already existing when creating a new one";

        } else if (resultCode == ResultCode.ACCOUNT_NOT_THE_SAME) {
            return "Authenticated with a different account than the one updating";
        } else if (resultCode == ResultCode.INVALID_CHARACTER_IN_NAME) {
            return "The file name contains an forbidden character";
        }

        return "Operation finished with HTTP status code " + mHttpCode + " (" + (isSuccess() ? "success" : "fail")
                + ")";

    }

    public boolean isServerFail() {
        return (mHttpCode >= HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    public boolean isException() {
        return (mException != null);
    }

    public boolean isTemporalRedirection() {
        return (mHttpCode == 302 || mHttpCode == 307);
    }

    public Object getSoapResponseResult() {
          Object soapResponse = null;
         /* for (Object m : mData) {
              if (m instanceof SoapObject) {
                  soapResponse = m;
                  break;
              } else {
                  soapResponse = m;
              }
          }*/
          return soapResponse;
      }

    public String getStrResult() {
        String reuslt = "";
        for (Object m : mData) {
            if (m instanceof String)
                reuslt += m;
        }
        return reuslt;
    }


}