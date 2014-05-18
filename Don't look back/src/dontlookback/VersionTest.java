package dontlookback;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class VersionTest {
    String text = null;

    public VersionTest() throws LWJGLException{
        Display.create();
        text = "Your OpenGL version is " + GL11.glGetString(GL11.GL_VERSION);
        Display.destroy();
        
    }
}
