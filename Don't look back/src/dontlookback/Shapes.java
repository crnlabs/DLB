
package dontlookback;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3f;


public class Shapes {
    public static void renderCube(){
        
            glBegin(GL_QUADS); //this is the color cube
		
		glColor3f(1.0f,1.0f,0.0f); //side 1, color:
		glVertex3f(45.0f,30.0f,15.0f);
		glVertex3f(15.0f,30.0f,15.0f);
		glVertex3f(15.0f,30.0f,45.0f);
		glVertex3f(45.0f,30.0f,45.0f);
			
		glColor3f(1.0f,0.5f,0.0f); //side 2, color:
		glVertex3f(45.0f,0.0f,45.0f);
		glVertex3f(15.0f,0.0f,45.0f);
		glVertex3f(15.0f,0.0f,15.0f);
		glVertex3f(45.0f,0.0f,15.0f);
			
		glColor3f(1.5f,0.0f,0.0f); //side 3, color:
		glVertex3f(45.0f,30.0f,45.0f);
		glVertex3f(15.0f,30.0f,45.0f);
		glVertex3f(15.0f,0.0f,45.0f);
		glVertex3f(45.0f,0.0f,45.0f);
			
		glColor3f(0.5f,0.5f,0.5f); //side 4, color:
		glVertex3f(45.0f,0.0f,15.0f);
		glVertex3f(15.0f,0.0f,15.0f);
		glVertex3f(15.0f,30.0f,15.0f);
		glVertex3f(45.0f,30.0f,15.0f);
			
		glColor3f(0.0f,0.0f,1.0f); //side 5, color:
		glVertex3f(15.0f,30.0f,45.0f);
		glVertex3f(15.0f,30.0f,15.0f);
		glVertex3f(15.0f,0.0f,15.0f);
		glVertex3f(15.0f,0.0f,45.0f);
		
		glColor3f(1.0f,0.0f,1.0f); //side 6, color:
		glVertex3f(45.0f,30.0f,15.0f);
		glVertex3f(45.0f,30.0f,45.0f);
		glVertex3f(45.0f,0.0f,45.0f);
		glVertex3f(45.0f,0.0f,15.0f); 
			
            glEnd();
    }
    public void renderCube(double center){
        //lets default this to length, width, height, of 5
            glBegin(GL_QUADS); //this is the color cube
		
		glColor3f(1.0f,1.0f,0.0f); //side 1, color:
		glVertex3f(45.0f,30.0f,15.0f);
		glVertex3f(15.0f,30.0f,15.0f);
		glVertex3f(15.0f,30.0f,45.0f);
		glVertex3f(45.0f,30.0f,45.0f);
			
		glColor3f(1.0f,0.5f,0.0f); //side 2, color:
		glVertex3f(45.0f,0.0f,45.0f);
		glVertex3f(15.0f,0.0f,45.0f);
		glVertex3f(15.0f,0.0f,15.0f);
		glVertex3f(45.0f,0.0f,15.0f);
			
		glColor3f(1.5f,0.0f,0.0f); //side 3, color:
		glVertex3f(45.0f,30.0f,45.0f);
		glVertex3f(15.0f,30.0f,45.0f);
		glVertex3f(15.0f,0.0f,45.0f);
		glVertex3f(45.0f,0.0f,45.0f);
			
		glColor3f(0.5f,0.5f,0.5f); //side 4, color:
		glVertex3f(45.0f,0.0f,15.0f);
		glVertex3f(15.0f,0.0f,15.0f);
		glVertex3f(15.0f,30.0f,15.0f);
		glVertex3f(45.0f,30.0f,15.0f);
			
		glColor3f(0.0f,0.0f,1.0f); //side 5, color:
		glVertex3f(15.0f,30.0f,45.0f);
		glVertex3f(15.0f,30.0f,15.0f);
		glVertex3f(15.0f,0.0f,15.0f);
		glVertex3f(15.0f,0.0f,45.0f);
		
		glColor3f(1.0f,0.0f,1.0f); //side 6, color:
		glVertex3f(45.0f,30.0f,15.0f);
		glVertex3f(45.0f,30.0f,45.0f);
		glVertex3f(45.0f,0.0f,45.0f);
		glVertex3f(45.0f,0.0f,15.0f); 
			
            glEnd();
    }
    public void renderCube(double center, double size){
        
            glBegin(GL_QUADS); //this is the color cube
		
		glColor3f(1.0f,1.0f,0.0f); //side 1, color:
		glVertex3f(45.0f,30.0f,15.0f);
		glVertex3f(15.0f,30.0f,15.0f);
		glVertex3f(15.0f,30.0f,45.0f);
		glVertex3f(45.0f,30.0f,45.0f);
			
		glColor3f(1.0f,0.5f,0.0f); //side 2, color:
		glVertex3f(45.0f,0.0f,45.0f);
		glVertex3f(15.0f,0.0f,45.0f);
		glVertex3f(15.0f,0.0f,15.0f);
		glVertex3f(45.0f,0.0f,15.0f);
			
		glColor3f(1.5f,0.0f,0.0f); //side 3, color:
		glVertex3f(45.0f,30.0f,45.0f);
		glVertex3f(15.0f,30.0f,45.0f);
		glVertex3f(15.0f,0.0f,45.0f);
		glVertex3f(45.0f,0.0f,45.0f);
			
		glColor3f(0.5f,0.5f,0.5f); //side 4, color:
		glVertex3f(45.0f,0.0f,15.0f);
		glVertex3f(15.0f,0.0f,15.0f);
		glVertex3f(15.0f,30.0f,15.0f);
		glVertex3f(45.0f,30.0f,15.0f);
			
		glColor3f(0.0f,0.0f,1.0f); //side 5, color:
		glVertex3f(15.0f,30.0f,45.0f);
		glVertex3f(15.0f,30.0f,15.0f);
		glVertex3f(15.0f,0.0f,15.0f);
		glVertex3f(15.0f,0.0f,45.0f);
		
		glColor3f(1.0f,0.0f,1.0f); //side 6, color:
		glVertex3f(45.0f,30.0f,15.0f);
		glVertex3f(45.0f,30.0f,45.0f);
		glVertex3f(45.0f,0.0f,45.0f);
		glVertex3f(45.0f,0.0f,15.0f); 
			
            glEnd();
    }
    public static void renderTriangle(){
        glBegin(GL_TRIANGLES);                      // Drawing Using Triangles
        glVertex3f( 0.0f, 1.0f, 0.0f);              // Top
        glVertex3f(-1.0f,-1.0f, 0.0f);              // Bottom Left
        glVertex3f( 1.0f,-1.0f, 0.0f);              // Bottom Right
glEnd();
    }
}
