package uci.atcnea.student.model;

/**
 * Created by koyi on 31/05/16.
 */
public class Connection {
    private int indexA;
    private int indexB;
    private boolean touchA;

    public Connection(int a, int b, boolean tA) {
        indexA = a;
        indexB = b;
        touchA = tA;
    }

    public boolean isTouchA() {
        return touchA;
    }

    public void setTouchA(boolean touchA) {
        this.touchA = touchA;
    }

    public int getIndexA() {
        return indexA;
    }

    public void setIndexA(int indexA) {
        this.indexA = indexA;
    }

    public int getIndexB() {
        return indexB;
    }

    public void setIndexB(int indexB) {
        this.indexB = indexB;
    }
}
