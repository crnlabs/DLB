package dontlookback;

import de.matthiasmann.twl.utils.PNGDecoder;
import java.io.*;
import java.nio.ByteBuffer;
import org.lwjgl.*;
import org.lwjgl.opengl.*;

public class Window {

    public Window() {
        
        setUpDisplay();
        Display.sync(60);
        Display.update();
        
        //programming in the main menu here would probably be conveniant.
        
        //Display.destroy();
        //System.exit(0);
    }

    private static void setUpDisplay() {
        
        Splash splash = new Splash();  //Splash
        Settings config = new Settings();  //the logo should work, it finds it correctly?
        Display.setTitle("Loading"); //this isn't a splash this is a program icon for the menus and bar.
        try {
            int resolution[] = config.resolution();
            Display.setDisplayMode(new DisplayMode(resolution[0], resolution[1]));
            PNGDecoder imageDecoder = new PNGDecoder(new FileInputStream("res/images/logo128.png"));
            if (!imageDecoder.hasAlpha() && imageDecoder.getHeight() != 128 && imageDecoder.getWidth() != 128) {
                System.err.println("Icon does not have transparency info and cannot serve as an icon for the application.");
            }
            Display.setResizable(false);
            Display.create();
            ByteBuffer imageData = BufferUtils.createByteBuffer(4 * imageDecoder.getWidth() * imageDecoder.getHeight());
            imageDecoder.decode(imageData, imageDecoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            imageData.flip();
            //System.out.println(Display.setIcon(new ByteBuffer[]{imageData})); //this is where the 0 is coming from right now
            
            /*
            /try {
            /    Thread.sleep(1000);                 //1000 milliseconds is one second.
            /} catch (InterruptedException ex) {
            /    Thread.currentThread().interrupt();  //DELAY THREAD 1 SECOND 
            /}
            */
            
        } catch (LWJGLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        /**
         * On Windows you should supply at least one 16x16 icon and one 32x32.
         * Linux (and similar platforms) expect one 32x32 icon. Mac OS X should
         * be supplied one 128x128 icon
         */
        // 16x16, 32x32, 32x32, 128x128
    }
}
