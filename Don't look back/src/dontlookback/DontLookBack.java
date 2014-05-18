package dontlookback;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;

public class DontLookBack {
    
    public static void main(String[] args) throws LWJGLException { //exception thrown for version test
        newWindow(); //create window
        consoleIntro(); //output Into text to console
        consoleDebug(); //output debug code to console
        //loadSettings(); causes crash for some reason
        mainSequence(true); //this is the current "game" location, needs to be split up from window creation as turning off open gl 1.1 will break that class and thus all windows.
        //begin game
        //etc
    }

    private static void consoleIntro() {
        System.out.println("Don't look back.");
        System.out.println("A Game By:");
        System.out.println("Carl & James");
        System.out.println("~*~*~*~*~*~*~*~*~*~*~*~*~**~*~");
    }

    private static void consoleDebug() throws LWJGLException {
        String modelPath = System.getProperty("user.dir") + "/res/models/";
        System.out.println("Debug Information: ");
        System.out.println("Your OpenGL version is " + GL11.glGetString(GL11.GL_VERSION));
        System.out.println(modelPath + "monkey3.obj");
        System.out.println("~*~*~*~*~*~*~*~*~*~*~*~*~**~*~");

    }

    private static void mainSequence(boolean debug) {
        System.out.println("The following is movement tracking:");
        DLB_Graphics Graphics = new DLB_Graphics(debug);
    }
    
    private static void loadSettings(){
        Settings config = new Settings();
    }
    
    private static void newWindow(){
        Window window = new Window(); //should add "size" as a factor here?
    }
}
