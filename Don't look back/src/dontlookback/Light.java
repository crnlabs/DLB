/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dontlookback;

import java.awt.Color;

/**
 *
 * @author Carl
 */
public interface Light extends Intangibles{
    
    public int getIntensity();
    public double getAngle();
    public Color getLightColor();
    
}
