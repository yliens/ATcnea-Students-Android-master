package uci.atcnea.student.model;

/**
 * Created by koyi on 1/06/16.
 */
public class DividerDraw {
    private int startX;
    private int endX;
    private int y;

    public DividerDraw(int startX, int endX, int y) {
        this.startX = startX;
        this.endX = endX;
        this.y = y;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
