package dontlookback;

import org.lwjgl.LWJGLException;

public class DontLookBack {

    public static void main(String[] args) throws LWJGLException { //exception thrown for version test
        consoleIntro();
        consoleDebug();
        newWindow();
        //consoleTracking(true);
        //create window
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
        VersionTest version = new VersionTest();
        System.out.println("Debug Information: ");
        System.out.println(version.text);
        System.out.println(modelPath + "monkey3.obj");
        System.out.println("~*~*~*~*~*~*~*~*~*~*~*~*~**~*~");

    }

    private static void consoleTracking(boolean debug) {
        System.out.println("The following is movement tracking:");
        DLB_Graphics Graphics = new DLB_Graphics(debug);
    }
    
    private static void newWindow(){
        Window window = new Window();
    }
}
