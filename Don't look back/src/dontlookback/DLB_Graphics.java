/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dontlookback;

import org.lwjgl.opengl.*;
import org.lwjgl.opengl.GL44.*;
import org.lwjgl.*;
import org.lwjgl.input.*;

/**
 *
 * @author willi_000
 * 
 * I haven't done this in a LONG time like 5+ years so this is going to take
 * some time. -StrangeBard (James)
 * 
 * Realized I will need to be using OpenGL which I have no experience with
 * will get to work on learning and coding immediately. Do not be shocked to see
 * temporary code that has nothing to do with the game in here while I practice.
 * -StrangeBard (James)
 */
public class DLB_Graphics{
    
    public DLB_Graphics(){
        
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.setTitle("Don't Look Back");
            Display.create();
	} catch (LWJGLException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }
	while(!Display.isCloseRequested()) {
            Display.update();
            Display.sync(60);
	}
        Display.destroy();
        System.exit(0);
        
    }
    
}