package uci.atcnea.student.events;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by guillermo on 22/11/16.
 */
public class GlobalBus {
    static private Bus sBus;

    public static Bus getBus() {
        if(sBus == null)
            sBus = new Bus( ThreadEnforcer.ANY );
        return sBus;
    }
}
