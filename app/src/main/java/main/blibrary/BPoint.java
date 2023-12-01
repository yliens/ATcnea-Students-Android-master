package main.blibrary;

import java.io.Serializable;

/**
 * @author Odenys Almora Rodriguez
 *         Copyright (c) 2016-2017 FORTES, UCI.
 * @version 1.0
 * @class main.library
 * @date 15/11/16
 */

public class BPoint implements Serializable {

    private static final long serialVersionUID = 2666629L;

    /**
     * The x coordinate.
     *
     * @defaultValue 0.0
     */
    private double x;

    /**
     * The x coordinate.
     * @return the x coordinate
     */
    public final double getX() {
        return x;
    }

    /**
     * The y coordinate.
     *
     * @defaultValue 0.0
     */
    private double y;

    /**
     * The y coordinate.
     * @return the y coordinate
     */
    public final double getY() {
        return y;
    }

    /**
     * Cache the hash code to make computing hashes faster.
     */
    private int hash = 0;

    /**
     * Creates a new instance of {@code BPoint}.
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     */
    public BPoint(double x, double y) {
        this.x  = x;
        this.y = y;
    }

    /**
     * To compare object
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this.getX() == ((BPoint) o).getX() && this.getY() == ((BPoint) o).getY()) return true;
        else return false;
    }

    public BPoint() {
    }
}
