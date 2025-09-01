package dontlookback.modern;

import dontlookback.modern.ModernGraphics;

/**
 * Modern main class for Don't Look Back using LWJGL 3.x
 * This replaces the legacy DontLookBack class with modern APIs
 */
public class ModernDontLookBack {
    
    public static void main(String[] args) {
        System.out.println("Don't Look Back - Modern Edition");
        System.out.println("Using LWJGL 3.x with OpenGL 3.3+");
        System.out.println("Game A Day Studios");
        System.out.println("~*~*~*~*~*~*~*~*~*~*~*~*~**~*~");
        
        try {
            // Initialize modern graphics system
            new ModernGraphics();
        } catch (Exception e) {
            System.err.println("Failed to initialize modern graphics: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}