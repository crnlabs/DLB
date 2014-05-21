package dontlookback;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*; //should be 30 or above up to 44 (30 = 3.0 etc)
import static org.lwjgl.util.glu.GLU.gluPerspective;
import org.lwjgl.*;
import org.lwjgl.input.*;

public class DLB_Graphics {

    private static float cameraX, cameraY, cameraZ; //camera pos x,y,z
    private static float rotX, rotY, rotZ; //rotation of camera around x,y,z axis
    private static float cX, cY, cZ; //last camera pos x,y,z
    private static float rX, rY, rZ; //last rotation of camera around x,y,z axis

    private int delta; //something used to control movement independently of fps
    private static long lastFrame; //used in calculating delta
    private final float walkingSpeed; //walking speed (approx 1 m/s)
    private float velocityX = 0; //curent velocity in X direction. starts as 0 or rest state.
    private float velocityY = 0; //curent velocity in Y direction. starts as 0 or rest state.
    private float velocityZ = 0; //curent velocity in Z direction. starts at 0 or rest state.
    private float sprintSpeed = 0; // temporary. will be moved into player class as a max speed mulitplier.

    public DLB_Graphics(boolean debug) {

        Player player = new Player();
        //this.walkingSpeed = player.speed();
        walkingSpeed=.005f;
        int resolutionX = 1024, resolutionY = 768;
        try {
            
            Display.setDisplayMode(new DisplayMode(resolutionX, resolutionY)); //these modify the existing window
            glScissor(0, 0, resolutionX, resolutionY);
            glViewport(0, 0, resolutionX, resolutionY);
            Display.setTitle("Don't Look Back");
            Display.setResizable(true);
            Display.sync(60);
            Display.update();
        } catch (LWJGLException ex) {
            Logger.getLogger(DLB_Graphics.class.getName()).log(Level.SEVERE, null, ex);
        }

        cameraX = 0f;
        cameraZ = 0f;
        cameraY = -1.75f; //I bet there is a setting to reverse what Y is, that would make things conveniant going forward.

        rotX = 0;
        rotY = 0;
        rotZ = 0;

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(68, (float) Display.getWidth() / (float) Display.getHeight(), 0.3f, 4000f); //what is this refering to? still want to know? 0.3f? 4000f? 68?
        glMatrixMode(GL_MODELVIEW);
        glEnable(GL_DEPTH_TEST); //TESTS ARE DUMB NO MORE TESTING - G.L.A.D.O.S
        //glEnable(GL_CULL_FACE);
        //glCullFace(GL_FRONT); // Doesn't draw front faces
        //glCullFace(GL_BACK); // Doesn't draw back faces //when we are working correctly we don't need to draw the stuff not being seen. 

        while (!Display.isCloseRequested()) {

            delta = getDelta();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            render();

            glLoadIdentity();

            //updates camera
            updateCamera();
            //runs debug code that outputs camera position to console when a change occurs
            debugCamera(debug);

            grabMouse();

            while (Keyboard.next()) { //this is event driven actions, not polled
                if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A)) {
                    player.moveToLeft();
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
                    player.moveToRight();
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W)) {
                    player.moveToFront();
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
                    player.moveToBack();
                }

                //below are the ACTUAL event driven actions, above is practice
                if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                    player.jump();
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
                    player.pickUpItem();
                }

                if (Keyboard.isKeyDown(Keyboard.KEY_F11)) {
                    toggleFullscreen();
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                    if (!Mouse.isGrabbed() || Display.isFullscreen()) {
                        Display.destroy();
                        System.exit(0);
                    } else {
                        Mouse.setGrabbed(false);
                    }
                }
            }

            displayResize();

            Display.update();
            Display.sync(60);
        }

        Display.destroy();
        System.exit(0);

    }

    private void camera() {
        if (Mouse.isGrabbed() || Display.isFullscreen()) {
            float mouseDX = Mouse.getDX() * 2f * 0.16f;
            float mouseDY = Mouse.getDY() * 2f * 0.16f;

            if (rotY + mouseDX >= 360) {
                rotY = rotY + mouseDX - 360;
            } else if (rotY + mouseDX < 0) {
                rotY = 360 - rotY + mouseDX;
            } else {
                rotY += mouseDX;
            }

            if (rotX - mouseDY >= -85 && rotX - mouseDY <= 85) {
                rotX += -mouseDY;
            } else if (rotX - mouseDY < -85) {
                rotX = -85;
            } else if (rotX - mouseDY > 85) {
                rotX = 85;
            }
        }

        boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W);
        boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S);
        boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A);
        boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);
        boolean keySprint = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT); //neat I didn't know you could "or" booleans
        // i want to ramp up to full speed. if we don't have something like sliding to overcome inertia, then everyone will always run.
        //thus ramping up to full sprint is best. allowing for people to find a speed at which they are most comfortable with. this is tricky however
        //as we must account for the various conservations of inertia, but lets just start with it ramping up from 1 to 3 over 5 seconds or so
        //and then ramping down if you let go. if we use velocity to move, instead of individual commands, we can ramp it and change it over time, and have far more control over it.
        //as opposed to having to account for each if statement, we can just use them to modify and run different conditions.
        //this would be easier if the player always faced in one direction
        //anyway Lets ramp to 3 in 3 seconds, drop to 2 in 1 seconds. once you hit 1 m/s you can instantly change direction like normal.
        //since you don't run in a catesian direction, and we can't lock the velocity to the view angle, UNLESS we slow the view angle down to compensate for interia
        //IE you turn slower the faster you move, and the at a walk you have free movement again. in fact this might be best because it gives the impression of tunnel vision.
        //-----------
        //easiest will just ave velocity tied to the 3D coordinate system
        //and then use math to decide what direction "left" should be, all floats MANDATORY"
        //this IS how it currently behaves but it becomes slightly more complex when we have
        //the system remember and calculate total velocity
        float angle, hypotenuse;
        angle = hypotenuse = -1;
        if (keyUp && keyRight && !keyLeft && !keyDown) { //forward to the right //diagonal
            angle = rotY + 45;

            if (!keySprint) {
                hypotenuse = (walkingSpeed) * delta;

            } else {
                hypotenuse = (walkingSpeed * 3) * delta;

            }
        } else if (keyUp && keyLeft && !keyRight && !keyDown) { //forward to the left //diagonal
            angle = rotY - 45;
            if (!keySprint) {
                hypotenuse = (walkingSpeed) * delta;
            } else {
                hypotenuse = (walkingSpeed * 3) * delta;
            }
        } else if (keyUp && !keyLeft && !keyRight && !keyDown) { //forward
            angle = rotY;
            if (!keySprint) {
                hypotenuse = (walkingSpeed) * delta;
            } else {
                hypotenuse = (walkingSpeed * 3) * delta;
            }
        } else if (keyDown && keyLeft && !keyRight && !keyUp) { //reverse and to the left //diagonal
            angle = rotY - 135;
            if (!keySprint) {
                hypotenuse = (walkingSpeed) * delta;
            } else {
                hypotenuse = (walkingSpeed * 3) * delta;
            }
        } else if (keyDown && keyRight && !keyLeft && !keyUp) { //reverse and to the right //diagonal
            angle = rotY + 135;
            if (!keySprint) {
                hypotenuse = (walkingSpeed) * delta;
            } else {
                hypotenuse = (walkingSpeed * 3) * delta;
            }
        } else if (keyDown && !keyUp && !keyLeft && !keyRight) { //reverse
            angle = rotY;
            if (!keySprint) {
                hypotenuse = -(walkingSpeed) * delta;
            } else {
                hypotenuse = -(walkingSpeed * 3) * delta;
            }
        } else if (keyLeft && !keyRight && !keyUp && !keyDown) { //left
            angle = rotY - 90;
            if (!keySprint) {
                hypotenuse = (walkingSpeed) * delta;
            } else {
                hypotenuse = (walkingSpeed * 3) * delta;
            }
        } else if (keyRight && !keyLeft && !keyUp && !keyDown) { //right
            angle = rotY + 90;
            if (!keySprint) {
                hypotenuse = (walkingSpeed) * delta;
            } else {
                hypotenuse = (walkingSpeed * 3) * delta;
            }
        }

        if (angle != -1) {
            float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
            float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
            cameraZ += adjacent;
            cameraX -= opposite;
        }

    }

    private void render() {
        //broken but close
        //shape1 = new Shapes.renderCube();
        //shapeTriangle1 = new Shapes.renderTriangle();    
        //working but wrong
        
        float[] testCenter = {2f, 5f, 3f};
        float[] testCenter2 = {-2f, 7f, -3f};
        
        Cube cube1=new Cube();
        cube1.setX(30);
        cube1.setY(15);
        cube1.setZ(30);
        cube1.setWidth(30);
        cube1.setOrientation(45);
        Cube cube2=new Cube(testCenter,10,0);
        Cube cube3=new Cube(testCenter2,14,0);
        
        Shapes.floorTest();
        
        cube1.render();
        cube2.render();
        cube3.render();

    }

    private void updateCamera() {
        //used in debug code
        cX = cameraX;
        cY = cameraY;
        cZ = cameraZ;
        rX = rotX;
        rY = rotY;

        glRotatef(rotX, 1, 0, 0);
        glRotatef(rotY, 0, 1, 0);
        glRotatef(rotZ, 0, 0, 1);
        glTranslatef(cameraX, cameraY, cameraZ);
        camera();
    }

    private void debugCamera(boolean debug) {
        if (debug == true) {
            //outputs current x,y,z coords
            //and the rotation about the x and y axis
            if (cameraX != cX || cameraY != cY || cameraZ != cZ) {
                System.out.println("X: " + cameraX + ",  Y: " + cameraY + ", Z: " + cameraZ);
            }
            if (rotX != rX || rotY != rY) {
                System.out.println("RotX: " + rotX + ", RotY: " + rotY);
            }
        }
    }

    private void displayResize() {
        if (Display.wasResized()) {
            glViewport(0, 0, Display.getWidth(), Display.getHeight());
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            gluPerspective(68, (float) Display.getWidth() / (float) Display.getHeight(), 0.3f, 4000f);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
        }
    }

    private void toggleFullscreen() {
        try {

            if (!Display.isFullscreen()) {
                Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
                glViewport(0, 0, Display.getWidth(), Display.getHeight());
                glMatrixMode(GL_PROJECTION);
                glLoadIdentity();
                gluPerspective(68, (float) Display.getWidth() / (float) Display.getHeight(), 0.3f, 4000f);
                glMatrixMode(GL_MODELVIEW);
                glLoadIdentity();
                Mouse.setGrabbed(true);
            } else {
                Display.setFullscreen(false);
                Display.setResizable(true);
                Display.setDisplayMode(new DisplayMode(1024, 768));
                glViewport(0, 0, Display.getWidth(), Display.getHeight());
                glMatrixMode(GL_PROJECTION);
                glLoadIdentity();
                gluPerspective(68, (float) Display.getWidth() / (float) Display.getHeight(), 0.3f, 4000f);
                glMatrixMode(GL_MODELVIEW);
                glLoadIdentity();
            }
        } catch (LWJGLException ex) {
            ex.printStackTrace();
        }
    }

    private void grabMouse() {
        if (Mouse.isButtonDown(0) && !Mouse.isGrabbed()) {
            Mouse.setGrabbed(true);
        }
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
