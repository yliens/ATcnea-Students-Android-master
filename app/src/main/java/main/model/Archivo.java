package main.model;

import java.io.Serializable;

import uci.atcnea.student.dao.Resource;

/**
 * @class   Archivo
 * @version 1.0
 * @date 8/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public class Archivo implements Serializable {
    private String name;
    private String path;
    private long size;
    private boolean sent;
    private double percent;

    public Archivo(String name, String path, boolean sent) {
        this.name = name;
        this.path = path;
        this.sent = sent;
        percent = 0;
    }

    public Archivo() {
        percent = 0;
    }

    public Archivo(String name, String path, long size, boolean sent) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.sent = sent;
        percent = 0;
    }

    public Archivo(Resource resource) {
        this.name = resource.getName();
        this.path = resource.getUri();
        this.size = resource.getSize();
        percent = 1.0;
    }

    public Archivo(String name) {
        this.name = name;
    }

    /**
     *
     * @return retorna el tamaño del archivo
     */
    public long getSize() {
        return size/(1024*1024);
    }

    /**
     *
     * @return retorna el tamaño del archivo convertido a unidad de medida
     */
    public String getSizeByUnid(){
        if(size<(1024*1024))
            return size/1024+" KB";
        else
            return size/(1024*1024)+" MB";
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}
