package dontlookback;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Cube extends Objects{
    
    private float width;
    
    public Cube(){
        super();
        setWidth(0);
    }
    public Cube(float x,float y,float z,float angle,float width){
        super(x,y,z,angle);
        setWidth(width);
    }
    public Cube(float[] coords,float angle,float width){
        super(coords,angle);
        setWidth(width);
    }
    
    public void setWidth(float width){
        this.width=width;
    }
    public float getWidth(){
        return width;
    }
    
    public void render(){
        
        glPushMatrix();
        
        System.out.println(x+" "+y+" "+z);
        
        glTranslatef(x,y,z);
        glRotatef(orientation,0,1,0);
        glTranslatef(-1*x,-1*y,-1*z);        
        
        glBegin(GL_QUADS);
		
		glColor3f(rgb[0],rgb[1],rgb[2]);
		glVertex3f(x+(width/2),y+(width/2),z-(width/2));
		glVertex3f(x-(width/2),y+(width/2),z-(width/2));
		glVertex3f(x-(width/2),y+(width/2),z+(width/2));
		glVertex3f(x+(width/2),y+(width/2),z+(width/2));
			
		glColor3f(rgb[0],rgb[1],rgb[2]);
		glVertex3f(x+(width/2),y-(width/2),z+(width/2));
		glVertex3f(x-(width/2),y-(width/2),z+(width/2));
		glVertex3f(x-(width/2),y-(width/2),z-(width/2));
		glVertex3f(x+(width/2),y-(width/2),z-(width/2));
			
		glColor3f(rgb[0],rgb[1],rgb[2]);
		glVertex3f(x+(width/2),y+(width/2),z+(width/2));
		glVertex3f(x-(width/2),y+(width/2),z+(width/2));
		glVertex3f(x-(width/2),y-(width/2),z+(width/2));
		glVertex3f(x+(width/2),y-(width/2),z+(width/2));
			
		glColor3f(rgb[0],rgb[1],rgb[2]);
		glVertex3f(x+(width/2),y-(width/2),z-(width/2));
		glVertex3f(x-(width/2),y-(width/2),z-(width/2));
		glVertex3f(x-(width/2),y+(width/2),z-(width/2));
		glVertex3f(x+(width/2),y+(width/2),z-(width/2));
			
		glColor3f(rgb[0],rgb[1],rgb[2]);
		glVertex3f(x-(width/2),y+(width/2),z+(width/2));
		glVertex3f(x-(width/2),y+(width/2),z-(width/2));
		glVertex3f(x-(width/2),y-(width/2),z-(width/2));
		glVertex3f(x-(width/2),y-(width/2),z+(width/2));
		
		glColor3f(rgb[0],rgb[1],rgb[2]);
		glVertex3f(x+(width/2),y+(width/2),z-(width/2));
		glVertex3f(x+(width/2),y+(width/2),z+(width/2));
		glVertex3f(x+(width/2),y-(width/2),z+(width/2));
		glVertex3f(x+(width/2),y-(width/2),z-(width/2)); 
			
            glEnd();
        
            glPopMatrix();
            
    }
    
}
