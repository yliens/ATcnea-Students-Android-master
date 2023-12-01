package main.blibrary;

import android.graphics.Color;

import java.io.Serializable;

/**
 * @author Odenys Almora Rodriguez
 *         Copyright (c) 2016-2017 FORTES, UCI.
 * @version 1.0
 * @class main.blibrary
 * @date 18/11/16
 */

public class BColor implements Serializable {

    private static final long serialVersionUID = 2666622L;

    public BColor() {
    }

    /**
     * The red component of the {@code Color}, in the range {@code 0.0-1.0}.
     *
     * @defaultValue 0.0
     */
    public final int getRed() {
        return red;
    }

    private int red;

    /**
     * The green component of the {@code Color}, in the range {@code 0.0-1.0}.
     *
     * @defaultValue 0.0
     */
    public final int getGreen() {
        return green;
    }

    private int green;

    /**
     * The blue component of the {@code Color}, in the range {@code 0.0-1.0}.
     *
     * @defaultValue 0.0
     */
    public final int getBlue() {
        return blue;
    }

    private int blue;

    /**
     * The opacity of the {@code Color}, in the range {@code 0.0-1.0}.
     *
     * @defaultValue 1.0
     */
    public final int getOpacity() {
        return opacity;
    }

    private int opacity = 1;


    /**
     * Creates an sRGB color with the specified RGB values in the range {@code 0-255},
     * and a given opacity.
     *
     * @param red     the red component, in the range {@code 0-255}
     * @param green   the green component, in the range {@code 0-255}
     * @param blue    the blue component, in the range {@code 0-255}
     * @param opacity the opacity component, in the range {@code 0.0-1.0}
     * @return the {@code Color}
     * @throws IllegalArgumentException if any value is out of range
     */
    public BColor(int red, int green, int blue, int opacity) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.opacity = opacity;
    }

    /**
     * Get RGB color of this BRolor
     * @return
     */
    public int getRGBColor() {
        return Color.argb( opacity, red, green, blue );
    }


}
