
package dontlookback;

import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import org.lwjgl.*;
import org.lwjgl.input.*;

public class DLB_Graphics{

	private static float cameraX,cameraY,cameraZ; //camera pos x,y,z
	private static float rotX,rotY,rotZ; //rotation of camera around x,y,z axis
	private static float cX,cY,cZ; //last camera pos x,y,z
	private static float rX,rY,rZ; //last rotation of camera around x,y,z axis
        
	private int delta; //something used to control movement independently of fps
        private static long lastFrame; //used in calculating delta
	private final float walkingSpeed=70.4f; //walking speed (approx 5 ft/s)

	public DLB_Graphics(){

		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.setTitle("Don't Look Back");
			Display.create();
		}
		catch (LWJGLException e) {
			e.printStackTrace();
			Display.destroy();
			System.exit(1);
		}
		
		cameraX=0f;cameraZ=0f;
		cameraY=-70f; //what is the f? what is the 70 here
		
		rotX=0;rotY=0;rotZ=0;
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(68, (float) Display.getWidth() / (float) Display.getHeight(), 0.3f, 10000f);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_DEPTH_TEST);
		
		while(!Display.isCloseRequested()) {
		
			delta=getDelta();

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			render();
			
			glLoadIdentity();
                        
                        //if i'm getting this right...
                        //I can grab the old camera postions here?
                        //if not move this piece of code to whereever the "old"
                        //or last camera position was.
                        cX = cameraX;
                        cY = cameraY;
                        cZ = cameraZ;
                        rX = rotX;
                        rY = rotY;
                        
                        
                        //moves camera
			glRotatef(rotX, 1, 0, 0);
			glRotatef(rotY, 0, 1, 0);
			glRotatef(rotZ, 0, 0, 1);
			glTranslatef(cameraX,cameraY,cameraZ);
			
                        //calculates new camera pos
			camera();
                        
                        //outputs current x,y,z coords
                        //and the rotation about the x and y axis
                        if(cameraX != cX || cameraY != cY || cameraZ != cZ){
                        System.out.println( "X: " + cameraX+",  Y: "+cameraY+", Z: "+cameraZ);
                        }
                        if(rotX != rX || rotY != rY){
                        System.out.println("RotX: " + rotX+", RotY: "+rotY);
                        }

			Display.update();
			Display.sync(60);
		}
		Display.destroy();
		System.exit(0);

}
	//A bunch of calculations that make the camera work like it should
	private void camera(){
	
            if(Mouse.isButtonDown(0)){
		float mouseDX = Mouse.getDX() * 2f * 0.16f;
		float mouseDY = Mouse.getDY() * 2f * 0.16f;
		
		if (rotY + mouseDX >= 360) {
			rotY = rotY + mouseDX - 360;
		}
		else if (rotY + mouseDX < 0) {
			rotY = 360 - rotY + mouseDX;
		}
		else {
			rotY += mouseDX;
		}
		
		if (rotX - mouseDY >= -85 && rotX - mouseDY <= 85) {
			rotX += -mouseDY;
		}
		else if (rotX - mouseDY < -85) {
			rotX = -85;
		}
		else if (rotX - mouseDY > 85) {
			rotX = 85;
		}
            }
	
            boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W);
            boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S);
            boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A);
            boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);
            boolean keySprint = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
	
            float angle,hypotenuse;
            angle=hypotenuse=-1;
            	if (keyUp && keyRight && !keyLeft && !keyDown) {
            	angle = rotY + 45;
	
            	if(!keySprint){
            		hypotenuse = (walkingSpeed * 0.0002f) * delta;
		}
            	else{
			hypotenuse = ((walkingSpeed*4) * 0.0002f) * delta;
		}
            }
            else if (keyUp && keyLeft && !keyRight && !keyDown) {
		angle = rotY - 45;
		if(!keySprint){
			hypotenuse = (walkingSpeed * 0.0002f) * delta;
		}
		else{
			hypotenuse = ((walkingSpeed*4) * 0.0002f) * delta;
		}
            }
            else if (keyUp && !keyLeft && !keyRight && !keyDown) {
		angle = rotY;
		if(!keySprint){
			hypotenuse = (walkingSpeed * 0.0002f) * delta;
		}
		else{
			hypotenuse = ((walkingSpeed*4) * 0.0002f) * delta;
		}
            }
            else if (keyDown && keyLeft && !keyRight && !keyUp) {
		angle = rotY - 135;
		if(!keySprint){
			hypotenuse = (walkingSpeed * 0.0002f) * delta;
		}
		else{
			hypotenuse = ((walkingSpeed*4) * 0.0002f) * delta;
		}
            }
            else if (keyDown && keyRight && !keyLeft && !keyUp) {
		angle = rotY + 135;
		if(!keySprint){
			hypotenuse = (walkingSpeed * 0.0002f) * delta;
		}
		else{
			hypotenuse = ((walkingSpeed*4) * 0.0002f) * delta;
		}
            }
            else if (keyDown && !keyUp && !keyLeft && !keyRight) {
		angle = rotY;
		if(!keySprint){
			hypotenuse = -(walkingSpeed * 0.0002f) * delta;
		}
		else{
			hypotenuse = -((walkingSpeed*4) * 0.0002f) * delta;
		}
            }
            else if (keyLeft && !keyRight && !keyUp && !keyDown) {
		angle = rotY - 90;
		if(!keySprint){
			hypotenuse = (walkingSpeed * 0.0002f) * delta;
		}
		else{
			hypotenuse = ((walkingSpeed*4) * 0.0002f) * delta;
		}
            }
            else if (keyRight && !keyLeft && !keyUp && !keyDown) {
		angle = rotY + 90;
		if(!keySprint){
			hypotenuse = (walkingSpeed * 0.0002f) * delta;
		}
		else{
			hypotenuse = ((walkingSpeed*4) * 0.0002f) * delta;
		}
            }   
	
            if(angle!=-1){
		float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
		float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
		cameraZ += adjacent;
		cameraX -= opposite;
            }
	
	}
	
	private void render(){
            
            glBegin(GL_QUADS);
		
		glColor3f(1.0f,1.0f,0.0f);
		glVertex3f(45.0f,30.0f,15.0f);
		glVertex3f(15.0f,30.0f,15.0f);
		glVertex3f(15.0f,30.0f,45.0f);
		glVertex3f(45.0f,30.0f,45.0f);
			
		glColor3f(1.0f,0.5f,0.0f);
		glVertex3f(45.0f,0.0f,45.0f);
		glVertex3f(15.0f,0.0f,45.0f);
		glVertex3f(15.0f,0.0f,15.0f);
		glVertex3f(45.0f,0.0f,15.0f);
			
		glColor3f(1.5f,0.0f,0.0f);
		glVertex3f(45.0f,30.0f,45.0f);
		glVertex3f(15.0f,30.0f,45.0f);
		glVertex3f(15.0f,0.0f,45.0f);
		glVertex3f(45.0f,0.0f,45.0f);
			
		glColor3f(0.5f,0.5f,0.5f);
		glVertex3f(45.0f,0.0f,15.0f);
		glVertex3f(15.0f,0.0f,15.0f);
		glVertex3f(15.0f,30.0f,15.0f);
		glVertex3f(45.0f,30.0f,15.0f);
			
		glColor3f(0.0f,0.0f,1.0f);
		glVertex3f(15.0f,30.0f,45.0f);
		glVertex3f(15.0f,30.0f,15.0f);
		glVertex3f(15.0f,0.0f,15.0f);
		glVertex3f(15.0f,0.0f,45.0f);
		
		glColor3f(1.0f,0.0f,1.0f);
		glVertex3f(45.0f,30.0f,15.0f);
		glVertex3f(45.0f,30.0f,45.0f);
		glVertex3f(45.0f,0.0f,45.0f);
		glVertex3f(45.0f,0.0f,15.0f);
			
            glEnd();
        
            glColor3f(1,1,1);
            
            glBegin(GL_QUADS);
            
                glVertex3f(150f,0f,150f);
                glVertex3f(150f,0f,-150f);
                glVertex3f(-150f,0f,-150f);
                glVertex3f(-150f,0f,150f);
             
            glEnd();
            
	}
	
        //helps calculate delta
	private static long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
        
        //calculates delta or miliseconds since last frame udpate
	private static int getDelta() {
		long currentTime = getTime();
		int delta = (int) (currentTime - lastFrame);
		lastFrame = getTime();
		return delta;
	}

}
