package dontlookback;

import java.awt.Color;

public interface Light extends Intangibles {

    public int getIntensity();

    public double[] getOrientation();

    public float spread();

    public Color getLightColor();

}
