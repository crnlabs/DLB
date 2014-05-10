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
    private Button exitBorder;
    private Button startButton;
    private Button startBorder;
    
    private static enum State{
        MENU, GAME;
    }
    private State state=State.MENU;
    
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
        
        exitButton=new Button(325,180,150,75);
        exitBorder=new Button(320,175,160,85);
        exitBorder.setColor(0.75f,0,0.55f);
        
        startButton=new Button(325,75,150,75);
        startBorder=new Button(320,70,160,85);
        startBorder.setColor(0.75f,0,0.55f);
        
	while(!Display.isCloseRequested()) {
            
            glClear(GL_COLOR_BUFFER_BIT);
            
            render();
            
            Display.update();
            Display.sync(60);
	}
        Display.destroy();
        System.exit(0);
        
    }
    
    private void render(){
        switch(state){
            case MENU:
                if(Mouse.isButtonDown(0) && exitButton.isInBounds(Mouse.getX(), 600-Mouse.getY())){
                    Display.destroy();
                    System.exit(0);
                }
                else if(Mouse.isButtonDown(0) && startButton.isInBounds(Mouse.getX(), 600-Mouse.getY())){
                    state=State.GAME;
                }
                
                
                if(exitButton.isInBounds(Mouse.getX(), 600-Mouse.getY())){
                    exitButton.setColor(0.5f,0.5f,0.5f);
                }
                else if(startButton.isInBounds(Mouse.getX(), 600-Mouse.getY())){
                    startButton.setColor(0.5f,0.5f,0.5f);
                }
                else{
                    exitButton.setColor(0.75f,0.75f,0.75f);
                    startButton.setColor(0.75f,0.75f,0.75f);
                }
                
                glColor3f(0.75f,0.75f,0);
                glRecti(250,30,550,570);
                
                exitBorder.draw();
                startBorder.draw();
                
                exitButton.draw();
                startButton.draw();
                break;
            case GAME:
                if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
                    state=State.MENU;
                }
        }
    }
    
    private static class Button {

        public int x, y;
        public int x2,y2;
        public float red,green,blue;

        Button(int x, int y, int x2, int y2) {
            this.x = x;
            this.y = y;
            this.x2=x2;
            this.y2=y2;
        }

        boolean isInBounds(int mouseX, int mouseY) {
            return mouseX > x && mouseX < x + x2 && mouseY > y && mouseY < y + y2;
        }
        
        void setColor(float red, float green, float blue){
            this.red=red;
            this.green=green;
            this.blue=blue;
        }
        
        void draw(){
            glColor3f(red,green,blue);
            glBegin(GL_QUADS);
                glVertex2i(x, y);
                glVertex2i(x + x2, y);
                glVertex2i(x + x2, y + y2);
                glVertex2i(x, y + y2);
            glEnd();
        }
        
    }
    
}
