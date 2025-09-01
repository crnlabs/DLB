package dontlookback;

/**
 * Main class for Don't Look Back game
 * 
 * Entry point for the horror survival game featuring modern graphics
 * and cross-platform compatibility using LWJGL 3.x
 */
public class DontLookBack {
    
    public static void main(String[] args) {
        System.out.println("Don't Look Back");
        System.out.println("A Game By: Game A Day Studios");
        System.out.println("Using LWJGL 3.x with OpenGL 3.3+");
        System.out.println("~*~*~*~*~*~*~*~*~*~*~*~*~**~*~");
        
        try {
            // Initialize graphics system
            new Graphics();
        } catch (Exception e) {
            System.err.println("Failed to initialize graphics: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
