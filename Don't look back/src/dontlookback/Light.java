package dontlookback;

import java.awt.Color;

public interface Light extends Intangibles {

    public int getIntensity();

    public double[] getLightOrientation();

    public float spread();

    public Color getLightColor();

}
