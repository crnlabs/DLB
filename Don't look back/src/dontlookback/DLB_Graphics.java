/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dontlookback;

import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
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
    
    private Button exitButton;
    
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
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, 800, 600, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        
        exitButton=new Button(10,10);
        
	while(!Display.isCloseRequested()) {
            
            glClear(GL_COLOR_BUFFER_BIT);
            
            if(Mouse.isButtonDown(0) && exitButton.isInBounds(Mouse.getX(), 600-Mouse.getY())){
                Display.destroy();
                System.exit(0);
            }
            
            exitButton.draw();
            
            Display.update();
            Display.sync(60);
	}
        Display.destroy();
        System.exit(0);
        
    }
    
    private static class Button {

        public int x, y;

        Button(int x, int y) {
            this.x = x;
            this.y = y;
        }

        boolean isInBounds(int mouseX, int mouseY) {
            return mouseX > x && mouseX < x + 75 && mouseY > y && mouseY < y + 25;
        }

        void draw(){
            glBegin(GL_QUADS);
                glVertex2f(x, y);
                glVertex2f(x + 75, y);
                glVertex2f(x + 75, y + 25);
                glVertex2f(x, y + 25);
            glEnd();
        }
    }
    
}
