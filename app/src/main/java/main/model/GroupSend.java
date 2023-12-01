package main.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by adrian on 6/12/16.
 */
public class GroupSend implements Serializable {

    private static final long serialVersionUID = 810L;
    private int id;
    private String name;

    public GroupSend(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
