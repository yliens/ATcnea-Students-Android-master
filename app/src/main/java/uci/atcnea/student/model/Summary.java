package uci.atcnea.student.model;

import org.json.JSONObject;

/**
 * Created by guillermo on 14/10/16.
 */
public class Summary {
    String title;
    boolean state;

    public Summary(String title, boolean state) {
        this.title = title;
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public boolean isState() {
        return state;
    }
}
