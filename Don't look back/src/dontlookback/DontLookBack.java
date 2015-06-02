package dontlookback;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;

public class DontLookBack {

    public static void main(String[] args) throws LWJGLException { //exception thrown for version test
        newWindow(); //create window
        //splash screen/intro?
        consoleIntro(); //output Into text to console
        consoleDebug(); //output debug code to console
        //loadSettings(); //loads up settings, settings are mutable but do not need to be RUN as a seperate process, simply pulled open whenever needed.
        mainSequence(); //this is the current "game" location
        //begin game
        //etc
    }

    private static void consoleIntro() {
        System.out.println("Don't look back.");
        System.out.println("A Game By:");
        System.out.println("Game A Day Studios");
        System.out.println("~*~*~*~*~*~*~*~*~*~*~*~*~**~*~");
    }

    private static void consoleDebug() throws LWJGLException {
        String modelPath = System.getProperty("user.dir") + "/res/models/";
        System.out.println("Debug Information: ");
        System.out.println("Your OpenGL version is " + GL11.glGetString(GL11.GL_VERSION));
        System.out.println(modelPath + "monkey3.obj");
        System.out.println("~*~*~*~*~*~*~*~*~*~*~*~*~**~*~");

    }

    private static void mainSequence() {
        System.out.println("The following is movement tracking:");
        DLB_Graphics Graphics = new DLB_Graphics();
    }

    private static Settings loadSettings() {
        Settings config = new Settings();
        return config;
        //IDEALY it will all share one settings, but for now i'm satisfied with just using instances, we can go through and fix it all later
        //once opengl is moved off of 1.1 entirely.
    }

    private static void newWindow() {
        Window window = new Window(); //should add "size" as a factor here?
    }
}
