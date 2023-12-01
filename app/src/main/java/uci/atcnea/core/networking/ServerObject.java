package uci.atcnea.core.networking;

/**
 * @class   ServerObject
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public class ServerObject {
    private String host;
    private int port;
    private String servername;

    public ServerObject(String host, int port) {
        this.host = host.replace("/","");
        this.port = port;
    }

    public ServerObject(String host, int port, String servername) {
        this.host = host;
        this.port = port;
        this.servername = servername;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {

        this.host = host;
    }

    public int getPort() {

        return port;
    }

    public void setPort(int port) {

        this.port = port;
    }

    public String getServername() {
        return servername;
    }

    public void setServername(String servername) {
        this.servername = servername;
    }
}
