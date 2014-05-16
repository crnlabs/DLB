
package dontlookback;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glNormal3f;
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
    public static void renderCube(float[] center){
        //lets default this to length, width, height, of 5
            glBegin(GL_QUADS); //this is the color cube
		
                    // top
                    glColor3f(1.0f, 0.0f, 0.0f);
                    glNormal3f(center[0]+ 0.0f, center[1] + 1.0f, center[2] + 0.0f);
                    glVertex3f(center[0]+-0.5f, center[1] + 0.5f, center[2] + 0.5f);
                    glVertex3f(center[0]+ 0.5f, center[1] + 0.5f, center[2] + 0.5f);
                    glVertex3f(center[0]+ 0.5f, center[1] + 0.5f, center[2] +-0.5f);
                    glVertex3f(center[0]+-0.5f, center[1] + 0.5f, center[2] +-0.5f);
 
                    // front
                    glColor3f(0.0f, 1.0f, 0.0f);
                    glNormal3f(center[0]+ 0.0f, center[1] + 0.0f, center[2] + 1.0f);
                    glVertex3f(center[0]+ 0.5f, center[1] +-0.5f, center[2] + 0.5f);
                    glVertex3f(center[0]+ 0.5f, center[1] + 0.5f, center[2] + 0.5f);
                    glVertex3f(center[0]+-0.5f, center[1] + 0.5f, center[2] + 0.5f);
                    glVertex3f(center[0]+-0.5f, center[1] +-0.5f, center[2] + 0.5f);
 
                    // right
                    glColor3f(0.0f, 0.0f, 1.0f);
                    glNormal3f(center[0]+ 1.0f, center[1] + 0.0f, center[2] + 0.0f);
                    glVertex3f(center[0]+ 0.5f, center[1] + 0.5f, center[2] +-0.5f);
                    glVertex3f(center[0]+ 0.5f, center[1] + 0.5f, center[2] + 0.5f);
                    glVertex3f(center[0]+ 0.5f, center[1] +-0.5f, center[2] + 0.5f);
                    glVertex3f(center[0]+ 0.5f, center[1] +-0.5f, center[2] +-0.5f);
			
                    // left
                    glColor3f(0.0f, 0.0f, 0.5f);
                    glNormal3f(center[0]+-1.0f, center[1] + 0.0f, center[2] + 0.0f);
                    glVertex3f(center[0]+-0.5f, center[1] +-0.5f, center[2] + 0.5f);
                    glVertex3f(center[0]+-0.5f, center[1] + 0.5f, center[2] + 0.5f);
                    glVertex3f(center[0]+-0.5f, center[1] + 0.5f, center[2] +-0.5f);
                    glVertex3f(center[0]+-0.5f, center[1] +-0.5f, center[2] +-0.5f);
 
                    // bottom
                    glColor3f(0.5f, 0.0f, 0.0f);
                    glNormal3f(center[0]+ 0.0f, center[1] + -1.0f, center[2] + 0.0f);
                    glVertex3f(center[0]+ 0.5f, center[1] + -0.5f, center[2] + 0.5f);
                    glVertex3f(center[0]+-0.5f, center[1] + -0.5f, center[2] + 0.5f);
                    glVertex3f(center[0]+-0.5f, center[1] + -0.5f, center[2] +-0.5f);
                    glVertex3f(center[0]+ 0.5f, center[1] + -0.5f, center[2] +-0.5f);
 
                    // back
                    glColor3f(0.0f, 0.5f, 0.0f);
                    glNormal3f(center[0]+ 0.0f, center[1] + 0.0f, center[2] +-1.0f);
                    glVertex3f(center[0]+ 0.5f, center[1] + 0.5f, center[2] +-0.5f);
                    glVertex3f(center[0]+ 0.5f, center[1] +-0.5f, center[2] +-0.5f);
                    glVertex3f(center[0]+-0.5f, center[1] +-0.5f, center[2] +-0.5f);
                    glVertex3f(center[0]+-0.5f, center[1] + 0.5f, center[2] +-0.5f);
 
			
            glEnd();
    }
    public static void renderCube(float[] center, float size){
        
            glBegin(GL_QUADS); //this is the color cube
		
                    // top
                    glColor3f(1.0f, 0.0f, 0.0f);
                    glNormal3f(center[0]+ 0.0f *size, center[1] + 1.0f *size, center[2] + 0.0f *size);
                    glVertex3f(center[0]+-0.5f *size, center[1] + 0.5f *size, center[2] + 0.5f *size);
                    glVertex3f(center[0]+ 0.5f *size, center[1] + 0.5f *size, center[2] + 0.5f *size);
                    glVertex3f(center[0]+ 0.5f *size, center[1] + 0.5f *size, center[2] +-0.5f *size);
                    glVertex3f(center[0]+-0.5f *size, center[1] + 0.5f *size, center[2] +-0.5f *size);
 
                    // front
                    glColor3f(0.0f, 1.0f, 0.0f);
                    glNormal3f(center[0]+ 0.0f *size, center[1] + 0.0f *size, center[2] + 1.0f *size);
                    glVertex3f(center[0]+ 0.5f *size, center[1] +-0.5f *size, center[2] + 0.5f *size);
                    glVertex3f(center[0]+ 0.5f *size, center[1] + 0.5f *size, center[2] + 0.5f *size);
                    glVertex3f(center[0]+-0.5f *size, center[1] + 0.5f *size, center[2] + 0.5f *size);
                    glVertex3f(center[0]+-0.5f *size, center[1] +-0.5f *size, center[2] + 0.5f *size);
 
                    // right
                    glColor3f(0.0f, 0.0f, 1.0f);
                    glNormal3f(center[0]+ 1.0f *size, center[1] + 0.0f *size, center[2] + 0.0f *size);
                    glVertex3f(center[0]+ 0.5f *size, center[1] + 0.5f *size, center[2] +-0.5f *size);
                    glVertex3f(center[0]+ 0.5f *size, center[1] + 0.5f *size, center[2] + 0.5f *size);
                    glVertex3f(center[0]+ 0.5f *size, center[1] +-0.5f *size, center[2] + 0.5f *size);
                    glVertex3f(center[0]+ 0.5f *size, center[1] +-0.5f *size, center[2] +-0.5f *size);
			
                    // left
                    glColor3f(0.0f, 0.0f, 0.5f);
                    glNormal3f(center[0]+-1.0f *size, center[1] + 0.0f *size, center[2] + 0.0f *size);
                    glVertex3f(center[0]+-0.5f *size, center[1] +-0.5f *size, center[2] + 0.5f *size);
                    glVertex3f(center[0]+-0.5f *size, center[1] + 0.5f *size, center[2] + 0.5f *size);
                    glVertex3f(center[0]+-0.5f *size, center[1] + 0.5f *size, center[2] +-0.5f *size);
                    glVertex3f(center[0]+-0.5f *size, center[1] +-0.5f *size, center[2] +-0.5f *size);
 
                    // bottom
                    glColor3f(0.5f, 0.0f, 0.0f);
                    glNormal3f(center[0]+ 0.0f *size, center[1] + -1.0f *size, center[2] + 0.0f *size);
                    glVertex3f(center[0]+ 0.5f *size, center[1] + -0.5f *size, center[2] + 0.5f *size);
                    glVertex3f(center[0]+-0.5f *size, center[1] + -0.5f *size, center[2] + 0.5f *size);
                    glVertex3f(center[0]+-0.5f *size, center[1] + -0.5f *size, center[2] +-0.5f *size);
                    glVertex3f(center[0]+ 0.5f *size, center[1] + -0.5f *size, center[2] +-0.5f *size);
 
                    // back
                    glColor3f(0.0f, 0.5f, 0.0f);
                    glNormal3f(center[0]+ 0.0f *size, center[1] + 0.0f *size, center[2] +-1.0f *size);
                    glVertex3f(center[0]+ 0.5f *size, center[1] + 0.5f *size, center[2] +-0.5f *size);
                    glVertex3f(center[0]+ 0.5f *size, center[1] +-0.5f *size, center[2] +-0.5f *size);
                    glVertex3f(center[0]+-0.5f *size, center[1] +-0.5f *size, center[2] +-0.5f *size);
                    glVertex3f(center[0]+-0.5f *size, center[1] + 0.5f *size, center[2] +-0.5f *size);
 
			
            glEnd();
    }
    public static void renderTriangle(){
        glBegin(GL_TRIANGLES);                      // Drawing Using Triangles
        glVertex3f( 0.0f, 1.0f, 0.0f);              // Top
        glVertex3f(-1.0f,-1.0f, 0.0f);              // Bottom Left
        glVertex3f( 1.0f,-1.0f, 0.0f);              // Bottom Right
glEnd();
    }
    public static void renderTriangle(float[] center){
        glBegin(GL_TRIANGLES);                      // Drawing Using Triangles
        glVertex3f(center[0] + 0.0f, center[1] + 1.0f, center[2] + 0.0f);              // Top
        glVertex3f(center[0] +-1.0f, center[1] +-1.0f, center[2] + 0.0f);              // Bottom Left
        glVertex3f(center[0] + 1.0f, center[1] +-1.0f, center[2] + 0.0f);              // Bottom Right
glEnd();
    } // we should add a version with rotation but lets do that after rectangles are well constructed.
    public static void renderTriangle(float[] center, float size){
        glBegin(GL_TRIANGLES);                      // Drawing Using Triangles
        glVertex3f(center[0] + 0.0f * size, center[1] + 1.0f * size, center[2] + 0.0f * size);              // Top
        glVertex3f(center[0] +-1.0f * size, center[1] +-1.0f * size, center[2] + 0.0f * size);              // Bottom Left
        glVertex3f(center[0] + 1.0f * size, center[1] +-1.0f * size, center[2] + 0.0f * size);              // Bottom Right
glEnd();
    }
    public static void renderRectangle(){
        
    }
    public static void renderRectangle(float[]position){ //A good way to tackle the rotation is to add it in as a fourth value, and even fifth for some things, XYZ center, XYZ rotation. = POSITION
        
    }
    public static void renderRectangle(float[] position, float[] scale){  //second value is iffy here, should rectangles be a constant height? like wall height? a constant length??? hmm
        
    }
    
    public static void renderWall(){
        
    }
    public static void renderWall(float[] center){
        
    }
    public static void renderWall(float[] center, float size){
        
    }
    
    //these are tests, they will NOT be how rooms are in anyway rendered, but are nessessary for early play testing.
    public static void renderRoom(){ 
        
    }
    public static void renderRoom(float[] center){
        
    }
    public static void renderRoom(float[] center, float size){
        
    }
    
    
}
